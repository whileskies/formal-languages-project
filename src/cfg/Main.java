package cfg;

import java.util.HashSet;
import java.util.Set;

public class Main {
    public static void main(String[] args) {
//        CFG cfg = Builder.buildCFG("S", "S->AB; A->AaA|#; B->BbB|#; C->B|#");
        //CFG cfg = Builder.buildCFG("S", "S->aSA|bB; A->cA|c|#; B->bB|cA|#");
        //CFG cfg = Builder.buildCFG("S", "S->AB; A->aAA|#; B->bBB|#");

        CFG cfg = Builder.buildCFG("S", "S->aSb|a|b|#");

        System.out.println("原CFG:");
        System.out.println(Printer.cfgToString(cfg));
        System.out.println();

        CfgSimplification cfgSimplification = new CfgSimplification(cfg);

        cfgSimplification.eraseEpsilon();
        System.out.println("消除ε产生式:");
        System.out.println(Printer.cfgToString(cfg));
        System.out.println();

        cfgSimplification.eraseUnitProduction();
        System.out.println("消除单一产生式:");
        System.out.println(Printer.cfgToString(cfg));
        System.out.println();

        cfgSimplification.eraseUnusefulSymbols();
        System.out.println("消除无用符号:");
        System.out.println(Printer.cfgToString(cfg));
        System.out.println();

        CfgNormalForm cfgNormalForm = new CfgNormalForm(cfg);
        cfgNormalForm.toGreibachNormalForm();
        System.out.println("转为Greibach范式:");
        System.out.println(Printer.cfgToString(cfg));
        System.out.println();

        CfgPDA cfgPDA = new CfgPDA(cfg);
        System.out.println("状态转移函数:");
        System.out.println(Printer.cfgPDAToString(cfgPDA));
        System.out.println();

        CfgString cfgString = Builder.buildCfgString("aabb");
        System.out.println(Printer.cfgStringToString(cfgString) + " : " + cfgPDA.acceptCfgString(cfgString));

        CfgString cfgString2 = Builder.buildCfgString("aaaabb");
        System.out.println(Printer.cfgStringToString(cfgString2) + " : " + cfgPDA.acceptCfgString(cfgString2));
    }
}
