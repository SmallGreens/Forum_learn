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
import org.springframework.web.servlet.view.RedirectView;

import javax.net.ssl.HttpsURLConnection;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.websocket.server.PathParam;
import java.util.*;

/**
 * Modified by Matt on Jan-9th 2021
 */
// @Controller
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
    public String helloIndex(HttpSession httpSession){
        StringBuilder sb = new StringBuilder();
        sb.append("Hello world!" + "<br>");
        sb.append(httpSession.getAttribute("msg"));
        return sb.toString();
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

    // for learning the using of template framework Velocity.
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

    @RequestMapping(path = "/request")
    @ResponseBody
    public String request(Model model, HttpServletRequest request,
                          HttpServletResponse response,
                          HttpSession session,
                          // 可以直接在 参数列表中使用 annotation 进行解析
                          @CookieValue("JSESSIONID") String sessionID){
        StringBuilder sb = new StringBuilder();
        sb.append(sessionID + "<br>");

        // 也可以通过 request 对象进行读取
        Cookie[] cookie = request.getCookies();
        for(Cookie c : cookie){
            sb.append(c.getName() + ": " + c.getValue() + "<br>");
        }

        // request 对象中的其他参数，举例
        sb.append(request.getCookies() + "<br>");
        sb.append(request.getRequestURI() + "<br>");
        sb.append(request.getPathInfo() + "<br>");

        // 获取 request 头中各个参数
        // enumeration<T> 是一个 legacy 的接口。新的实现中使用 Iterator<T> 接口
        // 在 java 9 中，添加了一个 asIterator() 方法，来将 Enumeration 转换为 Iterator<T> 类型的对象
        Enumeration<String> e = request.getHeaderNames();
        while(e.hasMoreElements()){
            String name = e.nextElement();
            sb.append(name + " is: " + request.getHeader(name) + "<br>");
        }

        // response 对象可以用于传递参数给用户
        // Header 中的内容以 键值对的形式 出现
        // chrome 中 ctrl+ shift + i，network 查看对应的 request 请求，可以看到 response 回去的信息。
        response.addHeader("TestInfo", "Hello browser!");
        // 同上面的 header 查看方式，只是需要切换到 cookies Tab, 可以查看到 返回的 cookie
        response.addCookie(new Cookie("TestCookie", "Yummy!"));

        return sb.toString();
    }

    @RequestMapping(path = {"/redirect/{code}"}, method = {RequestMethod.GET})
  //  @ResponseBody
    public RedirectView redirect(@PathVariable("code") int code,
                           HttpSession httpSession){
        // 可以通过 httpSession 向其他页面传递 信息。
        httpSession.setAttribute("msg", "Say hello from the redirect page~");

        // 直接跳转到主页，302 跳转
        // 如果 有 @ResponseBody， 则 `return "redirect:/";` 不 work。而 RedirectView works.
        // see reference: https://stackoverflow.com/questions/22239526/spring-redirect-not-working-only-showing-redirect-url-in-browser/22240114
        // return "redirect:/";
        return new RedirectView("/");
    }

    @RequestMapping(path = "/testError", method = {RequestMethod.GET})
    @ResponseBody
    public String testError(@RequestParam("key") String key){
        if(key.equals("admin")) return "right";
        // 抛出下面的 exception 会直接跳到 ExceptionHandler 进行处理。
        throw new IllegalArgumentException("Wrong parameter!");
    }

    // 对于 spring MVC 外的 Exception 或 Spring MVC 没有处理的 Exception
    @ExceptionHandler()
    @ResponseBody
    public String error(Exception e){
        return "Error" + e.getMessage();
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
