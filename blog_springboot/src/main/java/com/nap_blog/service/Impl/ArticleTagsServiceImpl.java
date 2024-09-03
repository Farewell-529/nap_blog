package com.nap_blog.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.nap_blog.entity.ArticleTags;
import com.nap_blog.mapper.ArticleTagsMapper;
import com.nap_blog.service.ArticleTagsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ArticleTagsServiceImpl extends ServiceImpl<ArticleTagsMapper, ArticleTags> implements ArticleTagsService {


    @Override
    public Map<Long, Integer> countByTagsIds(List<Long> tagsIds) {
        if (tagsIds == null || tagsIds.isEmpty()) {
            return Collections.emptyMap();
        }
        return this.list(new LambdaQueryWrapper<ArticleTags>()
                        .in(ArticleTags::getTagsId,tagsIds))
                .stream()
                //Collectors.groupingBy(ArticleTags::getTagsId)将流按ArticleTags的tagsId字段进行分组。
                .collect(Collectors.groupingBy(
                        ArticleTags::getTagsId,
                        //求和
                        Collectors.summingInt(e -> 1)
                ));
    }
}
