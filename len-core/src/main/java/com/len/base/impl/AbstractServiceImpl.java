package com.len.base.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.len.base.BaseMapper;
import com.len.base.BaseService;
import com.len.exception.MyException;
import com.len.util.ReType;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.util.List;

/**
 * @author zhuxiaomeng
 * @date 2017/12/13.
 * @email 154040976@qq.com
 * update by 2019/11/12 mybatisplus
 */
@Slf4j
public class AbstractServiceImpl<T, E extends Serializable> extends ServiceImpl<BaseMapper<T>, T> implements BaseService<T, E> {


    /**
     * 公共展示类
     *
     * @param t     实体
     * @param page  页
     * @param limit 行
     * @return
     */
    @Override
    public ReType show(T t, int page, int limit) {
        List<T> tList = null;
        Page<T> tPage = PageHelper.startPage(page, limit);
        try {
            tList = getBaseMapper().selectListByPage(t);
        } catch (MyException e) {
            log.error("class:BaseServiceImpl ->method:show->message:" + e.getMessage());
            e.printStackTrace();
        }
        return new ReType(tPage.getTotal(), tList);
    }

    @Override
    public List<T> showAll(T t) {
        List<T> tList = null;
        try {
            tList = getBaseMapper().selectListByPage(t);
        } catch (MyException e) {
            log.error("class:BaseServiceImpl ->method:show->message:" + e.getMessage());
            e.printStackTrace();
        }
        return tList;
    }

    @Override
    public ReType getList(T t, int page, int limit) {
        List<T> tList = null;
        Page<T> tPage = PageHelper.startPage(page, limit);
        try {
            tList = getBaseMapper().selectListByPage(t);
        } catch (MyException e) {
            log.error("class:BaseServiceImpl ->method:getList->message:" + e.getMessage());
            e.printStackTrace();
        }
        return new ReType(tPage.getTotal(), tPage.getPageNum(), tList);
    }
}
