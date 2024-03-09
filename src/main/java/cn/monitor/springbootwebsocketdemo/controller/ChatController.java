package cn.monitor.springbootwebsocketdemo.controller;

import cn.monitor.springbootwebsocketdemo.model.ChatMessage;
import cn.monitor.springbootwebsocketdemo.model.User;
import cn.monitor.springbootwebsocketdemo.service.ChatService;
import cn.monitor.springbootwebsocketdemo.util.JsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@Controller
public class ChatController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ChatController.class);

    @Value("${redis.channel.msgToAll}")
    private String msgToAll;

    @Value("${redis.set.onlineUsers}")
    private String onlineUsers;

    @Value("${redis.channel.userStatus}")
    private String userStatus;

    @Autowired
    private ChatService chatService;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;


    @MessageMapping("/chat.sendMessage")
    @SendTo("/topic/public1")
    public ChatMessage sendMessage(@Payload ChatMessage chatMessage) {
        try {
            redisTemplate.convertAndSend(msgToAll, JsonUtil.parseObjToJson(chatMessage));
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
        if ("lisi".equals(chatMessage.getContent())){
            chatMessage.setContent(UUID.randomUUID().toString());
        }
        return chatMessage;
    }

    @MessageMapping("/chat1.addUser")
    @SendTo("/topic/public1")
    public ChatMessage addUser(@Payload ChatMessage chatMessage, SimpMessageHeaderAccessor headerAccessor) {

        try {
            headerAccessor.getSessionAttributes().put("username", chatMessage.getSender());
            redisTemplate.opsForSet().add(onlineUsers, chatMessage.getSender());
            redisTemplate.convertAndSend(userStatus, JsonUtil.parseObjToJson(chatMessage));
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
        if ("lisi".equals(chatMessage.getSender())){
            chatMessage.setContent(UUID.randomUUID().toString());
        }
        return chatMessage;
    }

   // 服务端 添加MessageMapping注解，@SendoTo对应前台的订阅地址。这里也可以用更灵活的方式，
    // 使用spring的SimpMessagingTemplate模板，messagingTemplate.convertAndSend方法广播式通信。
   //var socket = new SockJS(“http://172.16.10.156:80/cmi/webSocketServer“),var stompClient = Stomp.over(socket);
    /*广播*/
    @MessageMapping("/broadcast")
    @SendTo("/topic/getResponse")
    public ResponseEntity topic() throws Exception {
        System.out.println("---");
        return new ResponseEntity(HttpStatus.OK);
    }

    /*点对点通信*/
    @MessageMapping(value = "/p1")
    public void templateTest1(Principal principal) {
        List<String> userInfos = redisTemplate.opsForList().range("userInfos", 0, -1);
        List<User> users = new ArrayList<>();
        for (String s:userInfos) {
            User user = JsonUtil.parseJsonToObj(s, User.class);
            users.add(user);
        }

        int i = 1;
        for (User user :users) {
            log.info("用户" + i++ + "---" + user.getUsername());
        }
        //发送消息给指定用户
        messagingTemplate.convertAndSendToUser("test", "/queue/message1","服务器主动推的数据");
    }




}
