Excalibur Fortress Platform


At the moment this project is rather raw.  To build, you'll need a full checkout
of the Excalibur svn repository.  Then run: maven:build-dist.  If you get warnings
about GPG, don't worry about it.

You'll find two distributables in target/distributions.  You should be able
to unzip these anywhere and run "bin/fortress.bat console" (or bin/fortress.sh) to startup
the simple Hello World Translator.

Yeah, there's lots of work todo.  Here's a couple ideas:

  1. Use a different example.  Better yet, clean up the ../examples project.
  2. Provide some useful documentation for end users who would actually
     download this thing
  3. Identify "optional" libraries and put them in /lib/optional/
  4. Move this list into some sort of xdocs/tasks or better yet JIRA
  5. Right now we build off of maven:dist.  Maybe use some other target?
     maven:dist creates a jar that we don't need.  Or maybe merge this
     subproject with the examples subproject?
     
So..much..to..do..

