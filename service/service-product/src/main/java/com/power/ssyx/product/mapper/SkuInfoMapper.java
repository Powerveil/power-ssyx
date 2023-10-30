package com.power.ssyx.product.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.power.ssyx.model.product.SkuInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * @author power
 * @description 针对表【sku_info(sku信息)】的数据库操作Mapper
 * @createDate 2023-08-05 15:19:29
 * @Entity com.power.ssyx.product.domain.SkuInfo
 */
@Mapper
public interface SkuInfoMapper extends BaseMapper<SkuInfo> {

    int check(@Param("id") Long id, @Param("status") Integer status);

    int publish(@Param("id") Long id, @Param("status") Integer status);

    int isNewPerson(@Param("id") Long id, @Param("status") Integer status);

    // 解锁库存
    void unlockStock(@Param("skuId") Long skuId, @Param("skuNum") Integer skuNum);

    // 验证库存
    SkuInfo checkStock(@Param("skuId") Long skuId, @Param("skuNum") Integer skuNum);

    // 锁定库存:update
    Integer lockStock(@Param("skuId") Long skuId, @Param("skuNum") Integer skuNum);
}




