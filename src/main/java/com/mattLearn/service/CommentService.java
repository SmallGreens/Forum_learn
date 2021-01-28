package com.mattLearn.service;

import com.mattLearn.dao.CommentDAO;
import com.mattLearn.model.Comment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import java.util.List;

/**
 * @author Matt
 * @date 2021/1/20 17:31
 */
@Service
public class CommentService {
    @Autowired
    CommentDAO commentDAO;

    @Autowired
    SensitiveWordService sensitiveWordService;

    public List<Comment> getCommentByEntity(int entityId, int entityType){
        return commentDAO.selectCommentByEntity(entityId, entityType);
    }

    public int addComment(Comment comment){
        // 添加敏感词过滤功能。
        comment.setContent(HtmlUtils.htmlEscape(comment.getContent()));
        comment.setContent(sensitiveWordService.filter(comment.getContent()));
        return commentDAO.addComment(comment) > 0 ? comment.getId() : 0;
    }

    public int getCommentCount(int entityId, int entityType){
        return commentDAO.getCommentCount(entityId, entityType);
    }

    public int getUserCommentCount(int userId) {
        return commentDAO.getUserCommentCount(userId);
    }

    public boolean deleteComment(int commentId){
        return commentDAO.updateStatus(commentId, 1) > 0;
    }

    public Comment getCommentById(int id){
        return commentDAO.getCommentById(id);
    }
}
