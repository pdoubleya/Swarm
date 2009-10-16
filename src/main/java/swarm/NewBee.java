package swarm;

import net.jini.lookup.entry.Name;

public class NewBee extends Name {
    public Object task;
    public NewBee() { }
    public NewBee(String name) { super(name); }

    public String toString() {
        return "NewBee(" + task + ") :> " + super.toString();
    }
}
