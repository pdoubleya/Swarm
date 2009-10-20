package swarm;

import net.jini.id.UuidFactory;

import java.util.Date;

/**
 * Represents an operation we want to execute on a node. The operation is a serialized delimited continuation.
 */
public class TaskOperationEntry extends TaskEntry {
    private static final long serialVersionUID = -3356656986048911108L;
    public Object operation;

    public TaskOperationEntry() { }

    public static TaskOperationEntry newOperation(NodeIdentifier homeNode, NodeIdentifier targetNode, Object operation) {
        TaskOperationEntry toe = new TaskOperationEntry();
        toe.taskUuid = UuidFactory.generate();
        Date now = new Date();
        toe.when = now.getTime();
        toe.logMsg = String.format("Task added at %s", now);
        toe.operation = operation;
        toe.homeNode = homeNode;
        toe.targetNode = targetNode;
        return toe;
    }
}
