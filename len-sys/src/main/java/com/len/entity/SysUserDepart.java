package com.len.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@TableName(value = "sys_user_depart")
@Data
public class SysUserDepart {
    /**
     * 用户id
     */
    @TableId(type = IdType.UUID, value = "user_id")
    private String userId;

    /**
     * 部门id
     */
    @TableId(type = IdType.UUID, value = "depart_id")
    private String departId;
}