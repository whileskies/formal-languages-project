# 形式语言与自动机课程项目
Formal Languages and Automata course project

### 任务

1. 对一文法进行化简，将其转化为Greibach范式，并将Greibach范式转为PDA，判断是否能接受某一字符串
2. 模拟计算x^y的图灵机

### 上下文无关文法

使用方法：

##### 创建CFG

Builder.buildCFG()函数参数1为起始符号，参数2为字符串格式的上下文无关文法，小写字母(+数字)为终结符，大写字母(+数字)为非终结符，#为空串

```java
CFG cfg = Builder.buildCFG("S", "S->AB; A->AaA|#; B->BbB|#; C->B|#");
CFG cfg1 = Builder.buildCFG("S", "S->aSA|bB; A->cA|c|#; B->bB|cA|#");
CFG cfg2 = Builder.buildCFG("S", "S->AB; A->aAA|#; B->bBB|#");
CFG cfg3 = Builder.buildCFG("S", "S->aSb|a|b|#");

System.out.println(Printer.cfgToString(cfg));
```

##### CFG化简

```java
CfgSimplification cfgSimplification = new CfgSimplification(cfg);

cfgSimplification.eraseEpsilon();
System.out.println("消除ε产生式:");
System.out.println(Printer.cfgToString(cfg));

cfgSimplification.eraseUnitProduction();
System.out.println("消除单一产生式:");
System.out.println(Printer.cfgToString(cfg));

cfgSimplification.eraseUnusefulSymbols();
System.out.println("消除无用符号:");
System.out.println(Printer.cfgToString(cfg));

CfgSimplification cfgSimplification = new 
CfgSimplification(cfg).eraseEpsilon().eraseUnitProduction().eraseUnusefulSymbols();
```

##### 转为Greibach范式

```java
CfgNormalForm cfgNormalForm = new CfgNormalForm(cfg);
cfgNormalForm.toGreibachNormalForm();
System.out.println("转为Greibach范式:");
System.out.println(Printer.cfgToString(cfg));
```

##### Greibach范式转为PDA

```java
CfgPDA cfgPDA = new CfgPDA(cfg);
System.out.println("状态转移函数:");
System.out.println(Printer.cfgPDAToString(cfgPDA));
```

##### 判断字符串是否被PDA接受

```java
CfgString cfgString = Builder.buildCfgString("aabb");
System.out.println(Printer.cfgStringToString(cfgString) + " : " + cfgPDA.acceptCfgString(cfgString));

CfgString cfgString2 = Builder.buildCfgString("aaaabb");
System.out.println(Printer.cfgStringToString(cfgString2) + " : " + cfgPDA.acceptCfgString(cfgString2));
```

### 图灵机

使用方法：

```java
PowTuringMachine tm = new PowTuringMachine();
System.out.println("2^3= " + tm.pow(2, 3));
```

