package cfg;

import java.util.List;

public class Printer {
    public static String rightToString(Right right) {
        StringBuilder sb = new StringBuilder();

        List<Symbol> symbols = right.getSymbols();
        for (Symbol symbol : symbols) {
            sb.append(symbol.getSymbol());
        }

        return sb.toString();
    }

    public static String productionToString(Production production) {
        StringBuilder sb = new StringBuilder();

        sb.append(production.getLeft().getSymbol());
        sb.append("->");

        boolean need = false;
        for (Right right : production.getRights()) {
            if (!need) {
                need = true;
            } else {
                sb.append("|");
            }
            sb.append(rightToString(right));
        }

        return sb.toString();
    }

    public static String cfgToString(CFG cfg) {
        StringBuilder sb = new StringBuilder();

        boolean need = false;
        for (Production production : cfg.getProductions().values()) {
            if (!need) {
                need = true;
            } else {
                sb.append("\n");
            }
            sb.append(productionToString(production));
        }

        return sb.toString();
    }

    public static String transitionFunctionToString(TransitionFunction transitionFunction) {
        StringBuilder sb = new StringBuilder();

        sb.append("δ(q, ");
        sb.append(transitionFunction.getRead().getSymbol());
        sb.append(" ,");
        sb.append(transitionFunction.getStackTop().getSymbol());
        sb.append(") = ");
        sb.append("(q, ");
        for (Symbol symbol : transitionFunction.getPushed()) {
            sb.append(symbol.getSymbol());
        }
        sb.append(")");

        return sb.toString();
    }

    public static String cfgPDAToString(CfgPDA cfgPDA) {
        StringBuilder sb = new StringBuilder();

        boolean need = false;
        List<TransitionFunction> transitionFunctions = cfgPDA.getTransitionFunctions();
        for (TransitionFunction function : transitionFunctions) {
            if (!need) {
                need = true;
            } else {
                sb.append("\n");
            }
            sb.append(transitionFunctionToString(function));
        }

        return sb.toString();
    }

    public static String cfgStringToString(CfgString cfgString) {
        StringBuilder sb = new StringBuilder();
        for (Symbol symbol : cfgString.getSymbols()) {
            sb.append(symbol.getSymbol());
        }

        return sb.toString();
    }
}
