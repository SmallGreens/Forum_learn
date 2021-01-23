package com.mattLearn.controller;

import com.mattLearn.model.EntityType;
import com.mattLearn.model.HostHolder;
import com.mattLearn.service.LikeService;
import com.mattLearn.util.ForumUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author Matt
 * @date 2021/1/23 14:49
 */
@Controller
public class LikeController {

    @Autowired
    LikeService likeService;

    @Autowired
    HostHolder hostHolder;

    // Note: 赞踩以 post 形式实现。
    @RequestMapping(path = "/like", method = {RequestMethod.POST})
    @ResponseBody   // ajax 请求
    public String like(@RequestParam("commentId") int commentId){
        if(hostHolder.getUser() == null){
            return ForumUtil.getJSONString(999);
        }
        long likeCount = likeService.like(hostHolder.getUser().getId(), EntityType.ENTITY_COMMENT, commentId);
        return ForumUtil.getJSONString(0, String.valueOf(likeCount));
    }

    @RequestMapping(path = "/dislike", method = {RequestMethod.POST})
    @ResponseBody   // ajax 请求
    public String dislike(@RequestParam("commentId") int commentId){
        if(hostHolder.getUser() == null){
            return ForumUtil.getJSONString(999);
        }
        long likeCount = likeService.dislike(hostHolder.getUser().getId(), EntityType.ENTITY_COMMENT, commentId);
        return ForumUtil.getJSONString(0, String.valueOf(likeCount));
    }
}
