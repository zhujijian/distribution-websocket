package cn.monitor.springbootwebsocketdemo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.LongAdder;

@Component
public class WebSocketConnCounter {

    private LongAdder connections = new LongAdder();

    @Autowired
    private SimpMessagingTemplate template;

    public void increment() {
        connections.increment();
        template.convertAndSend("/topic/getResponse", String.valueOf(connections.sum()));
    }

    public void decrement() {
        connections.decrement();
        template.convertAndSend("/topic/getResponse", String.valueOf(connections.sum()));
    }

    public long onlineUsers() {
        return connections.sum();
    }
}
