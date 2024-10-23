package com.nap_blog.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.nap_blog.entity.Article;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper

public interface ArticleMapper extends BaseMapper<Article> {
    @Update("update article set status=1 where id=#{id}")
    boolean draftToPublishById(Long id);

    void setStatusBatchByIds(@Param("articleIds") List<Integer> articleIds,@Param("status") int status);

    void setCategoryIds(@Param("articleIds") List<Long> articleIds);
}
