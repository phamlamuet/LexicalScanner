import java.util.Objects;

public class State {
    int stateID = 0;
    boolean retract = false;
    boolean accepted = false;

    public State(int stateID, boolean retract, boolean accepted) {
        this.stateID = stateID;
        this.retract = retract;
        this.accepted = accepted;
    }

    public State() {
    }

    public int getStateID() {
        return stateID;
    }

    public void setStateID(int stateID) {
        this.stateID = stateID;
    }

    public boolean isRetract() {
        return retract;
    }

    public void setRetract(boolean retract) {
        this.retract = retract;
    }

    public boolean isAccepted() {
        return accepted;
    }

    public void setAccepted(boolean accepted) {
        this.accepted = accepted;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        State state = (State) o;
        return stateID == state.stateID && retract == state.retract && accepted == state.accepted;
    }

    @Override
    public int hashCode() {
        return Objects.hash(stateID, retract, accepted);
    }

    @Override
    public String toString() {
        return stateID + " " + accepted + " " + retract+ "\n";
    }
}
