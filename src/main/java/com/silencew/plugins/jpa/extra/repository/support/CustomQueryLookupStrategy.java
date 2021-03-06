package com.silencew.plugins.jpa.extra.repository.support;

import com.silencew.plugins.jpa.extra.converter.MapToEntityConverter;
import com.silencew.plugins.jpa.extra.repository.query.CriterionFilter;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.provider.PersistenceProvider;
import org.springframework.data.jpa.provider.QueryExtractor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.query.*;
import org.springframework.data.projection.ProjectionFactory;
import org.springframework.data.repository.core.NamedQueries;
import org.springframework.data.repository.core.RepositoryMetadata;
import org.springframework.data.repository.query.QueryLookupStrategy;
import org.springframework.data.repository.query.QueryMethodEvaluationContextProvider;
import org.springframework.data.repository.query.RepositoryQuery;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

import javax.persistence.EntityManager;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.stream.Stream;

public class CustomQueryLookupStrategy implements QueryLookupStrategy {
    private final EntityManager em;
    private QueryLookupStrategy queryLookupStrategy;
    private final QueryExtractor extractor;
    private EscapeCharacter escapeCharacter = EscapeCharacter.DEFAULT;
    private JpaQueryMethodFactory factory;

    public CustomQueryLookupStrategy(EntityManager em,
//                                     JpaQueryMethodFactory factory,
                                     Key key, QueryExtractor extractor, QueryMethodEvaluationContextProvider evaluationContextProvider) {
        Assert.notNull(em, "EntityManager must not be null!");
        this.em = em;
        this.extractor = extractor;
        this.factory = new DefaultJpaQueryMethodFactory(extractor);
        this.queryLookupStrategy = JpaQueryLookupStrategy.create(em, factory, key, evaluationContextProvider,
                escapeCharacter);
    }

    public static QueryLookupStrategy create(EntityManager em, @Nullable Key key, QueryMethodEvaluationContextProvider evaluationContextProvider) {
        QueryExtractor extractor = PersistenceProvider.fromEntityManager(em);

        return new CustomQueryLookupStrategy(em, key, extractor, evaluationContextProvider);
    }

    @Override
    public RepositoryQuery resolveQuery(Method method, RepositoryMetadata repositoryMetadata, ProjectionFactory projectionFactory, NamedQueries namedQueries) {
        Query query = method.getAnnotation(Query.class);
        if (query != null) {
            if (!query.nativeQuery() && needConvert(method, repositoryMetadata)) {
                DefaultConversionService sharedInstance = (DefaultConversionService) DefaultConversionService.getSharedInstance();
                sharedInstance.addConverter(new MapToEntityConverter());
            }
            boolean anyMatch = Stream.of(method.getParameterTypes())
                    .anyMatch(x -> x.isAssignableFrom(Specification.class) ||
                            (Iterable.class.isAssignableFrom(x) ?
                            ((Class<?>)((ParameterizedType)x.getGenericInterfaces()[0]).getActualTypeArguments()[0]).isAssignableFrom(CriterionFilter.class)
                            : false)
                            );
            if (query.nativeQuery() || anyMatch) {
                return new CriterionJpaQuery(factory.build(method, repositoryMetadata, projectionFactory), em);
            }
        }
        return queryLookupStrategy.resolveQuery(method, repositoryMetadata, projectionFactory, namedQueries);
    }

    private boolean needConvert(Method method, RepositoryMetadata repositoryMetadata) {
        Class<?> returnType = method.getReturnType();
        Class<?> domainType = repositoryMetadata.getDomainType();
        if (Iterable.class.isAssignableFrom(returnType)) {
            Type grt = method.getGenericReturnType();
            if (grt instanceof ParameterizedType) {
                ParameterizedType pt = (ParameterizedType) grt;
                Type ata = pt.getActualTypeArguments()[0];
                if (!(ata instanceof ParameterizedType)) {
                    Class<?> argument = (Class<?>) ata;
                    if (!domainType.isAssignableFrom(argument)) {
                        boolean assignableFrom = Map.class.isAssignableFrom(argument);
                        if (!assignableFrom) {
                            return true;
                        }
                    }
                }
            }
        } else {
            boolean isExtendDomainType = domainType.isAssignableFrom(returnType);
            if (!isExtendDomainType) {
                return true;
            }
        }
        return false;
    }

}
