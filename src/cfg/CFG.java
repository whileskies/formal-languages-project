package cfg;

import java.util.*;

public class CFG {

    private HashMap<Symbol, Production> productions;

    private Symbol start;


    public CFG(Symbol start, List<Production> productions) {
        this.start = start;

        this.productions = new HashMap<>();
        for (Production production : productions) {
            if (this.productions.containsKey(production.getLeft())) {
                this.productions.get(production.getLeft()).getRights()
                        .addAll(production.getRights());

            } else {
                this.productions.put(production.getLeft(), production);
            }
        }

    }

    public HashMap<Symbol, Production> getProductions() {
        return productions;
    }

    public Symbol getStart() {
        return start;
    }

    public void setProductions(HashMap<Symbol, Production> productions) {
        this.productions = productions;
    }

    public void setStart(Symbol start) {
        this.start = start;
    }

    public Set<Symbol> getTerminalSymbols() {
        Set<Symbol> symbols = new HashSet<>();

        for (Map.Entry<Symbol, Production> entry : productions.entrySet()) {
            Production production = entry.getValue();
            symbols.addAll(production.getTerminalSymbols());
        }

        return symbols;
    }

    public Set<Symbol> getNonTerminalSymbols() {
        Set<Symbol> symbols = new HashSet<>();

        for (Map.Entry<Symbol, Production> entry : productions.entrySet()) {
            Production production = entry.getValue();
            symbols.addAll(production.getNonTerminalSymbols());
        }

        return symbols;
    }

    @Override
    public String toString() {
        return "cfg.CFG{" +
                ", productions=" + productions +
                ", start=" + start +
                '}';
    }
}
