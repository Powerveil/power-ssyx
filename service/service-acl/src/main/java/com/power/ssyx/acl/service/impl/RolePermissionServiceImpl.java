package com.power.ssyx.acl.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.power.ssyx.acl.mapper.RolePermissionMapper;
import com.power.ssyx.acl.service.RolePermissionService;
import com.power.ssyx.model.acl.RolePermission;
import org.springframework.stereotype.Service;

/**
 * @author Powerveil
 * @Date 2023/8/2 21:43
 */
@Service
public class RolePermissionServiceImpl extends ServiceImpl<RolePermissionMapper, RolePermission> implements RolePermissionService {
}
