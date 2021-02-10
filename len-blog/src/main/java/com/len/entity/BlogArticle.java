package com.len.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@TableName(value = "blog_article")
@Data
public class BlogArticle {

    @TableId(value = "id", type = IdType.UUID)
    private String id;

    /**
     * code
     */
    @TableField(value = "code")
    private String code;


    /**
     * 标题
     */
    @TableField(value = "title")
    private String title;

    /**
     * 列表缩略图
     */
    @TableField(value="first_img")
    private String firstImg;

    /**
     * 阅读次数
     */
    @TableField(value = "read_number")
    private Integer readNumber;

    /**
     * 次序(置顶功能)
     */
    @TableField(value = "top_num")
    private Integer topNum;

    @TableField(value = "create_by")
    private String createBy;

    @TableField(value = "update_by")
    private String updateBy;

    @TableField(value = "create_date")
    private Date createDate;

    @TableField(value = "update_date")
    private Date updateDate;

    /**
     * 文章内容
     */
    @TableField(value = "content")
    private String content;

    @TableField(value = "del_flag")
    private Byte delFlag;


}