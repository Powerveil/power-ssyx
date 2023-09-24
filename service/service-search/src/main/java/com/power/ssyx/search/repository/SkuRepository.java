package com.power.ssyx.search.repository;

import com.power.ssyx.model.search.SkuEs;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * @author Powerveil
 * @Date 2023/8/19 17:32
 */
public interface SkuRepository extends ElasticsearchRepository<SkuEs, Long> {

    // 获取爆款商品
    Page<SkuEs> findByOrderByHotScoreDesc(Pageable pageable);

    Page<SkuEs> findByCategoryIdAndWareId(Long categoryId, Long wareId, Pageable pageable);

    Page<SkuEs> findByCategoryIdAndWareIdAndKeywordContaining(Long categoryId, Long wareId, String keyword, Pageable pageable);

//    List<SkuEs> findByOrderHotScoreDesc(Pageable pageable);
}
