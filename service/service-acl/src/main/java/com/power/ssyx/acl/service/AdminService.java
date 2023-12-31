package com.power.ssyx.acl.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.power.ssyx.common.result.Result;
import com.power.ssyx.model.acl.Admin;
import com.power.ssyx.vo.acl.AdminQueryVo;

import java.util.List;

/**
 * @author Powerveil
 * @Date 2023/7/29 16:12
 */
public interface AdminService extends IService<Admin> {
    Result getPageList(Integer page, Integer limit, AdminQueryVo adminQueryVo);

    Result get(Long id);

    Result saveAdmin(Admin admin);

    Result updateAdminById(Admin admin);

    Result deleteAdminById(Long id);

    Result deleteAdminByIds(List<Long> ids);
}
