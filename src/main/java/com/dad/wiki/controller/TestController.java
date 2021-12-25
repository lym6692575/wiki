package com.dad.wiki.controller;

import com.dad.wiki.domain.Test;
import com.dad.wiki.service.TestService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

@RestController
public class TestController {

    @Resource
    private TestService testService;

    @GetMapping("/hello")
    public String hello() {
        return "Hello world!";
    }

    @PostMapping("/hello/post")
    public String helloPost(String name) {
        return "Hello World,Post," + name;
    }

    @GetMapping("/test/list")
    public List<Test> list() {
        return testService.list();
    }
}
