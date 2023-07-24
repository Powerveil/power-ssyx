package com.power.ssyx.acl.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.power.ssyx.common.result.Result;
import com.power.ssyx.model.acl.Role;
import com.power.ssyx.vo.acl.RoleQueryVo;

/**
 * @author Powerveil
 * @Date 2023/7/24 19:53
 */
public interface RoleService extends IService<Role> {
    Result pageList(Integer current, Integer limit, RoleQueryVo roleQueryVo);
}
