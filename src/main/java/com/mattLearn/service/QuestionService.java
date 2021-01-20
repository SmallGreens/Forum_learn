package com.mattLearn.service;

import com.mattLearn.dao.QuestionDAO;
import com.mattLearn.model.Question;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import java.util.List;

/**
 * Matt Jan.14th 2021
 */
@Service
public class QuestionService {
    @Autowired
    QuestionDAO questionDAO;

    @Autowired
    SensitiveWordService sensitiveWordService;

    private int limit = 10;
    private int offset = 0;

    public List<Question> getLatestQuestions(int userId, int offset, int limit) {
        this.limit = limit;
        this.offset = offset;
        return questionDAO.selectLatestQuestions(userId, offset, limit);
    }

    // overload a default get Latest question service
    public List<Question> getLatestQuestions(int userId){
        return questionDAO.selectLatestQuestions(userId, offset, limit);
    }

    public int addQuestion(Question question){
        // 敏感词过滤，过滤 html 标签，例如 <script>
        // 使用 spring 框架提供的htmlUtils 可以对 相关 html 标签进行转义

        question.setContent(HtmlUtils.htmlEscape(question.getContent()));
        question.setContent(sensitiveWordService.filter(question.getContent()));
        question.setTitle(HtmlUtils.htmlEscape(question.getTitle()));
        question.setTitle(sensitiveWordService.filter(question.getTitle()));


        // 如果能够顺利添加 question，返回 question 的 id，否则，返回 0
        return questionDAO.addQuestion(question) > 0 ? question.getId(): 0;
    }

    public Question getQuestionById(int id){
        return questionDAO.SelectById(id);
    }

    public boolean updateCommentCount(int count, int questionId){
        return questionDAO.updateCommentCount(count, questionId) > 0;
    }
}
