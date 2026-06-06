package com.javaee.blog.service;

import com.javaee.blog.dto.request.AboutUpdateRequest;
import com.javaee.blog.dto.vo.AboutVO;

public interface AboutService {

    AboutVO get();

    void update(AboutUpdateRequest request);
}
