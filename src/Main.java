import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Main {
    public static void main(String[] args) {
        //CFG cfg = Builder.buildCFG("S", "S->AB; A->AaA|#; B->BbB|#; C->B|#");
        CFG cfg = Builder.buildCFG("S", "S->aSA|bB; A->cA|c|#; B->bB|cA|#");


        System.out.println(cfg);
        System.out.println(Printer.cfgToString(cfg));

        cfg = new CfgSimplification(cfg).eraseEpsilon();
        System.out.println(cfg);
        System.out.println(Printer.cfgToString(cfg));

    }
}