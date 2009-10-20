package swarm;

/**
 *
 */
public class TaskErrorEntry extends TaskEntry {
    private static final long serialVersionUID = 6270970416558520762L;

    public TaskErrorEntry() { }

    public static TaskErrorEntry newForException(TaskOperationEntry _op, Exception _e) {
        TaskErrorEntry tee = new TaskErrorEntry();
        tee.targetNode = new NodeIdentifier(_op.homeNode.uuid);
        tee.taskUuid = _op.taskUuid;
        tee.when = System.currentTimeMillis();
        tee.logMsg = String.format("Exception thrown while processing task: %s", _e.getMessage());
        return tee;
    }
}
