package com.len.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName(value = "sys_role_menu")
public class SysRoleMenu {

    @TableId(type = IdType.UUID)
    private String roleId;

    @TableId(type = IdType.UUID)
    private String menuId;
}