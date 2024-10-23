package com.nap_blog.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.nap_blog.entity.Visits;
import jakarta.servlet.http.HttpServletRequest;

import java.text.ParseException;

public interface VisitsService extends IService<Visits> {
    void recordVisitsToRedis(HttpServletRequest httpServletRequest);
    void saveVisitsToDatabase() throws ParseException;
}
