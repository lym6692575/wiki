package com.dad.wiki.service;


import com.dad.wiki.domain.Doc;
import com.dad.wiki.domain.DocExample;
import com.dad.wiki.mapper.DocMapper;
import com.dad.wiki.req.DocQueryReq;
import com.dad.wiki.req.DocSaveReq;
import com.dad.wiki.resp.DocQueryResp;
import com.dad.wiki.resp.PageResp;
import com.dad.wiki.utils.CopyUtil;
import com.dad.wiki.utils.SnowFlake;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.util.List;

@Service
public class DocService {

    private static final Logger Log = LoggerFactory.getLogger(DocService.class);

    @Resource
    private DocMapper docMapper;

    @Resource
    private SnowFlake snowFlake;

    public List<DocQueryResp> all() {

        DocExample docExample = new DocExample();
        docExample.setOrderByClause("sort asc");
        List<Doc> docList = docMapper.selectByExample(docExample);

        //列表复制
        List<DocQueryResp> list = CopyUtil.copyList(docList, DocQueryResp.class);

        return list;
    }

    public PageResp<DocQueryResp> list(DocQueryReq req) {

        DocExample docExample = new DocExample();
        docExample.setOrderByClause("sort asc");
        DocExample.Criteria criteria = docExample.createCriteria();

        // 分页帮助
        PageHelper.startPage(req.getPage(), req.getSize());
        List<Doc> docList = docMapper.selectByExample(docExample);

        PageInfo<Doc> pageInfo = new PageInfo<>(docList);
        Log.info("总行数:{}", pageInfo.getTotal());
        Log.info("总页数:{}", pageInfo.getPages());

//        List<EbookResp> respList = new ArrayList<>();
//        for (Ebook ebook : ebookList) {
////            EbookResp ebookResp = new EbookResp();
////            BeanUtils.copyProperties(ebook,ebookResp);
//
//            EbookResp ebookResp = CopyUtil.copy(ebook, EbookResp.class);
//            respList.add(ebookResp);
//        }
        //列表复制
        List<DocQueryResp> list = CopyUtil.copyList(docList, DocQueryResp.class);


        PageResp<DocQueryResp> pageResp = new PageResp();
        pageResp.setTotal(pageInfo.getTotal());
        pageResp.setList(list);
        return pageResp;
    }

    /**
     * 保存
     * @param req
     */
    public void save(DocSaveReq req) {
        Doc doc = CopyUtil.copy(req, Doc.class);
        // 判断id是否为空来判断是新增还是编辑
        if (ObjectUtils.isEmpty(req.getId())) {
            //新增
            doc.setId(snowFlake.nextId());
            docMapper.insert(doc);
        } else {
            //编辑
            docMapper.updateByPrimaryKey(doc);
        }
    }

    /**
     * 保存
     * @param id
     */
    public void delete(Long id) {
        docMapper.deleteByPrimaryKey(id);
    }

    public void delete(List<String> ids) {
        DocExample docExample = new DocExample();
        DocExample.Criteria criteria = docExample.createCriteria();
        criteria.andIdIn(ids);
        docMapper.deleteByExample(docExample);
    }
}
