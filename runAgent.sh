#!/bin/bash
#
# Usage
#   sh ./runServer.sh
#

TACAA_HOME=`pwd`
echo $TACAA_HOME
echo $CLASSPATH

java -cp "lib/*:out/artifacts/*" tau.tac.adx.agentware.Main -config config/aw-1.conf
