#!/bin/sh
JAVA_CMD=java
JAVA_ARGS="-Xms200M -Xmx500M"

PRG="lib/mauve"
while [ -h "$PRG" ] ; do
    ls=`ls -ld "$PRG"`
    link=`expr "$ls" : '.*-> \(.*\)$'`
    if expr "$link" : '/.*' > /dev/null; then
PRG="$link"
    else
PRG="`dirname "$PRG"`/$link"
    fi
done
echo "$PRG"
MAUVE0="$PRG"
MAUVE=`cd "$MAUVE0" && pwd`
cd "$saveddir"
$JAVA_CMD $JAVA_ARGS -DmauveDir=$MAUVE/ -cp $MAUVE/Mauve.jar org.gel.mauve.contigs.ContigOrderer -output $1 -ref $2 -draft $3 

