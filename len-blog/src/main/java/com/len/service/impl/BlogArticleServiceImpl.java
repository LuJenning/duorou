package com.len.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.pagehelper.PageHelper;
import com.len.base.impl.BaseServiceImpl;
import com.len.core.LenUser;
import com.len.entity.*;
import com.len.mapper.BlogArticleMapper;
import com.len.model.Article;
import com.len.model.SimpleArticle;
import com.len.redis.RedisService;
import com.len.service.*;
import com.len.util.BeanUtil;
import com.len.util.LenResponse;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @author zhuxiaomeng
 * @date 2018/9/9.
 * @email 154040976@qq.com
 */
@Service
public class BlogArticleServiceImpl extends BaseServiceImpl<BlogArticle, String> implements BlogArticleService {

    @Autowired
    private BlogArticleMapper blogArticleMapper;

    @Autowired
    private ArticleTagService articleTagService;

    @Autowired
    private ArticleCategoryService articleCategoryService;

    @Autowired
    private BlogTagService tagService;

    @Autowired
    private RedisService redisService;

    @Autowired
    private SysUserService sysUserService;

    private static final Pattern IMG = Pattern.compile("<(img)(.*?)(/>|></img>|>)");

    private static final Pattern SRC = Pattern.compile("src\\s*=\\s*\"?(.*?)(\"|>|\\s+)");


    private ArticleDetail getArticleByCode(String code) {

        QueryWrapper<BlogArticle> blogArticleQueryWrapper = new QueryWrapper<>();
        blogArticleQueryWrapper.eq("code", code).eq("del_flag", 0);
        blogArticleMapper.selectList(blogArticleQueryWrapper);
        List<BlogArticle> articles = blogArticleMapper.selectList(blogArticleQueryWrapper);
        ;
        if (articles.isEmpty()) {
            return null;
        }
        ArticleDetail detail = new ArticleDetail();
        BlogArticle blogArticle = articles.get(0);


        Article article = new Article();
        BeanUtil.copyNotNullBean(blogArticle, article);
        detail.setArticle(article);

        String createBy = blogArticle.getCreateBy();
        if (!StringUtils.isEmpty(createBy)) {
            SysUser sysUser = sysUserService.getById(createBy);
            if (sysUser != null) {
                article.setCreateName(sysUser.getUsername());
            }
        } else {
            article.setCreateName("admin");
        }

        ArticleTag articleTag = new ArticleTag();
        articleTag.setArticleId(blogArticle.getId());
        QueryWrapper<ArticleTag> tagQueryWrapper = new QueryWrapper<>(articleTag);
        List<ArticleTag> articleTags = articleTagService.list(tagQueryWrapper);
        if (!articleTags.isEmpty()) {
            QueryWrapper<BlogTag> queryWrapper = new QueryWrapper<>();
            queryWrapper.in("id", articleTags.stream()
                    .map(ArticleTag::getTagId).collect(Collectors.toList()));
            List<BlogTag> blogTags = tagService.list(queryWrapper);

            detail.setTags(blogTags.stream().map(BlogTag::getTagCode).collect(Collectors.toList()));
        }
        ArticleCategory articleCategory = new ArticleCategory();
        articleCategory.setArticleId(blogArticle.getId());
        QueryWrapper<ArticleCategory> blogTagQueryWrapper = new QueryWrapper<>(articleCategory);
        List<ArticleCategory> articleCategories = articleCategoryService.list(blogTagQueryWrapper);
        if (!articleCategories.isEmpty()) {
            detail.setCategory(articleCategories.stream().map(ArticleCategory::getCategoryId)
                    .collect(Collectors.toList()));
        }
        return detail;
    }

    @Override
    public List<Article> indexSelect() {
        return blogArticleMapper.indexSelect();
    }

    @Override
    public LenResponse getDetail(String code) {
        LenResponse json = new LenResponse();
        ArticleDetail detail = getArticleByCode(code);
        if (detail == null) {
            json.setStatus(404);
            json.setFlag(false);
            return json;
        }
        json.setData(detail);
        json.setFlag(true);
        return json;

    }

