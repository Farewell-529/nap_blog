package com.nap_blog.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.nap_blog.entity.*;
import com.nap_blog.vo.query.ArticleQuery;
import com.nap_blog.vo.PageResult;
import com.nap_blog.vo.response.*;
import com.nap_blog.mapper.ArticleMapper;
import com.nap_blog.mapper.ArticleTagsMapper;
import com.nap_blog.mapper.ImgMapper;
import com.nap_blog.service.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Lazy
@Slf4j
@Service
public class ArticleServiceImpl extends ServiceImpl<ArticleMapper, Article> implements ArticleService {
    @Autowired
    ArticleTagsService articleTagsService;
    @Autowired
    ArticleTagsMapper articleTagsMapper;
    @Autowired
    TagsService tagsService;
    @Autowired
    ArticleMapper articleMapper;
    @Autowired
    ImgService imgService;
    @Autowired
    ImgMapper imgMapper;
    @Autowired
    CategoryService categoryService;

    @Override
    public PageResult<ArticleBackRes> listArticleBackVO(ArticleQuery articleQuery) {
        //创建page对象
        Page<Article> resultPage = queryArticles(articleQuery, 1);
        long total = resultPage.getTotal();
        List<Article> articles = resultPage.getRecords();
        Map<Long, String> categoryMap = getCategoryMap();
        Map<Long, List<Tags>> articleAndTagsMap = getArticleAndTagsMap();
        List<ArticleBackRes> articleBackListVOList = articles.stream()
                .map(item -> ArticleBackRes.builder()
                        .id(item.getId())
                        .title(item.getTitle())
                        .createDate(item.getCreateDate())
                        .updateDate(item.getUpdateDate())
                        .categoryName(categoryMap.get((long) item.getCategoryId()))
                        .tagsList(articleAndTagsMap.get(item.getId()))
                        .build()).toList();

        return new PageResult<>(articleBackListVOList, total);
    }

    @Override
    public List<ArticleSelectRes> listArticleAllBackVO() {
        List<Article> articles = articleMapper.selectList(null);
        return articles.stream().map(item -> {
            ArticleSelectRes articleSelectRes = new ArticleSelectRes();
            articleSelectRes.setId(item.getId());
            articleSelectRes.setTitle(item.getTitle());
            return articleSelectRes;
        }).toList();
    }

    @Override
    public PageResult<ArticleRes> listArticleVO(ArticleQuery articleQuery) {
        Page<Article> resultPage = queryArticles(articleQuery, 1);
        long total = resultPage.getTotal();
        List<Article> articles = resultPage.getRecords();
        Map<Long, String> categoryMap = getCategoryMap();
        Map<Long, List<Tags>> articleAndTagsMap = getArticleAndTagsMap();

        List<ArticleRes> articleListVOList = articles.stream()
                .map(item -> ArticleRes.builder()
                        .id(item.getId())
                        .title(item.getTitle())
                        .createDate(item.getCreateDate())
                        .updateDate(item.getUpdateDate())
                        .categoryName(categoryMap.get((long) item.getCategoryId()))
                        .categoryId(item.getCategoryId())
                        .tagsList(articleAndTagsMap.get(item.getId()))
                        .content(item.getContent())
                        .build()).toList();

        return new PageResult<>(articleListVOList, total);
    }

    @Override
    public ArticleBackInfoRes getArticleByIdVO(Long id) {
        LambdaQueryWrapper<Category> categoryLQW = new LambdaQueryWrapper<>();
        LambdaQueryWrapper<ArticleTags> articleTagsLQW = new LambdaQueryWrapper<>();
        LambdaQueryWrapper<Tags> tagsLQW = new LambdaQueryWrapper<>();
        Article articleById = articleMapper.selectById(id);
        //查询文章对应的分类名
        categoryLQW.eq(Category::getId, articleById.getCategoryId());
        String categoryName = categoryService.getOne(categoryLQW).getCategoryName();
        //查询文章对应的标签
        articleTagsLQW.in(ArticleTags::getArticleId, id);
        List<Long> tagsId = articleTagsService.list(articleTagsLQW).stream().map(ArticleTags::getTagsId).toList();
        tagsLQW.in(Tags::getId, tagsId);
        List<Tags> tagsList = tagsService.list(tagsLQW);
        // 构建返回的 VO 对象
        return ArticleBackInfoRes.builder()
                .id(id)
                .title(articleById.getTitle())
                .content(articleById.getContent())
                .createDate(articleById.getCreateDate())
                .updateDate(articleById.getUpdateDate())
                .tags(tagsList)
                .categoryName(categoryName)
                .build();
    }

