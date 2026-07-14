package com.javaee.blog.service;

import com.javaee.blog.dto.request.NoteCreateRequest;
import com.javaee.blog.dto.vo.NoteVO;

import java.util.List;

public interface NoteService {

    /** 前台：笔记列表（支持搜索和标签筛选），显示正文全文 */
    List<NoteVO> list(String keyword, String tagSlug);

    /** 取前 N 条 */
    List<NoteVO> list(String keyword, String tagSlug, int limit);

    /** 后台：创建笔记 */
    void create(NoteCreateRequest request);

    /** 后台：编辑笔记（只改标题/正文，不动标签） */
    void update(Long id, NoteCreateRequest request);

    /** 后台：编辑笔记标签 */
    void updateTags(Long id, List<Long> tagIds, List<String> newTags);

    /** 后台：获取笔记（供编辑页使用） */
    NoteVO getById(Long id);

    /** 后台：删除笔记 */
    void delete(Long id);
}
