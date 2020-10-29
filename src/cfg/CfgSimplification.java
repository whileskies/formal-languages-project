package cfg;

import java.util.*;

public class CfgSimplification {
    private CFG cfg;

    public CfgSimplification(CFG cfg) {
        this.cfg = cfg;
    }

    //消除Epsilon产生式
    public CFG eraseEpsilon() {
        Set<Symbol> nullableVariable = getNullableVariable();

        HashMap<Symbol, Production> productions = cfg.getProductions();
        //删除rights中的epsilon、只含有epsilon的production
        Iterator<Map.Entry<Symbol, Production>> iterator = productions.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<Symbol, Production> entry = iterator.next();
            Production production = entry.getValue();
            Set<Right> rights = production.getRights();

            rights.remove(Builder.buildEpsilonRight());
            if (production.getRightsNum() == 0) {
                cfg.removeSymbolFromVariableSet(production.getLeft());
                iterator.remove();
            }
        }

        //替换生成式
        for (Map.Entry<Symbol, Production> entry : productions.entrySet()) {
            Production production = entry.getValue();
            Set<Right> rights = production.getRights();
            Set<Right> newRights = new HashSet<>();

            for (Right right : rights) {
                Set<Right> replacedRights = replaceProduction(right, nullableVariable);
                if (replacedRights.size() > 0)
                    newRights.addAll(replacedRights);
            }

            rights.addAll(newRights);
        }

        return this.cfg;
    }

    public Set<Symbol> getNullableVariable() {
        Set<Symbol> nullableVariable = new HashSet<>();

        //如果A->epsilon，则A是可空的
        HashMap<Symbol, Production> productions = cfg.getProductions();
        for (Production production : productions.values()) {
            Symbol left = production.getLeft();
            Set<Right> rights = production.getRights();

            boolean epsilon = false;
            for (Right right : rights) {
                if (right.containSymbol(Builder.buildEpsilon())) {
                    epsilon = true;
                }
            }

            if (epsilon) {
                nullableVariable.add(left);
            }
        }

        //如果B->alpha，且alpha中的每个符号都是可空的，则B是可空的
        Production nextPro;
        while ((nextPro = nextNullableVariable(nullableVariable)) != null) {
            nullableVariable.add(nextPro.getLeft());
        }

        return nullableVariable;
    }

    private Production nextNullableVariable(Set<Symbol> nullableVariable) {
        HashMap<Symbol, Production> productions = cfg.getProductions();
        for (Production production : productions.values()) {
            Set<Right> rights = production.getRights();
            for (Right right : rights) {
                if (rightSymbolsAllInSet(right, nullableVariable)) {
                    if (!nullableVariable.contains(production.getLeft()))
                        return production;
                }
            }
        }

        return null;
    }

    private static boolean rightSymbolsAllInSet(Right right, Set<Symbol> set) {
        for (Symbol symbol : right.getSymbols()) {
            if (!set.contains(symbol)) {
                return false;
            }
        }
        return true;
    }

    private static Set<Right> replaceProduction(Right right, Set<Symbol> nullableVariable) {
        Set<Right> ret = new HashSet<>();
        List<Symbol> symbols = right.getSymbols();

        List<Integer> nullIndex = new ArrayList<>();
        for (int i = 0; i < symbols.size(); i++) {
            if (nullableVariable.contains(symbols.get(i))) {
                nullIndex.add(i);
            }
        }

        List<List<Integer>> subsets = Utils.subsets(nullIndex);
        for (List<Integer> ignore : subsets) {
            List<Symbol> newSymbols = new ArrayList<>();

            for (int i = 0; i < symbols.size(); i++) {
                if (!ignore.contains(i))
                    newSymbols.add(symbols.get(i));
            }

            if (newSymbols.size() != 0)
                ret.add(new Right(newSymbols));
        }

        return ret;
    }

    public static void main(String[] args) {
        Right right = Builder.buildRight("AaA");
        Set<Symbol> nullableVariable = new HashSet<>();
        nullableVariable.add(Builder.buildSymbol("A"));
        System.out.println(replaceProduction(right, nullableVariable));
    }
}
