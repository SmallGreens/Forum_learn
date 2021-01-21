package com.mattLearn.service;

import com.mattLearn.dao.MessageDAO;
import com.mattLearn.model.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import java.util.List;

/**
 * @author Matt
 * @date 2021/1/20 22:24
 */
@Service
public class MessageService {

    @Autowired
    MessageDAO messageDAO;

    @Autowired
    SensitiveWordService sensitiveWordService;

    public int addMessage(Message message){
        message.setContent(HtmlUtils.htmlEscape(message.getContent()));
        message.setContent(sensitiveWordService.filter(message.getContent()));
        return messageDAO.addMessage(message) > 0? message.getId(): 0;
    }

    public List<Message> getConversationDetail(String conversationId, int offset, int limit){
        return messageDAO.getConversationDetail(conversationId, offset, limit);
    }

    public List<Message> getConversationList(int userId){
        return messageDAO.getConversationList(userId);
    }

    public int getConversationUnreadCount(String conversationId){
        return messageDAO.getConversationUnreadCount(conversationId);
    }
}
