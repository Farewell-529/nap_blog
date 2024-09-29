package com.nap_blog.vo.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FriendInfo {
    private Long id;
    private String friendName;
    private String url;
    private String avatarUrl;
    private String description;
}
