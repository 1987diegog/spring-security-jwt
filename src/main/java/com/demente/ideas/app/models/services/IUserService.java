package com.demente.ideas.app.models.services;

import com.demente.ideas.app.models.entity.User;
import javassist.NotFoundException;

import java.util.List;

public interface IUserService {

    User save(User user);
    User getMockUser();
    List<User> findAll();
    User find(Long id) throws NotFoundException;
    void delete(Long id);
}
