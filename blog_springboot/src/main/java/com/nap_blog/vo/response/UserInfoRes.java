package com.nap_blog.vo.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserInfoRes {
    private Long id;
    private String username;
    private String avatar;
    private String bio;
    private String motto;
}
