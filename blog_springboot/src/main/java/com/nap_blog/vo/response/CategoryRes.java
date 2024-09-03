package com.nap_blog.vo.response;

import lombok.Builder;
import lombok.Data;

import java.util.Date;
@Data
@Builder
public class CategoryRes {
    private Long id;
    private String categoryName;
    private Integer useCount;
    private Date createDate;
}
