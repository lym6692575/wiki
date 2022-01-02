package com.dad.wiki.controller;

import com.dad.wiki.req.EbookQueryReq;
import com.dad.wiki.req.EbookSaveReq;
import com.dad.wiki.resp.CommonResp;
import com.dad.wiki.resp.EbookQueryResp;
import com.dad.wiki.resp.PageResp;
import com.dad.wiki.service.EbookService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@RequestMapping("/ebook")
public class EbookController {

    @Resource
    private EbookService ebookService;

    @GetMapping("/ebook")
    public String hello() {
        return "Hello ebook!";
    }

    @GetMapping("/list")
    public CommonResp list(EbookQueryReq req) {
        CommonResp<PageResp<EbookQueryResp>> resp = new CommonResp<>();
        PageResp<EbookQueryResp> list = ebookService.list(req);
        resp.setContent(list);
        return resp;
    }

    @PostMapping("/save")
    public CommonResp save(@RequestBody EbookSaveReq req) {
        CommonResp resp = new CommonResp<>();
        ebookService.save(req);
        return resp;
    }
}
