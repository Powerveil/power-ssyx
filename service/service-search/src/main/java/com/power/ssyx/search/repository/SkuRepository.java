package com.power.ssyx.search.repository;

import com.power.ssyx.model.search.SkuEs;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * @author Powerveil
 * @Date 2023/8/19 17:32
 */
public interface SkuRepository extends ElasticsearchRepository<SkuEs, Long> {

}
