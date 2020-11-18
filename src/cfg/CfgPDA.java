package cfg;

import java.util.*;

public class CfgPDA {
    private final CFG cfg;

    public List<TransitionFunction> transitionFunctions;

    public CfgPDA(CFG cfg) {
        this.cfg = cfg;

        setTransitionFunctions();
    }

    public CFG getCfg() {
        return cfg;
    }

    public List<TransitionFunction> getTransitionFunctions() {
        return transitionFunctions;
    }

    public List<TransitionFunction> getTransitionFunByReadStackTop(Symbol read, Symbol stackTop) {
        List<TransitionFunction> ret = new ArrayList<>();

        for (TransitionFunction tran : transitionFunctions) {
            if (tran.getRead().equals(read) && tran.getStackTop().equals(stackTop)) {
                ret.add(tran);
            }
        }

        return ret;
    }

    private void setTransitionFunctions() {
        CfgNormalForm cfgNormalForm = new CfgNormalForm(cfg);
        cfgNormalForm.toGreibachNormalForm();

        transitionFunctions = new ArrayList<>();

        HashMap<Symbol, Production> productions = cfg.getProductions();
        for (Map.Entry<Symbol, Production> entry : productions.entrySet()) {
            Symbol stackTop = entry.getKey();
            Production production = entry.getValue();

            for (Right right : production.getRights()) {
                List<Symbol> symbols = right.getSymbols();
                Symbol read = symbols.get(0);
                List<Symbol> pushed;
                if (symbols.size() > 1) {
                    pushed = symbols.subList(1, symbols.size());
                } else {
                    pushed = new ArrayList<>();
                    pushed.add(Builder.buildEpsilon());
                }
                transitionFunctions.add(new TransitionFunction(read, stackTop, pushed));
            }
        }
    }

    public boolean acceptCfgString(CfgString cfgString) {
        List<PDAStack> pdaStacks = new ArrayList<>();
        PDAStack firstStack = new PDAStack();
        pdaStacks.add(firstStack);
        firstStack.pushStart(cfg.getStart());

        List<Symbol> symbols = cfgString.getSymbols();
        for (int i = 0; i < symbols.size(); i++) {
            Symbol read = symbols.get(i);
            for (ListIterator<PDAStack> iterator = pdaStacks.listIterator(); iterator.hasNext(); ) {
                PDAStack pdaStack = iterator.next();
                if (pdaStack.getOpNum() != i + 1)
                    continue;
                Symbol stackTop = pdaStack.getTop();
                if (stackTop != null) {
                    List<TransitionFunction> transitions = getTransitionFunByReadStackTop(read, stackTop);
                    for (TransitionFunction transition : transitions) {
                        PDAStack newStack = new PDAStack(pdaStack);
                        iterator.add(newStack);
                        newStack.popAndPush(transition.getPushed());
                    }
                }
            }

            for (Iterator<PDAStack> iterator = pdaStacks.iterator(); iterator.hasNext(); ) {
                PDAStack pdaStack = iterator.next();
                if (pdaStack.getOpNum() == i + 1) {
                    iterator.remove();
                }
            }
        }

        for (PDAStack pdaStack : pdaStacks) {
            if (pdaStack.getOpNum() == symbols.size() + 1 && pdaStack.isEmpty()) {
                return true;
            }
        }

        return false;
    }

    public static void main(String[] args) {
        CFG cfg = Builder.buildCFG("S", "S->aSb|a|b|#");
        CfgPDA cfgPDA = new CfgPDA(cfg);

        System.out.println(Printer.cfgToString(cfg));
        System.out.println(Printer.cfgPDAToString(cfgPDA));

        CfgString cfgString = Builder.buildCfgString("");
        System.out.println(cfgString);
        System.out.println(cfgPDA.acceptCfgString(cfgString));
    }
}
