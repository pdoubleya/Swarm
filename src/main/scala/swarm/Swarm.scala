package swarm

import scala.continuations._
import scala.continuations.ControlContext._
import scala.continuations.Loops._

import org.slf4j.Logger
import net.jini.id.{Uuid, UuidFactory}
import net.jini.core.lease.Lease
import net.jini.space.JavaSpace05;
import org.slf4j.LoggerFactory;

import java.net._
import java.io._

object Swarm {
  val logger: Logger = LoggerFactory.getLogger("swarm.Swarm")
  type swarm = cps[Bee, Bee];

  var myLocation: Location = null

  var localUuid = UuidFactory.generate()

  var localIdentifier : ServerIdentifier = null

  var shouldLog = true;

  def isLocal(loc: Location) = {
    loc.equals(myLocation);
  }

  def isLocal(uuid: Uuid) = {
    uuid.equals(localUuid);
  }

  def log(message: String) = {
    logger.info("{}: {}", localUuid, message)
  }

  def listen(serverId : String, port: Short) = {
    val space = JiniUtil.getSpaceSDM()
    localIdentifier = JiniUtil.register(serverId, localUuid)
    var listenThreadSpace = new Thread() {
      override def run() = {
        while (true) {
          try {
            val tmpl = new NewBee(format("swarm(%s)", localIdentifier))
            log(format("Waiting for entries in Space using template: %s", tmpl));
            val bee: NewBee = space.take(tmpl, null, 10 * 1000).asInstanceOf[NewBee]
            if (bee == null) {
              log("nothing found; sleeping")
              Thread.sleep(1000)
            } else {
              log(format("READ: Got bee %s from Space; executing continuation", bee));

              if (bee.task == null) {
                log("hmm, task is null: " + bee)
              } else {
                var task: (Unit => Bee) = bee.task.asInstanceOf[(Unit => Bee)]
                Swarm.run((Unit) => shiftUnit(task()))
              }
            }
          }
          catch {
            case e => e.printStackTrace()
          }
        }
      }
    }

    listenThreadSpace.start();
    Thread.sleep(500);
  }


  def run(toRun: Unit => Bee@swarm) = {
    execute(reset {
      log("Running task");
      toRun();
      //			log("Completed task");
      //			NoBee()
    })
  }

  /**
   * Start a new Swarm task (will return immediately as
   * task is started in a new thread)
   */
  def spawn(toRun: Unit => Bee@swarm) = {
    val thread = new Thread() {
      override def run() = {
        execute(reset {
          log("Running task");
          toRun();
          log("Completed task");
          NoBee()
        })
      }
    };
    thread.start();
  }

/*
  def moveTo(location: Location) = shift {
    c: (Unit => Bee) => {
      log("Move to")
      if (Swarm.isLocal(location)) {
        log("Is local")
        c()
        //				NoBee()
      } else {
        log("Moving task to " + location.port);
        IsBee(c, location)
      }
    }
  }
*/

  def lookup(serverId:String) : ServerLocation = {
    val space = JiniUtil.getSpaceSDM()
    val tmpl = new ServerIdentifier()
    tmpl.serverName = serverId
    val sid = space.read(tmpl, null, 0).asInstanceOf[ServerIdentifier]
    if (sid == null) {
      throw new RuntimeException(format("Cannot locate server %s in a Space", serverId))
    }
    log(format("found server %s at %s", serverId, sid))
    new ServerLocation(sid)  
  }


  def moveTo(uuid: Uuid) = shift {
    c: (Unit => Bee) => {
      log("Move to")
      if (Swarm.isLocal(uuid)) {
        log("Is local")
        c()
        //				NoBee()
      } else {
        log("Moving task to " + uuid);
        IsBee(c, null)
      }
    }
  }

  def moveTo(sloc : ServerLocation) = shift {
    val uuid = sloc.serverId.uuid
    c: (Unit => Bee) => {
      log("Move to")
      if (Swarm.isLocal(uuid)) {
        log("Is local")
        c()
        //				NoBee()
      } else {
        log("Moving task to " + sloc);
        IsBee(c, sloc.serverId)
      }
    }
  }

  def execute(bee: Bee) = {
    bee match {
/*
      case IsBee(contFunc, location) => {
        space = JiniUtil.getSpaceSDM()
        val bee = new NewBee(format("swarm(%s:%s)", location.address, location.port))
        log(format("writing entry %s into space", bee))
        bee.task = contFunc
        try {
          val lb = space.write(bee, null, Lease.FOREVER)
          log(format("wrote entries %s into space", bee))
        }
        catch {
          case e => e.printStackTrace()
        }

        /*
        val bee = new SpaceBee(format("swarm(%s:%s)", location.address, location.port), null)

        log("Transmitting task to " + location.port);
        val skt = new Socket(location.address, location.port);
        val oos = new ObjectOutputStream(skt.getOutputStream());
        oos.writeObject(contFunc);
        oos.close();
        log("Transmission complete");*/
      }
*/
      case IsBee(contFunc, serverId) => {
        val space = JiniUtil.getSpaceSDM()
        val bee = new NewBee(format("swarm(%s)", serverId))
        log(format("writing entry %s into space", bee))
        bee.task = contFunc
        try {
          val lb = space.write(bee, null, Lease.FOREVER)
          log(format("wrote entries %s into space", bee))
        }
        catch {
          case e => e.printStackTrace()
        }

        /*
        val bee = new SpaceBee(format("swarm(%s:%s)", location.address, location.port), null)

        log("Transmitting task to " + location.port);
        val skt = new Socket(location.address, location.port);
        val oos = new ObjectOutputStream(skt.getOutputStream());
        oos.writeObject(contFunc);
        oos.close();
        log("Transmission complete");*/
      }
      case NoBee() => {
        log("No more continuations to execute");
      }
    }
  }
}
