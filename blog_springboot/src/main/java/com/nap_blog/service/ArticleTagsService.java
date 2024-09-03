package com.nap_blog.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.nap_blog.entity.ArticleTags;

import java.util.List;
import java.util.Map;

public interface ArticleTagsService extends IService<ArticleTags> {
    Map<Long, Integer> countByTagsIds(List<Long> tagsIds);
}
