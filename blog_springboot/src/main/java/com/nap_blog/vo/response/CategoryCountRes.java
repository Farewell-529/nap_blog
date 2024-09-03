package com.nap_blog.vo.response;

import lombok.Data;

@Data
public class CategoryCountRes {
    private Long id;
    private String categoryName;
    private Integer articleCount;
}
