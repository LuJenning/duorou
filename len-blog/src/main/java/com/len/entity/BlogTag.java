package com.len.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

@TableName(value = "blog_tag")
public class BlogTag {
    @TableId(value = "id", type = IdType.UUID)
    private String id;

    /**
     * 标签code
     */
    @TableField(value = "tag_code")
    private String tagCode;

    /**
     * 标签name
     */
    @TableField(value = "tag_name")
    private String tagName;

    /**
     * @return id
     */
    public String getId() {
        return id;
    }

    /**
     * @param id
     */
    public void setId(String id) {
        this.id = id == null ? null : id.trim();
    }

    /**
     * 获取标签code
     *
     * @return tag_code - 标签code
     */
    public String getTagCode() {
        return tagCode;
    }

    /**
     * 设置标签code
     *
     * @param tagCode 标签code
     */
    public void setTagCode(String tagCode) {
        this.tagCode = tagCode == null ? null : tagCode.trim();
    }

    /**
     * 获取标签name
     *
     * @return tag_name - 标签name
     */
    public String getTagName() {
        return tagName;
    }

    /**
     * 设置标签name
     *
     * @param tagName 标签name
     */
    public void setTagName(String tagName) {
        this.tagName = tagName == null ? null : tagName.trim();
    }
}