package com.nap_blog.vo.query;

import lombok.Data;

@Data
public class ForgetPasswordQuery {
    private String username;
    private String email;
}
