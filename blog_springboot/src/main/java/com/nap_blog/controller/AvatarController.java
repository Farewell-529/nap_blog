package com.nap_blog.controller;

import com.nap_blog.vo.Result;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
@RestController
public class AvatarController {
    @GetMapping("/api/gravatar")
    public Result getGravatarUrl(@RequestParam String email) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("MD5");
        md.update(email.trim().toLowerCase().getBytes(StandardCharsets.UTF_8));
        byte[] digest = md.digest();
        StringBuilder sb = new StringBuilder();
        for (byte b : digest) {
            sb.append(String.format("%02x", b));
        }
        return Result.success("https://www.gravatar.com/avatar/" + sb.toString());
    }
}
