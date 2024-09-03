package com.nap_blog.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.nap_blog.entity.ArticleTags;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ArticleTagsMapper extends BaseMapper<ArticleTags> {
    
}
