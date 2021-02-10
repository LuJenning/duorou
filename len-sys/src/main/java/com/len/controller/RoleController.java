package com.len.controller;

import com.alibaba.fastjson.JSONArray;
import com.len.base.BaseController;
import com.len.core.annotation.Log;
import com.len.core.annotation.Log.LOG_TYPE;
import com.len.entity.SysRole;
import com.len.service.MenuService;
import com.len.service.RoleMenuService;
import com.len.service.RoleService;
import com.len.util.LenResponse;
import com.len.util.ReType;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * @author zhuxiaomeng
 * @date 2017/12/19.
 * @email 154040976@qq.com
 * 角色业务
 */
@Controller
@RequestMapping(value = "/role")
@Api(value = "用户角色管理", tags = "角色业务处理")
public class RoleController extends BaseController {

    @Autowired
    private RoleService roleService;

    @Autowired
    private MenuService menuService;

    @Autowired
    private RoleMenuService roleMenuService;

    @GetMapping(value = "showRole")
    @RequiresPermissions(value = "role:show")
    public String showRole() {
        return "/system/role/roleList";
    }

    @ApiOperation(value = "/showRoleList", httpMethod = "GET", notes = "展示角色")
    @GetMapping(value = "showRoleList")
    @ResponseBody
    @RequiresPermissions("role:show")
    public ReType showRoleList(SysRole role, String page, String limit) {
        return roleService.show(role, Integer.valueOf(page), Integer.valueOf(limit));
    }

    @ApiOperation(value = "/showaLLRoleList", httpMethod = "GET", notes = "展示角色")
    @GetMapping(value = "showaLLRoleList")
    @ResponseBody
    @RequiresPermissions("role:show")
    public List<SysRole> showRoleList(SysRole role) {
        return roleService.showAll(role);
    }


    @GetMapping(value = "showAddRole")
    public String goAddRole(Model model) {
        JSONArray jsonArray = menuService.getTreeUtil(null);
        model.addAttribute("menus", jsonArray.toJSONString());
        return "/system/role/add-role";
    }

    @ApiOperation(value = "/addRole", httpMethod = "POST", notes = "添加角色")
    @Log(desc = "添加角色")
    @PostMapping(value = "addRole")
    @ResponseBody
    public LenResponse addRole(SysRole sysRole, String[] menus) {
        if (StringUtils.isEmpty(sysRole.getRoleName())) {
            return error("角色名称不能为空");
        }
        return roleService.addRole(sysRole, menus);
    }

    @GetMapping(value = "updateRole")
    public String updateRole(String id, Model model, boolean detail) {
        if (StringUtils.isNotEmpty(id)) {
            SysRole role = roleService.getById(id);
            model.addAttribute("role", role);
            JSONArray jsonArray = menuService.getTreeUtil(id);
            model.addAttribute("menus", jsonArray.toJSONString());
        }
        model.addAttribute("detail", detail);
        return "system/role/update-role";
    }

    @ApiOperation(value = "/updateRole", httpMethod = "POST", notes = "更新角色")
    @Log(desc = "更新角色")
    @PostMapping(value = "updateRole")
    @ResponseBody
    public LenResponse updateUser(SysRole role, String[] menus) {
        if (role == null) {
            return error("获取数据失败");
        }
        return roleService.updateUser(role, menus);
    }

    @ApiOperation(value = "/del", httpMethod = "POST", notes = "删除角色")
    @Log(desc = "删除角色", type = LOG_TYPE.DEL)
    @PostMapping(value = "del")
    @ResponseBody
    @RequiresPermissions("role:del")
    public LenResponse del(String id) {
        if (StringUtils.isEmpty(id)) {
            return error("获取数据失败");
        }
        return roleService.del(id);
    }

}
