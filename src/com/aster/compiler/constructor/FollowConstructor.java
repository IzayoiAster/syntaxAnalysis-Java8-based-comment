package com.aster.compiler.constructor;

import com.aster.compiler.entity.Grammar;
import com.aster.compiler.entity.Production;

import java.util.*;

/**
 * FOLLOW集构造类
 */
public class FollowConstructor {
    /**
     * 构造文法里每个非终结符的FOLLOW集
     *
     * @param g 文法
     */
    public static void constructFollow(Grammar g) {
        Map<String, Set<String>> follow = new HashMap<>();
        g.setFollow(follow);
        // 为g中的每一个非终结符都创建一个FOLLOW集
        for (String vn : g.getVN()) {
            follow.put(vn, new HashSet<>());
        }
        follow.get(g.getS().iterator().next()).add("#");
        boolean flag = true; // 记录有没有更新
        while (flag) {
            flag = false;
            // 枚举g中的产生式
            for (Production p : g.getP()) {
                String A = p.getLeft();
                List<String> R = p.getRight();
                for (int i = 0; i < R.size(); i++) {
                    // 遍历当前产生式右部的符号B
                    String B = R.get(i);
                    if (!g.getVN().contains(B)) { // 如果B是终结符.那不用算FOLLOW集
                        continue;
                    }
                    if (i == R.size() - 1) { // 如果当前产生式右部已经遍历完了
                        // 那就尝试用B的FOLLOW更新A的FOLLOW，并记录有没有变化
                        flag |= union(follow.get(B), follow.get(A));
                    } else { // 如果B是非终结符
                        // beta是R中，B右侧的串
                        List<String> beta = new ArrayList<>(R.subList(i + 1, R.size()));
                        // 获取B右侧串的FIRST集
                        Set<String> first = FirstConstructor.constructFirst(g, beta);
                        if (first.contains("ε")) {
                            // 如果右边的FIRST集可以空，那么B就也有可能称为最后的那个符号，（A -> B空串），因此也进行更新
                            flag |= union(follow.get(B), follow.get(A));
                        }
                        // 再考虑不空的情况
                        first.remove("ε");
                        flag |= union(follow.get(B), first);
                    }
                }
            }
        }
        display(g);
    }

    private static void display(Grammar g) {
        Map<String, Set<String>> follow = g.getFollow();
        for (String key : follow.keySet()) {
            System.out.println(key + " : " + follow.get(key));
        }
        // System.out.println("--------------------------------------------------------");
    }

    static boolean union(Set<String> a, Set<String> b) {
        int aSize = a.size();
        a.addAll(b);
        return aSize != a.size();
    }
}
