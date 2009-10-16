package swarm.demos;

import swarm._
import java.lang.String;
import swarm.Swarm._;

object Listen {
  val serverName: String = "Listener"
  def main(args : Array[String]) = {
    Swarm.listen(serverName, java.lang.Short.parseShort(args(0)))
		while(true) {
			Thread.sleep(1000);
		}
	}
}