package com.nap_blog.vo.query;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class CategoryQuery extends PageQuery{
    //搜索内容
    private String keyword;
}
