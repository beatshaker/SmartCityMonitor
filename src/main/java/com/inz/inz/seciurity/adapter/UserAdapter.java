package com.inz.inz.seciurity.adapter;

import com.inz.inz.exceptionhandler.DbException;
import com.inz.inz.seciurity.Resource.UserAuthResoruce;
import com.inz.inz.seciurity.Resource.UserRank;
import com.inz.inz.seciurity.Resource.UserResourcePost;
import com.inz.inz.seciurity.model.User;

import java.util.List;

public interface UserAdapter {
    User createUser(UserResourcePost user) throws DbException;

    UserAuthResoruce mapUserAuthResource(User user);

    List<UserRank> getRank();

    void sendNewPassword(String email) throws DbException;
}
