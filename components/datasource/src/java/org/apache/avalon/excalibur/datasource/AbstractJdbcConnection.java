/*
 * Copyright 2002-2004 The Apache Software Foundation
 * Licensed  under the  Apache License,  Version 2.0  (the "License");
 * you may not use  this file  except in  compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed  under the  License is distributed on an "AS IS" BASIS,
 * WITHOUT  WARRANTIES OR CONDITIONS  OF ANY KIND, either  express  or
 * implied.
 *
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.avalon.excalibur.datasource;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.avalon.excalibur.pool.Pool;
import org.apache.avalon.excalibur.pool.Poolable;
import org.apache.avalon.framework.activity.Disposable;
import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.avalon.framework.logger.Logger;

/**
 * The Connection object used in conjunction with the JdbcDataSource
 * object.
 *
 * @author <a href="mailto:dev@avalon.apache.org">Avalon Development Team</a>
 * @version CVS $Revision: 1.4 $ $Date: 2004/02/28 11:47:14 $
 * @since 4.1
 */
public class AbstractJdbcConnection
        extends AbstractLogEnabled
        implements PoolSettable, Disposable, ProxiedJdbcConnection
{
    protected Connection m_connection;
    protected boolean m_autoCommit;

    private Object m_proxy;
    protected Pool m_pool;
    
    /** Flag to keep track of whether or not an error has been thrown since the last
     *   time the DB was pinged. */
    protected boolean m_encounteredError;

    /** The maximum time since a connection was last used before it will be pinged. */
    protected int m_testAge;
    protected PreparedStatement m_testStatement;
    protected SQLException m_testException;
    protected long m_lastUsed = System.currentTimeMillis();
    private static final Map m_methods;

    static
    {
        m_methods = new HashMap();
        Method[] methods = AbstractJdbcConnection.class.getDeclaredMethods();

        for( int i = 0; i < methods.length; i++ )
        {
            m_methods.put( methods[i].getName(), methods[i] );
        }
    }

    /**
     * Contains Statements created on the original jdbc connection
     * between a {@link JdbcDataSource#getConnection} and {@link
     * Connection#close}. The statements are registered using
     * {@link #registerAllocatedStatement} and deallocated in
     * {@link #close}. LinkedList was chosen because access
     * to elements is sequential through Iterator and the number
     * of elements is not known in advance. Synchronization is
     * done on the Link instance itself.
     */
    final private List m_allocatedStatements = new LinkedList();

    /**
     * @deprecated Use the version with keepAlive specified
     */
    public AbstractJdbcConnection( final Connection connection, final boolean oradb )
    {
        this( connection, ( oradb ) ? "select 1 from dual" : "select 1" );
    }

    /**
     * @param connection a driver specific JDBC connection to be wrapped.
     * @param keepAlive a query which will be used to check the statis of the connection after it
     *                  has been idle.  A null value will cause the keep alive feature to
     *                  be disabled.
     */
    public AbstractJdbcConnection( final Connection connection, final String keepAlive )
    {
        this( connection, keepAlive, 5000 );
    }

    /**
     * @param connection a driver specific JDBC connection to be wrapped.
     * @param keepAlive a query which will be used to check the statis of the connection after it
     *                  has been idle.  A null value will cause the keep alive feature to
     *                  be disabled.
     * @param keepAliveAge the maximum age in milliseconds since a connection was last
     *                     used before it must be pinged using the keepAlive query.  Ignored
     *                     if keepAlive is null.
     */
    public AbstractJdbcConnection( final Connection connection,
                                   final String keepAlive,
                                   final int keepAliveAge )
    {
        m_connection = connection;

        // subclasses can override initialize()
        this.initialize();

        m_testAge = keepAliveAge;
        if( null == keepAlive || "".equals( keepAlive.trim() ) )
        {
            m_testStatement = null;
            m_testException = null;
        }
        else
        {
            try
            {
                // test statement is allocated directly from the
                // underlying connection, it is special and should not
                // be closed during recycling session
                m_testStatement = m_connection.prepareStatement( keepAlive );
            }
            catch( final SQLException se )
            {
                m_testStatement = null;
                m_testException = se;
            }
        }
    }

    public void initialize()
    {
    }

    public void enableLogging( final Logger log )
    {
        super.enableLogging( log );

        if( m_testStatement == null && m_testException != null )
        {
            if( getLogger().isWarnEnabled() )
            {
                getLogger().warn( "Could not prepare test statement, connection recycled on basis of time.", m_testException );
            }
            m_testException = null;
        }
    }

    public void setPool( Pool pool )
    {
        m_pool = pool;
    }

    public void recycle()
    {
        m_testException = null;
        try
        {
            clearAllocatedStatements();
            m_connection.clearWarnings();
        }
        catch( SQLException se )
        {
            // ignore
        }
    }

    public void setProxiedConnection( Object proxy )
    {
        m_proxy = proxy;
    }

    public Connection getConnection()
    {
        return m_connection;
    }

    /**
     * @see org.apache.avalon.excalibur.datasource.PoolSettable#setAutoCommit
     * @todo this method only stores the auto commit flag but does not modify the connection.  Should it?
     */
    public void setAutoCommit(boolean autoCommit){
        m_autoCommit = autoCommit;
    }

    public boolean isClosed()
            throws SQLException
    {
        if( m_connection.isClosed() )
        {
            return true;
        }

        if ( m_testStatement == null )
        {
            // No test statement was configured so it is not possible to ping the DB to
            //  revalidate the connection.
            if ( m_encounteredError )
            {
                // There is no way to guarantee that this connection is still good.
                //  Cause the connection to be invalidated.
                return true;
            }
        }
        else
        {
            // A ping statement was configured.
            long age = System.currentTimeMillis() - m_lastUsed;
            boolean ping = false;
            if ( age > m_testAge )
            {
                if( getLogger().isDebugEnabled() )
                {
                    getLogger().debug( "Pinging database after " + age + "ms of inactivity." );
                }
                ping = true;
            }
            else if ( m_encounteredError )
            {
                // If an error was encountered since the last time the DB has been pinged we want
                //  to force a ping.
                // If a client is attempting to query a DB once every couple seconds then the
                //  connection will never be unused for long enough to cause a ping on its own.
                //  This means that if any reocurring error, like the socket being closed, occurrs
                //  the pool would normally never invalidate the connection.  Ideally the underlying
                //  Connection's isClosed() method should return false in such a case, but it does
                //  not always do so. (Oracle for example)
                //
                // The encounteredError flag is only set if an error is thrown from a Connection
                //  method.  It will not be set if an exception is thrown by a Statement or other
                //  object obtained from the Connection.  We are mainly worried about low level
                //  socket problems here however so this should not be a problem.  A failed query
                //  caused by bad SQL does not mean the connection might fail for future calls.
                getLogger().debug( "Pinging database after a previously thrown error." );
                ping = true;
            }
            
            // Always reset the flag here in case the connection was old and there was an error.
            m_encounteredError = false;
            
            if( ping )
            {
                try
                {
                    ResultSet rs = m_testStatement.executeQuery();
                    rs.close();
                }
                catch( final SQLException se )
                {
                    getLogger().debug( "Ping of connection failed.", se );
                    this.dispose();
                    return true;
                }
            }
        }

        return false;
    }

    public void close()
            throws SQLException
    {
        // IMPORTANT - never simply call dispose within this method.  The
        //  pool will have no way of knowing that the connection was disposed
        //  and blocking pools will eventually run out of resources thinking
        //  that all of the connections are in use.
        try
        {
            // Always mark the time the connection was placed back in the pool
            //  as its last used time.
            m_lastUsed = System.currentTimeMillis();

            try
            {
                clearAllocatedStatements();
                m_connection.clearWarnings();
            }
            catch( SQLException se )
            {
                // This can be ignored here.
            }
        }
        finally
        {
            // Always put the connection back into the pool
            m_pool.put( (Poolable)m_proxy );
        }
    }

    /**
     * Closes statements that were registered and removes all
     * statements from the list of allocated ones.  If any statement
     * fails to properly close, the rest of the statements is ignored.
     * But the registration list if cleared in any case.
     * <p>
     * Holds m_allocatedStatements locked the whole time. This should
     * not be a problem because connections are inherently single
     * threaded objects and any attempt to use them from a different
     * thread while it is being closed is a violation of the contract.
     *
     * @throws SQLException of the first Statement.close()
     */
    protected void clearAllocatedStatements() throws SQLException
    {
        synchronized( m_allocatedStatements )
        {
            try
            {
                final Iterator iterator = m_allocatedStatements.iterator();
                while( iterator.hasNext() )
                {
                    Statement stmt = (Statement)iterator.next();
                    stmt.close();
                }
            }
            finally
            {
                m_allocatedStatements.clear();
            }
        }
    }

    /**
     * Adds the statement to the list of this connection.  Used by
     * subclasses to ensure release of statements when connection is
     * logically terminated and returned to the pool.
     */
    protected void registerAllocatedStatement( Statement stmt )
    {
        synchronized( m_allocatedStatements )
        {
            m_allocatedStatements.add( stmt );
        }
    }

    public void dispose()
    {
        try
        {
            m_connection.close();
        }
        catch( final SQLException se )
        {
            if( getLogger().isDebugEnabled() )
            {
                getLogger().debug( "Could not close connection", se );
            }
        }
    }

    public boolean equals( Object obj )
    {
        if( Proxy.isProxyClass( obj.getClass() ) )
        {
            final InvocationHandler handler = Proxy.getInvocationHandler(obj );

            if( handler instanceof ProxiedJdbcConnection )
            {
                return m_connection.equals( ( (ProxiedJdbcConnection)handler ).getConnection() );
            }
        }

        return false;
    }

    public Object invoke( Object proxy, Method method, Object[] args )
            throws Throwable
    {
        // NOTE - getLogger() will return null in here before the enableLogging method
        //  has been called.
        
        Object retVal = null;
        Method executeMethod = (Method)m_methods.get( method.getName() );

        try
        {
            if( null == executeMethod )
            {
                retVal = method.invoke( m_connection, args );
            }
            else
            {
                retVal = executeMethod.invoke( this, args );
            }
        }
        catch( InvocationTargetException e )
        {
            // Remember that an error of some sort was thrown so we can make sure to retest
            //  the connection before it is once again used by a client.
            m_encounteredError = true;
            
            throw e.getTargetException();
        }

        return retVal;
    }
}
