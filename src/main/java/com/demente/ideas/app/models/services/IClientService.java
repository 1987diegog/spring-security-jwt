package com.demente.ideas.app.models.services;

import com.demente.ideas.app.models.entity.Client;
import javassist.NotFoundException;

import java.util.List;

public interface IClientService {

    Client save(Client client);
    Client getMockClient();
    List<Client> findAll();
    Client find(Long id) throws NotFoundException;
    void delete(Long id);
}
