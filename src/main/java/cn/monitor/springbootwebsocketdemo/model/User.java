package cn.monitor.springbootwebsocketdemo.model;

import lombok.Data;

import java.security.Principal;
import java.util.List;

@Data
public class User implements Principal {

    private String username;

    private String password;

    private String role;

    private List<Url> urls;

    @Override
    public String getName() {
        return username;
    }

}
