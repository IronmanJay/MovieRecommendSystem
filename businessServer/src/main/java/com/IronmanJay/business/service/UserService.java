package com.IronmanJay.business.service;

import com.IronmanJay.business.model.domain.User;
import com.IronmanJay.business.model.request.LoginUserRequest;
import com.IronmanJay.business.model.request.RegisterUserRequest;
import com.IronmanJay.business.utils.Constant;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.util.JSON;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;

// 用于用户具体处理业务服务的服务类
@Service
public class UserService {

    @Autowired
    private MongoClient mongoClient;
    @Autowired
    private ObjectMapper objectMapper;

    // 用于获取User表连接
    private MongoCollection<Document> userCollection;

    private MongoCollection<Document> getUserCollection() {
        if (null == userCollection)
            userCollection = mongoClient.getDatabase(Constant.MONGODB_DATABASE).getCollection(Constant.MONGODB_USER_COLLECTION);
        return userCollection;
    }

    // 注册用户
    public boolean registerUser(RegisterUserRequest request) {
        // 创建一个用户
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(request.getPassword());
        user.setFirst(true);
        user.setTimestamp(System.currentTimeMillis());
        try {
            // 插入一个用户
            getUserCollection().insertOne(Document.parse(objectMapper.writeValueAsString(user)));
            return true;
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return false;
        }
    }

    // 用户登录
    public User loginUser(LoginUserRequest request) {
        // 需要找到这个用户
        User user = findByUsername(request.getUsername());
        if (null == user) {
            return null;
            // 验证密码
        } else if (!user.passwordMatch(request.getPassword())) {
            return null;
        }
        return user;
    }

    // 将document转换成user
    private User documentToUser(Document document) {
        try {
            return objectMapper.readValue(JSON.serialize(document), User.class);
        } catch (JsonParseException e) {
            e.printStackTrace();
            return null;
        } catch (JsonMappingException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    // 将user转换成document
    private Document userToDocument(User user) {
        try {
            Document document = Document.parse(objectMapper.writeValueAsString(user));
            return document;
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return null;
        }
    }

    // 检查用户名是否存在
    public boolean checkUserExist(String username) {
        return null != findByUsername(username);
    }

    // 查找数据库中是否有这个用户
    public User findByUsername(String username) {
        Document user = getUserCollection().find(new Document("username", username)).first();
        if (null == user || user.isEmpty())
            return null;
        return documentToUser(user);
    }

    // 用于更新用户第一次登录选择的电影类别
    public boolean updateUser(User user) {
        getUserCollection().updateOne(Filters.eq("uid", user.getUid()), new Document().append("$set", new Document("first", user.isFirst())));
        getUserCollection().updateOne(Filters.eq("uid", user.getUid()), new Document().append("$set", new Document("prefGenres", user.getPrefGenres())));
        return true;
    }

    // 根据uid查找user
    public User findByUID(int uid) {
        Document user = getUserCollection().find(new Document("uid", uid)).first();
        if (null == user || user.isEmpty())
            return null;
        return documentToUser(user);
    }

    // 删除user
    public void removeUser(String username) {
        getUserCollection().deleteOne(new Document("username", username));
    }

}
