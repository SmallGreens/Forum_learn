package com.mattLearn.dao;

import com.mattLearn.model.Question;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * Modified by Matt on Jan.13rd 2021
 */
@Mapper
public interface QuestionDAO {
    String TABLE_NAME = " question ";
    String INSERT_FIELDS = " title, content, created_date, user_id, comment_count ";
    String SELECT_FIELDS = " id, " + INSERT_FIELDS;

    @Insert({"insert into ", TABLE_NAME, "(", INSERT_FIELDS,
            ") values (#{title},#{content},#{createdDate},#{userId},#{commentCount})"})
    int addQuestion(Question question);

    // 使用 xml 配置文件进行 数据库查询语句与程序接口的映射。
    // 对应的 QuestionDAO.xml 文件位于 resources 文件夹 -》com.mattLearn.dao 文件夹中。
    List<Question> selectLatestQuestions(@Param("userId") int userId, @Param("offset") int offset,
                                         @Param("limit") int limit);
    @Select({"select ", SELECT_FIELDS, " from ", TABLE_NAME, " where id=#{id}"})
    Question SelectById(int id);

}
