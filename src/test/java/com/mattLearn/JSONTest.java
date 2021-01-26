package com.mattLearn;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.mattLearn.async.EventModel;
import com.mattLearn.async.EventType;
import com.mattLearn.model.EntityType;

/**
 * @author Matt
 * @date 2021/1/26 21:20
 */
public class JSONTest {

    EventModel eventModel = new EventModel(EventType.LOGIN).setExt("username","username")
                        .setExt("email","receiver@gmail.com").setActorId(10).setEntityType(EntityType.ENTITY_COMMENT);
    String json = JSONObject.toJSONString(eventModel);

    public static void main(String[] args) {
        JSONTest test = new JSONTest();
        System.out.println(test.json);
    }

}
