package com.mattLearn.async.handler;

import com.mattLearn.async.EventHandler;
import com.mattLearn.async.EventModel;
import com.mattLearn.async.EventType;
import com.mattLearn.model.Message;
import com.mattLearn.model.User;
import com.mattLearn.service.MessageService;
import com.mattLearn.service.UserService;
import com.mattLearn.util.ForumUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * @author Matt
 * @date 2021/1/25 17:27
 */
@Component
public class LikeHandler implements EventHandler {
    private static final Logger logger = LoggerFactory.getLogger(LikeHandler.class);

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
        message.setContent("User " + user.getName() + " thumb your question.");
        messageService.addMessage(message);
    }

    @Override
    public List<EventType> getSupportEventTypes() {
        return Arrays.asList(EventType.LIKE);
    }
}
