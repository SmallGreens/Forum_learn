package com.mattLearn.async.handler;

import com.mattLearn.async.EventHandler;
import com.mattLearn.async.EventModel;
import com.mattLearn.async.EventType;
import com.mattLearn.util.MailSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Matt
 * @date 2021/1/26 17:30
 */
@Component
public class LoginExceptionHandler implements EventHandler {
    private static final Logger logger = LoggerFactory.getLogger(LoginExceptionHandler.class);
    @Autowired
    MailSender mailSender;

    @Override
    public void doHandle(EventModel event) {
        Map<String, Object> map = new HashMap<>();
        map.put("username", event.getExt("username"));
        mailSender.sendWithHTMLTemplate(event.getExt("email"),
                "登录异常", "mails/login_exception.html", map);
    }

    @Override
    public List<EventType> getSupportEventTypes() {
        return Arrays.asList(EventType.LOGIN);
    }
}
