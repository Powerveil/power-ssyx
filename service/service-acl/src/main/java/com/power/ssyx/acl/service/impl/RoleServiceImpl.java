package com.power.ssyx.acl.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.power.ssyx.acl.mapper.RoleMapper;
import com.power.ssyx.acl.service.RoleService;
import com.power.ssyx.common.result.Result;
import com.power.ssyx.model.acl.Role;
import com.power.ssyx.vo.acl.RoleQueryVo;
import org.springframework.stereotype.Service;

/**
 * @author Powerveil
 * @Date 2023/7/24 19:54
 */
@Service
public class RoleServiceImpl extends ServiceImpl<RoleMapper, Role> implements RoleService {
    @Override
    public Result pageList(Integer current, Integer limit, RoleQueryVo roleQueryVo) {
        //1.创建page对象，传递当前页和每页记录数

        //2.调用service方法实现条件分页查询，返回分页对象
        return null;
    }
}
