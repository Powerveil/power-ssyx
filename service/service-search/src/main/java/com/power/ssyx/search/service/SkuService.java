package com.power.ssyx.search.service;

import com.power.ssyx.common.result.Result;
import com.power.ssyx.model.search.SkuEs;
import com.power.ssyx.vo.search.SkuEsQueryVo;

import java.util.List;

/**
 * @author Powerveil
 * @Date 2023/8/19 17:30
 */
public interface SkuService {

    Result upperSku(Long skuId);

    Result lowerSku(Long skuId);

    List<SkuEs> findHotSkuList();

    Result search(Integer page, Integer limit, SkuEsQueryVo skuEsQueryVo);

    Boolean incrHotScore(Long skuId);
}
