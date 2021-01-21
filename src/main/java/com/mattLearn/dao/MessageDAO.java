package com.mattLearn.dao;

import com.mattLearn.model.Message;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @author Matt
 * @date 2021/1/20 22:11
 */

@Mapper
public interface MessageDAO {
    String TABLE_NAME = " message ";
    String INSERT_FIELDS = " from_id, to_id, content, created_date, has_read, conversation_id";
    String SELECT_FIELDS = " id, " + INSERT_FIELDS;

    @Insert({" insert into ", TABLE_NAME, "(", INSERT_FIELDS, ") values (#{fromId}, #{toId}, #{content}, #{createdDate}, #{hasRead}, #{conversationId})"})
    int addMessage(Message message);

    @Select({" select ", SELECT_FIELDS, " from ", TABLE_NAME, " where conversation_id = #{conversationId} order by created_date desc limit #{offset}, #{limit}"})
    List<Message> getConversationDetail(@Param("conversationId") String conversationId,
                                        @Param("offset") int offset,
                                        @Param("limit") int limit);

    // select *, count(id) as cnt from (select * from message order by created_date desc) tt group by conversation_id order by created_date desc;
    // 注意，由于这里需要实现 从数据库到 model 对象的映射，做了一个小 trick，将 count(id) 这个值放到了 model 类 message 的 id 中
    @Select({" select ", INSERT_FIELDS, " , count(id) as id from ( select * from ", TABLE_NAME, " where from_id=#{userId} or to_id=#{userId} " +
            " order by created_date desc) tt group by conversation_id order by created_date desc"})
    List<Message> getConversationList(@Param("userId") int userId);


    @Select({" select count(id) from ", TABLE_NAME, " where has_read=0 and conversation_id=#{conversationId}"})
    int getConversationUnreadCount(@Param("conversationId") String conversationId);
}
