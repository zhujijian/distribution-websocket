package cn.monitor.springbootwebsocketdemo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.stereotype.Controller;

@Controller
public class WebSocketController {

    @Autowired
    private WebSocketConnCounter connCounter;

    /**
     * 用于初始化数据
     * 初次连接返回数据
     * 只执行一次
     **/
    @SubscribeMapping("welcome")
    public String welcome() {
        return String.valueOf(connCounter.onlineUsers());
    }
}
