package com.aster.compiler;

import com.aster.compiler.analyzer.LexicalAnalyzer;
import com.aster.compiler.analyzer.SLRAnalyzer;
import com.aster.compiler.entity.Grammar;
import com.aster.compiler.entity.Production;
import com.aster.compiler.constructor.FirstConstructor;
import com.aster.compiler.constructor.FollowConstructor;
import com.aster.compiler.constructor.LR0Constructor;
import com.aster.compiler.constructor.SLRTableConstructor;

import java.io.BufferedInputStream;
import java.util.*;

public class SyntaxAnalyze {

    public static void main(String[] args) {

        Grammar G = new Grammar();
        init(G);

        System.out.println("\n构造FIRST集\n");
        FirstConstructor.constructFirst(G);
        System.out.println("--------------------------------------------------------------------------------");

        System.out.println("\n构造FOLLOW集\n");
        FollowConstructor.constructFollow(G);
        System.out.println("--------------------------------------------------------------------------------");

        System.out.println("\n构造LR(0)项目集\n");
        LR0Constructor.constructLR0Collection(G);
        System.out.println("--------------------------------------------------------------------------------");

        System.out.println("\n构造SLR分析表\n");
        SLRTableConstructor.getSLRTable(G);
        System.out.println("--------------------------------------------------------------------------------");

        System.out.println("\n读取源程序\n");
        BufferedInputStream in = new BufferedInputStream(Objects.requireNonNull(SyntaxAnalyze.class.getResourceAsStream("/com/aster/compiler/code2.txt")));
        Scanner scanner = new Scanner(in);
        List<String> input = new ArrayList<>();
        while (scanner.hasNext()) {
            input.add(scanner.next());
        }
        System.out.println("--------------------------------------------------------------------------------");

        System.out.println("\n词法分析\n");
        List<String> code = LexicalAnalyzer.analyze(input);
        System.out.println("--------------------------------------------------------------------------------");

        System.out.println("\n语法分析\n");
        SLRAnalyzer.analyze(G, code);
    }

    private static void init(Grammar g) {
        Set<String> s = new HashSet<String>(Collections.singleton("程序"));
        Set<String> vn = new HashSet<String>(Arrays.asList("程序", "函数定义", "形式参数", "代码块", "变量类型", "算术表达式", "布尔表达式", "比较运算符", "算术运算符"));
        Set<String> vt = new HashSet<String>(Arrays.asList("id", "value", "(", ")", "{", "}", ",", ";", "=", "while", "if", "else", "return", "int", "float", "double", "bool", "char", "&&", "||", "!", "true", "false", "<", ">", "<=", ">=", "==", "!=", "-", "+", "*", "/"));
        List<Production> p = new ArrayList<Production>();

        p.add(new Production("程序", new ArrayList<>(Arrays.asList("函数定义"))));
        p.add(new Production("函数定义", new ArrayList<>(Arrays.asList("函数定义", "函数定义"))));
        p.add(new Production("函数定义", new ArrayList<>(Arrays.asList("变量类型", "id", "(", ")", "{", "代码块", "}"))));
        p.add(new Production("函数定义", new ArrayList<>(Arrays.asList("变量类型", "id", "(", "形式参数", ")", "{", "代码块", "}"))));
        p.add(new Production("形式参数", new ArrayList<>(Arrays.asList("变量类型", "id"))));
        p.add(new Production("形式参数", new ArrayList<>(Arrays.asList("变量类型", "id", ",", "形式参数"))));

        p.add(new Production("代码块", new ArrayList<>(Arrays.asList("代码块", "代码块"))));
        p.add(new Production("代码块", new ArrayList<>(Arrays.asList("变量类型", "id", ";"))));
        p.add(new Production("代码块", new ArrayList<>(Arrays.asList("变量类型", "id", "=", "算术表达式", ";"))));
        p.add(new Production("代码块", new ArrayList<>(Arrays.asList("id", "=", "算术表达式", ";"))));
        p.add(new Production("代码块", new ArrayList<>(Arrays.asList("while", "(", "布尔表达式", ")", "{", "代码块", "}"))));
        p.add(new Production("代码块", new ArrayList<>(Arrays.asList("if", "(", "布尔表达式", ")", "{", "代码块", "}"))));
        p.add(new Production("代码块", new ArrayList<>(Arrays.asList("if", "(", "布尔表达式", ")", "{", "代码块", "}", "else", "{", "代码块", "}"))));
        p.add(new Production("代码块", new ArrayList<>(Arrays.asList("return", ";"))));
        p.add(new Production("代码块", new ArrayList<>(Arrays.asList("return", "算术表达式", ";"))));

        p.add(new Production("变量类型", new ArrayList<>(Arrays.asList("int"))));
        p.add(new Production("变量类型", new ArrayList<>(Arrays.asList("float"))));
        p.add(new Production("变量类型", new ArrayList<>(Arrays.asList("double"))));
        p.add(new Production("变量类型", new ArrayList<>(Arrays.asList("bool"))));
        p.add(new Production("变量类型", new ArrayList<>(Arrays.asList("char"))));

        p.add(new Production("算术表达式", new ArrayList<>(Arrays.asList("算术表达式", "算术运算符", "算术表达式"))));
        p.add(new Production("算术表达式", new ArrayList<>(Arrays.asList("-", "算术表达式"))));
        p.add(new Production("算术表达式", new ArrayList<>(Arrays.asList("(", "算术表达式", ")"))));
        p.add(new Production("算术表达式", new ArrayList<>(Arrays.asList("id"))));
        p.add(new Production("算术表达式", new ArrayList<>(Arrays.asList("value"))));

        p.add(new Production("布尔表达式", new ArrayList<>(Arrays.asList("算术表达式", "比较运算符", "算术表达式"))));
        p.add(new Production("布尔表达式", new ArrayList<>(Arrays.asList("布尔表达式", "&&", "布尔表达式"))));
        p.add(new Production("布尔表达式", new ArrayList<>(Arrays.asList("布尔表达式", "||", "布尔表达式"))));
        p.add(new Production("布尔表达式", new ArrayList<>(Arrays.asList("!", "布尔表达式"))));
        p.add(new Production("布尔表达式", new ArrayList<>(Arrays.asList("(", "布尔表达式", ")"))));
        p.add(new Production("布尔表达式", new ArrayList<>(Arrays.asList("true"))));
        p.add(new Production("布尔表达式", new ArrayList<>(Arrays.asList("false"))));

        p.add(new Production("比较运算符", new ArrayList<>(Arrays.asList("<"))));
        p.add(new Production("比较运算符", new ArrayList<>(Arrays.asList(">"))));
        p.add(new Production("比较运算符", new ArrayList<>(Arrays.asList("<="))));
        p.add(new Production("比较运算符", new ArrayList<>(Arrays.asList(">="))));
        p.add(new Production("比较运算符", new ArrayList<>(Arrays.asList("=="))));
        p.add(new Production("比较运算符", new ArrayList<>(Arrays.asList("!="))));

        p.add(new Production("算术运算符", new ArrayList<>(Arrays.asList("+"))));
        p.add(new Production("算术运算符", new ArrayList<>(Arrays.asList("-"))));
        p.add(new Production("算术运算符", new ArrayList<>(Arrays.asList("*"))));
        p.add(new Production("算术运算符", new ArrayList<>(Arrays.asList("/"))));

        g.setS(s);
        g.setVN(vn);
        g.setVT(vt);
        g.setP(p);
    }
}