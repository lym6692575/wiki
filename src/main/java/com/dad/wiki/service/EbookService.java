package com.dad.wiki.service;


import com.dad.wiki.domain.Ebook;
import com.dad.wiki.domain.EbookExample;
import com.dad.wiki.mapper.EbookMapper;
import com.dad.wiki.req.EbookReq;
import com.dad.wiki.resp.EbookResp;
import com.dad.wiki.resp.PageResp;
import com.dad.wiki.utils.CopyUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.util.List;

@Service
public class EbookService {

    private static final Logger Log = LoggerFactory.getLogger(EbookService.class);

    @Resource
    private EbookMapper ebookMapper;

    public PageResp<EbookResp> list(EbookReq req) {

        EbookExample ebookExample = new EbookExample();
        EbookExample.Criteria criteria = ebookExample.createCriteria();
        if(!ObjectUtils.isEmpty(req.getName())) {
            criteria.andNameLike("%"+ req.getName() +"%");
        }
        // 分页帮助
        PageHelper.startPage(req.getPage(),req.getSize());
        List<Ebook> ebookList = ebookMapper.selectByExample(ebookExample);

        PageInfo<Ebook> pageInfo = new PageInfo<>(ebookList);
        Log.info("总行数:{}",pageInfo.getTotal());
        Log.info("总页数:{}",pageInfo.getPages());

//        List<EbookResp> respList = new ArrayList<>();
//        for (Ebook ebook : ebookList) {
////            EbookResp ebookResp = new EbookResp();
////            BeanUtils.copyProperties(ebook,ebookResp);
//
//            EbookResp ebookResp = CopyUtil.copy(ebook, EbookResp.class);
//            respList.add(ebookResp);
//        }
        //列表复制
        List<EbookResp> list = CopyUtil.copyList(ebookList, EbookResp.class);


        PageResp<EbookResp> pageResp = new PageResp();
        pageResp.setTotal(pageInfo.getTotal());
        pageResp.setList(list);
        return  pageResp;
    }
}
