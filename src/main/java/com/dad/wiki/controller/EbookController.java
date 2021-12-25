package com.dad.wiki.controller;

import com.dad.wiki.domain.Ebook;
import com.dad.wiki.resp.CommonResp;
import com.dad.wiki.service.EbookService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

@RestController
public class EbookController {

    @Resource
    private EbookService ebookService;

    @GetMapping("/ebook")
    public String hello() {
        return "Hello ebook!";
    }

    @GetMapping("/ebook/list")
    public CommonResp list() {
        CommonResp<List<Ebook>> resp = new CommonResp<>();
        List<Ebook> list = ebookService.list();
        resp.setContent(list);
        return resp;
    }
}