    @Override
    public LenResponse detail(String code, String ip) {
        LenResponse json = new LenResponse();
        ArticleDetail detail = getArticleByCode(code);
        if (detail == null) {
            json.setStatus(404);
            json.setFlag(false);
            return json;
        }
        Article article = detail.getArticle();
        //点击次数
        int clickNum = addArticleReadNum(ip, article.getId());
        if (clickNum > 0) {
            article.setReadNumber(clickNum);
        }

        //上一篇
        PageHelper.startPage(1, 1);
        BlogArticle previous = selectPrevious(article.getCreateDate());
        if (previous != null) {
            SimpleArticle simpleArticle = new SimpleArticle();
            BeanUtil.copyNotNullBean(previous, simpleArticle);
            detail.setPrevious(simpleArticle);
        }
        //下一篇
        PageHelper.startPage(1, 1);
        BlogArticle next = selectNext(article.getCreateDate());
        if (next != null) {
            SimpleArticle simpleArticle = new SimpleArticle();
            BeanUtil.copyNotNullBean(next, simpleArticle);
            detail.setNext(simpleArticle);
        }
        json.setData(detail);
        json.setFlag(true);
        return json;
    }

    @Override
    public List<Article> selectArticle(String code) {
        return blogArticleMapper.selectArticle(code);
    }

    @Override
    public List<Article> selectArticleByTag(String tagCode) {
        return blogArticleMapper.selectArticleByTag(tagCode);
    }

    @Override
    public BlogArticle selectPrevious(Date date) {
        return blogArticleMapper.selectPrevious(date);
    }

    @Override
    public BlogArticle selectNext(Date date) {
        return blogArticleMapper.selectNext(date);
    }


