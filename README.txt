Apache Excalibur
================

Welcome to Apache Excalibur! For more information, please see the website at

  http://excalibur.apache.org/

To build everything from scratch, you will need Maven (http://maven.apache.org)
and simply run the command;

  maven multiproject:install

you might need to download and install a few jars by hand, in particular, the following libraries:

  jsse-1.0.3_02 (groupid: jsse)
  mailapi-1.3.1 (groupid: javamail)
  jms-1.1       (groupid: jms)

Besides the maven-based buildsystem, Excalibur can also be built using a home-grown ant-1.6-based system that lives in the 'buildsystem' directory. See buildsystem/jbs/README.txt
for more information.
