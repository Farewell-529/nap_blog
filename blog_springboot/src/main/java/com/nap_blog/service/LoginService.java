package com.nap_blog.service;

import com.nap_blog.vo.Result;
import com.nap_blog.entity.UserAuth;

public interface LoginService {
   UserAuth login (UserAuth userAuth);

   Result register(UserAuth userAuth);
}
