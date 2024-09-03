package com.nap_blog.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class PageResult<T> {
    //分页结果
    private List<T> recordList;

    //总数
    private Long total;
}
