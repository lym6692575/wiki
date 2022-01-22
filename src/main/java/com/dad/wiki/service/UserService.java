package com.dad.wiki.service;


import com.dad.wiki.domain.User;
import com.dad.wiki.domain.UserExample;
import com.dad.wiki.mapper.UserMapper;
import com.dad.wiki.req.UserQueryReq;
import com.dad.wiki.req.UserSaveReq;
import com.dad.wiki.resp.UserQueryResp;
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
public class UserService {

    private static final Logger Log = LoggerFactory.getLogger(UserService.class);

    @Resource
    private UserMapper userMapper;

    @Resource
    private SnowFlake snowFlake;

    public PageResp<UserQueryResp> list(UserQueryReq req) {

        UserExample userExample = new UserExample();
        UserExample.Criteria criteria = userExample.createCriteria();

        if (!ObjectUtils.isEmpty(req.getLoginName())) {
            criteria.andLoginNameEqualTo(req.getLoginName());
        }
        // 分页帮助
        PageHelper.startPage(req.getPage(), req.getSize());
        List<User> userList = userMapper.selectByExample(userExample);

        PageInfo<User> pageInfo = new PageInfo<>(userList);
        Log.info("总行数:{}", pageInfo.getTotal());
        Log.info("总页数:{}", pageInfo.getPages());

//        List<UserResp> respList = new ArrayList<>();
//        for (User user : userList) {
////            UserResp userResp = new UserResp();
////            BeanUtils.copyProperties(user,userResp);
//
//            UserResp userResp = CopyUtil.copy(user, UserResp.class);
//            respList.add(userResp);
//        }
        //列表复制
        List<UserQueryResp> list = CopyUtil.copyList(userList, UserQueryResp.class);


        PageResp<UserQueryResp> pageResp = new PageResp();
        pageResp.setTotal(pageInfo.getTotal());
        pageResp.setList(list);
        return pageResp;
    }

    /**
     * 保存
     * @param req
     */
    public void save(UserSaveReq req) {
        User user = CopyUtil.copy(req, User.class);
        // 判断id是否为空来判断是新增还是编辑
        if (ObjectUtils.isEmpty(req.getId())) {
            //新增
            user.setId(snowFlake.nextId());
            userMapper.insert(user);
        } else {
            //编辑
            userMapper.updateByPrimaryKey(user);
        }
    }

    /**
     * 保存
     * @param id
     */
    public void delete(Long id) {
        userMapper.deleteByPrimaryKey(id);
    }
}
