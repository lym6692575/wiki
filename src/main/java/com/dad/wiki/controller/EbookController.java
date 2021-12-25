package com.dad.wiki.controller;

import com.dad.wiki.req.EbookReq;
import com.dad.wiki.resp.CommonResp;
import com.dad.wiki.resp.EbookResp;
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
    public CommonResp list(EbookReq req) {
        CommonResp<List<EbookResp>> resp = new CommonResp<>();
        List<EbookResp> list = ebookService.list(req);
        resp.setContent(list);
        return resp;
    }
}
