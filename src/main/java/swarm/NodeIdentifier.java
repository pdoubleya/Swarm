package swarm;

import net.jini.core.entry.Entry;
import net.jini.entry.AbstractEntry;
import net.jini.id.Uuid;
import net.jini.id.UuidFactory;

public class NodeIdentifier extends AbstractEntry {
    private static final long serialVersionUID = -2085402142691778039L;
    public String serverName;
    public Uuid uuid;

    public NodeIdentifier() {}
    public NodeIdentifier(Uuid uuid) {
        this.uuid = uuid;
    }

    public static NodeIdentifier newIdentifier(String _name) {
        NodeIdentifier ni = new NodeIdentifier();
        ni.serverName = _name;
        ni.uuid = UuidFactory.generate();
        return ni;
    }

    @Override
    public String toString() {
        return "server(" + serverName + ", " + uuid + ")";
    }
}
