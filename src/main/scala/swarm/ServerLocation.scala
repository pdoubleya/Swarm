package swarm

@serializable class ServerLocation(val serverId : ServerIdentifier) {
	override def equals(other : Any) = {
		if (!other.isInstanceOf[Any]) false;
		val o = other.asInstanceOf[ServerIdentifier];
		this == o;
	}

  override def toString = serverId.toString
}