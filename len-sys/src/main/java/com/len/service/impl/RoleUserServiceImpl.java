package com.len.service.impl;

import com.len.base.BaseMapper;
import com.len.base.impl.BaseServiceImpl;
import com.len.entity.SysRoleUser;
import com.len.mapper.SysRoleUserMapper;
import com.len.service.RoleUserService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author zhuxiaomeng
 * @date 2017/12/21.
 * @email 154040976@qq.com
 */
@Service
public class RoleUserServiceImpl extends BaseServiceImpl<SysRoleUser,String> implements
    RoleUserService {
}
