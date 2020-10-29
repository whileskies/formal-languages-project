package cfg;

import java.util.*;

public class Right {
    private List<Symbol> symbols;

    public Right(List<Symbol> symbols) {
        this.symbols = symbols;
    }

    public List<Symbol> getSymbols() {
        return symbols;
    }

    public void setSymbols(List<Symbol> symbols) {
        this.symbols = symbols;
    }

    public int symbolsNum() {
        return symbols.size();
    }

    public Set<Symbol> getTerminalSet() {
        Set<Symbol> set = new HashSet<>();
        for (Symbol symbol : symbols) {
            if (symbol.isTerminalSymbol()) {
                set.add(symbol);
            }
        }

        return set;
    }

    public Set<Symbol> getNonTerminalSet() {
        Set<Symbol> set = new HashSet<>();
        for (Symbol symbol : symbols) {
            if (symbol.isNonTerminalSymbol()) {
                set.add(symbol);
            }
        }

        return set;
    }

    public boolean containSymbol(Symbol symbol) {
        for (Symbol s : symbols) {
            if (s.equals(symbol))
                return true;
        }

        return false;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Right)) return false;
        Right right = (Right) o;
        if (this.symbols.size() != right.symbols.size())
            return false;
        for (int i = 0; i < this.symbols.size(); i++) {
            if (!this.symbols.get(i).getSymbol().equals(right.symbols.get(i).getSymbol()) ||
                    this.symbols.get(i).getType() != right.symbols.get(i).getType()) {
                return false;
            }
        }

        return true;
    }

    @Override
    public int hashCode() {
        return Objects.hash(symbols);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Symbol symbol : symbols) {
            sb.append(symbol.getSymbol());
            //sb.append(' ');
        }

        return sb.toString();
    }
}
