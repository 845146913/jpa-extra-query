package com.silencew.plugins.jpa.extra.repository;

import com.silencew.plugins.jpa.extra.repository.query.CriterionFilter;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.List;

/**
 * @NoRepositoryBean filter componentScan
 * Created by IntelliJ IDEA.
 * author: wangshuiping
 * date: 2021/1/20
 */
@NoRepositoryBean
public interface GenericRepository<T, ID> extends JpaRepository<T, ID>, JpaSpecificationExecutor<T> {

    <DTO> List<DTO> findAll(List<CriterionFilter> dynamicFilter, Pageable pageable);
}
