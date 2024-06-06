package com.power.ssyx.cart.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.power.ssyx.model.order.CartInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @ClassName CartInfoMapper
 * @Description TODO(一句话描述该类的功能)
 * @Author Powerveil
 * @Date 2024/6/6 10:30
 * @Version 1.0
 */
@Mapper
public interface CartInfoMapper extends BaseMapper<CartInfo> {
    void deleteAllCartByUserId(@Param("userId") Long userId);

    void updateCheckStatus(@Param("userId") Long userId,
                           @Param("skuId") Long skuId,
                           @Param("isChecked") Integer isChecked);

    void updateChectStatusAll(@Param("userId") Long userId,
                              @Param("isChecked") Integer isChecked);

    void updateChectStatusList(@Param("userId") Long userId,
                               @Param("skuIdList") List<Long> skuIdList,
                               @Param("isChecked") Integer isChecked);
}
