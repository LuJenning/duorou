package com.len.base;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author zxm
 * @date 2019-11-12.
 */
@Data
public class AbstractEntity implements Serializable {

    @TableId(type = IdType.UUID)
    private String id;

    private String createBy;

    private Date createDate;

    private String updateBy;

    private Date updateDate;
}
