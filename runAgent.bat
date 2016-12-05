#!/bin/bash
#
# Usage
#   sh ./runServer.sh
#

TACAA_HOME=%pwd%
echo %TACAA_HOME%
echo %CLASSPATH%

java -cp "lib/*;out/artifacts/An_Agent_Has_No_Name/*" tau.tac.adx.agentware.Main -config config/aw-1.conf
