package cfg;

import java.util.*;

public class CfgNormalForm {
    public static final int MAX_RUN_SECOND = 5;

    private final CFG cfg;

    public CfgNormalForm(CFG cfg) {
        this.cfg = cfg;
    }

    // A -> x1 B x2
    // B -> y1|y2|...|yn
    // A->x1 y1 x2|x1 y2 x2|...|x1 yn x2
    public static Production replaceFirstSymbol(Symbol ALeft, Right ARight, Production B) {
        Production newProduction = new Production();
        newProduction.setLeft(ALeft);

        Set<Right> newRights = new HashSet<>();
        newProduction.setRights(newRights);

        Symbol replacedSymbol = B.getLeft();
        List<Symbol> ARightList = ARight.getSymbols();

        int indexInAright = ARightList.indexOf(replacedSymbol);
        if (indexInAright == -1) {
            newRights.add(ARight);
            return newProduction;
        } else {
            List<Symbol> ARightLeft = ARightList.subList(0, indexInAright);
            List<Symbol> ARightRight = ARightList.subList(indexInAright + 1, ARightList.size());

            for (Right bRight : B.getRights()) {
                List<Symbol> newRightSymbolList = new ArrayList<>();
                newRightSymbolList.addAll(ARightLeft);
                newRightSymbolList.addAll(bRight.getSymbols());
                newRightSymbolList.addAll(ARightRight);
                Right newRight = new Right(newRightSymbolList);
                newRights.add(newRight);
            }
        }

        return newProduction;
    }

    private static boolean isSingleTerminalProduction(Production production) {
        return production.getRights().size() == 1 && production.getAllSymbols().size() == 1
                && production.getTerminalSymbols().size() == 1;
    }

    private Symbol getLeftSymbolFromSingleTerminalProduction(Symbol terminalSymbol, Map<Symbol, Production> addedProductions) {
        Symbol left = null;
        Map<Symbol, Production> productions = new HashMap<>();
        productions.putAll(cfg.getProductions());
        productions.putAll(addedProductions);
        for (Map.Entry<Symbol, Production> entry : productions.entrySet()) {
            Production production = entry.getValue();
            if (isSingleTerminalProduction(production)) {
                Set<Symbol> terminalSymbols = production.getTerminalSymbols();
                if (terminalSymbols.toArray()[0].equals(terminalSymbol)) {
                    left = entry.getKey();
                }
            }
        }

        return left;
    }

    //替换表达式中的终结符（除了出现在第一个位置的）
    private void replaceTerminalSymbolFromProduction() {
        Map<Symbol, Production> productions = cfg.getProductions();
        Map<Symbol, Production> addedProductions = new HashMap<>();
        Map<Symbol, Production> addedExistProductions = new HashMap<>();

        for (Map.Entry<Symbol, Production> entry : productions.entrySet()) {
            Symbol symbol = entry.getKey();
            Production production = entry.getValue();
            for (Iterator<Right> iterator = production.getRights().iterator(); iterator.hasNext(); ) {
                Right right = iterator.next();
                List<Symbol> symbolList = right.getSymbols();
                List<Symbol> newRightList = new ArrayList<>(symbolList);
                boolean modified = false;
                for (int i = 1; i < symbolList.size(); i++) {
                    Symbol cur = symbolList.get(i);
                    if (cur.isTerminalSymbol()) {
                        Symbol nonTerminal = getLeftSymbolFromSingleTerminalProduction(cur, addedProductions);
                        if (nonTerminal == null) {
                            Set<Symbol> excludeSymbols = new HashSet<>();
                            excludeSymbols.addAll(cfg.getNonTerminalSymbols());
                            excludeSymbols.addAll(addedProductions.keySet());
                            nonTerminal = Builder.getNonTerminalSymbol(cur, excludeSymbols);
                            Production p = new Production();
                            p.setLeft(nonTerminal);

                            List<Symbol> symbols = new ArrayList<>();
                            symbols.add(cur);
                            Right r = new Right(symbols);
                            Set<Right> rights = new HashSet<>();
                            rights.add(r);
                            p.setRights(rights);

                            addedProductions.put(p.getLeft(), p);
                        }

                        newRightList.set(i, nonTerminal);
                        modified = true;
                    }
                }

                if (modified) {
                    iterator.remove();
                    Right newRight = new Right(newRightList);

                    Production curPro = addedExistProductions.get(symbol);
                    if (curPro == null) {
                        Set<Right> rights = new HashSet<>();
                        rights.add(newRight);
                        addedExistProductions.put(symbol, new Production(symbol, rights));
                    } else {
                        curPro.addRight(newRight);
                    }
                }
            }
        }

        for (Map.Entry<Symbol, Production> entry : cfg.getProductions().entrySet()) {
            Symbol symbol = entry.getKey();
            Production production = entry.getValue();
            if (addedExistProductions.containsKey(symbol)) {
                production.addRights(addedExistProductions.get(symbol).getRights());
            }
        }
        productions.putAll(addedProductions);
    }

    private void iterateReplaceFirstNonTerminalSymbol() {
        long start = System.nanoTime();
        while (true) {
            boolean hasFirst = false;

            HashMap<Symbol, Production> productions = cfg.getProductions();
            for (Map.Entry<Symbol, Production> entry : productions.entrySet()) {
                Symbol left = entry.getKey();
                Production production = entry.getValue();

                Production addedRights = new Production(left, new HashSet<>());
                for (Iterator<Right> iterator = production.getRights().iterator(); iterator.hasNext(); ) {
                    Right right = iterator.next();
                    Symbol first = right.getSymbols().get(0);
                    if (first.isNonTerminalSymbol()) {
                        hasFirst = true;
                        Production B = productions.get(first);
                        if (B == null)
                            throw new RuntimeException("文法错误,无法转化为范式");
                        Production newPro = replaceFirstSymbol(left, right, B);
                        iterator.remove();

                        addedRights.addRights(newPro.getRights());
                    }

                    long cur = System.nanoTime();
                    if ((cur - start) / 1000000000 > MAX_RUN_SECOND)
                        throw new RuntimeException("可能存在左递归");
                }

                production.addRights(addedRights.getRights());
            }
            if (!hasFirst)
                break;

        }
    }

    public void toGreibachNormalForm() {
        CfgSimplification cfgSimplification = new CfgSimplification(cfg);
        cfgSimplification.simplify();

        replaceTerminalSymbolFromProduction();

        iterateReplaceFirstNonTerminalSymbol();

    }

    public static void main(String[] args) {
        CFG cfg = Builder.buildCFG("S", "S->aAbBC; A->aA|B|#; B->bcB|Cca; C->cC|c");
        System.out.println("原CFG：");
        System.out.println(Printer.cfgToString(cfg));
        System.out.println();

        CfgSimplification cfgSimplification = new CfgSimplification(cfg).eraseEpsilon().eraseUnitProduction().eraseUnusefulSymbols();

        System.out.println("化简后：");
        System.out.println(Printer.cfgToString(cfg));
        System.out.println();

        CfgNormalForm cfgNormalForm = new CfgNormalForm(cfg);
        cfgNormalForm.toGreibachNormalForm();

        System.out.println("Greibach范式：");
        System.out.println(Printer.cfgToString(cfg));

    }
}
