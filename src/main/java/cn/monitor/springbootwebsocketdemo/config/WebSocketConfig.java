package cn.monitor.springbootwebsocketdemo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.*;


@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {



    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {


//        registry.addEndpoint("/ws").withSockJS();
        // 允许使用socketJs方式访问，访问点为webSocketServer，允许跨域
        // 在网页上我们就可以通过这个链接
        // http://localhost:8080/webSocketServer
        // 来和服务器的WebSocket连接
        registry.addEndpoint("/webSocketServer1")
                .setAllowedOrigins("*")
//                .addInterceptors(new OriginHandshakeInterceptor())
                .withSockJS();

        //上面介绍的方式前台建立连接还是使用的http或者https的方式，如果要使用ws/wss协议，前后台稍微作一下改造
        //
        //后台 将withSokJS()注释掉
        //
        //registry.addEndpoint("/webSocketServer")
        //                .setAllowedOrigins("*")
        //                .addInterceptors(new SessionAuthHandshakeInterceptor());
        //                //.withSockJS();
        //1
        //2
        //3
        //4
        //前台 构建客户端对象处作一点改动
        //
        //
        //        // var socket = new WebSocket("http://ip:port/context-path/webSocketServer");
        //        //  var stompClient = Stomp.over(socket);
        //        var url = "ws://192.168.100.90:80/cmi/webSocketServer";
        //        var stompClient = Stomp.client(url);

    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        /*
        registry.enableStompBrokerRelay("/topic")
                .setRelayHost("localhost")
                .setRelayPort(61613)
                .setClientLogin("guest")
                .setClientPasscode("guest");
        */

        // 订阅Broker名称
        registry.enableSimpleBroker("/queue", "/topic");
        // 全局使用的消息前缀（客户端订阅路径上会体现出来）
        registry.setApplicationDestinationPrefixes("/app");
        // 点对点使用的订阅前缀（客户端订阅路径上会体现出来），不设置的话，默认也是/user/
        registry.setUserDestinationPrefix("/user/");

    }


    /**
     * 配置客户端入站通道拦截器
     */
    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(createUserInterceptor());

    }


    /*将客户端渠道拦截器加入spring ioc容器*/
    @Bean
    public UserInterceptor createUserInterceptor() {
        return new UserInterceptor();
    }


    @Override
    public void configureWebSocketTransport(WebSocketTransportRegistration registration) {
        registration.setMessageSizeLimit(500 * 1024 * 1024);
        registration.setSendBufferSizeLimit(1024 * 1024 * 1024);
        registration.setSendTimeLimit(200000);
    }




}
