package com.nap_blog.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.nap_blog.entity.Article;
import com.nap_blog.entity.Category;
import com.nap_blog.entity.Tags;
import com.nap_blog.vo.query.CategoryQuery;
import com.nap_blog.vo.PageResult;
import com.nap_blog.vo.response.CategoryCountRes;
import com.nap_blog.vo.response.CategoryRes;
import com.nap_blog.mapper.ArticleMapper;
import com.nap_blog.mapper.CategoryMapper;
import com.nap_blog.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {
    @Autowired
    ArticleMapper articleMapper;
    @Autowired
    CategoryMapper categoryMapper;

    @Override
    public PageResult<CategoryRes> categoryBackList(CategoryQuery categoryQuery) {
        //查询标签数量
        long total = this.count();
        if (total == 0) {
            return new PageResult<>();
        }
        LambdaQueryWrapper<Category> lqw = new LambdaQueryWrapper<>();
        if (categoryQuery.getKeyword() != null && !categoryQuery.getKeyword().trim().isEmpty()) {
            lqw.like(Category::getCategoryName, categoryQuery.getKeyword());
        }
        if (categoryQuery.getCreateDate() != null && !categoryQuery.getCreateDate().trim().isEmpty()) {
            LocalDate localDate = LocalDate.parse(categoryQuery.getCreateDate());
            LocalDateTime startOfDay = localDate.atStartOfDay();
            LocalDateTime startOfNextDay = localDate.plusDays(1).atStartOfDay();
            lqw.ge(Category::getCreateDate,startOfDay);
            lqw.lt(Category::getCreateDate,startOfNextDay);
        }
        int current = (categoryQuery.getCurrent() == null) ? 1 : categoryQuery.getCurrent();
        int size = (categoryQuery.getSize() == null) ? 10 : categoryQuery.getSize();
        // 创建分页对象
        Page<Category> page = new Page<>(current, size);
        List<Category> categoryList = categoryMapper.selectList(page, lqw);
        //获取id集合
        List<Long> ids = categoryList.stream().map(Category::getId).toList();
        Map<Long, Integer> countMap = this.countByCategoryIds(ids);
        List<CategoryRes> catgeoryBackList = categoryList.stream()
                .map(item -> CategoryRes.builder()
                        .id(item.getId())
                        .categoryName(item.getCategoryName())
                        .useCount(countMap.getOrDefault(item.getId(), 0))
                        .createDate(item.getCreateDate())
                        .build()
                ).toList();
        return new PageResult<>(catgeoryBackList, total);
    }

    @Override
    public void saveCategory(Category category) {
        category.setCreateDate(new Date());
        category.setUpdateDate(new Date());
        categoryMapper.insert(category);

    }

    @Override
    public void updateCategory(Category category) {
        category.setUpdateDate(new Date());
        categoryMapper.updateById(category);
    }

    @Transactional
    @Override
    public void deleteCategory(List<Integer> ids) {
        //将使用这个分类的文章的分类id改为-1
        List<Long> articleIds = articleMapper.selectList(new LambdaQueryWrapper<Article>()
                .in(Article::getCategoryId, ids)).stream().map(Article::getId).toList();
        articleMapper.setCategoryIds(articleIds);
        categoryMapper.deleteBatchIds(ids);
    }

    @Override
    public Map<Long, Integer> countByCategoryIds(List<Long> categoryIds) {
        if (categoryIds == null || categoryIds.isEmpty()) {
            return Collections.emptyMap();
        }
        return articleMapper.selectList(new LambdaQueryWrapper<Article>()
                        .in(Article::getCategoryId, categoryIds))
                .stream()
                .collect(Collectors.groupingBy(
                        article -> article.getCategoryId().longValue(),
                        Collectors.summingInt(e -> 1)
                ));
    }

    @Override
    public PageResult<CategoryCountRes> getCategoryCountsList() {
        List<Category> categoryList = categoryMapper.selectList(null);
        List<Long> categoryIds = categoryList.stream().map(Category::getId).toList();
        LambdaQueryWrapper<Article> lqw = new LambdaQueryWrapper<>();
        lqw.eq(Article::getStatus, 1).in(Article::getCategoryId, categoryIds);
        Map<Long, Integer> countMap = articleMapper.selectList(lqw)
                .stream()
                .collect(Collectors.groupingBy(
                        article -> article.getCategoryId().longValue(),
                        Collectors.summingInt(e -> 1)
                ));
        List<CategoryCountRes> categoryCountResList = categoryList.stream().map(item -> {
            CategoryCountRes categoryCountRes = new CategoryCountRes();
            categoryCountRes.setId(item.getId());
            categoryCountRes.setCategoryName(item.getCategoryName());
            categoryCountRes.setArticleCount(countMap.get(item.getId()));
            return categoryCountRes;
        }).toList();
        Long total = categoryMapper.selectCount(null);
        return new PageResult<>(categoryCountResList, total);
    }

}
