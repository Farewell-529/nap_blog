package com.nap_blog.controller;

import com.nap_blog.entity.Blocked;
import com.nap_blog.service.BlockedService;
import com.nap_blog.vo.Result;
import com.nap_blog.vo.query.BlockedQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class BlockedController {
    @Autowired
    BlockedService blockedService;
    @GetMapping("/admin/blocked/list")
    public Result getBlockedList(BlockedQuery blockedQuery) {
        return Result.success(blockedService.getBlockedList(blockedQuery));
    }

    @PostMapping("/admin/blocked/save")
    public Result saveBlocked(@RequestBody Blocked blocked){
        blockedService.saveBlocked(blocked);
        return Result.success();
    }
    @PutMapping("/admin/blocked/edit")
    public Result editBlocked(@RequestBody Blocked blocked){
        blockedService.editBlocked(blocked);
        return Result.success();
    }

    @DeleteMapping("/admin/blocked/delete")
    public Result editBlocked(@RequestBody List<Integer> ids){
        blockedService.deleteBlockedByIds(ids);
        return Result.success();
    }
}
