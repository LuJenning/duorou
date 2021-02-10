package com.len.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.len.base.AbstractEntity;
import com.len.validator.group.AddGroup;
import com.len.validator.group.UpdateGroup;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.NotEmpty;

@TableName(value = "sys_user")
@Data
@ToString
public class SysUser extends AbstractEntity {


    @NotEmpty(message = "用户名不能为空", groups = {AddGroup.class, UpdateGroup.class})
    @TableField
    private String username;

    @NotEmpty(message = "密码不能为空", groups = {AddGroup.class, UpdateGroup.class})
    @TableField
    private String password;
    @TableField
    private Integer age;
    @TableField
    private String email;
    @TableField
    private String photo;

    private String realName;

    /**
     * 0可用1封禁
     */
    private Byte delFlag;
}