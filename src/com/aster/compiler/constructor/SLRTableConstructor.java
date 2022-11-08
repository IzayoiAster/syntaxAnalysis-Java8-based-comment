package com.aster.compiler.constructor;

import com.aster.compiler.entity.Grammar;
import com.aster.compiler.entity.Item;
import com.aster.compiler.utils.Pair;
import com.aster.compiler.entity.Production;

import java.util.*;

/**
 * 构造SLR分析表
 */
public class SLRTableConstructor {
    public static void getSLRTable(Grammar g) {
        // ACTION表，即SLR分析表左半部分
        g.setActionTable(new ArrayList<>());
        // GOTO表，即SLR分析表右半部分
        g.setGotoTable(new ArrayList<>());
        // 对于每个状态，建立HashMap，即SLR表中的 “一行”
        for (int i = 0; i < g.getStateCnt(); i++) {
            g.getActionTable().add(i, new HashMap<>());
            g.getGotoTable().add(i, new HashMap<>());
        }
        // 这里的k就是状态号
        for (int k = 0; k < g.getClosure().size(); k++) {
            Set<Item> I = g.getClosure().get(k);
            for (Item i : I) {
                // 如果点不在最右边
                if (i.getDot() < i.getRight().size()) {
                    // 看点右边的下一个符号next
                    String next = i.getRight().get(i.getDot());
                    for (Set<Item> J : g.getClosure()) {
                        if (setCmp(LR0Constructor.GOTO(g, I, next), J)) { // 看看从next是不是能走到新闭包
                            if (g.getVT().contains(next)) // 如果能从next走到新闭包，next是终结符，
                                // 那就能移入next，在ACTION表里记录状态k的移入
                                g.getActionTable().get(k).put(next, new Pair<>("S", g.getClosure().indexOf(J)));
                            // 不是终结符，那就记录到GOTO表里
                            else g.getGotoTable().get(k).put(next, g.getClosure().indexOf(J));
                        }
                    }
                } else { // 如果点在最右边，要规约了
                    if (i.getLeft().equals("S'")) { // 已经是S'了，就不用规约了，accept
                        g.getActionTable().get(k).put("#", new Pair<>("Acc", 0));
                        continue;
                    }
                    // 寻找与当前项目i对应的产生式j
                    int j = g.getP().indexOf(new Production(i.getLeft(), i.getRight()));
                    for (String t : g.getVT()) {
                        if (g.getFollow().get(i.getLeft()).contains(t))
                            // 枚举所有终结符，如果有出现在左部FOLLOW集（ g.getFollow().get(i.getLeft()) ）
                            // 中的终结符t，那说明在当前状态k的时候遇到了t就可以规约了，在Action中加入对应的R规约
                            g.getActionTable().get(k).put(t, new Pair<>("R", j));
                    }
                    if (g.getFollow().get(i.getLeft()).contains("#"))
                        g.getActionTable().get(k).put("#", new Pair<>("R", j));
                    break;
                }
            }
        }

        display(g);
    }

    private static void display(Grammar g) {
        List<String> y = Arrays.asList("id", "value", "(", ")", "{", "}", ",", ";", "=", "while", "if", "else", "return", "int", "float", "double", "bool", "char", "&&", "||", "!", "true", "false", "<", ">", "<=", ">=", "==", "!=", "-", "+", "*", "/", "#");
        System.out.print("\t|");
        for (String s : y) {
            System.out.print(s + "\t");
            if (s.length() < 3) System.out.print("\t");
            System.out.print("|");
        }
        for (int state = 0; state < g.getStateCnt(); state++) {
            System.out.println();
            System.out.println("-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------");
            System.out.print(state + "\t|");
            for (String s : y) {
                Pair<String, Integer> p = g.getActionTable().get(state).get(s);
                if (p == null) {
                    System.out.print("\t\t|");
                } else {
                    String out = p.getFirst() + p.getSecond().toString();
                    System.out.print(out + "\t");
                    if (out.length() < 3) System.out.print("\t");
                    System.out.print("|");
                }
            }
        }

        System.out.println("\t");
        y = Arrays.asList("程序", "函数定义", "形式参数", "代码块", "变量类型", "算术表达式", "布尔表达式", "比较运算符", "算术运算符");
        System.out.print("\t|");
        for (String s : y) {
            System.out.print(s + "\t");
            if (s.length() < 4) System.out.print("\t");
            if (s.length() < 8) System.out.print("\t");
            System.out.print("|");
        }
        for (int state = 0; state < g.getStateCnt(); state++) {
            System.out.println();
            System.out.println("-----------------------------------------------------------------------------------------------------------------------------------------------------");
            System.out.print(state + "\t|");
            for (String s : y) {
                Integer p = g.getGotoTable().get(state).get(s);
                if (p == null) {
                    System.out.print("\t\t\t\t|");
                } else {
                    String out = p.toString();
                    System.out.print(out + "\t\t\t");
                    if (out.length() < 3) System.out.print("\t");
                    System.out.print("|");
                }
            }
        }
    }

    /**
     * 比较两个集合
     *
     * @param a
     * @param b
     * @return 相同返回true
     */
    private static boolean setCmp(Set<Item> a, Set<Item> b) {
        if (a.size() != b.size()) return false;
        for (Item i : a) {
            if (!b.contains(i)) return false;
        }
        return true;
    }
}
