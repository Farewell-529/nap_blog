package com.nap_blog.filter;

import com.alibaba.fastjson.JSONObject;
import com.auth0.jwt.interfaces.Claim;
import com.nap_blog.utils.JwtUtils;
import com.nap_blog.vo.Result;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;

@Slf4j
@Component
@WebFilter(urlPatterns = "/**")
public class CheckLogin implements Filter {
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        Filter.super.init(filterConfig);
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) servletRequest;
        HttpServletResponse res = (HttpServletResponse) servletResponse;

        res.setCharacterEncoding("UTF-8");
        //允许跨域请求
        res.setHeader("Access-Control-Allow-Headers", "content-type");
        res.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        res.setHeader("Access-Control-Allow-Headers", "Content-Type, Authorization");
        String origin = req.getHeader("Origin");
        if (origin != null) {
            res.setHeader("Access-Control-Allow-Origin", origin);
        } else {
            res.setHeader("Access-Control-Allow-Origin", "*");
        }
        String url = req.getRequestURL().toString();
        //如果是登录，不需要过滤
        if (url.contains("/login")||url.contains("/static")||url.contains("/register")||url.contains("/api")) {
            chain.doFilter(servletRequest, servletResponse);
            return;
        }
        //获取header里的token
        String token = req.getHeader("authorization");
        if (token == null || token.isEmpty()) {
            Result error = Result.error(403, "NOT_LOGIN");
            //将 JSON 字符串写入到响应中，返回给客户端
            String notLogin = JSONObject.toJSONString(error);
            res.getWriter().write(notLogin);
            return;
        }
        Map<String, Claim> Claims = null;
        try {
            Claims = JwtUtils.verifyToken(token);
        } catch (Exception e) {
            e.printStackTrace();
            log.info("解析令牌失效");
            Result error = Result.error(403,"NOT_LOGIN");
            String notLogin = JSONObject.toJSONString(error);
            res.getWriter().write(notLogin);
            return;
        }
        if (Claims != null && Claims.containsKey("id")) {
            Long id = Claims.get("id").asLong();
            log.info("用户id为:{}", id);
            req.setAttribute("userId", id);
        }
        chain.doFilter(servletRequest, servletResponse);
    }


    @Override
    public void destroy() {
        Filter.super.destroy();
    }
}
