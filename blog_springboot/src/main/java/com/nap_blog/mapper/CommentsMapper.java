package com.nap_blog.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.nap_blog.entity.Comments;
import org.apache.ibatis.annotations.Mapper;

@Mapper

public interface CommentsMapper extends BaseMapper<Comments> {
}
