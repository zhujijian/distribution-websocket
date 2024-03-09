package cn.monitor.springbootwebsocketdemo.config;

import cn.monitor.springbootwebsocketdemo.model.User;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;

import java.util.LinkedList;
import java.util.Map;

public class UserInterceptor implements ChannelInterceptor {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
            Object raw = message.getHeaders().get(SimpMessageHeaderAccessor.NATIVE_HEADERS);
            System.out.println(raw);
            if (raw instanceof Map) {
                //这里就是token

                Object name = ((Map) raw).get("username");
                if (name instanceof LinkedList) {
                    // 设置当前访问器的认证用户
                    String token = ((LinkedList) name).get(0).toString();

                    User user = new User();
                    user.setUsername(token);
                    accessor.setUser(user);
                    redisTemplate.opsForList().leftPush("userInfos", JSONObject.toJSONString(user));
                }
            }
        }
        return message;
    }

    @Override
    public void postSend(Message<?> message, MessageChannel channel, boolean sent) {
        
    }

    @Override
    public void afterSendCompletion(Message<?> message, MessageChannel channel, boolean sent, Exception ex) {
        
    }

    @Override
    public boolean preReceive(MessageChannel channel) {

        return false;
    }

    @Override
    public Message<?> postReceive(Message<?> message, MessageChannel channel) {
        
        return null;
    }

    @Override
    public void afterReceiveCompletion(Message<?> message, MessageChannel channel, Exception ex) {
        
    }

}
