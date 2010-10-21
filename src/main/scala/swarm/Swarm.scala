package swarm

import scala.util.continuations._
import net.jini.id.Uuid
import net.jini.core.lease.Lease
import org.slf4j. {Logger, LoggerFactory}

//import scala.util.continuations.ControlContext._ 
//import scala.util.continuations.Loops._

import net.jini.core.event.{RemoteEvent, RemoteEventListener}
import net.jini.core.transaction.Transaction
import net.jini.space.JavaSpace05


object Swarm {
  val logger: Logger = LoggerFactory.getLogger("swarm.Swarm")

	type swarm = cpsParam[Bee, Bee];
	
	var myLocation : Location = null;
	
	var shouldLog = true;
	
	def isLocal(loc : Location) = {
		loc.equals(myLocation);
	}

  var localIdentifier: NodeIdentifier = null

  def isLocal(uuid: Uuid) = {uuid.equals(localIdentifier.uuid)}

  def inform(message: String) = {logger.info("{}: {}", localIdentifier.uuid, message)}

  def warn(message: String) = {logger.warn("{}: {}", localIdentifier.uuid, message)}

  def listen(serverName: String) = {
    val space = JiniUtil.getSpaceSDM()
    localIdentifier = JiniUtil.registerNode(serverName)

    val operationListener = new Thread() {
      override def run() = {
        while (true) {
          try {
            val waitMs: Int = 5 * 1000
            val trn: Transaction = null
            val tmpl = TaskEntry.templateForTargetNode(localIdentifier)
            inform(format("Waiting for entries in Space using template: %s", tmpl));
            val taskEntry = space.take(tmpl, trn, waitMs).asInstanceOf[TaskEntry]
            if (taskEntry == null) {
              inform("nothing found; sleeping")
              Thread.sleep(1000)
            } else {
              inform(format("READ: Got task %s from Space; executing continuation", taskEntry));
              taskEntry match {
                case toe: TaskOperationEntry =>
                  inform(toe.toString)
                  if (toe.operation == null) {
                    warn("hmm, task is null, serialization problem? : " + toe)
                  } else {
                    var task = toe.operation.asInstanceOf[(Unit => Bee)]
                    try {
                      Swarm.run((Unit) => shiftUnit(task()))
                    }
                    catch {
                      case ex: Exception => handleFailedTask(space, toe, ex)
                    }
                  }

                case f: TaskResultEntry[AnyRef] => inform(f.toString)
                case g: TaskErrorEntry => inform(g.toString)
              }

            }
          }
          catch {
            case e => warn("Exception in task processing: " + e.getMessage())
          }
        }
      }
    }


    operationListener.start();
    Thread.sleep(500);
  }

  def setupNotify(nodeId: NodeIdentifier): Unit = {
    JiniUtil.getSpace().notify(TemplateTaskEntry.newForTargetNode(nodeId), null, null/*newNotifyListener()*/, Lease.FOREVER, null)
  }

  def handleFailedTask(space: JavaSpace05, taskEntry: TaskOperationEntry, e: Exception) {
    var errorEntry: TaskErrorEntry = TaskErrorEntry.newForException(taskEntry, e)
    space.write(errorEntry, null, Lease.FOREVER)
    warn(format("Unexpected exception in processing task %s, %s", taskEntry, e.getMessage()))
    warn(format("Wrote task for exception %s", errorEntry))
  }

  def run(toRun: Unit => Bee@swarm) = {
    execute(reset {
      inform("Running task");
      toRun();
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
          inform("Running task");
          toRun();
          inform("Completed task");
          NoBee()
        })
      }
    };
    thread.start();
  }

  def lookup(serverId: String): ServerLocation = {
    val space = JiniUtil.getSpaceSDM()
    val tmpl = new NodeIdentifier()
    tmpl.serverName = serverId
    val sid = space.read(tmpl, null, 0).asInstanceOf[NodeIdentifier]
    if (sid == null) {
      throw new RuntimeException(format("Cannot locate server %s in a Space", serverId))
    }
    inform(format("found server %s at %s", serverId, sid))
    new ServerLocation(sid)
  }


  def moveTo(uuid: Uuid) = shift {
    c: (Unit => Bee) => {
      inform("Move to")
      if (Swarm.isLocal(uuid)) {
        inform("Is local")
        c()
      } else {
        inform("Moving task to " + uuid);
        IsBee(c, new NodeIdentifier(uuid))
      }
    }
  }


  def execute(bee: Bee) = {
    bee match {
      case IsBee(contFunc, nodeId) => {
        val space = JiniUtil.getSpaceSDM()
        val toe = TaskOperationEntry.newOperation(localIdentifier, nodeId, contFunc)
        inform(format("writing entry %s into space", toe))
        try {
          val lb = space.write(toe, null, Lease.FOREVER)
          inform(format("wrote entries %s into space", toe))
        }
        catch {
          case e => e.printStackTrace()
        }
      }
      case NoBee() => {
        inform("No more continuations to execute");
      }
    }
  }
}