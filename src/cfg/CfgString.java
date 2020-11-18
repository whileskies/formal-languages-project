package cfg;

import java.util.List;

public class CfgString {
    private List<Symbol> symbols;

    public CfgString(List<Symbol> symbols) {
        this.symbols = symbols;
    }

    public List<Symbol> getSymbols() {
        return symbols;
    }

    public void setSymbols(List<Symbol> symbols) {
        this.symbols = symbols;
    }

    @Override
    public String toString() {
        return "CfgString{" +
                "symbols=" + symbols +
                '}';
    }
}
