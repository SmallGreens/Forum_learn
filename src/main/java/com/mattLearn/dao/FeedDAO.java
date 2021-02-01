package com.mattLearn.dao;

import com.mattLearn.model.Comment;
import com.mattLearn.model.Feed;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * @author Matt
 * @date 2021/1/29 16:10
 */
@Mapper
public interface FeedDAO {
    String TABLE_NAME = "feed ";
    String INSERT_FIELDS = " user_id, data, created_date, type ";
    String SELECT_FIELDS = " id, " + INSERT_FIELDS;

    @Insert({"insert into ", TABLE_NAME, " (", INSERT_FIELDS, ") values (#{userId}, #{data}, #{createdDate}, #{type})"})
    int addFeed(Feed feed);

    // 拉模式，获取我关注的用户的 新活动-- xml
    List<Feed> selectUserFeeds(@Param("maxId") int maxId,                    // 增量刷新模式，当前获取到的最新的 id
                               @Param("userIds") List<Integer> userIds,     // 我关注的用户列表
                               @Param("count") int count);                  // feed 的数量

    @Select({"Select ", SELECT_FIELDS, " from ", TABLE_NAME, "where Id = #{id}"})
    Feed getFeedById(int id);
}
