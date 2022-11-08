package com.aster.compiler.constructor;

import com.aster.compiler.entity.Grammar;
import com.aster.compiler.entity.Production;

import java.util.*;

/**
 * FIRST集构造类
 */
public class FirstConstructor {
    /**
     * 构造文法里每个非终结符的FIRST集
     *
     * @param g 文法
     */
    public static void constructFirst(Grammar g) {
        Map<String, Set<String>> first = new HashMap<>();
        g.setFirst(first);
        // 终结符的串首终结符集（即FIRST集）就是它本身
        for (String t : g.getVT()) {
            first.put(t, new HashSet<>(Arrays.asList(t)));
        }
        // 为g中的每一个非终结符都创建一个FIRST集
        for (String v : g.getVN()) {
            first.put(v, new HashSet<>());
        }
        // 枚举g中的产生式
        for (Production p : g.getP()) {
            // 如果产生式的右部的串首符号是终结符（ p.getRight().get(0)：p的右部的串首符号 ）
            // 那就将这个串首符号加入到p的左部的FIRST集里
            if (g.getVT().contains(p.getRight().get(0))) {
                String right = p.getRight().get(0);
                first.get(p.getLeft()).add(right);
            }
            // 如果产生式中含有空产生式
            if (p.getRight().contains("ε")) {
                first.get(p.getLeft()).add("ε");
            }
        }

        boolean flag = true; // flag记录本轮构造中有没有发生更新，当某次构造没发生更新，说明都找齐了
        while (flag) {
            flag = false;
            // 枚举g中的产生式
            for (Production p : g.getP()) {
                if (g.getVN().contains(p.getRight().get(0))) { // 当前产生式右部首符号为非终结符
                    Set<String> temp = first.get(p.getRight().get(0));
                    temp.remove("ε"); // 这句话意思就是如果当前剩下的要检测的右部temp，前面推导出空串了，删掉它，以便之后顺位往下接着找
                    flag |= union(first.get(p.getLeft()), temp); // 尝试更新first集，并更新flag
                }
                if (p.getRight().size() > 1) { // 如果右部不只一个符号
                    boolean epsilon = false; // 空串标记
                    for (String R : p.getRight()) { // 继续，枚举剩下的右部的每一个符号R（token）
                        epsilon = false;
                        if (g.getFirst().get(R).contains("ε")) { // 看R的first集中有没有空串，即R能不能推到出空串
                            epsilon = true; // 如果能推导出空串，标记
                            // 找下去
                            Set<String> temp = first.get(R);
                            temp.remove("ε");
                            flag |= union(first.get(p.getLeft()), temp);
                        }
                        if (!epsilon) { // R推不出空串
                            // 正常更新first集，然后break，就此打住，因为R不能空，轮不到后面的
                            Set<String> temp = first.get(R);
                            temp.remove("ε");
                            flag |= union(first.get(p.getLeft()), temp);
                            break;
                        }
                    }
                    if (epsilon) { // 右部每一个字符都能推出空串，则右部整体可以推出空串
                        flag |= union(first.get(p.getLeft()), new HashSet<>(Arrays.asList("ε")));
                    }
                }
            }
        }

        display(g);
    }

    private static void display(Grammar g) {
        Map<String, Set<String>> first = g.getFirst();
        for (String key : first.keySet()) {
            System.out.println(key + " : " + first.get(key));
        }
        // System.out.println("--------------------------------------------------------");
    }


    public static Set<String> constructFirst(Grammar g, List<String> a) {
        if (a.size() == 1) {
            if (a.contains("ε")) {
                return new HashSet<>(Arrays.asList("ε"));
            }
            return g.getFirst().get(a.get(0));
        }
        boolean epsilon = false;
        Set<String> result = new HashSet<>();
        for (String x : a) {
            epsilon = false;
            if (g.getFirst().get(x).contains("ε")) {
                epsilon = true;
                Set<String> temp = g.getFirst().get(x);
                temp.remove("ε");
                union(result, temp);
            }
            if (!epsilon) {
                Set<String> temp = g.getFirst().get(x);
                temp.remove("ε");
                union(result, temp);
                break;
            }
        }
        if (epsilon) {
            result.add("ε");
        }
        return result;
    }

    static boolean union(Set<String> a, Set<String> b) {
        int aSize = a.size();
        a.addAll(b);
        return aSize != a.size();
    }
}
