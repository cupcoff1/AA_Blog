package com.javaee.blog.service.impl;

import com.javaee.blog.dto.request.AboutUpdateRequest;
import com.javaee.blog.dto.vo.AboutVO;
import com.javaee.blog.entity.About;
import com.javaee.blog.mapper.AboutMapper;
import com.javaee.blog.service.AboutService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AboutServiceImpl implements AboutService {

    private final AboutMapper aboutMapper;

    @Override
    public AboutVO get() {
        About about = aboutMapper.selectById(1L);
        if (about == null) return new AboutVO();
        return toVO(about);
    }

    @Override
    @Transactional
    public void update(AboutUpdateRequest request) {
        About about = aboutMapper.selectById(1L);
        if (about == null) return;
        about.setNickname(request.getNickname());
        about.setAvatar(request.getAvatar());
        about.setBio(request.getBio());
        about.setSkills(request.getSkills());
        about.setHobbies(request.getHobbies());
        about.setLocation(request.getLocation());
        about.setSocialLinks(request.getSocialLinks());
        aboutMapper.updateById(about);
    }

    private AboutVO toVO(About about) {
        AboutVO vo = new AboutVO();
        vo.setNickname(about.getNickname());
        vo.setAvatar(about.getAvatar());
        vo.setBio(about.getBio());
        vo.setSkills(about.getSkills());
        vo.setHobbies(about.getHobbies());
        vo.setLocation(about.getLocation());
        vo.setSocialLinks(about.getSocialLinks());
        return vo;
    }
}
