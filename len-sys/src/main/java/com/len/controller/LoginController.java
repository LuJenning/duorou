package com.len.controller;

import com.len.core.annotation.Log;
import com.len.core.shiro.Principal;
import com.len.entity.SysUser;
import com.len.menu.LoginType;
import com.len.service.SysUserService;
import com.len.util.CustomUsernamePasswordToken;
import com.len.util.VerifyCodeUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.ExcessiveAttemptsException;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * @author zhuxiaomeng
 * @date 2017/12/4.
 * @email 154040976@qq.com
 * 登录、退出页面
 */
@Controller
@Slf4j
@Api(value = "登录业务", tags = "登录校验处理")
public class LoginController {

    @Autowired
    SysUserService userService;
    private static final String CODE_ERROR = "code.error";
    private static final Long TWO_WEEK = 1000 * 60 * 60 * 24 * 14L;

    @GetMapping(value = "")
    public String login() {
        return toLogin();
    }

    @GetMapping(value = "goLogin")
    public String goLogin(Model model) {
        Subject sub = SecurityUtils.getSubject();
        if (sub.isAuthenticated()) {
            return "/main/main";
        } else {
            model.addAttribute("message", "请重新登录");
            return "/login2";
        }
    }

    @GetMapping(value = "/login")
    public String toLogin() {
        Subject sub = SecurityUtils.getSubject();
        if (sub.isAuthenticated() || sub.isRemembered()) {
            return "/main/main";
        }
        return "/login2";
    }

    /**
     * 登录动作
     *
     * @param user
     * @param model
     * @param rememberMe
     * @return
     */
    @ApiOperation(value = "/login", httpMethod = "POST", notes = "登录method")
    @PostMapping("/login")
    public String login(SysUser user, Model model, String rememberMe, HttpServletRequest request) {
        String codeMsg = (String) request.getAttribute("shiroLoginFailure");
        if (CODE_ERROR.equals(codeMsg)) {
            model.addAttribute("message", "验证码错误");
            return "/login2";
        }
        CustomUsernamePasswordToken token = new CustomUsernamePasswordToken(user.getUsername().trim(),
                user.getPassword(), LoginType.SYS);
        Subject subject = Principal.getSubject();
        String msg = null;
        try {
            subject.login(token);
            if (subject.isAuthenticated()) {
                String isRemember = request.getParameter("isRemember");
                if (!StringUtils.isEmpty(isRemember)) {
                    if ("true".equals(isRemember)) {
                        subject.getSession().setTimeout(TWO_WEEK);
                    }
                }

                return "redirect:/main";
            }
        } catch (UnknownAccountException | IncorrectCredentialsException e) {
            msg = "用户名/密码错误";
        } catch (ExcessiveAttemptsException e) {
            msg = "登录失败多次，账户锁定10分钟";
        }
        if (msg != null) {
            model.addAttribute("message", msg);
        }
        return "/login2";
    }

    @GetMapping("/main")
    public String main() {
        return "main/main";
    }

    @Log(desc = "用户退出平台")
    @GetMapping(value = "/logout")
    public String logout() {
        Subject sub = SecurityUtils.getSubject();
        sub.logout();
        return "/login2";
    }


    @GetMapping(value = "/getCode")
    public void getYzm(HttpServletResponse response, HttpServletRequest request) {
        try {
            response.setHeader("Pragma", "No-cache");
            response.setHeader("Cache-Control", "no-cache");
            response.setDateHeader("Expires", 0);
            response.setContentType("image/jpg");

            //生成随机字串
            String verifyCode = VerifyCodeUtils.generateVerifyCode(4);
            log.info("verifyCode:{}", verifyCode);
            //存入会话session
            HttpSession session = request.getSession(true);
            session.setAttribute("_code", verifyCode.toLowerCase());
            //生成图片
            int w = 100, h = 35;
            VerifyCodeUtils.outputImage(w, h, response.getOutputStream(), verifyCode);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
