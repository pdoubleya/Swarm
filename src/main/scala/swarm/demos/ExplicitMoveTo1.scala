package swarm.demos;

import swarm._;
import swarm.Swarm._;
import scala.Console._;
import Integer._;

object ExplicitMoveTo1 {
	def main(args : Array[String]) = {		
		Swarm.listen("Sender")

		if (args.length > 0 && args(0) == "start") {
			Swarm.spawn(emt1Thread);
		}

		while(true) {
			Thread.sleep(1000);
		}
	}
	
	def emt1Thread(u : Unit) = {
		val name = scala.Console.readLine("What is your name? : ");
		moveTo(lookup("Listener")) // remote
		val age = parseInt(readLine("Hello "+name+", what age are you? : "))
		moveTo(lookup("Sender")) // local
		println("Wow "+name+", you're half way to "+(age*2)+" years old")
		NoBee()
	}
}