package com.len.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.len.base.AbstractEntity;
import com.len.validator.group.AddGroup;
import com.len.validator.group.UpdateGroup;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.validation.constraints.NotEmpty;
import java.util.Date;

@EqualsAndHashCode(callSuper = true)
@TableName(value = "sys_job")
@Data
@ToString
public class SysJob extends AbstractEntity {

    /**
     * 描述任务
     */
    @NotEmpty(message = "任务描述不能为空", groups = {AddGroup.class, UpdateGroup.class})
    private String jobName;

    /**
     * 任务表达式
     */
    @NotEmpty(message = "表达式不能为空", groups = {AddGroup.class, UpdateGroup.class})
    private String cron;

    /**
     * 状态:0未启动false/1启动true
     */
    private Boolean status;

    /**
     * 任务执行方法
     */
    @NotEmpty(message = "执行方法不能未空", groups = {AddGroup.class, UpdateGroup.class})
    private String clazzPath;

    /**
     * 其他描述
     */
    private String jobDesc;

}