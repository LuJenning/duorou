package com.len.service;

import com.len.base.BaseService;
import com.len.entity.SysRole;
import com.len.util.LenResponse;

/**
 * @author zhuxiaomeng
 * @date 2017/12/19.
 * @email 154040976@qq.com
 */
public interface RoleService extends BaseService<SysRole, String> {



    LenResponse addRole(SysRole sysRole, String[] menus);

    LenResponse updateUser(SysRole role, String[] menus);

    LenResponse del(String id);
}
