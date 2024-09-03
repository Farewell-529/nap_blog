package com.nap_blog.controller;

import com.nap_blog.entity.Category;
import com.nap_blog.vo.query.CategoryQuery;
import com.nap_blog.vo.Result;
import com.nap_blog.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
public class CategoryController {
    @Autowired
    CategoryService categoryService;

    @GetMapping("/admin/category/list")
    public Result categoryBackList(CategoryQuery categoryQuery) {
        return Result.success(categoryService.categoryBackList(categoryQuery));
    }

    @GetMapping("/category/list")
    public Result categoryList() {
        return Result.success(categoryService.list());
    }

    @GetMapping("/admin/category/{id}")
    public Result getCategoryById(@PathVariable Long id) {
        return Result.success(categoryService.getOptById(id));
    }

    @PostMapping("/admin/category")
    public Result saveCategory(@RequestBody Category category) {
        categoryService.saveCategory(category);
        return Result.success();
    }

    @PutMapping("/admin/category")
    public Result updateCategory(@RequestBody Category category) {
        categoryService.updateCategory(category);
        return Result.success();
    }

    @DeleteMapping("/admin/category/delete")
    public Result deleteCategory(@RequestBody List<Integer> ids) {
        categoryService.deleteCategory(ids);
        return Result.success();
    }

    @GetMapping("/api/category/countsList")
    public Result getCategoryCountsList(){
        return Result.success(categoryService.getCategoryCountsList());
    }
}
