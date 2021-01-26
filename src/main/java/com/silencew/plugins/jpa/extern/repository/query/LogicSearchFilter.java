package com.silencew.plugins.jpa.extern.repository.query;


/**
 * 特殊条件用来处理 AND、OR这些
 * @Author: wang
 * @Date: 2020/3/18 15:35
 */
public class LogicSearchFilter implements CriterionFilter {

    private final CriterionFilter[] criterion;
    private final Operator op;

    public LogicSearchFilter(Operator op, CriterionFilter... criterion) {
        this.criterion = criterion;
        this.op = op;
    }

    public CriterionFilter[] getCriterion() {
        return criterion;
    }

    public Operator getOp() {
        return op;
    }
}
