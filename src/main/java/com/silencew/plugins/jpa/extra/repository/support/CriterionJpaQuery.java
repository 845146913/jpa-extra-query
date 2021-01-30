package com.silencew.plugins.jpa.extra.repository.support;

import org.springframework.data.jpa.repository.query.AbstractJpaQuery;
import org.springframework.data.jpa.repository.query.JpaParametersParameterAccessor;
import org.springframework.data.jpa.repository.query.JpaQueryMethod;

import javax.persistence.EntityManager;
import javax.persistence.Query;

public class CriterionJpaQuery extends AbstractJpaQuery {

    /**
     * Creates a new {@link AbstractJpaQuery} from the given {@link JpaQueryMethod}.
     *
     * @param method
     * @param em
     */
    public CriterionJpaQuery(JpaQueryMethod method, EntityManager em) {
        super(method, em);
    }

    @Override
    protected Query doCreateQuery(JpaParametersParameterAccessor accessor) {
        return null;
    }

    @Override
    protected Query doCreateCountQuery(JpaParametersParameterAccessor accessor) {
        return null;
    }
}
