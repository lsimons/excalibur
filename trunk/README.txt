Apache Excalibur
================

Welcome to Apache Excalibur! For more information, please see the website at

  http://excalibur.apache.org/

To build everything from scratch, you will need Maven (http://maven.apache.org)
and simply run the command;

  maven multiproject:install

You will need to add the following to your list of remote repositories in
your Maven build.properties in your home directory:

  maven.repo.remote=http://ibiblio.org/maven,http://www.apache.org/dist/java-repository,http://svn.apache.org/repos/asf/excalibur/repository

You might need to download and install a few jars by hand, in particular, the following:

  jsse-1.0.3_03 (groupid: jsse)

Logkit and Excalibur-Logger use the geronimo-spec JMS and JavaMail jars for
building and testing.  For production use, developers should download the JMS
and JavaMail dependencies from Sun.
