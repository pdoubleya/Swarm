package swarm.demos;

import swarm._
import java.lang.String;


object Listen {
  val serverName: String = "Listener"
  def main(args : Array[String]) = {
    Swarm.listen(serverName)
		while(true) {
			Thread.sleep(1000);
		}
	}
}