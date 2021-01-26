package com.silencew.plugins.jpa.extern.repository.query;


/**
 * @Author: wang
 * @Date: 2020/3/18 15:48
 */
public interface CriterionFilter {

    enum Operator {
        EQ("="), NE("!="), LIKE("like"), GT(">"), LT("<"),
        GTE(">="), LTE("<="), AND("and"), OR("or"), IN("in"),
        NOTIN("not in"), BETWEEN("between");

        private String value;

        Operator(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }
}
