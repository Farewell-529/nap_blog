package com.nap_blog.controller;

import com.nap_blog.entity.Friend;
import com.nap_blog.vo.Result;
import com.nap_blog.vo.query.FriendQuery;
import com.nap_blog.service.FriendService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class FriendController {
    @Autowired
    FriendService friendService;
    @GetMapping("/admin/friend/list")
    public Result listFriend(FriendQuery friendQuery){
        return Result.success(friendService.listFriend(friendQuery));
    }
    @GetMapping("/admin/friend/{id}")
    public Result getFriendById(@PathVariable("id") Long id){
        return Result.success(friendService.getById(id));
    }
    @PostMapping("/admin/friend")
    public Result saveFriend(@RequestBody Friend friend){
        friendService.saveFriend(friend);
        return Result.success();
    }
    @PutMapping("/admin/friend")
    public Result updateFriend(@RequestBody Friend friend){
        friendService.updateFriend(friend);
        return Result.success();
    }
    @DeleteMapping("/admin/friend/delete")
    public Result deleteFriend(@RequestBody List<Integer> ids){
        friendService.deleteFriend(ids);
        return Result.success();
    }
    @GetMapping("/api/friend/list")
    public Result listFriendFront( ){
        return Result.success(friendService.listFriendFront());
    }
}
