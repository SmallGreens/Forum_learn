package com.mattLearn;

import com.mattLearn.aspect.LogAspect;
import com.mattLearn.dao.QuestionDAO;
import com.mattLearn.dao.UserDAO;
import com.mattLearn.model.Question;
import com.mattLearn.model.User;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Date;
import java.util.Random;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = ForumApplication.class)
@Sql("/init-schema.sql")  // 已经初始化过了。
public class InitDatabaseTests {
    @Autowired
    UserDAO userDAO;
    @Autowired
    QuestionDAO questionDAO;

    private static final Logger logger = LoggerFactory.getLogger(InitDatabaseTests.class);

    @Test
    public void contextLoads() {
        Random random = new Random();
        for (int i = 0; i < 11; ++i) {
            User user = new User();
            // 设置头像为 github 头像，后面的数字为一个随机数，从而随机挑选一个头像
            user.setHeadUrl(String.format("https://avatars2.githubusercontent.com/u/%d", random.nextInt(10000)));
            user.setName(String.format("USER%d", i));
            user.setPassword("");
            user.setSalt("");
            userDAO.addUser(user);
        }

        User user1;
        user1 = userDAO.selectById(8);
        logger.info("user8's head url is: " + user1.getHeadUrl());
        user1 = userDAO.selectById(6);
        logger.info("user6's head url is: " + user1.getHeadUrl());

    for(int i = 0; i < 10; ++i){
        Question question = new Question();
        question.setCommentCount(i);
        Date date = new Date();
        date.setTime(date.getTime()+ 1000*3600*i);
        question.setCreatedDate(date);
        question.setUserId(i+1);
        question.setTitle(String.format("Title {%d}", i));
        question.setContent(String.format("hahahahhah content of %d", i));
        questionDAO.addQuestion(question);


    }




    }
}
//            user.setPassword("newpassword");
//            userDAO.updatePassword(user);
//
//            Question question = new Question();
//            question.setCommentCount(i);
//            Date date = new Date();
//            date.setTime(date.getTime() + 1000 * 3600 * 5 * i);
//            question.setCreatedDate(date);
//            question.setUserId(i + 1);
//            question.setTitle(String.format("TITLE{%d}", i));
//            question.setContent(String.format("Balaababalalalal Content %d", i));
//            questionDAO.addQuestion(question);
//        }

//        Assert.assertEquals("newpassword", userDAO.selectById(1).getPassword());
//        userDAO.deleteById(1);
//        Assert.assertNull(userDAO.selectById(1));
//    }
//}
