package com.javaee.blog.service;

import com.javaee.blog.dto.vo.DashboardVO;
import com.javaee.blog.dto.vo.HomeVO;

public interface DashboardService {

    HomeVO getHome();

    DashboardVO getDashboard();
}
