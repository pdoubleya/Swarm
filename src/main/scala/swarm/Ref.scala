package swarm

import swarm.Swarm._

import scala.continuations._
import net.jini.id.Uuid
import scala.continuations.ControlContext._ 
import scala.actors.remote._

/**
 Represents a reference to an object which may reside on a remote computer.
 If apply() is called to retrieve the remote object, it will result in
 the thread being serialized and moved to the remote computer, before
 returning the object.
 **/
//@serializable class Ref[Type](val typeClass : Class[Type], val location : Location, val uid : Long) {
@serializable class Ref[Type](val typeClass : Class[Type], val uuid : Uuid, val uid : Long) {
  def apply() = {
		Swarm.moveTo(uuid);
		Store(typeClass, uid) match {
			case Some(v) => v
			case None => throw new RuntimeException("Unable to find item with uid "+uid+" in local store");
		};
	}
}

object Ref {
	def apply[Type](value : AnyRef) : Ref[Type] @swarm = {
		apply(Swarm.localUuid, value);
	}
	
/*
	def apply[Type](location : Location, value : AnyRef) : Ref[Type] @swarm = {
		Swarm.moveTo(location);
		val uid = Store.save(value);
		new Ref[Type](value.getClass().asInstanceOf[Class[Type]], location, uid);
	}
*/

	def apply[Type](uuid : Uuid, value : AnyRef) : Ref[Type] @swarm = {
		Swarm.moveTo(uuid);
		val uid = Store.save(value);
		new Ref[Type](value.getClass().asInstanceOf[Class[Type]], uuid, uid);
	}

	def unapply[Type](ref : Ref[Type]) = {
		Some(ref())
	}
}