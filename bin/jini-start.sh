#!/bin/bash
script_path=$(cd $(dirname $0);pwd)
swarm_home=$(dirname $script_path)
jini_lib_path=$swarm_home/lib/jini/lib
jini_lib_dl_path=$swarm_home/lib/jini/lib-dl
jini_res_path=$swarm_home/src/main/resources/jini

launch() {
  service_name=$1
  log=/tmp/$service_name.log
  cmd="java -Dswarm_home=$swarm_home -Dservice_name=$service_name -Dpolicy=$jini_res_path/$service_name.policy -Djini_res_path=$jini_res_path -Djini_lib_path=$jini_lib_path -Djini_lib_dl_path=$jini_lib_dl_path -Djava.security.manager=java.rmi.RMISecurityManager -Djava.security.policy=$jini_res_path/all-permissions.policy -Djava.util.logging.manager=java.util.logging.LogManager -Djava.util.logging.config.file=$jini_res_path/logging.properties -jar $jini_lib_path/start.jar $jini_res_path/start-$service_name.config"
  #echo $cmd
  $cmd &> $log &
  pid=$!
  echo $pid>/tmp/$service_name.pid
  echo "started $service_name, pid $pid log at $log"
}

launch reggie
launch outrigger
launch mahalo