    @Override
    public void saveArticle(Article article) {
        List<Tags> tagsList = article.getTagsList();
        if (tagsList.isEmpty()) return;
        article.setCreateDate(new Date());
        article.setUpdateDate(new Date());
        article.setWordCount(culWordCount(article.getContent()));
        articleMapper.insert(article);
        addArticleTags(tagsList, article);
    }

    @Override
    public void publishArticle(Article article) {
        article.setStatus(1);
        saveArticle(article);
    }

    @Override
    public void publishArticleList(List<Integer> articleIds) {
        articleMapper.setStatusBatchByIds(articleIds, 1);
    }


    @Override
    public void updateArticle(Article article) {
        article.setUpdateDate(new Date());
        if (!this.updateById(article)) return;
        //如果修改了图片链接就删除图片
        LambdaQueryWrapper<Img> lqwImg = new LambdaQueryWrapper<>();
        lqwImg.eq(Img::getArticleId, article.getId());
        List<Img> imgList = imgService.list(lqwImg);
        imgList.forEach(img -> {
            if (!article.getContent().contains(img.getImgName())) {
                // 将 article.getId() 转换成 List<Integer>
                List<Integer> articleIdList = List.of(article.getId().intValue());
                imgService.deleteImg(articleIdList);
            }
        });

        // 获取当前文章的所有关联标签列表
        List<Tags> currentTagsList = tagsService.getTagListByArticleId(article.getId());
        // 获取新传入的标签列表
        List<Tags> newTagsList = article.getTagsList();
        // 找出需要删除的标签
        List<Tags> tagsToDelete = currentTagsList.stream()
                //anyMatch方法遍历流中是否有满足给定条件，如果有就返回true并结束方法,如果没有找到任何元素满足条件，就返回false。
                .filter(tag -> !newTagsList.stream().anyMatch(newTag -> newTag.getTagsName()
                        .equals(tag.getTagsName()))).toList();
//        log.info("需要删除的List{}", tagsToDelete);
        // 找出需要新增的标签
        List<Tags> tagsToAdd = newTagsList.stream().filter(newTag -> !currentTagsList.stream()
                .anyMatch(tag -> tag.getTagsName().equals(newTag.getTagsName()))).toList();
//        log.info("需要新增的List{}", tagsToAdd);
        // 删除需要删除的关联表
        for (Tags tag : tagsToDelete) {
            LambdaQueryWrapper<ArticleTags> lqw = new LambdaQueryWrapper<>();
            lqw.eq(ArticleTags::getTagsId, tag.getId()).eq(ArticleTags::getArticleId, article.getId());
            articleTagsService.remove(lqw);
        }
        //添加需要添加的关联表
        addArticleTags(tagsToAdd, article);
    }

    @Override
    public void deleteArticle(List<Integer> articleIds) {
        LambdaQueryWrapper<ArticleTags> lqwArticleTags = new LambdaQueryWrapper<>();
        lqwArticleTags.in(ArticleTags::getArticleId, articleIds);
        articleTagsMapper.delete(lqwArticleTags);
        imgService.deleteImg(articleIds);
        articleMapper.deleteBatchIds(articleIds);
    }

    @Override
    public PageResult<ArticleBackRes> draftListArticleBackVO(ArticleQuery articleQuery) {
        //创建page对象
        Page<Article> resultPage = queryArticles(articleQuery, 0);
        long total = resultPage.getTotal();
        List<Article> articles = resultPage.getRecords();
        Map<Long, String> categoryMap = getCategoryMap();
        Map<Long, List<Tags>> articleAndTagsMap = getArticleAndTagsMap();

        List<ArticleBackRes> articleBackListVOList = articles.stream()
                .map(item -> ArticleBackRes.builder()
                        .id(item.getId())
                        .title(item.getTitle())
                        .createDate(item.getCreateDate())
                        .updateDate(item.getUpdateDate())
                        .categoryName(categoryMap.get((long) item.getCategoryId()))
                        .tagsList(articleAndTagsMap.get(item.getId()))
                        .build()).toList();

        return new PageResult<>(articleBackListVOList, total);
    }

    @Override
    public void draftToPublishById(Long id) {
        articleMapper.draftToPublishById(id);
    }

    @Override
    public void articleToDraft(Integer articleId) {
        Article article = new Article();
        article.setStatus(0);
        articleMapper.update(article, new LambdaQueryWrapper<Article>()
                .eq(Article::getId, articleId));
    }

