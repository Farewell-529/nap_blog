package com.nap_blog.service;

import com.nap_blog.vo.Result;
import com.nap_blog.entity.User;

public interface LoginService {
   User login (User user);

   Result register(User user);
}
