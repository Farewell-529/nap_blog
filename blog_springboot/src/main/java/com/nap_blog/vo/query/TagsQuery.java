package com.nap_blog.vo.query;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class TagsQuery extends PageQuery {
    //搜索内容
    private String keyword;
    private String createDate;
}
