package com.hyy.boot.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Description TODO
 * @Date 2019/11/20 12:34
 * @Author huangyangyang
 */
@RestController
@RequestMapping("index")
public class HelloController {

    @RequestMapping("hello")
    public String hello(String[] args) {
        return "hello";
    }

}
