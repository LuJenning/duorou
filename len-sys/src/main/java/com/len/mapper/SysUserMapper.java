package com.len.mapper;

import com.len.entity.SysUser;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
@Mapper
public interface SysUserMapper extends com.len.base.BaseMapper<SysUser> {

    SysUser login(@Param("username") String username);

    int count();

    int add(SysUser user);

    int delById(String id);

    int checkUser(String username);

    /**
     * 更新密码
     *
     * @param user
     * @return
     */
    int rePass(SysUser user);

    List<SysUser> getUserByRoleId(@Param("roleId") String roleId);
}