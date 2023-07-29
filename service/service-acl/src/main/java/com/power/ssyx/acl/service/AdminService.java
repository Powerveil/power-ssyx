package com.power.ssyx.acl.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
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

    IPage<Admin> selectAdminPage(Page<Admin> pageParam, AdminQueryVo adminQueryVo);

    Result get(Integer id);

    Result saveAdmin(Admin admin);

    Result updateAdminById(Admin admin);

    Result deleteAdminById(Integer id);

    Result deleteAdminByIds(List<Long> ids);
}
