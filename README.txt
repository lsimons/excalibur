Apache Excalibur
================

Welcome to Apache Excalibur! For more information, please see the website at

  http://excalibur.apache.org/

To build everything from scratch, you will need Maven (http://maven.apache.org)
and simply run the command;

  maven multiproject:install

you might need to download and install a few jars by hand, in particular, the following:

  jsse-1.0.3_03 (groupid: jsse)
  
Logkit and Excalibur-Logger use the geronimo-spec JMS and JavaMail jars for
building and testing.  For production use, developers should download the JMS
and JavaMail dependencies from Sun.
