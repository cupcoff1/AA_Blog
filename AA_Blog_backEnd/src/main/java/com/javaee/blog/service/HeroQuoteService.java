package com.javaee.blog.service;

import com.javaee.blog.entity.HeroQuote;

import java.util.List;

public interface HeroQuoteService {

    List<HeroQuote> list();

    void create(String content, String author, String source);

    void delete(Long id);
}
