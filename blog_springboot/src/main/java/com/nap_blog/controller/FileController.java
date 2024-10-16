package com.nap_blog.controller;

import com.nap_blog.vo.Result;
import com.nap_blog.entity.User;
import com.nap_blog.service.ImgService;
import com.nap_blog.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.UUID;

@RestController
public class FileController {
    @Autowired
    UserService userService;
    @Autowired
    ImgService imgService;
    @PostMapping("/uploadImg")
    public Result uploadImg(@RequestParam("file") MultipartFile file,@RequestParam("articleId") String articleId) {
        String pre = "C:\\Users\\Farewell\\Desktop\\Blog\\upload\\img\\";
        String fileName= uploadFile(file,pre);
        imgService.saveImg(Long.parseLong(articleId),fileName);
        return Result.success(fileName);
    }
    @PostMapping("/user/uploadImg")
    public Result userUploadImg(@RequestParam("file") MultipartFile file) {
        String pre = "C:\\Users\\Farewell\\Desktop\\Blog\\upload\\user\\img\\";
        String fileName= uploadFile(file,pre);
        return Result.success(fileName);
    }
    @PostMapping("/user/uploadAvatar")
    public Result userUploadAvatar(@RequestParam("file") MultipartFile file, HttpServletRequest request) {
        String pre = "C:\\Users\\Farewell\\Desktop\\Blog\\upload\\user\\avatar\\";
        Long userId = (Long) request.getAttribute("userId");
        // 获取用户旧头像文件名
        User user = userService.getById(userId);
        String oldAvatar = user.getAvatar();
        if (oldAvatar != null) {
            String oldPath = pre + oldAvatar;
            File oldFile = new File(oldPath);
            if (oldFile.exists()) {
                oldFile.delete();
            }
        }
        String fileName = uploadFile(file, pre);
        //更新头像
        user.setAvatar(fileName);
        user.setCreateDate(new Date());
        userService.updateById(user);
        return Result.success(fileName);
    }

    public String uploadFile(MultipartFile file, String pre){
        if (file.isEmpty()) {
            throw new RuntimeException("文件为空");
        }
        //file重命名
        String originalFilename = file.getOriginalFilename();//原文件名
        //获取文件后缀名
        String fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
        String uuid = UUID.randomUUID().toString().replace("-", "");

        String fileName = uuid + fileExtension;

        String path = pre + fileName;
        try {
            file.transferTo(new File(path));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return fileName;
    }

}
