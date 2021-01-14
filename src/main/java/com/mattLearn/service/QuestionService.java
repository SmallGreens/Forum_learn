package com.mattLearn.service;

import com.mattLearn.dao.QuestionDAO;
import com.mattLearn.model.Question;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Matt Jan.14th 2021
 */
@Service
public class QuestionService {
    @Autowired
    QuestionDAO questionDAO;
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
}
