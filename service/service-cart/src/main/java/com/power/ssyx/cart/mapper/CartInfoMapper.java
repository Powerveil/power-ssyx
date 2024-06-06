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
    // 清空购物车
    void deleteAllCartByUserId(@Param("userId") Long userId);

    // 修改sku选中状态 (如果skuIdList为null则是更新全部)
    void updateCheckStatus(@Param("userId") Long userId,
                               @Param("skuIdList") List<Long> skuIdList,
                               @Param("isChecked") Integer isChecked);
}
