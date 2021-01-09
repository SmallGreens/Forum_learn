package com.mattLearn.controller;

import com.mattLearn.model.Question;
import com.mattLearn.model.Student;
import com.mattLearn.model.ViewObject;
import com.mattLearn.service.QuestionService;
import com.mattLearn.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.websocket.server.PathParam;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Modified by Matt on Jan-9th 2021
 */
@Controller
public class HomeController {
    private static final Logger logger = LoggerFactory.getLogger(HomeController.class);

    @Autowired
    QuestionService questionService;

    @Autowired
    UserService userService;
/*
    private List<ViewObject> getQuestions(int userId, int offset, int limit) {
        List<Question> questionList = questionService.getLatestQuestions(userId, offset, limit);
        List<ViewObject> vos = new ArrayList<>();
        for (Question question : questionList) {
            ViewObject vo = new ViewObject();
            vo.set("question", question);
            vo.set("user", userService.getUser(question.getUserId()));
            vos.add(vo);
        }
        return vos;
    }

*/
    // 路径解析
    @RequestMapping(path = {"/", "/index"})
    @ResponseBody
    public String helloIndex(){
        return "Hello world2";
    }

    // 使用 PathVariable 解析路径， RequestParam 解析 request url 中带有的 request 参数
    @RequestMapping(path = {"/profile/{userID}"}, method = {RequestMethod.POST})
    @ResponseBody
    public String profile(@PathVariable(value = "userID") int userID,
                        // 使用 default value 设定 默认值
                        @RequestParam(value = "type", defaultValue = "0") int type,
                        @RequestParam(value = "id", defaultValue = "idString") String id){
        return String.format("Profile of user %d, with type = %d, and id = %s", userID, type,id);
    }


    @RequestMapping(path = "/vm", method = {RequestMethod.GET})
    public String template(Model model){
        model.addAttribute("value1", "hahahahahhahahahahahaha");
        Map<String, String> map = new HashMap<>();
        for(int i = 0; i < 10; ++i){
            map.put(String.valueOf(i), String.valueOf(i*i));
        }
        model.addAttribute("map", map);
        model.addAttribute("student", new Student("Xiaoming"));
        return "home";
    }


/*
    @RequestMapping(path = {"/", "/index"}, method = {RequestMethod.GET, RequestMethod.POST})
    public String index(Model model,
                        @RequestParam(value = "pop", defaultValue = "0") int pop) {
        model.addAttribute("vos", getQuestions(0, 0, 10));
        return "index";
    }


    @RequestMapping(path = {"/user/{userId}"}, method = {RequestMethod.GET, RequestMethod.POST})
    public String userIndex(Model model, @PathVariable("userId") int userId) {
        model.addAttribute("vos", getQuestions(userId, 0, 10));
        return "index";
    }
     */


}
