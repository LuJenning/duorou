package com.len.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@TableName(value = "sys_depart")
@Data
public class SysDepart {
    
    @TableId(value = "id", type = IdType.UUID)
    private String id;

    /**
     * 编码
     */
    private String code;

    /**
     * 父级id
     */
    private String parentId;

    /**
     * 部门名称
     */
    private String departName;
}