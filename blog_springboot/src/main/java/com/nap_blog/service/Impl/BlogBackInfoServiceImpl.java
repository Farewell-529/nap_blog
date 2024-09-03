package com.nap_blog.service.Impl;

import com.nap_blog.entity.Category;
import com.nap_blog.entity.Tags;
import com.nap_blog.vo.response.BlogBackInfoRes;
import com.nap_blog.vo.response.CategoryCountRes;
import com.nap_blog.vo.response.TagsCountRes;
import com.nap_blog.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

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


    @Override
    public BlogBackInfoRes getBlogBackInfo() {
        Long articleCount = articleService.count();
        Long categoryCount = categoryService.count();
        Long tagsCount = tagsService.count();
        //查询每个分类的使用次数
        List<CategoryCountRes> categoryCountResList = categoryService.getCategoryCountsList().getRecordList();
        //查询每个标签的使用次数
        List<TagsCountRes> tagsCountResList = tagsService.getTagsCountsList().getRecordList();
        return BlogBackInfoRes.builder()
                .articleCount(articleCount)
                .categoryCount(categoryCount)
                .tagsCount(tagsCount)
                .categoryVOList(categoryCountResList)
                .tagsVOList(tagsCountResList)
                .build();
    }
}
