package cfg;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class Production {
    private Symbol left;
    private Set<Right> rights;

    public Production() {
    }

    public Production(Symbol left, Set<Right> rights) {
        setLeft(left);
        setRights(rights);
    }

    public Symbol getLeft() {
        return left;
    }

    public void setLeft(Symbol left) {
        if (left.getType() != Symbol.NON_TERMINAL_SYMBOL)
            throw new RuntimeException("左部必须为非终结符");
        this.left = left;
    }

    public Set<Right> getRights() {
        return rights;
    }

    public void setRights(Set<Right> rights) {
        this.rights = rights;
    }

    public int getRightsNum() {
        return rights.size();
    }

    public void removeRight(Right right) {
        rights.remove(right);
    }

    public void addRights(Set<Right> newRights) {
        rights.addAll(newRights);
    }

    public void addRight(Right newRight) {
        rights.add(newRight);
    }

    public Set<Symbol> getAllSymbols() {
        Set<Symbol> symbols = new HashSet<>();
        for (Right right : rights) {
            symbols.addAll(right.getSymbolSet());
        }
        return symbols;
    }

    public Set<Symbol> getTerminalSymbols() {
        Set<Symbol> set = new HashSet<>();
        for (Right right : rights) {
            set.addAll(right.getTerminalSet());
        }

        return set;
    }

    public Set<Symbol> getNonTerminalSymbols() {
        Set<Symbol> set = new HashSet<>();
        for (Right right : rights) {
            set.addAll(right.getNonTerminalSet());
        }

        return set;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Production)) return false;
        Production that = (Production) o;
        return left.equals(that.left) &&
                rights.equals(that.rights);
    }

    @Override
    public int hashCode() {
        return Objects.hash(left, rights);
    }

    @Override
    public String toString() {
        return "cfg.Production{" +
                "left=" + left +
                ", rights=" + rights +
                '}';
    }
}
