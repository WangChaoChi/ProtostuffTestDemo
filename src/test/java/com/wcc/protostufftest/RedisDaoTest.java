package com.wcc.protostufftest;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * @author wangcc
 * @decription:
 * @date 2018/4/10 14:08
 */
public class RedisDaoTest {

    RedisDao redisDao = new RedisDao("localhost", 6379);

    @Test
    public void setAndGetUser() throws Exception {
        String uId = "123456";
        //Redis存数据成功会返回OK字符串
        String result = redisDao.setUser(new User(uId, "小明", 15, true));
        System.out.println(result);
        User user = redisDao.getUser(uId);
        System.out.println(user);
    }

    @Test
    public void setAndGetUserList() throws Exception {
        String key = "UserList";
        List<User> users = new ArrayList<User>();
        users.add(new User("456789", "Jeck", 25, true));
        users.add(new User("123789", "Rose", 22, false));
        users.add(new User("741852", "Lili", 19, false));
        String result = redisDao.setUserList(users, key);
        System.out.println(result);
        users = redisDao.getUserList(key);
        System.out.println(users);
    }

}