import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CFG {
    private final Set<Symbol> variable;

    private final Set<Symbol> terminal;

    private final HashMap<Symbol, Production> productions;

    private final Symbol start;


    public CFG(Symbol start, List<Production> productions) {
        variable = new HashSet<>();
        terminal = new HashSet<>();

        this.start = start;
        variable.add(this.start);

        this.productions = new HashMap<>();
        for (Production production : productions) {
            if (this.productions.containsKey(production.getLeft())) {
                this.productions.get(production.getLeft()).getRights()
                        .addAll(production.getRights());

            } else {
                this.productions.put(production.getLeft(), production);
            }

            variable.add(production.getLeft());
            for (Right right : production.getRights()) {
                variable.addAll(right.getNonTerminalSet());
                terminal.addAll(right.getTerminalSet());
            }
        }
    }

    public Set<Symbol> getVariable() {
        return variable;
    }

    public Set<Symbol> getTerminal() {
        return terminal;
    }

    public HashMap<Symbol, Production> getProductions() {
        return productions;
    }

    public Symbol getStart() {
        return start;
    }

    public void removeSymbolFromVariableSet(Symbol symbol) {
        variable.remove(symbol);
    }

    public void removeSymbolFromTerminal(Symbol symbol) {
        terminal.remove(symbol);
    }


    @Override
    public String toString() {
        return "CFG{" +
                "variable=" + variable +
                ", terminal=" + terminal +
                ", productions=" + productions +
                ", start=" + start +
                '}';
    }
}
