package com.mattLearn.async.handler;

import com.mattLearn.async.EventHandler;
import com.mattLearn.async.EventModel;
import com.mattLearn.async.EventType;
import com.mattLearn.model.EntityType;
import com.mattLearn.model.Message;
import com.mattLearn.model.User;
import com.mattLearn.service.MessageService;
import com.mattLearn.service.UserService;
import com.mattLearn.util.ForumUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * @author Matt
 * @date 2021/1/28 20:11
 */
@Component
public class FollowHandler implements EventHandler {

    @Autowired
    MessageService messageService;

    @Autowired
    UserService userService;

    @Override
    public void doHandle(EventModel event) {
        Message message = new Message();
        message.setFromId(ForumUtil.SYSTEM_USERID);
        message.setToId(event.getEntityOwnerId());
        message.setCreatedDate(new Date());
        User user = userService.getUser(event.getActorId());

        if(event.getEntityType() == EntityType.ENTITY_QUESTION){
            message.setContent("User " + user.getName() + " follows your question: http://127.0.0.1:8080/question/"
                    + event.getEntityId());
        } else if(event.getEntityType() == EntityType.ENTITY_USER){
            message.setContent("User " + user.getName() + " follows you! ");
        }
        messageService.addMessage(message);
    }

    @Override
    public List<EventType> getSupportEventTypes() {
        return Arrays.asList(EventType.FOLLOW);
    }
}
