package com.nap_blog.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.nap_blog.entity.Article;
import com.nap_blog.entity.Category;
import com.nap_blog.vo.PageResult;
import com.nap_blog.vo.response.ArchiveArticleRes;
import com.nap_blog.vo.response.BlogBackInfoRes;
import com.nap_blog.vo.response.CategoryCountRes;
import com.nap_blog.vo.response.TagsCountRes;
import com.nap_blog.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class BlogBackInfoServiceImpl implements BlogInfoService {
    @Autowired
    ArticleService articleService;
    @Autowired
    TagsService tagsService;
    @Autowired
    CategoryService categoryService;
    @Autowired
    ArticleTagsService articleTagsService;
    @Autowired
    CommentsService commentsService;
    @Autowired
    FriendService friendService;


    @Override
    public BlogBackInfoRes getBlogBackInfo() {
        List<Article> articles = articleService.list();
        Long articleCount=0L;
        Long viewCount=0L;
        for (Article article : articles) {
            articleCount++;
            viewCount += article.getViewCount();
        }
        Long categoryCount = categoryService.count();
        Long tagsCount = tagsService.count();
        Long commentsCount=commentsService.count();
        Long friendCount=friendService.count();
        //查询每个分类的使用次数
        List<CategoryCountRes> categoryCountResList = categoryService.getCategoryCountsList().getRecordList();
        //查询每个标签的使用次数
        List<TagsCountRes> tagsCountResList = tagsService.getTagsCountsList().getRecordList();
        return BlogBackInfoRes.builder()
                .articleCount(articleCount)
                .categoryCount(categoryCount)
                .tagsCount(tagsCount)
                .commentCount(commentsCount)
                .friendCount(friendCount)
                .viewCount(viewCount)
                .categoryVOList(categoryCountResList)
                .tagsVOList(tagsCountResList)
                .build();
    }

    @Override
    public PageResult<ArchiveArticleRes> getArchiveArticle() {
        LambdaQueryWrapper<Article> lqw=new LambdaQueryWrapper<>();
        lqw.eq(Article::getStatus,1);
        List<Category> categories = categoryService.list();
        Map<Long, String> categoryMap = categories.stream().collect(
                Collectors.toMap(Category::getId, Category::getCategoryName)
        );
        List<Article> articles = articleService.list(lqw);
        List<ArchiveArticleRes> list = articles.stream().map(article -> {
            return ArchiveArticleRes
                    .builder()
                    .id(article.getId())
                    .articleTitle(article.getTitle())
                    .createDate(article.getCreateDate())
                    .categoryName(categoryMap.get((long) article.getCategoryId()))
                    .build();
        }).toList();
        long total = articleService.count(lqw);
        return new PageResult<>(list,total);
    }
}
