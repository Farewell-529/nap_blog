package com.nap_blog.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.nap_blog.entity.Article;
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

import java.time.LocalDate;
import java.time.LocalDateTime;
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
    ArticleMapper articleMapper;
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
        if (tagsQuery.getCreateDate() != null && !tagsQuery.getCreateDate().trim().isEmpty()) {
            LocalDate localDate = LocalDate.parse(tagsQuery.getCreateDate());
            LocalDateTime startOfDay = localDate.atStartOfDay();
            LocalDateTime startOfNextDay = localDate.plusDays(1).atStartOfDay();
            lqw.ge(Tags::getCreateDate,startOfDay);
            lqw.lt(Tags::getCreateDate,startOfNextDay);
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
        List<TagsBackRes> tagsBackResList = tagsList.stream().map(item -> TagsBackRes.builder()
                .id(item.getId()).tagsName(item.getTagsName()).useCount(useCountMap.getOrDefault(item.getId(), 0)).createDate(item.getCreateDate()).build()).toList();
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
        articleTagsMapper.delete(new LambdaQueryWrapper<ArticleTags>().in(ArticleTags::getTagsId, ids));
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
    public PageResult<TagsCountRes> getTagsCountsListFront() {
      return getTagsCountsList(0);// 用户接口，过滤文章数量为 0 的标签
    }
    @Override
    public PageResult<TagsCountRes> getTagsCountsListAdmin() {
        return getTagsCountsList(-1);// 管理员接口，返回所有标签
    }

    public PageResult<TagsCountRes> getTagsCountsList(Integer count) {
        List<Tags> tagsList = tagsMapper.selectList(null);
        // 获取所有标签的ID
        List<Long> tagsIds = tagsList.stream().map(Tags::getId).toList();
        // 查询所有状态为1的文章
        List<Article> articles = articleMapper.selectList(new LambdaQueryWrapper<Article>().eq(Article::getStatus, 1));

        // 获取所有状态为1的文章ID
        List<Long> articleIds = articles.stream().map(Article::getId).toList();
        // 批量查询每个标签的使用数量
        LambdaQueryWrapper<ArticleTags> lqw=new LambdaQueryWrapper<>();
        lqw.in(ArticleTags::getTagsId, tagsIds).in(ArticleTags::getArticleId,articleIds);
        Map<Long, Integer> countMap = articleTagsMapper.selectList(lqw).stream()
                //Collectors.groupingBy(ArticleTags::getTagsId)将流按ArticleTags的tagsId字段进行分组。
                .collect(Collectors.groupingBy(ArticleTags::getTagsId,
                        //求和
                        Collectors.summingInt(e -> 1)));
        // 构建返回的标签统计列表，过滤掉文章数量为0的标签 
        List<TagsCountRes> TagsCountResList = tagsList.stream()
                .filter(item -> count == -1 || countMap.getOrDefault(item.getId(), 0) > count)
                .map(item -> {
                    TagsCountRes tagsCountRes = new TagsCountRes();
                    tagsCountRes.setId(item.getId());
                    tagsCountRes.setArticleCount(countMap.getOrDefault(item.getId(), 0)); // 使用默认值 0
                    tagsCountRes.setTagsName(item.getTagsName());
                    return tagsCountRes;
                }).toList();
        Long total = tagsMapper.selectCount(null);
        //构建返回数据
        return new PageResult<>(TagsCountResList, total);
    }
}
