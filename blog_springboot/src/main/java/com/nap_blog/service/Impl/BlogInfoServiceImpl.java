package com.nap_blog.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.nap_blog.entity.Article;
import com.nap_blog.entity.BlogInfo;
import com.nap_blog.entity.Category;
import com.nap_blog.mapper.BlogInfoMapper;
import com.nap_blog.mapper.VisitsMapper;
import com.nap_blog.vo.PageResult;
import com.nap_blog.vo.query.BloggerInfoEditQuery;
import com.nap_blog.vo.response.*;
import com.nap_blog.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

import java.lang.reflect.Field;

@Service
public class BlogInfoServiceImpl extends ServiceImpl<BlogInfoMapper, BlogInfo> implements BlogInfoService {
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
    @Autowired
    VisitsMapper visitsMapper;
    @Autowired
    BlogInfoMapper blogInfoMapper;

    @Override
    public BloggerInfoRes getBloggerInfo() {
        List<String> list = Arrays.stream(BloggerInfoRes.class.getDeclaredFields())
                .map(Field::getName)
                .toList();
        List<BlogInfo> blogInfoList = blogInfoMapper.selectList(new LambdaQueryWrapper<BlogInfo>()
                .in(BlogInfo::getInfoKey, list));
        BloggerInfoRes bloggerInfoRes = new BloggerInfoRes();
        blogInfoList.forEach(item -> {
            try {
                Field field = BloggerInfoRes.class.getDeclaredField(item.getInfoKey());
                field.setAccessible(true);
                field.set(bloggerInfoRes, item.getValue());
            } catch (NoSuchFieldException | IllegalAccessException e) {
                e.printStackTrace();
            }
        });
        return bloggerInfoRes;
    }

    @Override
    @Transactional
    public void editBlogInfoBatch(BloggerInfoEditQuery bloggerInfoEditQuery) {
        // 获取所有字段
        Field[] fields = BloggerInfoEditQuery.class.getDeclaredFields();

        // 获取所有 infoKey
        List<String> list = Arrays.stream(BloggerInfoRes.class.getDeclaredFields())
                .map(Field::getName)
                .toList();

        List<BlogInfo> blogInfoList = blogInfoMapper.selectList(new LambdaQueryWrapper<BlogInfo>()
                .in(BlogInfo::getInfoKey, list));

        // 将 blogInfoList 转换为 Map 以便快速查找
        Map<String, BlogInfo> blogInfoMap = blogInfoList.stream()
                .collect(Collectors.toMap(BlogInfo::getInfoKey, blogInfo -> blogInfo));

        Date now = new Date(); // 获取当前时间一次

        for (Field field : fields) {
            field.setAccessible(true); // 允许访问私有属性
            try {
                String infoKey = field.getName(); // 获取属性名
                String value = (String) field.get(bloggerInfoEditQuery); // 获取属性值
                if (blogInfoMap.containsKey(infoKey)) {
                    BlogInfo blogInfo = blogInfoMap.get(infoKey);
                    blogInfo.setUpdateDate(now);
                    blogInfo.setValue(value);
                    blogInfoMapper.updateById(blogInfo);
                }
            } catch (IllegalAccessException e) {
                // 处理异常，例如记录日志
                e.printStackTrace();
            }
        }
    }

    @Override
    public void editBlogInfo(BlogInfo blogInfo) {
        blogInfo.setUpdateDate(new Date());
        blogInfoMapper.updateById(blogInfo);
    }

    @Override
    public HomePageInfoRes getHomePageInfo() {
        List<Article> articles = articleService.list();
        Long articleCount = 0L;
        Long viewCount = 0L;
        for (Article article : articles) {
            articleCount++;
            viewCount += article.getViewCount() == null ? 1 : article.getViewCount();
        }
        Long categoryCount = categoryService.count();
        Long tagsCount = tagsService.count();
        Long commentsCount = commentsService.count();
        Long friendCount = friendService.count();
        List<ViewsChartRes> viewsChartList = visitsMapper.getViewsChartByDays(7);
        //查询每个分类的使用次数
        List<CategoryCountRes> categoryCountResList = categoryService.getCategoryCountsList().getRecordList();
        //查询每个标签的使用次数
        List<TagsCountRes> tagsCountResList = tagsService.getTagsCountsList().getRecordList();
        return HomePageInfoRes.builder()
                .articleCount(articleCount)
                .categoryCount(categoryCount)
                .tagsCount(tagsCount)
                .commentCount(commentsCount)
                .friendCount(friendCount)
                .viewCount(viewCount)
                .categoryVOList(categoryCountResList)
                .viewsChartList(viewsChartList)
                .tagsVOList(tagsCountResList)
                .build();
    }

    @Override
    public PageResult<ArchiveArticleRes> getArchiveArticle() {
        LambdaQueryWrapper<Article> lqw = new LambdaQueryWrapper<>();
        lqw.eq(Article::getStatus, 1);
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
        return new PageResult<>(list, total);
    }
}
