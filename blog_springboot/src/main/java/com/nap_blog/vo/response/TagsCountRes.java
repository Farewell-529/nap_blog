package com.nap_blog.vo.response;

import lombok.Data;

@Data
public class TagsCountRes {
    private Long id;
    private String tagsName;
    private Integer ArticleCount;
}
