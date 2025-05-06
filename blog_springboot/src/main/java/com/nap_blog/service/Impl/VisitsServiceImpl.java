package com.nap_blog.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.nap_blog.entity.Visits;
import com.nap_blog.mapper.VisitsMapper;
import com.nap_blog.service.VisitsService;
import com.nap_blog.utils.IpUtil;
import com.nap_blog.vo.PageResult;
import com.nap_blog.vo.query.VisitsQuery;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class VisitsServiceImpl extends ServiceImpl<VisitsMapper, Visits> implements VisitsService {
    @Autowired
    VisitsMapper visitsMapper;
    @Autowired
    StringRedisTemplate redisTemplate;

    private static final String VISIT_KEY = "visit:";

    @Override
    public void recordVisitsToRedis(HttpServletRequest httpServletRequest) {
        String ip = httpServletRequest.getRemoteAddr();
        String visitKey = VISIT_KEY + ip;
        String lastVisitTime = redisTemplate.opsForValue().get("lastVisit_" + visitKey);
        long currentTIme = System.currentTimeMillis();
        if (lastVisitTime != null) {
            long lastTime = Long.parseLong(lastVisitTime);
            if (currentTIme - lastTime < 60000) {//小于一分钟
                return;//不记录
            }
        }
        //更新最后访问时间
        redisTemplate.opsForValue().set("lastVisit_" + visitKey, String.valueOf(currentTIme));
        String browser = getBrowserName(httpServletRequest.getHeader("User-Agent"));
        //Referer 请求头包含了当前请求页面的来源页面的地址
        String fromUrl = httpServletRequest.getHeader("Referer");
        String city = IpUtil.getIpRegion(ip);
        // 创建访问记录
        String visitRecord = String.format("%s|%s|%s|%s", browser, fromUrl, city, new Date());
        // 将访问记录添加到列表中
        Long l = redisTemplate.opsForList().rightPush(visitKey, visitRecord);
        System.out.println("已经执行了" + l);
        // 设置过期时间
        redisTemplate.expire(visitKey, 1, TimeUnit.HOURS);
    }

    @Override
    @Scheduled(fixedDelay = 3600000)//每小时将 Redis 中的访问记录批量保存到数据库
    public void saveVisitsToDatabase() throws ParseException {
        // 获取所有以 VISIT_KEY 开头的键
        Set<String> keys = redisTemplate.keys(VISIT_KEY + "*");
        assert keys != null;
        for (String key : keys) {
            // 从列表中读取所有访问记录
            List<String> visitRecords = redisTemplate.opsForList().range(key, 0, -1);
            String ip = key.substring(VISIT_KEY.length());
            if (visitRecords != null) {
                for (String record : visitRecords) {
                    String[] details = record.split("\\|");
                    String browser = details[0];
                    String fromUrl = details[1];
                    String city = details[2];
                    Date visitTime = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.ENGLISH).parse(details[3]);
                    Visits visits = Visits.builder()
                            .ip(ip)
                            .browser(browser)
                            .city(city)
                            .fromUrl(fromUrl)
                            .visitTime(visitTime)
                            .build();
                    visitsMapper.insert(visits);
                }
            }
            // 清除 Redis 数据
            redisTemplate.delete(key);
        }
    }

    @Override
    public PageResult<Visits> listVisitsAdmin(VisitsQuery visitsQuery) {
        //查询浏览数量
        long total = this.count();
        if (total == 0) {
            return new PageResult<>();
        }
        LambdaQueryWrapper<Visits> lqw = new LambdaQueryWrapper<>();
        if (visitsQuery.getCity() != null && !visitsQuery.getCity().trim().isEmpty()) {
            lqw.like(Visits::getCity, visitsQuery.getCity());
        }
        if (visitsQuery.getIp() != null && !visitsQuery.getIp().trim().isEmpty()) {
            lqw.like(Visits::getIp, visitsQuery.getIp());
        }
        if (visitsQuery.getVisitTime() != null && !visitsQuery.getVisitTime().trim().isEmpty()) {
            LocalDate visitDate = LocalDate.parse(visitsQuery.getVisitTime());
            // 设置开始时间为当天的00:00:00
            LocalDateTime startOfDay = visitDate.atStartOfDay();
            // 设置结束时间为第二天的00:00:00
            LocalDateTime startOfNextDay = visitDate.plusDays(1).atStartOfDay();
            lqw.ge(Visits::getVisitTime, startOfDay); // 大于等于当天的开始时间
            lqw.lt(Visits::getVisitTime, startOfNextDay); // 小于第二天的开始时间
        }
        if (visitsQuery.getBrowser() != null && !visitsQuery.getBrowser().trim().isEmpty()) {
            lqw.like(Visits::getBrowser, visitsQuery.getBrowser());
        }
        if (visitsQuery.getFromUrl() != null && !visitsQuery.getFromUrl().trim().isEmpty()) {
            lqw.like(Visits::getFromUrl, visitsQuery.getFromUrl());
        }
        int current = (visitsQuery.getCurrent() == null) ? 1 : visitsQuery.getCurrent();
        int size = (visitsQuery.getSize() == null) ? 10 : visitsQuery.getSize();
        // 创建分页对象
        Page<Visits> page = new Page<>(current, size);
        List<Visits> visitsList = visitsMapper.selectList(page, lqw);
        return new PageResult<>(visitsList, total);
    }

    @Override
    public void saveVisitsAdmin(Visits visits) {
        visitsMapper.insert(visits);
    }

    @Override
    public void editVisitsAdmin(Visits visits) {
        visitsMapper.updateById(visits);
    }

    @Override
    public void deleteVisitsAdmin(List<Integer> ids) {
        visitsMapper.deleteBatchIds(ids);
    }

    private String getBrowserName(String userAgent) {
        if (userAgent == null) {
            return "Unknown";
        }
        // 定义正则表达式来匹配常见的浏览器
        String regex = "(Chrome|Firefox|Safari|Opera|MSIE|Trident|Edge|Edg)";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(userAgent);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return "Unknown"; // 如果没有匹配，返回 Unknown
    }
}
