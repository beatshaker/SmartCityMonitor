package com.inz.inz.seciurity.AdapterImpl;

import com.inz.inz.ExcpetionHandler.DbException;
import com.inz.inz.ExcpetionHandler.ErrorSpecifcation;
import com.inz.inz.entity.BanEntity;
import com.inz.inz.entity.UserRatingEntity;
import com.inz.inz.mapper.UserMapper;
import com.inz.inz.repository.AuthorityRepository;
import com.inz.inz.repository.BanEntityRepository;
import com.inz.inz.repository.UserRatingRepository;
import com.inz.inz.repository.UserRepository;
import com.inz.inz.seciurity.Resource.UserAuthResoruce;
import com.inz.inz.seciurity.Resource.UserResourcePost;
import com.inz.inz.seciurity.adapter.UserAdapter;
import com.inz.inz.seciurity.model.User;
import com.inz.inz.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class UserAdapterImpl implements UserAdapter {

    @Autowired
    UserRepository userRepository;

    @Autowired
    AuthorityRepository authorityRepository;

    @Autowired
    UserService userService;

    @Autowired
    BanEntityRepository banEntityRepository;

    @Autowired
    UserRatingRepository userRatingRepository;

    @Autowired
    UserMapper userMapper;


    @Override
    public User createUser(UserResourcePost userResourcePost) throws DbException {
        User user;
        try {
            user=userMapper.mapUserResourcePostToUser(userResourcePost);
            createRealtions(user);
            user = userRepository.save(user);
        } catch (DataIntegrityViolationException ex) {

            DbException dbException = new DbException();
            dbException.setCaused(ErrorSpecifcation.USEREXIST.getDetails());
            dbException.setCode(ErrorSpecifcation.USEREXIST.getCode());
            throw dbException;
        }
        return user;
    }

    @Override
    public UserAuthResoruce mapUserAuthResource(User user) {
        UserAuthResoruce userAuthResoruce ;

        userAuthResoruce=userMapper.mapToUserAuthResource(user);


        return userAuthResoruce;
    }

    private void createRealtions(User user) throws DbException {
        BanEntity banEntity = new BanEntity();
        UserRatingEntity userRatingEntity = new UserRatingEntity();
        try {
            banEntity.setBanCounter(0);
            banEntity.setBanned(false);
            banEntityRepository.save(banEntity);

            userRatingRepository.save(userRatingEntity);

        } catch (DataIntegrityViolationException ex) {

            DbException dbException = new DbException();
            dbException.setCaused(ErrorSpecifcation.CREATINGERROR.getDetails()+"User");
            dbException.setCode(ErrorSpecifcation.CREATINGERROR.getCode());
            throw dbException;
        }
        user.setBanEntity(banEntity);
        user.setUserRatingEntity(userRatingEntity);
        user.setReportsList(new ArrayList<>());
    }
}