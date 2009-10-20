package swarm

@serializable class ServerLocation(val serverId : NodeIdentifier) {
	override def equals(other : Any) = {
		if (!other.isInstanceOf[Any]) false;
		val o = other.asInstanceOf[NodeIdentifier];
		this == o;
	}

  override def toString = serverId.toString
}