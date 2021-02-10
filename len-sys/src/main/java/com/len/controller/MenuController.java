package com.len.controller;

import com.alibaba.fastjson.JSONArray;
import com.len.base.BaseController;
import com.len.core.annotation.Log;
import com.len.core.annotation.Log.LOG_TYPE;
import com.len.entity.SysMenu;
import com.len.service.MenuService;
import com.len.util.BeanUtil;
import com.len.util.LenResponse;
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

/**
 * @author zhuxiaomeng
 * @date 2017/12/13.
 * @email 154040976@qq.com
 * 菜单
 */
@RequestMapping("/menu")
@Controller
@Api(value = "菜单管理", tags = "菜单业务处理")
public class MenuController extends BaseController {

    @Autowired
    private MenuService menuService;

    /**
     * 展示tree
     *
     * @param model
     * @return
     */
    @ApiOperation(value = "/showMenu", httpMethod = "GET", notes = "展示菜单")
    @GetMapping(value = "showMenu")
    @RequiresPermissions("menu:show")
    public String showMenu(Model model) {
        JSONArray ja = menuService.getMenuJsonList();
        model.addAttribute("menus", ja.toJSONString());
        return "/system/menu/menuList";
    }

    @GetMapping(value = "showAddMenu")
    public String addMenu(Model model) {
        JSONArray ja = menuService.getMenuJsonList();
        model.addAttribute("menus", ja.toJSONString());
        return "/system/menu/add-menu";
    }

    @Log(desc = "添加菜单", type = LOG_TYPE.UPDATE)
    @ApiOperation(value = "/addMenu", httpMethod = "POST", notes = "添加菜单")
    @PostMapping(value = "addMenu")
    @ResponseBody
    public LenResponse addMenu(SysMenu sysMenu) {
        if (sysMenu == null) {
            return error("获取数据失败");
        }
        if (StringUtils.isEmpty(sysMenu.getPId())) {
            sysMenu.setPId(null);
        }
        if (StringUtils.isEmpty(sysMenu.getUrl())) {
            sysMenu.setUrl(null);
        }
        if (StringUtils.isEmpty(sysMenu.getPermission())) {
            sysMenu.setPermission(null);
        }
        if (sysMenu.getMenuType() == 2) {
            sysMenu.setMenuType((byte) 0);
        }
        menuService.save(sysMenu);
        return succ("添加成功");
    }

    @GetMapping(value = "showUpdateMenu")
    public String showUpdateMenu(Model model, String id) {
        SysMenu sysMenu = menuService.getById(id);
        JSONArray ja = menuService.getMenuJsonList();
        model.addAttribute("menus", ja.toJSONString());
        model.addAttribute("sysMenu", sysMenu);
        if (null != sysMenu.getPId()) {
            SysMenu pSysMenu = menuService.getById(sysMenu.getPId());
            model.addAttribute("pName", pSysMenu.getName());
        }
        return "/system/menu/update-menu";
    }


    @Log(desc = "更新菜单", type = LOG_TYPE.ADD)
    @PostMapping(value = "updateMenu")
    @ResponseBody
    public LenResponse updateMenu(SysMenu sysMenu) {
        SysMenu oldMenu = menuService.getById(sysMenu.getId());
        BeanUtil.copyNotNullBean(sysMenu, oldMenu);
        menuService.updateById(oldMenu);
        return succ("保存成功");
    }

    @Log(desc = "删除菜单", type = LOG_TYPE.DEL)
    @PostMapping("/menu-del")
    @ResponseBody
    public LenResponse del(String id) {
        return menuService.del(id);
    }

}
