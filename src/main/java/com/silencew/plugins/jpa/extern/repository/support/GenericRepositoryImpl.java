package com.silencew.plugins.jpa.extern.repository.support;

import com.silencew.plugins.jpa.extern.repository.GenericRepository;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;

import javax.persistence.EntityManager;

/**
 * Created by IntelliJ IDEA.
 * author: wangshuiping
 * date: 2021/1/20
 */
public class GenericRepositoryImpl<T, ID> extends SimpleJpaRepository<T, ID> implements GenericRepository<T, ID> {
    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    public GenericRepositoryImpl(Class<T> domainClass, EntityManager em) {
        super(domainClass, em);
    }
}
