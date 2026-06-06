package com.javaee.blog.service;

import com.javaee.blog.dto.request.NotesCreateRequest;
import com.javaee.blog.dto.vo.NotesVO;

import java.util.List;

public interface NotesService {

    /** 前台：笔记列表（支持搜索和标签筛选），显示正文全文 */
    List<NotesVO> list(String keyword, String tagSlug);

    /** 取前 N 条 */
    List<NotesVO> list(String keyword, String tagSlug, int limit);

    /** 后台：创建笔记 */
    void create(NotesCreateRequest request);

    /** 后台：编辑笔记 */
    void update(Long id, NotesCreateRequest request);

    /** 后台：获取笔记（供编辑页使用） */
    NotesVO getById(Long id);

    /** 后台：删除笔记 */
    void delete(Long id);
}
