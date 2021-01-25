package com.mattLearn.dao;

import com.mattLearn.model.Comment;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * @author Matt
 * @date 2021/1/20 16:55
 */

@Mapper
public interface CommentDAO {
    String TABLE_NAME = " comment ";
    String INSERT_FIELDS = " user_id, content, created_date, entity_id, entity_type, status ";
    String SELECT_FIELDS = " id, " + INSERT_FIELDS;

    // 插入评论
    @Insert({"insert into ", TABLE_NAME, " (", INSERT_FIELDS, ") values (#{userId}, #{content}, #{createdDate}, #{entityId}, #{entityType}, #{status})"})
    int addComment(Comment comment);

    // 选择 question 下所有的评论
    @Select({"Select ", SELECT_FIELDS, " from ", TABLE_NAME, "where entity_id = #{entityId} and entity_type=#{entityType} order by created_date desc"})
    List<Comment> selectCommentByEntity(@Param("entityId") int entityId,
                                        @Param("entityType") int entityType);
    // 获取评论的数量
    // 这里 count(id)  是什么意思？？ -- 应该意思是数满足后面条件的 id 的数量
    // 刚开始这里写错了， 注意 @Param("名字") 必须与 #{名字} 相同！！
    @Select({" select count(id) from ", TABLE_NAME, " where entity_id=#{entityId} and entity_type=#{entityType}"})
    int getCommentCount(@Param("entityId") int entityId, @Param("entityType") int entityType);

    @Update({"update ", TABLE_NAME, " set command_count = #{commentCount} where id=#{id}"})
    int updateCommentCount(@Param("id") int id, @Param("commentCount") int commentCount);

    // 可以直接写sql 语句 不用拼接
    // 这里 返回的 int 值是啥？？？
    @Update(" update comment set status=#{status} where id = #{id}")
    int updateStatus(@Param("id") int id, @Param("status") int status);

    @Select({"Select ", SELECT_FIELDS, " from ", TABLE_NAME, "where Id = #{id}"})
    Comment getCommentById(int id);
}
