package com.javaee.blog.service;

import com.javaee.blog.dto.request.HeroQuoteCreateRequest;
import com.javaee.blog.dto.vo.HeroQuoteVO;

import java.util.List;

public interface HeroQuoteService {

    List<HeroQuoteVO> list();

    void create(HeroQuoteCreateRequest request);

    void delete(Long id);
}
