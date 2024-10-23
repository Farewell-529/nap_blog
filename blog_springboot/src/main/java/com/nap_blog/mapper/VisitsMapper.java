package com.nap_blog.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.nap_blog.entity.Visits;
import com.nap_blog.vo.response.ViewsChartRes;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface VisitsMapper extends BaseMapper<Visits> {
    List<ViewsChartRes> getViewsChartByDays(@Param("day")Integer day);
    List<Integer> getPageViewsByDays(@Param("day")Integer day);
    List<Integer> getVisitorsByDays(@Param("day")Integer day);
    List<String> getVisitDays(@Param("day")Integer day);
}
