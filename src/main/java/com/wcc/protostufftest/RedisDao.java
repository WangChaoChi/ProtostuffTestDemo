package com.wcc.protostufftest;

import com.dyuproject.protostuff.LinkedBuffer;
import com.dyuproject.protostuff.ProtostuffIOUtil;
import com.dyuproject.protostuff.runtime.RuntimeSchema;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

/**
 * @author wangcc
 * @decription:
 * @date 2018/4/10 13:46
 */
public class RedisDao {
    private JedisPool jedisPool;

    public RedisDao(String ip, int port) {
        this.jedisPool = new JedisPool(ip, port);
    }

    //得到User的schema
    private RuntimeSchema<User> schema = RuntimeSchema.createFrom(User.class);

    //取User
    public User getUser(String id) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            String key = "User" + id;
            byte[] bytes = jedis.get(key.getBytes());
            if (bytes != null) {
                User user = schema.newMessage();//空对象
                //byte[] -> 反序列化 -> Object(User)
                ProtostuffIOUtil.mergeFrom(bytes, user, schema);
                return user;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return null;
    }

    //存User
    public String setUser(User user) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            String key = "User" + user.getId();
            //Object(User) -> 序列化（byte[]）
            byte[] bytes = ProtostuffIOUtil.toByteArray(user, schema,
                    LinkedBuffer.allocate(LinkedBuffer.DEFAULT_BUFFER_SIZE));
            int timeOut = 60 * 60;//超时缓存，一小时（单位是秒）
            String result = jedis.setex(key.getBytes(), timeOut, bytes);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return null;
    }

    //取User集合
    public List<User> getUserList(String key) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            byte[] bytes = jedis.get(key.getBytes());
            List<User> users = ProtostuffIOUtil.parseListFrom(new ByteArrayInputStream(bytes), schema);
            return users;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return null;
    }

    //存user集合
    public String setUserList(List<User> users, String key) {
        Jedis jedis = null;
        ByteArrayOutputStream bos = null;
        try {
            jedis = jedisPool.getResource();
            bos = new ByteArrayOutputStream();
            ProtostuffIOUtil.writeListTo(bos, users, schema,
                    LinkedBuffer.allocate(LinkedBuffer.DEFAULT_BUFFER_SIZE));
            int timeOut = 60 * 60;
            String result = jedis.setex(key.getBytes(), timeOut, bos.toByteArray());
            return result;
        } catch (Exception e) {
            if (bos != null) {
                try {
                    bos.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
            if (jedis != null) {
                jedis.close();
            }
        }
        return null;
    }
}
