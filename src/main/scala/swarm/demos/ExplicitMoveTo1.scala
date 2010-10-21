package swarm.demos;

import swarm._;
import swarm.Swarm._;
import scala.Console._;
import Integer._;
import java.net.InetAddress

object ExplicitMoveTo1 {
  
  var origAddress: InetAddress = null
  var destAddress: InetAddress = null
  //lazy val origAddress: InetAddress
  //lazy val destAddress: InetAddress
  var listenPort: Short = 9998
  
  def main(args : Array[String]) = {		
    listenPort = java.lang.Short.parseShort(args(0))
    // FIXME Swarm.listen(listenPort)
    Swarm.listen("Sender")

    val (orig,dest) = 
      if (args.length > 1) 
        (InetAddress.getAllByName(args(1)).head, InetAddress.getAllByName(args(2)).head)
      else 
        (myLocation.address, myLocation.address)
    
    origAddress = orig
    destAddress = dest

    Swarm.spawn(emt1Thread);
    /*
    if (args.length > 1 && args(1) == "start") {
			Swarm.spawn(emt1Thread);
		        }*/

    while(true) {
      Thread.sleep(1000);
    }
  }
	
  def emt1Thread(u : Unit) = {
    val name = scala.Console.readLine("What is your name? : ");
    moveTo(lookup("Listener").serverId.uuid) // remote
    val age = parseInt(readLine("Hello "+name+", what age are you? : "))
    moveTo(lookup("Sender").serverId.uuid) // local
    println("Wow "+name+", you're half way to "+(age*2)+" years old")
    NoBee()


    /*val name = scala.Console.readLine("What is your name? : ");
    moveTo(new Location(destAddress, 9997))
    val age = parseInt(readLine("Hello "+name+", what age are you? : "))
    println("origAddress:" + origAddress)
    moveTo(new Location(origAddress, listenPort))
    println("Wow "+name+", you're half way to "+(age*2)+" years old")
    NoBee()*/
  }
}
