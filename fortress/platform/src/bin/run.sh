
JAVA_HOME=/usr

CLASSPATH=.

for jar in `ls lib/*.jar`
do
  CLASSPATH=$CLASSPATH:$jar
done

$JAVA_HOME/bin/java -cp $CLASSPATH org.apache.avalon.fortress.examples.swing.Main conf/fortress.xconf conf/fortress.xlog

