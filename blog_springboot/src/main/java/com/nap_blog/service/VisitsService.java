package com.nap_blog.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.nap_blog.entity.Visits;
import com.nap_blog.vo.PageResult;
import com.nap_blog.vo.query.VisitsQuery;
import jakarta.servlet.http.HttpServletRequest;

import java.text.ParseException;
import java.util.List;

public interface VisitsService extends IService<Visits> {
    void recordVisitsToRedis(HttpServletRequest httpServletRequest);
    void saveVisitsToDatabase() throws ParseException;
    PageResult<Visits> listVisitsAdmin(VisitsQuery visitsQuery);

    void saveVisitsAdmin (Visits visits);
    void editVisitsAdmin(Visits visits);
    void deleteVisitsAdmin(List<Integer> ids);
}
