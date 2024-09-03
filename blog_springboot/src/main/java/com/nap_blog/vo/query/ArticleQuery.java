package com.nap_blog.vo.query;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class ArticleQuery extends PageQuery {

    //搜索内容
    private String keyword;

    //分类id
    private Integer categoryId;

    //标签id
    private Integer tagsId;
}
