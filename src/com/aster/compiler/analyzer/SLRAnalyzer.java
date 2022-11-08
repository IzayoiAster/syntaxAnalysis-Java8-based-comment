package com.aster.compiler.analyzer;

import com.aster.compiler.entity.Grammar;
import com.aster.compiler.utils.Pair;
import com.aster.compiler.entity.Production;

import java.util.List;
import java.util.Stack;

/**
 * SLR语法分析类
 */
public class SLRAnalyzer {
    /**
     * 语法分析
     * @param g 文法
     * @param input 转为了机内表示（token序列）的源程序
     */
    public static void analyze(Grammar g, List<String> input) {
        // 状态栈
        Stack<Integer> stateStack = new Stack<>();
        // 符号栈
        Stack<String> symbolStack = new Stack<>();
        // 初状态为0，结束符入栈
        stateStack.push(0);
        symbolStack.push("$");
        input.add("#");

        int pos = 0; // 输入指针
        int iter = 0; // 记录迭代次数
        while (true) {
            System.out.println("Iteration " + iter + ":");
            System.out.println("状态栈: " + stateStack);
            System.out.println("符号栈: " + symbolStack);
            System.out.print("剩余输入: ");
            for (String s : input.subList(pos, input.size())) {
                System.out.print(s + " ");
            }
            System.out.println();
            iter++;

            // 取出栈顶状态和栈顶符号
            int state = stateStack.peek();
            String symbol = input.get(pos);
            // 对应查找SLR分析表
            if (g.getActionTable().get(state).containsKey(symbol)) {
                Pair<String, Integer> action = g.getActionTable().get(state).get(symbol);
                if (action.getFirst().equals("S")) { // 移入
                    stateStack.push(action.getSecond());  // 转移到对应状态
                    symbolStack.push(symbol); // 对应符号入栈
                    pos++; // 输入指针右移
                } else if (action.getFirst().equals("R")) { // 规约
                    int index = action.getSecond(); // 查表获取此时的规约根据第几条产生式
                    Production p = g.getP().get(index);
                    for (int i = 0; i < p.getRight().size(); i++) { // 符号与其状态同步出栈
                        stateStack.pop();
                        symbolStack.pop();
                    }
                    state = stateStack.peek();
                    symbol = p.getLeft(); // 获取规约得到的左部
                    stateStack.push(g.getGotoTable().get(state).get(symbol)); // 将新的符号（左部）与状态入栈
                    symbolStack.push(symbol);
                } else if (action.getFirst().equals("Acc")) { // accept
                    System.out.println("\naccecpt");
                    break;
                }
            } else {
                System.out.println("\nerror");
                break;
            }
        }
    }
}
