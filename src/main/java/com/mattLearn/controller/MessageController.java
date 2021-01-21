package com.mattLearn.controller;

import com.mattLearn.model.HostHolder;
import com.mattLearn.model.Message;
import com.mattLearn.model.User;
import com.mattLearn.model.ViewObject;
import com.mattLearn.service.MessageService;
import com.mattLearn.service.UserService;
import com.mattLearn.util.ForumUtil;
import org.apache.ibatis.annotations.Param;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.swing.text.View;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author Matt
 * @date 2021/1/20 22:30
 */
@Controller
public class MessageController {
    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);

    @Autowired
    HostHolder hostHolder;

    @Autowired
    UserService userService;

    @Autowired
    MessageService messageService;

    @RequestMapping(path = "/msg/addMessage", method = {RequestMethod.POST})
    @ResponseBody
    public String addMessage(@Param("toName") String toName,
                             @Param("content") String content){
        try {
            if(hostHolder.getUser() == null){
                return ForumUtil.getJSONString(999,"未登录");
            }

            User user = userService.getUser(toName);
            if(user == null){
                return ForumUtil.getJSONString(999, "用户不存在");
            }
            Message message = new Message();
            message.setContent(content);
            message.setCreatedDate(new Date());
            message.setFromId(hostHolder.getUser().getId());
            message.setToId(user.getId());
            messageService.addMessage(message);
            // 返回 0 表示成功添加
            return ForumUtil.getJSONString(0);


        }catch (Exception e){
            logger.error("Send message fail!" + e.getMessage());
            return ForumUtil.getJSONString(1, "发送失败");
        }
    }

    @RequestMapping(path = "/msg/list", method = {RequestMethod.GET})
    public String getConversationList(Model model){
        if(hostHolder.getUser() == null){
            return  "redirect:/reglogin";
        }
        int localUserId = hostHolder.getUser().getId();
        List<Message> messageList = messageService.getConversationList(localUserId);
        List<ViewObject> conversations = new ArrayList<>();
        for(Message message : messageList){
            ViewObject vo = new ViewObject();
            vo.set("conversation", message);
            if(message.getFromId() == localUserId){
                vo.set("user", userService.getUser(message.getToId()));
            }else{
                vo.set("user", userService.getUser(message.getFromId()));
            }
            vo.set("unread", messageService.getConversationUnreadCount(message.getConversationId()));
            conversations.add(vo);
        }
        model.addAttribute("conversations", conversations);
        return "letter";
    }

    @RequestMapping(path = "/msg/detail", method = {RequestMethod.GET})
    public String getConversationDetail(Model model,
                                        @RequestParam("conversationId") String conversationId){

        List<Message> messageList = messageService.getConversationDetail(conversationId,0,10);
        List<ViewObject> messages = new ArrayList<>();
        for(Message message : messageList){
            ViewObject vo = new ViewObject();
            // 取出消息及关联的用户
            vo.set("message", message);
            if(message.getFromId() == hostHolder.getUser().getId()){
                vo.set("user", userService.getUser(message.getToId()));
            }else{
                vo.set("user", userService.getUser(message.getFromId()));
            }

            messages.add(vo);
        }

        model.addAttribute("messages", messages);
        return "letterDetail";
    }
}
