package com.len.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.len.entity.SysRole;
import com.len.entity.SysRoleUser;
import com.len.entity.SysUser;
import com.len.service.RoleService;
import com.len.service.RoleUserService;
import com.len.service.SysUserService;
import com.len.util.JWTUtil;
import com.len.util.LenResponse;
import com.len.util.Md5Util;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authc.UnknownAccountException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author zhuxiaomeng
 * @date 2018/7/25.
 * @email 154040976@qq.com
 */
@CrossOrigin
@RestController
@RequestMapping("/")
@Slf4j
public class AuthController {

    private static final String INIT_BLOG_ROLE = "blogAdmin";

    @Autowired
    private SysUserService sysUserService;

    @Autowired
    private RoleService roleService;

    @Autowired
    private RoleUserService roleUserService;

    @Value("${len-blog.roles}")
    private List<String> roles;


    @ApiOperation(value = "/blogLogin", httpMethod = "POST", notes = "登录method")
    @PostMapping(value = "/blogLogin")
    public LenResponse blogLogin(SysUser user) {
        if (StringUtils.isEmpty(user.getUsername()) || StringUtils.isEmpty(user.getPassword())) {
            throw new UnknownAccountException("用户名或密码不能为空");
        }
        String pass = user.getPassword();
        user.setPassword(null);
        QueryWrapper<SysUser> userQueryWrapper = new QueryWrapper<>(user);
        SysUser sysUser = sysUserService.getOne(userQueryWrapper);
        if (sysUser == null) {
            throw new UnknownAccountException("用户名或密码错误");
        }
        String md5 = Md5Util.getMD5(pass, sysUser.getUsername());
        if (!md5.equals(sysUser.getPassword())) {
            throw new UnknownAccountException("用户名或密码错误");
        }

        QueryWrapper<SysRoleUser> roleUserQueryWrapper = new QueryWrapper<>();
        roleUserQueryWrapper.eq("user_id", sysUser.getId());
        List<SysRoleUser> sysRoleUsers = roleUserService.list(roleUserQueryWrapper);

        if (sysRoleUsers.isEmpty()) {
            throw new UnknownAccountException("权限不足");
        }
        List<String> roleList = sysRoleUsers
                .stream()
                .map(SysRoleUser::getRoleId)
                .collect(Collectors.toList());

        QueryWrapper<SysRole> roleQueryWrapper = new QueryWrapper<>();
        roleQueryWrapper.in("id", roleList);
        List<SysRole> sysRoles = roleService.list(roleQueryWrapper);
        if (roles == null || roles.isEmpty()) {
            log.error("not init blog roles!");
            roles.add(INIT_BLOG_ROLE);
        }
        long isBlogAdmin = sysRoles.stream().filter(s -> roles.contains(s.getRoleName())).count();
        if (isBlogAdmin == 0) {
            throw new UnknownAccountException("权限不足");
        }
        List<String> roleNames = sysRoles
                .stream()
                .map(SysRole::getRoleName)
                .collect(Collectors.toList());

        return new LenResponse(true, JWTUtil.sign(sysUser.getUsername(), sysUser.getId(), roleNames, sysUser.getPassword()), 200);
    }
}
