package com.aster.compiler.entity;

import com.aster.compiler.utils.Pair;

import java.util.*;

/**
 * 文法实体类
 */
public class Grammar {
    // 开始符合，非终结符集合，终结符集合
    private Set<String> S, VN, VT;
    // 产生式
    private List<Production> P;
    private List<Item> I;
    private Map<String, Set<String>> first, follow;
    private List<Map<String, Pair<String, Integer>>> actionTable;
    private List<Map<String, Integer>> gotoTable;
    private List<Set<Item>> closure;

    private Integer stateCnt;

    public Grammar() {
    }

    public Set<String> getS() {
        return S;
    }

    public void setS(Set<String> s) {
        S = s;
    }

    public Set<String> getVN() {
        return VN;
    }

    public void setVN(Set<String> VN) {
        this.VN = VN;
    }

    public Set<String> getVT() {
        return VT;
    }

    public void setVT(Set<String> VT) {
        this.VT = VT;
    }

    public List<Production> getP() {
        return P;
    }

    public void setP(List<Production> p) {
        P = p;
    }

    public List<Item> getI() {
        return I;
    }

    public void setI(List<Item> i) {
        I = i;
    }

    public Map<String, Set<String>> getFirst() {
        return first;
    }

    public void setFirst(Map<String, Set<String>> first) {
        this.first = first;
    }

    public Map<String, Set<String>> getFollow() {
        return follow;
    }

    public void setFollow(Map<String, Set<String>> follow) {
        this.follow = follow;
    }


    public List<Map<String, Pair<String, Integer>>> getActionTable() {
        return actionTable;
    }

    public void setActionTable(List<Map<String, Pair<String, Integer>>> actionTable) {
        this.actionTable = actionTable;
    }

    public List<Map<String, Integer>> getGotoTable() {
        return gotoTable;
    }

    public void setGotoTable(List<Map<String, Integer>> gotoTable) {
        this.gotoTable = gotoTable;
    }

    public List<Set<Item>> getClosure() {
        return closure;
    }

    public void setClosure(List<Set<Item>> closure) {
        this.closure = closure;
    }

    public Integer getStateCnt() {
        return stateCnt;
    }

    public void setStateCnt(Integer stateCnt) {
        this.stateCnt = stateCnt;
    }
}
