package com.nap_blog.vo.query;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PageQuery {
    //当前页
    private Integer current;
    //条数
    private Integer size;

}
