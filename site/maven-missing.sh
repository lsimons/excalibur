#!/usr/bin/env bash

maventarget="site"

mavenmissing=`maven $maventarget 2>&1 | grep "Error retrieving artifact from" | sed 's/Error retrieving artifact from \[//' | sed 's/\]: java.io.IOException: Unknown error downloading; status code was: 301//'`
localrepo="$HOME/.maven/repository"

missedsome="no"
for i in $mavenmissing; do
  missedsome="yes"
  repopath=`echo $i | sed 's/http:\/\/www.ibiblio.org\/maven\///'`
  #filename=`echo $i | sed 's/.*\/jars\///'`
  echo wget -q --output-document=$localrepo/$repopath $i
  wget -q --output-document=$localrepo/$repopath $i
done

if [[ "$missedsome" == "no" ]]; then
  echo "yay! Seems like we are done downloading"
else
  echo "Please run me again, maven is probably not done downloading yet"
fi