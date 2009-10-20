package swarm;

import net.jini.entry.AbstractEntry;
import net.jini.id.Uuid;

/**
 * Superclass of all tasks we push into the space.
 */
public class TaskEntry extends AbstractEntry {
    private static final long serialVersionUID = 911857499454583662L;
    public NodeIdentifier homeNode;
    public NodeIdentifier targetNode;
    public Uuid taskUuid;
    public String logMsg;
    public Long when;

    public TaskEntry() { }

    public static TaskEntry templateForTargetNode(NodeIdentifier target) {
        TaskEntry toe = new TaskEntry();
        toe.targetNode = new NodeIdentifier(target.uuid);
        return toe;
    }

}
