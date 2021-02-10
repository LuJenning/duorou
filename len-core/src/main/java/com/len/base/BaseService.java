package com.len.base;

import com.baomidou.mybatisplus.extension.service.IService;
import com.len.util.ReType;

import java.io.Serializable;
import java.util.List;

/**
 * @author zhuxiaomeng
 * @date 2017/12/13.
 * @email 154040976@qq.com
 * 通用service层
 */
public interface BaseService<T,E extends Serializable> extends IService<T> {


    public ReType show(T t, int page, int limit);

    public ReType getList(T t, int page, int limit);

    public List<T> showAll(T t);
}
