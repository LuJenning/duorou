package com.len.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.len.base.BaseController;
import com.len.core.annotation.Log;
import com.len.core.annotation.Log.LOG_TYPE;
import com.len.core.quartz.JobTask;
import com.len.entity.SysUser;
import com.len.service.RoleUserService;
import com.len.service.SysUserService;
import com.len.util.Checkbox;
import com.len.util.LenResponse;
import com.len.util.Md5Util;
import com.len.util.ReType;
import com.len.util.UploadUtil;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author zhuxiaomeng
 * @date 2017/12/6.
 * @email 154040976@qq.com
 * 用户管理
 */
//@Api(value="user")
@Controller
@RequestMapping(value = "/user")
@Api(value = "用户管理", tags = "用户管理业务")
public class UserController extends BaseController {

    //private static final Logger

    @Autowired
    SysUserService userService;

    @Autowired
    RoleUserService roleUserService;

    @Autowired
    JobTask task;

    @GetMapping(value = "mainTest")
    @RequiresPermissions("user:show")
    public String showTest() {
        return "system/user/mainTest";
    }

    @GetMapping(value = "showUser")
    @RequiresPermissions("user:show")
    public String showUser() {
        return "/system/user/userList";
    }

    @GetMapping(value = "showUserList")
    @ResponseBody
    @RequiresPermissions("user:show")
    public ReType showUser(SysUser user, String page, String limit) {
        return userService.show(user, Integer.valueOf(page), Integer.valueOf(limit));
    }

    @ApiOperation(value = "/listByRoleId", httpMethod = "GET", notes = "展示角色")
    @GetMapping(value = "listByRoleId")
    @ResponseBody
    @RequiresPermissions("user:show")
    public String showUser(String roleId, int page, int limit) {
        JSONObject returnValue = new JSONObject();
        Page<Object> startPage = PageHelper.startPage(page, limit);
        List<SysUser> users = userService.getUserByRoleId(roleId);
        returnValue.put("users", users);
        returnValue.put("totals", startPage.getTotal());
        return JSON.toJSONString(returnValue);
    }


    @GetMapping(value = "showAddUser")
    public String goAddUser(Model model) {
        List<Checkbox> checkboxList = userService.getUserRoleByJson(null);
        model.addAttribute("boxJson", checkboxList);
        return "/system/user/add-user";
    }

    @ApiOperation(value = "/addUser", httpMethod = "POST", notes = "添加用户")
    @Log(desc = "添加用户")
    @PostMapping(value = "addUser")
    @ResponseBody
    public LenResponse addUser(SysUser user, String[] role) {
        if (user == null) {
            return error("获取数据失败");
        }
        if (StringUtils.isBlank(user.getUsername())) {
            return error("用户名不能为空");
        }
        if (StringUtils.isBlank(user.getPassword())) {
            return error("密码不能为空");
        }
        if (role == null) {
            return error("请选择角色");
        }
        int result = userService.checkUser(user.getUsername());
        if (result > 0) {
            return error("用户名已存在");
        }
        userService.add(user, Arrays.asList(role));

        return succ();
    }

    @GetMapping(value = "updateUser")
    public String goUpdateUser(String id, Model model, boolean detail) {
        if (StringUtils.isNotEmpty(id)) {
            //用户-角色
            List<Checkbox> checkboxList = userService.getUserRoleByJson(id);
            SysUser user = userService.getById(id);
            model.addAttribute("user", user);
            model.addAttribute("boxJson", checkboxList);
        }
        model.addAttribute("detail", detail);
        return "system/user/update-user";
    }


    @ApiOperation(value = "/updateUser", httpMethod = "POST", notes = "更新用户")
    @Log(desc = "更新用户", type = LOG_TYPE.UPDATE)
    @PostMapping(value = "updateUser")
    @ResponseBody
    public LenResponse updateUser(SysUser user, String[] role) {
        if (user == null) {
            return error("获取数据失败");
        }
        List<String> roles = new ArrayList<>();
        if (role != null) {
            roles = Arrays.asList(role);
        }
        userService.updateUser(user, roles);
        return succ("修改成功");
    }

    @Log(desc = "删除用户", type = LOG_TYPE.DEL)
    @ApiOperation(value = "/del", httpMethod = "POST", notes = "删除用户")
    @PostMapping(value = "/del")
    @ResponseBody
    @RequiresPermissions("user:del")
    public LenResponse del(String id, boolean flag) {
        return userService.delById(id, flag);
    }

    @GetMapping(value = "goRePass")
    public String goRePass(String id, Model model) {
        if (StringUtils.isEmpty(id)) {
            return "获取账户信息失败";
        }
        SysUser user = userService.getById(id);
        model.addAttribute("user", user);
        return "/system/user/re-pass";
    }

    /**
     * 修改密码
     *
     * @param id
     * @param newPwd
     * @return
     */
    @Log(desc = "修改密码", type = LOG_TYPE.UPDATE)
    @PostMapping(value = "rePass")
    @ResponseBody
    @RequiresPermissions("user:repass")
    public LenResponse rePass(String id, String newPwd) {
        boolean flag = StringUtils.isEmpty(id) || StringUtils.isEmpty(newPwd);
        if (flag) {
            return error("获取数据失败，修改失败");
        }
        SysUser user = userService.getById(id);
        newPwd = Md5Util.getMD5(newPwd, user.getUsername());
        if (newPwd.equals(user.getPassword())) {
            return resp(false, "新密码不能与旧密码相同");
        }
        user.setPassword(newPwd);
        userService.rePass(user);

        return succ("修改成功");
    }

    @Autowired
    UploadUtil uploadUtil;

    /**
     * 头像上传 目前首先相对路径
     */
    @PostMapping(value = "upload")
    @ResponseBody
    public LenResponse imgUpload(HttpServletRequest req, @RequestParam("file") MultipartFile file) {
        String fileName = uploadUtil.upload(file);
        LenResponse j = new LenResponse();
        j.setMsg(fileName);
        return j;
    }

    /**
     * 验证用户名是否存在
     */
    @GetMapping(value = "checkUser")
    @ResponseBody
    public LenResponse checkUser(String uname) {
        if (StringUtils.isEmpty(uname)) {
            return error("获取数据失败");
        }
        int result = userService.checkUser(uname);
        if (result > 0) {
            return error("用户名已存在");
        }
        return succ();
    }


}
