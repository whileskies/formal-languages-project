package tm;

public class StateTransition {
    private final int curState;
    private final char read;

    private final int nextState;
    private final char write;
    private final boolean toRight;

    public StateTransition(int curState, char read, int nextState, char write, boolean toRight) {
        this.curState = curState;
        this.read = read;
        this.nextState = nextState;
        this.write = write;
        this.toRight = toRight;
    }

    public int getCurState() {
        return curState;
    }

    public char getRead() {
        return read;
    }

    public int getNextState() {
        return nextState;
    }

    public char getWrite() {
        return write;
    }

    public boolean isToRight() {
        return toRight;
    }
}
