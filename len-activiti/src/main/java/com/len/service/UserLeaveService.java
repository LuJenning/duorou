package com.len.service;

import com.len.base.BaseService;
import com.len.entity.UserLeave;

import java.util.List;

/**
 * @author zhuxiaomeng
 * @date 2018/1/21.
 * @email 154040976@qq.com
 */
public interface UserLeaveService extends BaseService<UserLeave,String> {

    public List<UserLeave> selectListByPage(UserLeave record);
}
