package swarm;

import java.io.Serializable;

/**
 * The end result of executing a task.
 */
public class TaskResultEntry<T extends Serializable> extends TaskEntry {
    private static final long serialVersionUID = -7409299410100898280L;
    public T result;

    public TaskResultEntry() { }

    public static <T extends Serializable> TaskResultEntry newForResult(TaskOperationEntry _op, T _result) {
        TaskResultEntry tre = new TaskResultEntry();
        tre.result = _result;
        long now = System.currentTimeMillis();
        tre.when = now;
        tre.taskUuid = _op.taskUuid;
        tre.logMsg = String.format("Task completed without error in %s", _op.when - now);
        return tre;
    }
}
