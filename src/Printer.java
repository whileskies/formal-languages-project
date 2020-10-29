import java.util.Arrays;
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
}
