package cn.monitor.springbootwebsocketdemo.listener;

import cn.monitor.springbootwebsocketdemo.controller.WebSocketConnCounter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectEvent;

@Component
public class WebSocketConnectListener implements ApplicationListener<SessionConnectEvent> {

    private WebSocketConnCounter counter;

    @Autowired
    public WebSocketConnectListener(WebSocketConnCounter counter) {
        this.counter = counter;
    }

    @Override
    public void onApplicationEvent(SessionConnectEvent event) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
        String sessionId = accessor.getSessionId();
        System.out.println("sessionId：" + sessionId + " 已连接");
        counter.increment();
    }
}
