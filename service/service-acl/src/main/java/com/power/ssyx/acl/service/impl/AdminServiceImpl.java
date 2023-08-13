package com.power.ssyx.acl.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.power.ssyx.acl.mapper.AdminMapper;
import com.power.ssyx.acl.service.AdminService;
import com.power.ssyx.common.result.Result;
import com.power.ssyx.common.result.ResultCodeEnum;
import com.power.ssyx.common.utils.MD5;
import com.power.ssyx.model.acl.Admin;
import com.power.ssyx.vo.acl.AdminQueryVo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Objects;

/**
 * @author Powerveil
 * @Date 2023/7/29 16:13
 */
@Service
public class AdminServiceImpl extends ServiceImpl<AdminMapper, Admin> implements AdminService {
    @Override
    public Result getPageList(Integer page, Integer limit, AdminQueryVo adminQueryVo) {
        Page<Admin> pageParam = new Page<>(page, limit);
        IPage<Admin> pageModel = this.selectAdminPage(pageParam, adminQueryVo);
        return Result.ok(pageModel);
    }

    @Override
    public IPage<Admin> selectAdminPage(Page<Admin> pageParam, AdminQueryVo adminQueryVo) {
        String name = adminQueryVo.getName();
        String username = adminQueryVo.getUsername();
        LambdaQueryWrapper<Admin> queryWrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(name)) {
            queryWrapper.like(Admin::getName, name);
            System.out.println(name);
        }
        if (StringUtils.hasText(username)) {
            queryWrapper.like(Admin::getUsername, username);
            System.out.println(name);
        }
//        queryWrapper.orderByAsc(Admin::getCreateTime);
        IPage<Admin> page = baseMapper.selectPage(pageParam, queryWrapper);

//        IPage<Admin> page = baseMapper.selectPage(pageParam, queryWrapper); // 执行分页查询
//        List<Admin> records = baseMapper.selectListByWrapper(queryWrapper); // 执行查询记录的方法
//        page.setRecords(records); // 将查询结果设置到分页对象中
        return page;
    }

    @Override
    public Result get(Integer id) {
        Admin admin = this.getById(id);
        return Result.ok(admin);
    }

    @Override
    public Result saveAdmin(Admin admin) {
        String password = admin.getPassword();

        String passwordMD5 = MD5.encrypt(password);
        admin.setPassword(passwordMD5);

        // 先查询用户是否存在
        String username = admin.getUsername();
        // 用户名不能为空
        if (!StringUtils.hasText(username)) {
            return Result.build(null, ResultCodeEnum.USERNAME_IS_BLANK);
        }

        LambdaQueryWrapper<Admin> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Admin::getUsername, username);
        // 用户名不能相同
        if (count(queryWrapper) > 0) {
            return Result.build(null, ResultCodeEnum.ADMIN_IS_EXIST);
        }

        if (this.save(admin)) {
            return Result.ok(null);
        }
        return Result.fail("添加用户失败");
    }

    @Override
    public Result updateAdminById(Admin admin) {
        if (Objects.isNull(admin.getId())) {
            return Result.build(null, ResultCodeEnum.ID_IS_NULL);
        }
        String password = admin.getPassword();

        String passwordMD5 = MD5.encrypt(password);
        admin.setPassword(passwordMD5);

        // 先查询用户是否存在
        String username = admin.getUsername();
        // 用户名不能为空
        if (!StringUtils.hasText(username)) {
            return Result.build(null, ResultCodeEnum.USERNAME_IS_BLANK);
        }

        LambdaQueryWrapper<Admin> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Admin::getUsername, username);
        // 用户名不能相同
        Admin one = getOne(queryWrapper);
        if (!Objects.isNull(one) && !one.getId().equals(admin.getId())) {
            return Result.build(null, ResultCodeEnum.ADMIN_IS_EXIST);
        }

        if (this.updateById(admin)) {
            return Result.ok(null);
        }
        return Result.fail("更新用户失败");
    }

    @Override
    public Result deleteAdminById(Integer id) {
        if (this.removeById(id)) {
            return Result.ok(null);
        }
        return Result.fail("删除用户失败");
    }

    @Override
    @Transactional(rollbackFor = {Exception.class})
    public Result deleteAdminByIds(List<Long> ids) {
        if (this.removeByIds(ids)) {
            return Result.ok(null);
        }
        return Result.fail("批量删除用户失败");
    }
}
