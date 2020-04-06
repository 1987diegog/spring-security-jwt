package com.demente.ideas.app.controllers;

import com.demente.ideas.app.models.entity.ClientList;
import com.demente.ideas.app.models.services.IClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/clients")
public class ClientRestController {

    @Autowired
    private IClientService clientService;

    @GetMapping()
    public ClientList getClients() {
        return new ClientList(clientService.findAll());
    }
}
