package com.power.ssyx.sys.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.power.ssyx.common.result.Result;
import com.power.ssyx.model.sys.Ware;
import com.power.ssyx.vo.product.WareQueryVo;

import java.util.List;

/**
 * @author power
 * @description 针对表【ware(仓库表)】的数据库操作Service
 * @createDate 2023-08-03 20:48:29
 */
public interface WareService extends IService<Ware> {

    Result getPageList(Integer page, Integer limit, WareQueryVo wareQueryVo);

    Result findAllList();

    // 添加仓库
    Result saveWare(Ware regionWare);

    // 删除仓库 removeById
    Result removeWareById(Long id);


    // 批量删除仓库
    Result deleteWareByIds(List<Long> ids);

    // 根据id查询仓库
    Result get(Long id);

    // 修改仓库
    Result updateWareById(Ware ware);
}
