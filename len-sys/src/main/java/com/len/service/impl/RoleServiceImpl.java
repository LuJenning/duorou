package com.len.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.len.base.impl.BaseServiceImpl;
import com.len.entity.SysRole;
import com.len.entity.SysRoleMenu;
import com.len.entity.SysRoleUser;
import com.len.mapper.SysRoleMapper;
import com.len.service.RoleMenuService;
import com.len.service.RoleService;
import com.len.service.RoleUserService;
import com.len.util.BeanUtil;
import com.len.util.LenResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author zhuxiaomeng
 * @date 2017/12/19.
 * @email 154040976@qq.com
 */
@Service
public class RoleServiceImpl extends BaseServiceImpl<SysRole, String> implements RoleService {

    @Autowired
    private SysRoleMapper roleMapper;

    @Autowired
    private RoleMenuService roleMenuService;

    @Autowired
    private RoleUserService roleUserService;


    @Override
    public LenResponse addRole(SysRole sysRole, String[] menus) {
        LenResponse j = new LenResponse();
        roleMapper.insert(sysRole);
        //操作role-menu data
        if (menus != null) {
            for (String menu : menus) {
                SysRoleMenu sysRoleMenu = new SysRoleMenu();
                sysRoleMenu.setRoleId(sysRole.getId());
                sysRoleMenu.setMenuId(menu);
                roleMenuService.save(sysRoleMenu);
            }
        }
        j.setMsg("保存成功");

        return j;
    }

    @Override
    public LenResponse updateUser(SysRole role, String[] menus) {
        LenResponse jsonUtil = new LenResponse();
        jsonUtil.setFlag(false);
        SysRole oldRole = roleMapper.selectById(role.getId());
        BeanUtil.copyNotNullBean(role, oldRole);
        roleMapper.updateById(oldRole);

        SysRoleMenu sysRoleMenu = new SysRoleMenu();
        sysRoleMenu.setRoleId(role.getId());
        List<SysRoleMenu> menuList = roleMenuService.selectByCondition(sysRoleMenu);
        for (SysRoleMenu sysRoleMenu1 : menuList) {
            roleMenuService.deleteByPrimaryKey(sysRoleMenu1);
        }
        if (menus != null) {
            for (String menu : menus) {
                sysRoleMenu.setMenuId(menu);
                roleMenuService.save(sysRoleMenu);
            }
        }
        jsonUtil.setFlag(true);
        jsonUtil.setMsg("修改成功");

        return jsonUtil;
    }

    @Override
    public LenResponse del(String id) {
        SysRoleUser sysRoleUser = new SysRoleUser();
        sysRoleUser.setRoleId(id);
        LenResponse j = new LenResponse();
        QueryWrapper<SysRoleUser> wrapper = new QueryWrapper<>(sysRoleUser);
        int count = roleUserService.count(wrapper);
        if (count > 0) {
            return LenResponse.error("已分配给用户，删除失败");
        }
        roleMapper.deleteById(id);
        j.setMsg("删除成功");
        return j;
    }
}
