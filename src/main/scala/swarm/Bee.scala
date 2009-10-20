package swarm

import net.jini.id.Uuid

@serializable abstract class Bee
@serializable case class NoBee() extends Bee
//@serializable case class IsBee(contFunc : (Unit => Bee), location : Location) extends Bee
@serializable case class IsBee(contFunc : (Unit => Bee), serverId : NodeIdentifier) extends Bee
