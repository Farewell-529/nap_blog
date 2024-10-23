package com.nap_blog.controller;

import com.nap_blog.vo.Result;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class VisitsController {
    @GetMapping("/admin/visits/list")
    public Result listVisitsAdmin(){
        return null;
    }
}
