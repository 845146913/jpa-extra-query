package com.silencew.plugins.jpa.extra.repository.support;

import com.silencew.plugins.jpa.extra.repository.GenericRepository;
import com.silencew.plugins.jpa.extra.repository.query.CriterionFilter;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * author: wangshuiping
 * date: 2021/1/20
 */
@Repository
public class GenericRepositoryImpl<T, ID> extends SimpleJpaRepository<T, ID> implements GenericRepository<T, ID> {

    @PersistenceContext
    private EntityManager em;

    private Class<T> domainClass;

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    public GenericRepositoryImpl(Class<T> domainClass, EntityManager em) {
        super(domainClass, em);
        this.em = em;
        this.domainClass = domainClass;
    }

    @Override
    public <DTO> List<DTO> findAll(List<CriterionFilter> dynamicFilter, Pageable pageable) {
        return null;
    }
}