    private String generatorCode() {
        Random random = new Random();
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < 8; i++) {
            result.append(random.nextInt(9) + 1);
        }
        BlogArticle article = new BlogArticle();
        article.setCode(result.toString());
        QueryWrapper<BlogArticle> articleQueryWrapper = new QueryWrapper<>(article);
        BlogArticle blogArticle = blogArticleMapper.selectOne(articleQueryWrapper);
        if (blogArticle == null) {
            return result.toString();
        } else {
            return generatorCode();
        }
    }

    @Override
    @Transactional
    public boolean addArticle(ArticleDetail articleDetail) {
        Article article = articleDetail.getArticle();
        String articleId = UUID.randomUUID().toString().replace("-", "");
        article.setCode(generatorCode());
        article.setId(articleId);
        article.setCreateDate(new Date());
        article.setCreateBy(LenUser.getPrincipal().getUserId());
        article.setReadNumber(0);

        String content = article.getContent();

        Matcher matcher = IMG.matcher(content);
        boolean exists = matcher.find();
        if (exists) {
            String img = matcher.group();
            Matcher m = SRC.matcher(img);
            boolean b = m.find();
            if (b) {
                article.setFirstImg(m.group(1));
            }
        }

        BlogArticle blogArticle = new BlogArticle();
        BeanUtil.copyNotNullBean(article, blogArticle);

        int result = blogArticleMapper.insert(blogArticle);

        List<ArticleCategory> categories = new ArrayList<>();
        for (String cateId : articleDetail.getCategory()) {
            ArticleCategory articleCategory = new ArticleCategory();
            articleCategory.setId(UUID.randomUUID().toString().replace("-", ""));
            articleCategory.setArticleId(articleId);
            articleCategory.setCategoryId(cateId);
            categories.add(articleCategory);
        }
        boolean insertResult = articleCategoryService.saveBatch(categories);

        List<ArticleTag> articleTags = new ArrayList<>();
        List<BlogTag> blogTags = new ArrayList<>();
        ArticleTag articleTag;
        BlogTag blogTag;
        BlogTag oldTag;
        for (String tag : articleDetail.getTags()) {
            articleTag = new ArticleTag();
            articleTags.add(articleTag);

            articleTag.setArticleId(articleId);

            blogTag = new BlogTag();
            blogTag.setTagCode(tag);
            QueryWrapper<BlogTag> blogTagQueryWrapper = new QueryWrapper<>(blogTag);
            oldTag = tagService.getOne(blogTagQueryWrapper);

            if (oldTag != null) {
                articleTag.setTagId(oldTag.getId());
            } else {
                blogTags.add(blogTag);
                String id = UUID.randomUUID().toString().replace("-", "");
                blogTag.setId(id);
                blogTag.setTagName(tag);
                articleTag.setTagId(id);
            }
        }
        articleTagService.saveBatch(articleTags);
        if (!blogTags.isEmpty()) {
            tagService.saveBatch(blogTags);
        }

        return result > 0 && insertResult;
    }

    @Override
    @Transactional
    public boolean updateArticle(Article article, List<String> categoryIds, List<String> tags) {
        article.setUpdateBy(LenUser.getPrincipal().getUserId());
        article.setUpdateDate(new Date());

        String content = article.getContent();

        Matcher matcher = IMG.matcher(content);
        boolean exists = matcher.find();
        if (exists) {
            String img = matcher.group();
            Matcher m = SRC.matcher(img);
            boolean b = m.find();
            if (b) {
                article.setFirstImg(m.group(1));
            }
        }
        BlogArticle blogArticle = new BlogArticle();
        BeanUtil.copyNotNullBean(article, blogArticle);
        blogArticleMapper.updateById(blogArticle);

        ArticleTag articleTag = new ArticleTag();
        articleTag.setArticleId(article.getId());
        articleTagService.removeById(article.getId());

        List<ArticleTag> articleTags = new ArrayList<>();
        List<BlogTag> blogTags = new ArrayList<>();
        BlogTag needAddTag;
        for (String tag : tags) {
            articleTag = new ArticleTag();
            articleTags.add(articleTag);
            articleTag.setArticleId(article.getId());
            BlogTag blogTag = new BlogTag();
            blogTag.setTagCode(tag);
            QueryWrapper<BlogTag> blogTagQueryWrapper = new QueryWrapper<>(blogTag);
            BlogTag tag1 = tagService.getOne(blogTagQueryWrapper);
            if (tag1 != null) {
                articleTag.setTagId(tag1.getId());
            } else {
                String tagId = UUID.randomUUID().toString().replace("-", "");
                articleTag.setTagId(tagId);

                needAddTag = new BlogTag();
                blogTags.add(needAddTag);
                needAddTag.setId(tagId);
                needAddTag.setTagCode(tag);
                needAddTag.setTagName(tag);
            }
        }
        if (!blogTags.isEmpty()) {
            tagService.saveBatch(blogTags);
        }

        articleTagService.saveBatch(articleTags);
        ArticleCategory articleCategory = new ArticleCategory();
        articleCategory.setArticleId(article.getId());
        QueryWrapper<ArticleCategory> categoryQueryWrapper = new QueryWrapper<>(articleCategory);
        List<ArticleCategory> categories = articleCategoryService.list(categoryQueryWrapper);
        if (!categories.isEmpty()) {
            List<String> cateIds = categories.stream().map(ArticleCategory::getCategoryId)
                    .collect(Collectors.toList());
            List<String> collect = cateIds.stream().filter(categoryIds::contains)
                    .collect(Collectors.toList());
            categoryIds.removeAll(collect);
            cateIds.removeAll(collect);
            if (!cateIds.isEmpty()) {
                List<String> delCategoryIds = categories.stream().filter(s -> cateIds.contains(s.getCategoryId()))
                        .map(ArticleCategory::getId)
                        .collect(Collectors.toList());
                articleCategoryService.delByIds(delCategoryIds);
            }
        }
        if (!categoryIds.isEmpty()) {
            List<ArticleCategory> articleCategories = new ArrayList<>();
            ArticleCategory category;
            for (String ca : categoryIds) {
                category = new ArticleCategory();
                articleCategories.add(category);
                category.setId(UUID.randomUUID().toString().replace("-", ""));
                category.setArticleId(article.getId());
                category.setCategoryId(ca);
            }
            articleCategoryService.saveBatch(articleCategories);
        }
        return true;
    }

    /**
     * 半小时增加一次有效点击数
     *
     * @param ip        访问者ip
     * @param articleId 文章id
     */
    private int addArticleReadNum(String ip, String articleId) {
        String str = ip + "_" + articleId;
        if (!StringUtils.isBlank(str)) {
            if (StringUtils.isEmpty(redisService.get(str))) {
                redisService.set(str, "true", 60 * 30L);
                BlogArticle article = blogArticleMapper.selectById(articleId);
                article.setReadNumber(article.getReadNumber() + 1);
                blogArticleMapper.updateById(article);
                return article.getReadNumber();
            }
        }
        return -1;
    }
}
