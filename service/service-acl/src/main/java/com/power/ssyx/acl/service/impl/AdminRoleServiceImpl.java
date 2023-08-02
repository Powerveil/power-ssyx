package com.power.ssyx.acl.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.power.ssyx.acl.mapper.AdminRoleMapper;
import com.power.ssyx.acl.service.AdminRoleService;
import com.power.ssyx.model.acl.AdminRole;
import org.springframework.stereotype.Service;

/**
 * @author Powerveil
 * @Date 2023/8/2 19:24
 */
@Service
public class AdminRoleServiceImpl extends ServiceImpl<AdminRoleMapper, AdminRole> implements AdminRoleService {
}
