package cfg;

import java.util.List;

public class TransitionFunction {
    private final Symbol read;
    private final Symbol stackTop;
    private final List<Symbol> pushed;

    public TransitionFunction(Symbol read, Symbol stackTop, List<Symbol> pushed) {
        this.read = read;
        this.stackTop = stackTop;
        this.pushed = pushed;
    }

    public Symbol getRead() {
        return read;
    }

    public Symbol getStackTop() {
        return stackTop;
    }

    public List<Symbol> getPushed() {
        return pushed;
    }

    @Override
    public String toString() {
        return "TransitionFunction{" +
                "read=" + read +
                ", stackTop=" + stackTop +
                ", pushed=" + pushed +
                '}';
    }
}
