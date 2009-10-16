#!/bin/bash
stop() {
  svc=$1
  pidfile=/tmp/$svc.pid
  if [ ! -e $pidfile ];
  then
    echo "missing pid file for $svc, should be located at $pidfile. can't shut $svc down"
  else
    pid=$(cat /tmp/$svc.pid)
    ps $pid &> /dev/null
    if [ $? -ne 0 ];
    then
      echo "service $svc is supposed to have pid $pid, but not in process list; can't shut it down"
    else
      kill $pid
      if [ $? -ne 0 ];
      then
        echo "kill failed for $svc at pid $pid; can't shut it down."
      else
	rm $pidfile
        echo "stopped $svc, pid $pid"
      fi
    fi
  fi
}
stop mahalo
stop outrigger
stop reggie
