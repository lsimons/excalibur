package org.apache.avalon.fortress.tools;

import java.util.ArrayList;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.Log;

/**
 * The Maven-2 plugin to extract Fortress meta-data from Java sources.
 *
 * @author <a href="mailto:dev@excalibur.apache.org">The Excalibur Team</a>
 * @version CVS $Revision: 1.1 $ $Date: 2005/12/25 08:29:44 $
 *
 *
 * @author The Excalibur Team
 * @version $Id$
 * @goal collect-metainfo
 * @description Extracts Fortress Meta-information from annotations in .java files
 * @phase compile
 */
 public class ComponentMetaMavenMojo extends AbstractQDoxMojo
{

     /**
      * Wrap Maven plugin logger
      */
     private class MavenBuildLogger implements BuildLogger
    {
        private Log m_logger;

        private MavenBuildLogger()
        {
            //Disallow use, must use construcor below that supplies Maven logger
        }

        public MavenBuildLogger( Log logger )
        {
            m_logger = logger;
        }

        public void debug( String message )
        {
            m_logger.debug( message );
        }

        public void error( String message )
        {
            m_logger.error( message );
        }

        public void info( String message )
        {
            m_logger.info( message );
        }

        public void warn( String message )
        {
            m_logger.warn( message );
        }
    }

    public void execute() throws MojoExecutionException, MojoFailureException
    {

        validate();

        getLog().info( "Writing Info descriptors as property files (.meta)." );

        super.execute();

        ComponentMetaInfoCollector collector = new ComponentMetaInfoCollector( new MavenBuildLogger( getLog() ) );
        collector.setAllClasses( getAllClasses() );
        collector.setDestDir( getDestDir() );
        try
        {
            collector.execute();
        }
        catch ( Exception e )
        {
            throw new MojoExecutionException( e.getMessage(), e );
        }
    }

    private ArrayList getAllClasses()
    {
        return allClasses;
    }

    private void validate()
    {
    }

}
