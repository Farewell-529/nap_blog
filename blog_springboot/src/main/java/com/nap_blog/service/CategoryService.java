package com.nap_blog.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.nap_blog.entity.Category;
import com.nap_blog.vo.query.CategoryQuery;
import com.nap_blog.vo.PageResult;
import com.nap_blog.vo.response.CategoryCountRes;
import com.nap_blog.vo.response.CategoryRes;

import java.util.List;
import java.util.Map;

public interface CategoryService extends IService<Category> {

    PageResult<CategoryRes> categoryBackList(CategoryQuery categoryQuery);
    void saveCategory(Category category);
    void updateCategory(Category category);

    void deleteCategory(List<Integer> ids);
    Map<Long, Integer> countByCategoryIds(List<Long> categoryIds);

    PageResult<CategoryCountRes> getCategoryCountsList ();
}
