package com.mattLearn.service;

import com.mattLearn.dao.FeedDAO;
import com.mattLearn.model.Feed;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Matt
 * @date 2021/1/29 16:31
 */

@Service
public class FeedService {

    @Autowired
    FeedDAO feedDAO;

    public List<Feed> getUserFeeds(int maxId, List<Integer> userIds, int count){
        return  feedDAO.selectUserFeeds(maxId, userIds, count);
    }

    public boolean addFeed(Feed feed){
        return feedDAO.addFeed(feed) > 0;
    }

    public Feed getById(int id){
        return feedDAO.getFeedById(id);
    }
}