    @Override
    public ArticleInfoRes getArticleByIdFont(Long id) {
        Article articleById = articleMapper.selectById(id);
        //查询文章对应的分类名
        Category category = categoryService.getOne(new LambdaQueryWrapper<Category>()
                .eq(Category::getId, articleById.getCategoryId()));
        //查询文章对应的标签
        List<Long> tagsId = articleTagsService.list(new LambdaQueryWrapper<ArticleTags>()
                .in(ArticleTags::getArticleId, id)).stream().map(ArticleTags::getTagsId).toList();

        List<Tags> tagsList = tagsService.list(new LambdaQueryWrapper<Tags>()
                .in(Tags::getId, tagsId));
        // 构建返回的 VO 对象
        return ArticleInfoRes.builder()
                .id(id)
                .title(articleById.getTitle())
                .content(articleById.getContent())
                .createDate(articleById.getCreateDate())
                .updateDate(articleById.getUpdateDate())
                .tags(tagsList)
                .categoryName(category.getCategoryName())
                .build();
    }

    private int culWordCount(String content) {
        // 去除HTML标签、空格和换行符
        String text = content.replaceAll("<[^>]+>|\\s+", "");
        //返回文字数量
        return text.length();
    }

    //添加关联表
    private void addArticleTags(List<Tags> tagsList, Article article) {
        for (Tags tags : tagsList) {
            LambdaQueryWrapper<Tags> lqwTags = new LambdaQueryWrapper<>();
            lqwTags.eq(Tags::getTagsName, tags.getTagsName());
            Tags selecedtTags = tagsService.getOne(lqwTags);
            //如果是新的标签就新增标签后新增关联表
            ArticleTags articleTags = new ArticleTags();
            if (selecedtTags == null) {
                tags.setCreateDate(new Date());
                //保存到数据库后，tags自动获得id
                tagsService.save(tags);
                articleTags.setTagsId(tags.getId());
            } else {
                articleTags.setTagsId(selecedtTags.getId());
            }
            articleTags.setArticleId(article.getId());
            articleTagsService.save(articleTags);
        }
    }

    // 通用的文章查询逻辑
    private Page<Article> queryArticles(ArticleQuery articleQuery, int status) {
        LambdaQueryWrapper<Article> lqw = new LambdaQueryWrapper<>();
        lqw.eq(Article::getStatus, status);
        if (articleQuery.getKeyword() != null) {
            lqw.like(Article::getTitle, articleQuery.getKeyword());
        }
        if (articleQuery.getCategoryId() != null) {
            lqw.eq(Article::getCategoryId, articleQuery.getCategoryId());
        }
        if (articleQuery.getTagsId() != null) {
            //查询标签后的文章
            List<ArticleTags> articleTags = articleTagsMapper.selectList(new LambdaQueryWrapper<ArticleTags>()
                    .eq(ArticleTags::getTagsId, articleQuery.getTagsId()));
            List<Long> articleIds = articleTags.stream().map(ArticleTags::getArticleId).toList();
            lqw.in(Article::getId, articleIds);
        }
        int current = (articleQuery.getCurrent() == null) ? 1 : articleQuery.getCurrent();
        int size = (articleQuery.getSize() == null) ? 10 : articleQuery.getSize();
        Page<Article> page = new Page<>(current, size);
        return articleMapper.selectPage(page, lqw);
    }

    public Map<Long, List<Tags>> getArticleAndTagsMap() {
        List<ArticleTags> articleTags = articleTagsService.list();
        List<Tags> tagsList = tagsService.list();
        // 构建标签 ID 到标签对象的映射
        Map<Long, Tags> tagsMap = tagsList.stream()
                .collect(Collectors.toMap(Tags::getId, tag -> tag));
        // 构建 articleId -> tags 映射
        return articleTags.stream()
                //按照键分组
                .collect(Collectors.groupingBy(
                        // 以 articleId 作为键
                        ArticleTags::getArticleId,
                        // 映射成tags
                        Collectors.mapping(
                                articleTag -> tagsMap.get(articleTag.getTagsId()), // 从 tagsMap 中获取标签对象
                                Collectors.toList() // 将结果收集为列表，相同组的结果收集到相同列表中
                        )
                ));
    }

    // 获取分类映射
    private Map<Long, String> getCategoryMap() {
        return categoryService.list().stream()
                .collect(Collectors.toMap(Category::getId, Category::getCategoryName));
    }


}


