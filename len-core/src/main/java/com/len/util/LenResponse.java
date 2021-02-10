package com.len.util;


import com.alibaba.fastjson.JSONObject;
import lombok.Data;

/**
 * @author zhuxiaomeng
 * @date 2017/12/15.
 * @email 154040976@qq.com
 * ajax 回执
 */
@Data
public class LenResponse {

    //默认成功
    private boolean flag = true;
    private String msg;
    private JSONObject josnObj;
    private Integer status;
    private Object data;

    public boolean isFlag() {
        return flag;
    }

    public void setFlag(boolean flag) {
        this.flag = flag;
    }

    public LenResponse() {
    }

    public LenResponse(boolean flag, String msg) {
        this.flag = flag;
        this.msg = msg;
    }

    public LenResponse(boolean flag, String msg, Integer status) {
        this.flag = flag;
        this.msg = msg;
        this.status = status;
    }

    /**
     * restful 返回
     */
    public static LenResponse error(String msg) {
        return new LenResponse(false, msg);
    }

    public static LenResponse sucess(String msg) {
        return new LenResponse(true, msg);
    }
}
