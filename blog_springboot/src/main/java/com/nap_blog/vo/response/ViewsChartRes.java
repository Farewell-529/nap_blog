package com.nap_blog.vo.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ViewsChartRes {
    private Date viewTime;
    private Integer PageViews;
    private Integer Visitors;
}
