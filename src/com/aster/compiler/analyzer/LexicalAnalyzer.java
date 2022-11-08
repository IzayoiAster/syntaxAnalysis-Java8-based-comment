package com.aster.compiler.analyzer;

import com.aster.compiler.entity.Token;

import java.util.*;

/**
 * 词法分析类
 */
public class LexicalAnalyzer {

    // 关键字
    static final Set<String> keywords = new HashSet<>(Arrays.asList("int", "float", "char", "double", "bool", "if", "while", "return", "else"));
    // 运算符
    static final Set<String> operators = new HashSet<>(Arrays.asList("+", "-", "*", "/", "=", "==", ">", "<", ">=", "<=", "&&", "||", "!"));
    // 界限符
    static final Set<String> separators = new HashSet<>(Arrays.asList("(", ")", "[", "]", "{", "}", ";", ",", " ", "."));

    /**
     * 词法分析
     *
     * @param input 源程序
     * @return 源程序对应的统一机内标示（以词法单元token的形式表示）
     */
    public static List<String> analyze(List<String> input) {
        String codeLine = init(input);
        System.out.println("源程序: \n" + codeLine + "\n");
        List<Token> tokens = new ArrayList<Token>();

        // 源程序输入指针
        int pos = 0;
        boolean flag = true;
        while (pos < codeLine.length()) {
            char ch = codeLine.charAt(pos);

            if (ch == ' ') {
                pos++;
                continue;
            }

            if (separators.contains(ch + "")) {
                tokens.add(new Token("界限符", 7, ch + ""));
                pos++;
                continue;
            }

            if (operators.contains(ch + "")) {
                // 判断是不是两位运算符
                if (pos + 1 < codeLine.length() && operators.contains(ch + "" + codeLine.charAt(pos + 1))) {
                    tokens.add(new Token("运算符", 6, ch + "" + codeLine.charAt(pos + 1)));
                    pos += 2;
                } else {
                    tokens.add(new Token("运算符", 6, ch + ""));
                    pos++;
                }
                continue;
            }

            if (Character.isDigit(ch)) {
                int start = pos;
                // 将数字读取完
                while (pos < codeLine.length() && Character.isDigit(codeLine.charAt(pos))) {
                    pos++;
                }
                if (pos == codeLine.length() || Character.isLetter(codeLine.charAt(pos)) || codeLine.charAt(pos) == '_' || codeLine.charAt(pos) == '.')
                    flag = false;
                // 截取出数字串
                String str = codeLine.substring(start, pos);
                if (str.contains(".")) {
                    tokens.add(new Token("浮点数", 3, str));
                } else {
                    tokens.add(new Token("整数", 2, str));
                }
                continue;
            }

            if (ch == '\'' || ch == '"') {
                int start = pos;
                pos++;
                // 将字符串读取完
                while (pos < codeLine.length() && codeLine.charAt(pos) != ch) {
                    pos++;
                }
                if (pos == codeLine.length()) flag = false;
                tokens.add(new Token("字符串", 4, codeLine.substring(start, pos)));
                pos++;
                continue;
            }

            if (Character.isLetter(ch) || ch == '_') {
                int start = pos;
                while (pos < codeLine.length() && (Character.isDigit(codeLine.charAt(pos)) || Character.isLetter(codeLine.charAt(pos)) || codeLine.charAt(pos) == '_')) {
                    pos++;
                }
                String temp = codeLine.substring(start, pos);
                if (keywords.contains(temp)) {
                    tokens.add(new Token("关键字", 6, temp));
                } else {
                    tokens.add(new Token("标识符", 1, temp));
                }
            }
        }

        for (Token token : tokens) {
            System.out.println(token);
        }

        List<String> result = new ArrayList<>();
        for (Token token : tokens) {
            switch (token.getType()) {
                case "关键字":
                case "界限符":
                case "运算符":
                    result.add(token.getToken());
                    break;
                case "整数":
                case "字符串":
                case "浮点数":
                    result.add("value");
                    break;
                case "标识符":
                    result.add("id");
                    break;
                default:
                    System.out.println("Error: " + token);
            }
        }

        return result;
    }

    /**
     * 源程序预处理
     *
     * @param input
     * @return
     */
    private static String init(List<String> input) {
        StringBuilder sb = new StringBuilder();
        for (String line : input) {
            line = line.trim();
            if (line.equals("")) continue;
            sb.append(line).append(" ");
        }
        return sb.toString();
    }

}
