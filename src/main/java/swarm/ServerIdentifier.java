package swarm;

import net.jini.core.entry.Entry;
import net.jini.id.Uuid;

public class ServerIdentifier implements Entry {
    public String serverName;
    public Uuid uuid;

    public ServerIdentifier(String _sid, Uuid _uuid) {
        serverName = _sid;
        uuid = _uuid;
    }
    public ServerIdentifier() {}

    @Override
    public String toString() {
        return "server(" + serverName + ", " + uuid + ")";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ServerIdentifier that = (ServerIdentifier) o;

        if (serverName != null ? !serverName.equals(that.serverName) : that.serverName != null) return false;
        if (uuid != null ? !uuid.equals(that.uuid) : that.uuid != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = serverName != null ? serverName.hashCode() : 0;
        result = 31 * result + (uuid != null ? uuid.hashCode() : 0);
        return result;
    }
}
