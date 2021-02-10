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

@TableName(value = "sys_role")
@Data
@ToString
@EqualsAndHashCode
public class SysRole extends AbstractEntity {

    @NotEmpty(message = "角色名称不能为空", groups = {AddGroup.class, UpdateGroup.class})
    private String roleName;

    private String remark;
}