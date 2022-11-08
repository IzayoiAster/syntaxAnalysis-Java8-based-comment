package com.aster.compiler.constructor;

import com.aster.compiler.entity.Grammar;
import com.aster.compiler.entity.Item;
import com.aster.compiler.entity.Production;

import java.util.*;

/**
 * 构造LR0项目集等
 */
public class LR0Constructor {

    /**
     * 计算项目集闭包
     *
     * @param g 文法
     * @param I 待计算闭包的项目集
     * @return 加入了等价项目的新的I
     */
    private static Set<Item> CLOSURE(Grammar g, List<Item> I) {
        Set<Item> closure = new HashSet<>(I);
        Queue<Item> queue = new LinkedList<>(closure);
        while (!queue.isEmpty()) {
            Item i = queue.poll();
            // 点在最右边，规约状态，打住
            if (i.getRight().size() == i.getDot()) {
                continue;
            }
            // 点右边是终结符，也打住
            String b = i.getRight().get(i.getDot());
            if (!g.getVN().contains(b)) {
                continue;
            }
            // 点右边是非终结符，找能拓展的
            for (Production p : g.getP()) {
                if (p.getLeft().equals(b)) {
                    Item item = new Item(p, 0);
                    if (!closure.contains(item)) {
                        queue.add(item);
                        closure.add(item);
                    }
                }
            }
        }
        return closure;
    }

    /**
     * 构造后继项目集闭包
     *
     * @param g 文法
     * @param I 项目集
     * @param X 文法符号
     * @return 项目集I对应于文法符号X的后继项目集闭包
     */
    public static Set<Item> GOTO(Grammar g, Set<Item> I, String X) {
        // J存储返回值
        Set<Item> J = new HashSet<>();
        // 枚举项目集I里面能接着动点的项目
        for (Item item : I) {
            if (item.getDot() >= item.getRight().size()) continue;
            String B = item.getRight().get(item.getDot());
            if (B.equals(X)) { // 点右边正好是文法符号X，即接收到文法符号X可以到后继项目集，那就加进去
                J.add(new Item(item.getLeft(), item.getRight(), item.getDot() + 1));
            }
        }
        return CLOSURE(g, new ArrayList<>(J));
    }

    /**
     * 构造规范LR(0)项目集
     *
     * @param g 文法
     */
    public static void constructLR0Collection(Grammar g) {
        for (String s : g.getS()) {
            g.getP().add(0, new Production("S'", new ArrayList<>(Arrays.asList(s))));
        }
        // 手动添加唯一的开始符号S'
        g.getVN().add("S'");

        g.setI(new ArrayList<>());
        g.setClosure(new ArrayList<>());
        Item i0 = new Item(g.getP().get(0), 0);
        g.getClosure().add(CLOSURE(g, new ArrayList<>(Arrays.asList(i0))));

        Set<String> VTN = new HashSet<String>(g.getVN()); // 拿到所有符号
        VTN.addAll(g.getVT());

        // 从起始的项目集i0开始
        Queue<Set<Item>> queue = new LinkedList<>(new ArrayList<>(Arrays.asList(g.getClosure().get(0))));
        while (!queue.isEmpty()) {
            // 取出队头
            Set<Item> i = queue.poll();
            // 尝试每一个符号能不能拓展后继项目集
            for (String X : VTN) {
                Set<Item> J = GOTO(g, i, X);
                if (J.isEmpty()) continue; // 不能，就尝试下一个符号
                boolean flag = false;
                for (Set<Item> k : g.getClosure()) {
                    if (k.equals(J)) {
                        flag = true;
                        break;
                    }
                }
                if (flag) continue; // 如果拓展到了已经有的项目集就去试着接受下一个符号
                queue.add(J); // 不然，又找到个新的项目集，放进队尾等着拓展
                g.getClosure().add(J);
            }
        }

        g.setStateCnt(g.getClosure().size());
        display(g);
    }

    private static void display(Grammar g) {
        int state = 0;
        for (Set<Item> i : g.getClosure()) {
            System.out.println("I(" + state++ + "):");
            for (Item item : i) {
                System.out.println(item);
            }
        }
    }

}
