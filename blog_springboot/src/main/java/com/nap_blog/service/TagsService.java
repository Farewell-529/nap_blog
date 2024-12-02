package com.nap_blog.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.nap_blog.entity.Tags;
import com.nap_blog.vo.query.TagsQuery;
import com.nap_blog.vo.PageResult;
import com.nap_blog.vo.response.TagsBackRes;
import com.nap_blog.vo.response.TagsCountRes;

import java.util.List;

public interface TagsService extends IService<Tags> {
    PageResult<TagsBackRes> listTags(TagsQuery tagsQuery);
    void saveTags(Tags tags);

    void updateTags(Tags tags);
    void deleteTags(List<Integer> ids);

    List<Tags> getTagListByArticleId(Long articleId);

    PageResult<TagsCountRes> getTagsCountsListFront();

    PageResult<TagsCountRes> getTagsCountsListAdmin();
}
