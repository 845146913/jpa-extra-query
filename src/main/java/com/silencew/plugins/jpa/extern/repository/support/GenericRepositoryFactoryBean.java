package com.silencew.plugins.jpa.extern.repository.support;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.support.JpaRepositoryFactory;
import org.springframework.data.jpa.repository.support.JpaRepositoryFactoryBean;
import org.springframework.data.jpa.repository.support.JpaRepositoryImplementation;
import org.springframework.data.repository.core.RepositoryInformation;
import org.springframework.data.repository.core.RepositoryMetadata;
import org.springframework.data.repository.core.support.RepositoryFactorySupport;
import org.springframework.data.repository.query.QueryLookupStrategy;
import org.springframework.data.repository.query.QueryMethodEvaluationContextProvider;

import javax.persistence.EntityManager;
import java.util.Optional;

/**
 * Created by IntelliJ IDEA.
 * author: wangshuiping
 * date: 2021/1/20
 */
public class GenericRepositoryFactoryBean<R extends JpaRepository<T, ID>, T, ID> extends JpaRepositoryFactoryBean<R, T, ID> {
    public GenericRepositoryFactoryBean(Class<? extends R> repositoryInterface) {
        super(repositoryInterface);
    }

    @Override
    protected RepositoryFactorySupport createRepositoryFactory(EntityManager entityManager) {
        GenericRepositoryFactory<Object, Object> factory = new GenericRepositoryFactory<>(entityManager);
//        factory.addRepositoryProxyPostProcessor(new CustomRepositoryProxyPostProcessor());
        return factory;
    }

    private static class GenericRepositoryFactory<T, ID>
            extends JpaRepositoryFactory {
        private final EntityManager entityManager;

        public GenericRepositoryFactory(EntityManager entityManager) {
            super(entityManager);
            this.entityManager = entityManager;
        }

        @Override
        protected JpaRepositoryImplementation<?, ID> getTargetRepository(RepositoryInformation information, EntityManager entityManager) {
            return new GenericRepositoryImpl<>(information.getDomainType(), entityManager);
        }

        @Override
        protected Optional<QueryLookupStrategy> getQueryLookupStrategy(QueryLookupStrategy.Key key, QueryMethodEvaluationContextProvider evaluationContextProvider) {

            return Optional.of(CustomQueryLookupStrategy.create(entityManager, key, evaluationContextProvider));
        }

        @Override
        protected Class<?> getRepositoryBaseClass(RepositoryMetadata metadata) {
            return GenericRepositoryImpl.class;
        }
    }
}
