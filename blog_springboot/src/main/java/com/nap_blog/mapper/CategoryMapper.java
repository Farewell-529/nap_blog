package com.nap_blog.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.nap_blog.entity.Category;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CategoryMapper extends BaseMapper<Category> {
}
