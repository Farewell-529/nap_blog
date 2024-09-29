package com.nap_blog.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.nap_blog.entity.ArticleTags;
import com.nap_blog.entity.Tags;
import com.nap_blog.mapper.ArticleMapper;
import com.nap_blog.vo.query.TagsQuery;
import com.nap_blog.vo.PageResult;
import com.nap_blog.vo.response.TagsBackRes;
import com.nap_blog.mapper.ArticleTagsMapper;
import com.nap_blog.mapper.TagsMapper;
import com.nap_blog.service.ArticleTagsService;
import com.nap_blog.service.TagsService;
import com.nap_blog.vo.response.TagsCountRes;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
//解决循环依赖问题，俩个bean互相依赖
//@Lazy
public class TagsServiceImpl extends ServiceImpl<TagsMapper, Tags> implements TagsService {
    @Autowired
    ArticleTagsService articleTagsService;
    @Autowired
    ArticleTagsMapper articleTagsMapper;

    @Autowired
    TagsMapper tagsMapper;

    @Override
    public PageResult<TagsBackRes> listTags(TagsQuery tagsQuery) {
        //查询标签数量
        long total = this.count();
        if (total == 0) {
            return new PageResult<>();
        }
        LambdaQueryWrapper<Tags> lqw = new LambdaQueryWrapper<>();
        if (tagsQuery.getKeyword() != null && !tagsQuery.getKeyword().trim().isEmpty()) {
            lqw.like(Tags::getTagsName, tagsQuery.getKeyword());
        }
        int current = (tagsQuery.getCurrent() == null) ? 1 : tagsQuery.getCurrent();
        int size = (tagsQuery.getSize() == null) ? 10 : tagsQuery.getSize();
        // 创建分页对象
        Page<Tags> page = new Page<>(current, size);

        // 执行分页查询
        List<Tags> tagsList = this.list(page, lqw);

        // 获取标签的ID集合
        List<Long> tagsIds = tagsList.stream().map(Tags::getId).toList();
        // 批量查询每个标签的使用数量
        Map<Long, Integer> useCountMap = articleTagsService.countByTagsIds(tagsIds);

        // 组装结果列表
        List<TagsBackRes> tagsBackResList = tagsList.stream().map(item -> TagsBackRes.builder().id(item.getId()).tagsName(item.getTagsName()).useCount(useCountMap.getOrDefault(item.getId(), 0)).createDate(item.getCreateDate()).build()).toList();

        return new PageResult<>(tagsBackResList, total);
    }


    @Override
    public void saveTags(Tags tags) {
        tags.setCreateDate(new Date());
        tags.setUpdateDate(new Date());
        tagsMapper.insert(tags);
    }

    @Override
    public void updateTags(Tags tags) {
        tags.setUpdateDate(new Date());
        tagsMapper.updateById(tags);
    }

    @Override
    public void deleteTags(List<Integer> ids) {
       articleTagsMapper.delete(new LambdaQueryWrapper<ArticleTags>()
               .in(ArticleTags::getTagsId,ids));
       tagsMapper.deleteBatchIds(ids);
    }


    @Override
    public List<Tags> getTagListByArticleId(Long articleId) {
        LambdaQueryWrapper<ArticleTags> lqwArticleTag = new LambdaQueryWrapper<>();
        lqwArticleTag.eq(ArticleTags::getArticleId, articleId);
        List<ArticleTags> list = articleTagsMapper.selectList(lqwArticleTag);
        List<Tags> tagsList = new ArrayList<>();
        //如果为空就返回一个空集合，不用集合下面的代码
        if (list.isEmpty()) return tagsList;
        // 提取List<ArticleTag>中的id集合
        List<Long> TagIds = list.stream().map(ArticleTags::getTagsId).toList();
        LambdaQueryWrapper<Tags> lqwTag = new LambdaQueryWrapper<>();
        lqwTag.in(Tags::getId, TagIds);
        return this.list(lqwTag);
    }

    @Override
    public PageResult<TagsCountRes> getTagsCountsList() {
        List<Tags> tagsList = tagsMapper.selectList(null);
        // 获取所有标签的ID
        List<Long> tagsIds = tagsList.stream().map(Tags::getId).toList();
        // 批量查询每个标签的使用数量
        Map<Long, Integer> countMap = articleTagsMapper.selectList(new LambdaQueryWrapper<ArticleTags>()
                        .in(ArticleTags::getTagsId, tagsIds))
                .stream()
                //Collectors.groupingBy(ArticleTags::getTagsId)将流按ArticleTags的tagsId字段进行分组。
                .collect(Collectors.groupingBy(
                        ArticleTags::getTagsId,
                        //求和
                        Collectors.summingInt(e -> 1)
                ));
        List<TagsCountRes> TagsCountResList = tagsList.stream().map(item -> {
            TagsCountRes tagsCountRes = new TagsCountRes();
            tagsCountRes.setId(item.getId());
            tagsCountRes.setArticleCount(countMap.getOrDefault(item.getId(), 0));
            tagsCountRes.setTagsName(item.getTagsName());
            return tagsCountRes;
        }).toList();
        Long total = tagsMapper.selectCount(null);
        //构建返回数据
        return new PageResult<>(TagsCountResList, total);
    }
}
