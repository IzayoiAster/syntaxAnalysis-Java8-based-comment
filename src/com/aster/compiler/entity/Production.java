package com.aster.compiler.entity;

import java.util.List;
import java.util.Objects;

/**
 * 产生式实体类
 */
public class Production {
    // 左部
    String left;
    // 右部
    // 之所以是string，是因为表面上存储了符号，实际上存储的是词法分析后符号对应的一串token
    List<String> right;

    public Production(String left, List<String> right) {
        this.left = left;
        this.right = right;
    }

    public String getLeft() {
        return left;
    }

    public void setLeft(String left) {
        this.left = left;
    }

    public List<String> getRight() {
        return right;
    }

    public void setRight(List<String> right) {
        this.right = right;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Production production = (Production) o;
        return Objects.equals(left, production.left) && Objects.equals(right, production.right);
    }

    @Override
    public int hashCode() {
        return Objects.hash(left, right);
    }
}
