package com.len.base;


import com.len.util.ReType;

import java.util.List;

/**
 * @author zhuxiaomeng
 * @date 2017/12/12.
 * @email 154040976@qq.com
 * mapper封装 crud
 */
public interface BaseMapper<T> extends com.baomidou.mybatisplus.core.mapper.BaseMapper<T> {

    public List<T> selectListByPage(T record);

    public ReType show(T t, int page, int limit);

    public ReType getList(T t, int page, int limit);

    public String showAll(T t);
}
