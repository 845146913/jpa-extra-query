package com.silencew.plugins.jpa.extra.repository.query;

import java.util.Arrays;

/**
 * 条件构造类
 *
 * @Author: wang
 * @Date: 2020/3/19 9:47
 */
public interface CriterionBuilder {


    /**
     * 等于
     *
     * @param fieldName
     * @param value
     * @return
     */
    static SearchFilter eq(String fieldName, Object value) {
        return new SearchFilter(fieldName, CriterionFilter.Operator.EQ, value);
    }

    /**
     * 不等于
     *
     * @param fieldName
     * @param value
     * @return
     */
    static SearchFilter ne(String fieldName, Object value) {
        return new SearchFilter(fieldName, CriterionFilter.Operator.NE, value);
    }

    /**
     * like后模糊匹配【能利用索引】
     *
     * @param fieldName
     * @param value
     * @return
     */
    static SearchFilter like(String fieldName, Object value) {
        return new SearchFilter(fieldName, CriterionFilter.Operator.LIKE, value + "%");
    }

    /**
     * like全模糊匹配【不能利用索引】,建议少用
     *
     * @param fieldName
     * @param value
     * @return
     */
    static SearchFilter likeAll(String fieldName, Object value) {
        return new SearchFilter(fieldName, CriterionFilter.Operator.LIKE, "%" + value + "%");
    }

    /**
     * 大于
     *
     * @param fieldName
     * @param value
     * @return
     */
    static SearchFilter gt(String fieldName, Object value) {
        return new SearchFilter(fieldName, CriterionFilter.Operator.GT, value);
    }

    /**
     * 大于等于
     *
     * @param fieldName
     * @param value
     * @return
     */
    static SearchFilter gte(String fieldName, Object value) {
        return new SearchFilter(fieldName, CriterionFilter.Operator.GTE, value);
    }

    /**
     * 小于
     *
     * @param fieldName
     * @param value
     * @return
     */
    static SearchFilter lt(String fieldName, Object value) {
        return new SearchFilter(fieldName, CriterionFilter.Operator.LT, value);
    }

    /**
     * 小于等于
     *
     * @param fieldName
     * @param value
     * @return
     */
    static SearchFilter lte(String fieldName, Object value) {
        return new SearchFilter(fieldName, CriterionFilter.Operator.LTE, value);
    }

    /**
     * between操作
     *
     * @param fieldName
     * @param x
     * @param y
     * @return
     */
    static SearchFilter between(String fieldName, Object x, Object y) {
        return new SearchFilter(fieldName, CriterionFilter.Operator.BETWEEN, Arrays.asList(x, y));
    }

    /**
     * 包含于
     *
     * @param fieldName
     * @param values
     * @return
     */
    static SearchFilter in(String fieldName, Object... values) {
        return new SearchFilter(fieldName, CriterionFilter.Operator.IN, Arrays.asList(values));
    }

    /**
     * 不包含于
     *
     * @param fieldName
     * @param values
     * @return
     */
    static SearchFilter notIn(String fieldName, Object... values) {
        return new SearchFilter(fieldName, CriterionFilter.Operator.NOTIN, Arrays.asList(values));
    }

    /**
     * 复杂and
     *
     * @param filters
     * @return
     */
    static LogicSearchFilter and(CriterionFilter... filters) {
        return new LogicSearchFilter(CriterionFilter.Operator.AND, filters);
    }

    /**
     * or
     *
     * @param filters
     * @return
     */
    static LogicSearchFilter or(CriterionFilter... filters) {
        return new LogicSearchFilter(CriterionFilter.Operator.OR, filters);
    }
}
