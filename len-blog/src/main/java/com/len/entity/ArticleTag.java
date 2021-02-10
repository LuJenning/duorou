package com.len.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

@TableName(value = "blog_article_tag")
public class ArticleTag {

    @TableId(value = "article_id", type = IdType.UUID)
    private String articleId;

    @TableId(value = "tag_id", type = IdType.UUID)
    private String tagId;

    /**
     * @return article_id
     */
    public String getArticleId() {
        return articleId;
    }

    /**
     * @param articleId
     */
    public void setArticleId(String articleId) {
        this.articleId = articleId == null ? null : articleId.trim();
    }

    /**
     * @return tag_id
     */
    public String getTagId() {
        return tagId;
    }

    /**
     * @param tagId
     */
    public void setTagId(String tagId) {
        this.tagId = tagId == null ? null : tagId.trim();
    }
}