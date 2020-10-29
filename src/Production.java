import java.util.List;
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
        return "Production{" +
                "left=" + left +
                ", rights=" + rights +
                '}';
    }
}
