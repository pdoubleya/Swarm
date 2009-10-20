package swarm;

/**
 * This is never pushed directly, but rather used as a template for matching space entries.
 */
public class TemplateTaskEntry extends TaskEntry {
    private static final long serialVersionUID = 8365390053569294462L;

    public TemplateTaskEntry() { }
    public static TemplateTaskEntry newForTargetNode(NodeIdentifier nodeId) {
        TemplateTaskEntry tte = new TemplateTaskEntry();
        tte.targetNode = nodeId;
        return tte;
    }
}
