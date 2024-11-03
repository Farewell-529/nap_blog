package com.nap_blog.controller;

import com.nap_blog.entity.Visits;
import com.nap_blog.service.VisitsService;
import com.nap_blog.vo.Result;
import com.nap_blog.vo.query.VisitsQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class VisitsController {
    @Autowired
    VisitsService visitsService;
    @GetMapping("/admin/visits/list")
    public Result listVisitsAdmin(VisitsQuery visitsQuery){
        return Result.success(visitsService.listVisitsAdmin(visitsQuery));
    }
    @PostMapping("/admin/visits/save")
    public Result saveVisitsAdmin(@RequestBody Visits visits){
        visitsService.saveVisitsAdmin(visits);
        return Result.success();
    }
    @PutMapping("/admin/visits/edit")
    public Result editVisitsAdmin(@RequestBody Visits visits){
        visitsService.editVisitsAdmin(visits);
        return Result.success();
    }
    @DeleteMapping("/admin/visits/delete")
    public Result deleteVisitsAdmin(@RequestBody List<Integer> ids){
        visitsService.deleteVisitsAdmin(ids);
        return Result.success();
    }
}
