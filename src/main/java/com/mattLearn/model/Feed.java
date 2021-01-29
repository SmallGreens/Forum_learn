package com.mattLearn.model;

import com.alibaba.fastjson.JSONObject;

import java.util.Date;

/**
 * @author Matt
 * @date 2021/1/29 16:01
 *
 * 数据推送- 新鲜事的数据模型
 */
public class Feed {
    private int id;
    private int type;
    private int userId;
    private Date createdDate;
    // JSON 格式
    private String data;
    private JSONObject dataJSON = null;

    public JSONObject getDataJSON() {
        return dataJSON;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
        dataJSON = JSONObject.parseObject(data);
    }


    /**
     * 添加 该方法主要是为了方便 velocity 进行 解析，
     * 在 velocity 中， $obj.xxx => 会被解析为: obj.getXX() 或 obj.get("XXX") 或 obj.isXXX()
     * @param key
     * @return
     */
    public String get(String key){
        return dataJSON == null ? null : dataJSON.getString(key);
    }
}
