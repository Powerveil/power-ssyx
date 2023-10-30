package com.power.ssyx.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.power.ssyx.common.result.Result;
import com.power.ssyx.model.product.SkuInfo;
import com.power.ssyx.vo.product.SkuInfoQueryVo;
import com.power.ssyx.vo.product.SkuInfoVo;
import com.power.ssyx.vo.product.SkuStockLockVo;

import java.util.List;

/**
 * @author power
 * @description 针对表【sku_info(sku信息)】的数据库操作Service
 * @createDate 2023-08-05 15:19:29
 */
public interface SkuInfoService extends IService<SkuInfo> {


    Result getPageList(Integer page, Integer limit, SkuInfoQueryVo skuInfoQueryVo);

    Result get(Long id);

    Result saveSkuInfo(SkuInfoVo skuInfoVo);

    Result updateSkuInfoById(SkuInfoVo skuInfoVo);

    Result deleteSkuInfoById(Long id);

    Result deleteSkuInfoByIds(List<Long> ids);

    boolean saveSkuOthers(SkuInfoVo skuInfoVo);

    boolean deleteSkuOthersBySkuId(Long skuId);

    boolean deleteSkuOthersBySkuIds(List<Long> skuIds);

    Result check(Long id, Integer status);

    Result publish(Long skuId, Integer status);

    Result isNewPerson(Long id, Integer status);

    List<SkuInfo> getSkuListByIds(List<Long> ids);

    List<SkuInfo> findSkuInfoByKeyword(String keyword);

    List<SkuInfo> findNewPersonSkuInfoList();

    SkuInfoVo getSkuInfoVo(Long skuId);

    // 验证和锁定库存
    Boolean checkAndLock(List<SkuStockLockVo> skuStockLockVoList, String orderNo);
}
