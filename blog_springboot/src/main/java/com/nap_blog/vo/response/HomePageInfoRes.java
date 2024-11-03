package com.nap_blog.vo.response;

import lombok.Builder;
import lombok.Data;

import java.util.List;
@Data
@Builder
public class HomePageInfoRes {
    private Long articleCount;
    private Long categoryCount;
    private Long tagsCount;
    private Long friendCount;
    private Long commentCount;
    private Long articleViewCount;
    private Long pageViewCount;
    private Integer todayView;
    private Integer yesterdayView;
    private List<ViewsChartRes> viewsChartList;
    private List<CategoryCountRes> categoryVOList;
    private List<TagsCountRes> tagsVOList;
}
