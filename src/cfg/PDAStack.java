package cfg;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;

public class PDAStack {
    private final Deque<Symbol> deque;
    private int opNum;

    public PDAStack() {
        deque = new ArrayDeque<>();
        opNum = 0;
    }

    public PDAStack(PDAStack pdaStack) {
        deque = new ArrayDeque<>(pdaStack.deque);
        opNum = pdaStack.opNum;
    }

    public Deque<Symbol> getDeque() {
        return deque;
    }

    public int getOpNum() {
        return opNum;
    }

    public boolean isEmpty() {
        return deque.isEmpty();
    }

    public void popAndPush(List<Symbol> pushed) {
        if (deque.size() >= 1) {
            deque.removeLast();
            for (int i = pushed.size() - 1; i >= 0; i--) {
                if (!pushed.get(i).isEpsilonSymbol()) {
                    deque.addLast(pushed.get(i));
                }
            }
            opNum++;
        }
    }

    public void pushStart(Symbol start) {
        if (isEmpty()) {
            deque.addLast(start);
            opNum++;
        }
    }

    public Symbol getTop() {
        if (isEmpty())
            return null;
        return deque.peekLast();
    }

    @Override
    public String toString() {
        return "PDAStack{" +
                "deque=" + deque +
                ", opNum=" + opNum +
                '}';
    }
}
