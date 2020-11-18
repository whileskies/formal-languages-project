package cfg;

import java.util.Objects;

public class Symbol {
    public static final int EPSILON_SYMBOL = 0;
    public static final int TERMINAL_SYMBOL = 1;
    public static final int NON_TERMINAL_SYMBOL = 2;

    private String symbol;
    private int type;

    public Symbol() {
    }

    public Symbol(String symbol, int type) {
        this.symbol = symbol;
        this.type = type;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public boolean isNullSymbol() {
        return type == EPSILON_SYMBOL;
    }

    public boolean isTerminalSymbol() {
        return type == TERMINAL_SYMBOL;
    }

    public boolean isEpsilonSymbol() {
        return type == EPSILON_SYMBOL;
    }

    public boolean isNonTerminalSymbol() {
        return type == NON_TERMINAL_SYMBOL;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Symbol)) return false;
        Symbol symbol1 = (Symbol) o;
        return type == symbol1.type &&
                symbol.equals(symbol1.symbol);
    }

    @Override
    public int hashCode() {
        return Objects.hash(symbol, type);
    }

    @Override
    public String toString() {
        return "cfg.Symbol{" +
                "symbol='" + symbol + '\'' +
                ", type=" + type +
                '}';
    }
}
