package com.power.ssyx.search.controller;

import com.power.ssyx.common.result.Result;
import com.power.ssyx.model.search.SkuEs;
import com.power.ssyx.search.service.SkuService;
import com.power.ssyx.vo.search.SkuEsQueryVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author Powerveil
 * @Date 2023/8/19 17:30
 */
@RestController
@RequestMapping("/api/search/sku")
public class SkuApiController {

    @Autowired
    private SkuService skuService;

    @GetMapping("/inner/upperSku/{skuId}")
    public Result upperSku(@PathVariable("skuId") Long skuId) {
        return skuService.upperSku(skuId);
    }

    @GetMapping("/inner/lowerSku/{skuId}")
    public Result lowerSku(@PathVariable("skuId") Long skuId) {
        return skuService.lowerSku(skuId);
    }

    @GetMapping("/inner/findHotSkuList")
    public List<SkuEs> findHotSkuList() {
        return skuService.findHotSkuList();
    }

    // 查询分类商品
    @GetMapping("/{page}/{limit}")
    public Result listSku(@PathVariable("page") Integer page,
                          @PathVariable("limit") Integer limit,
                          SkuEsQueryVo skuEsQueryVo) {
        return skuService.search(page, limit, skuEsQueryVo);
    }

    // 更新商品热度
    @GetMapping("/inner/incrHotScore/{skuId}")
    public Boolean incrHotScore(@PathVariable("skuId") Long skuId) {
        return skuService.incrHotScore(skuId);
    }
}
