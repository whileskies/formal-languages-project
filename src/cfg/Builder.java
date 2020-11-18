package cfg;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Builder {
    //根据规则构建相应对象，小写字母(+数字)为终结符，大写字母(+数字)为非终结符，#为空串

    public static Symbol buildSymbol(String symbolStr) {
        Symbol symbol = new Symbol();

        String symbolStrOri = symbolStr;
        String re = "#|[a-zA-Z]\\d*";
        Pattern pattern = Pattern.compile(re);
        Matcher matcher = pattern.matcher(symbolStrOri);

        if (matcher.find()) {
            symbolStr = matcher.group();
        } else {
            throw new RuntimeException("符号解析错误:" + symbolStr);
        }

        if (symbolStr.charAt(0) == '#') {
            symbol.setSymbol("ε");
            symbol.setType(Symbol.EPSILON_SYMBOL);
        } else if (Character.isLowerCase(symbolStr.charAt(0))) {
            symbol.setSymbol(symbolStr);
            symbol.setType(Symbol.TERMINAL_SYMBOL);
        } else if (Character.isUpperCase(symbolStr.charAt(0))) {
            symbol.setSymbol(symbolStr);
            symbol.setType(Symbol.NON_TERMINAL_SYMBOL);
        } else {
            throw new RuntimeException("符号格式错误");
        }

        return symbol;
    }

    public static Symbol getNonTerminalSymbol(Symbol refer, Set<Symbol> excludeSymbols) {
        Symbol nonTerminalSymbol = null;
        if (refer.isTerminalSymbol()) {
            Symbol tmp = Builder.buildSymbol(refer.getSymbol().toUpperCase());
            if (!excludeSymbols.contains(tmp)) {
                nonTerminalSymbol = tmp;
            } else {
                for (int i = 1; i < 10; i++) {
                    tmp = Builder.buildSymbol(refer.getSymbol().toUpperCase() + i);
                    if (!excludeSymbols.contains(tmp)) {
                        nonTerminalSymbol = tmp;
                        break;
                    }
                }
            }
        }

        return nonTerminalSymbol;
    }

    public static Symbol buildEpsilon() {
        return buildSymbol("#");
    }

    public static Right buildEpsilonRight() {
        return buildRight("#");
    }

    public static Right buildRight(String rightStr) {
        List<Symbol> symbols = new ArrayList<>();

        String re = "#|[a-zA-Z]\\d*";
        Pattern pattern = Pattern.compile(re);
        Matcher matcher = pattern.matcher(rightStr);

        while (matcher.find()) {
            symbols.add(buildSymbol(matcher.group()));
        }

        return new Right(symbols);
    }

    public static Production buildProduction(String productionStr) {
        Production production = new Production();

        productionStr = productionStr.replaceAll(" ", "");
        String[] leftAndRight = productionStr.split("->");
        if (leftAndRight.length != 2)
            throw new RuntimeException("生成式格式错误:" + productionStr);

        production.setLeft(buildSymbol(leftAndRight[0]));

        Set<Right> rights = new HashSet<>();
        String[] rightsStr = leftAndRight[1].split("\\|");

        for (String right : rightsStr) {
            Right r = buildRight(right);
            if (r.symbolsNum() != 0) {
                rights.add(r);
            }
        }

        production.setRights(rights);

        return production;
    }

    public static CFG buildCFG(String start, String cfgStr) {
        List<Production> productions = new ArrayList<>();

        String[] splits = cfgStr.split(";");
        if (splits.length == 0)
            throw new RuntimeException("上下文无关文法格式错误:" + cfgStr);

        for (String proStr : splits) {
            productions.add(buildProduction(proStr));
        }

        Symbol startSymbol = buildSymbol(start);

        return new CFG(startSymbol, productions);
    }

    public static CfgString buildCfgString(String str) {
        List<Symbol> symbols = new ArrayList<>();

        String re = "#|[a-z]\\d*";
        Pattern pattern = Pattern.compile(re);
        Matcher matcher = pattern.matcher(str);

        while (matcher.find()) {
            symbols.add(buildSymbol(matcher.group()));
        }

        return new CfgString(symbols);
    }

    public static void main(String[] args) {
        Symbol a = Builder.buildSymbol("a");
        Symbol A = Builder.buildSymbol("A");
        Symbol A2 = Builder.buildSymbol("A2");
        Set<Symbol> exclude = new HashSet<>();
        exclude.add(A);
        exclude.add(A2);

        System.out.println(getNonTerminalSymbol(a, exclude));
    }
}
