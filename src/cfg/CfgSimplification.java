package cfg;

import java.util.*;

public class CfgSimplification {
    private final CFG cfg;

    public CfgSimplification(CFG cfg) {
        this.cfg = cfg;
    }

    //消除Epsilon产生式
    public CfgSimplification eraseEpsilon() {
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

        return this;
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

    static class UnitPair {
        Symbol first;
        Symbol second;

        public UnitPair(Symbol first, Symbol second) {
            this.first = first;
            this.second = second;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof UnitPair)) return false;
            UnitPair unitPair = (UnitPair) o;
            return Objects.equals(first, unitPair.first) &&
                    Objects.equals(second, unitPair.second);
        }

        @Override
        public int hashCode() {
            return Objects.hash(first, second);
        }

        @Override
        public String toString() {
            return "UnitPair{" +
                    "first=" + first +
                    ", second=" + second +
                    '}';
        }
    }

    public CfgSimplification eraseUnitProduction() {
        Set<UnitPair> unitPairs = getAllUnitPairs();

        HashMap<Symbol, Production> unUnitProductions = getUnUnitProductions();

        HashMap<Symbol, Production> replaceUnitProductions = replaceUnitProductions(unitPairs, unUnitProductions);

        HashMap<Symbol, Production> newProductions = new HashMap<>();
        combineProductions(newProductions, unUnitProductions);
        combineProductions(newProductions, replaceUnitProductions);

        cfg.setProductions(newProductions);

        return this;
    }

    private void combineProductions(HashMap<Symbol, Production> newProductions, HashMap<Symbol, Production> combined) {
        for (Map.Entry<Symbol, Production> entry : combined.entrySet()) {
            if (newProductions.containsKey(entry.getKey())) {
                newProductions.get(entry.getKey()).addRights(entry.getValue().getRights());
            } else {
                Production production = new Production();
                production.setLeft(entry.getKey());
                production.setRights(new HashSet<>(entry.getValue().getRights()));
                newProductions.put(production.getLeft(), production);
            }
        }
    }

    private Set<UnitPair> getAllUnitPairs() {
        Set<UnitPair> unitPairs = new HashSet<>();

        HashMap<Symbol, Production> productions = cfg.getProductions();
        //若A->B 则[A, B]为单元对
        for (Map.Entry<Symbol, Production> entry : productions.entrySet()) {
            Production production = entry.getValue();
            Set<Right> rights = production.getRights();
            for (Right right : rights) {
                if (right.isUnit()) {
                    if (!production.getLeft().equals(right.getUnitSymbol()))
                        unitPairs.add(new UnitPair(production.getLeft(), right.getUnitSymbol()));
                }
            }
        }

        //若[A, B]和[B, C]都是单元对，则[A, C]是单元对
        UnitPair nextPair;
        while ((nextPair = nextUnitPair(unitPairs)) != null) {
            unitPairs.add(nextPair);
        }

        return unitPairs;
    }

    private UnitPair nextUnitPair(Set<UnitPair> unitPairs) {
        List<UnitPair> unitPairList = new ArrayList<>(unitPairs);

        for (int i = 0; i < unitPairList.size(); i++) {
            for (int j = i + 1; j < unitPairs.size(); j++) {
                UnitPair pi = unitPairList.get(i);
                UnitPair pj = unitPairList.get(j);

                UnitPair unitPair = null;
                if (pi.second.equals(pj.first)) {
                    unitPair = new UnitPair(pi.first, pj.second);
                } else if (pj.second.equals(pi.first)) {
                    unitPair = new UnitPair(pj.first, pi.second);
                }
                if (unitPair != null && !unitPairs.contains(unitPair) && !unitPair.first.equals(unitPair.second)) {
                    return unitPair;
                }
            }
        }

        return null;
    }

    private HashMap<Symbol, Production> getUnUnitProductions() {
        HashMap<Symbol, Production> unUnitProductions = new HashMap<>();

        for (Map.Entry<Symbol, Production> entry : cfg.getProductions().entrySet()) {
            Set<Right> newRights = new HashSet<>();

            for (Right right : entry.getValue().getRights()) {
                if (!right.isUnit()) {
                    newRights.add(right);
                }
            }

            if (!newRights.isEmpty()) {
                unUnitProductions.put(entry.getKey(), new Production(entry.getKey(), newRights));
            }
        }

        return unUnitProductions;
    }

    private HashMap<Symbol, Production> replaceUnitProductions(Set<UnitPair> unitPairs,
                                                               HashMap<Symbol, Production> unUnitProductions) {
        HashMap<Symbol, Production> replacedProductions = new HashMap<>();

        for (UnitPair unitPair : unitPairs) {
            Production production;
            if (replacedProductions.containsKey(unitPair.first)) {
                production = replacedProductions.get(unitPair.first);
            } else {
                production = new Production();
                production.setLeft(unitPair.first);
                production.setRights(new HashSet<>());
            }

            if (unUnitProductions.containsKey(unitPair.second)) {
                production.addRights(unUnitProductions.get(unitPair.second).getRights());
            }
            replacedProductions.put(production.getLeft(), production);
        }

        return replacedProductions;
    }


    private boolean nextProduceSymbol(Set<Symbol> produceSymbols) {
        for (Map.Entry<Symbol, Production> entry : cfg.getProductions().entrySet()) {
            Production production = entry.getValue();
            for (Right right : production.getRights()) {
                if (produceSymbols.containsAll(right.getSymbolSet()) && !produceSymbols.contains(production.getLeft())) {
                    produceSymbols.add(production.getLeft());
                    return true;
                }
            }
        }

        return false;
    }

    private boolean nextReachableSymbol(Set<Symbol> reachableSymbols) {
        for (Map.Entry<Symbol, Production> entry : cfg.getProductions().entrySet()) {
            Production production = entry.getValue();

            if (reachableSymbols.contains(production.getLeft())) {
                Set<Symbol> allSymbols = production.getAllSymbols();
                if (!reachableSymbols.containsAll(allSymbols)) {
                    reachableSymbols.addAll(allSymbols);
                    return true;
                }
            }
        }

        return false;
    }

    private void removeUnusefulSymbols(Set<Symbol> usefulSymbols) {
        HashMap<Symbol, Production> productions = cfg.getProductions();

        Iterator<Map.Entry<Symbol, Production>> iterator = productions.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<Symbol, Production> entry = iterator.next();
            if (!usefulSymbols.contains(entry.getKey())) {
                iterator.remove();
            } else {
                Production production = entry.getValue();
                Set<Right> rights = production.getRights();
                Iterator<Right> rightIterator = rights.iterator();
                while (rightIterator.hasNext()) {
                    Right right = rightIterator.next();

                    boolean hasUnusefulSymbol = false;
                    Set<Symbol> rightSymbols = right.getSymbolSet();
                    for (Symbol symbol : rightSymbols) {
                        if (!usefulSymbols.contains(symbol)) {
                            hasUnusefulSymbol = true;
                            break;
                        }
                    }

                    if (hasUnusefulSymbol) {
                        rightIterator.remove();
                    }
                }
            }
        }
    }

    public CfgSimplification eraseUnusefulSymbols() {
        Set<Symbol> produceSymbols = new HashSet<>(cfg.getTerminalSymbols());
        while (nextProduceSymbol(produceSymbols));
        removeUnusefulSymbols(produceSymbols);

        Set<Symbol> reachableSymbols = new HashSet<>();
        reachableSymbols.add(cfg.getStart());
        while (nextReachableSymbol(reachableSymbols));
        removeUnusefulSymbols(reachableSymbols);

        return this;
    }

    public void simplify() {
        eraseEpsilon().eraseUnitProduction().eraseUnusefulSymbols();
    }

    public static void main(String[] args) {
//        Right right = Builder.buildRight("AaA");
//        Set<Symbol> nullableVariable = new HashSet<>();
//        nullableVariable.add(Builder.buildSymbol("A"));
//        System.out.println(replaceProduction(right, nullableVariable));

//        CFG cfg = Builder.buildCFG("S", "S->AB|a; A->b|#");
//        CFG cfg = Builder.buildCFG("S", "S->aA|aBB; A->aaA|#; B->bB|bbC; C->B");
        CFG cfg = Builder.buildCFG("S", "S->A|b; A->Aa");
        System.out.println(Printer.cfgToString(cfg));
        CfgSimplification cfgSimplification = new CfgSimplification(cfg);

        System.out.println();

        cfgSimplification.eraseEpsilon();
        System.out.println(Printer.cfgToString(cfg));

        System.out.println();

        cfgSimplification.eraseUnitProduction();
        System.out.println(Printer.cfgToString(cfg));

        System.out.println("unuseful");

        cfgSimplification.eraseUnusefulSymbols();
        System.out.println(Printer.cfgToString(cfg));
//        System.out.println();
//        cfg = cfgSimplification.eraseUnitProduction();
//        System.out.println(Printer.cfgToString(cfg));


    }
}
