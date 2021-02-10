package com.len.util;

import com.len.menu.LoginType;
import org.apache.shiro.authc.AuthenticationToken;

/**
 * @author zhuxiaomeng
 * @date 2018/8/19.
 * @email 154040976@qq.com
 */
public class JwtToken implements AuthenticationToken {

    private String token;
    private LoginType type;

    public JwtToken(String token,LoginType type) {
        this.token = token;
        this.type=type;
    }

    @Override
    public Object getPrincipal() {
        return token;
    }

    @Override
    public Object getCredentials() {
        return token;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public LoginType getType() {
        return type;
    }

    public void setType(LoginType type) {
        this.type = type;
    }
}
