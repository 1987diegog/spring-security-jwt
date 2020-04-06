package com.demente.ideas.app.models.entity;

import java.io.Serializable;
import java.util.List;

public class ClientList implements Serializable {

    private List<Client> clients;

    public ClientList() {
    }

    public ClientList(List<Client> clients) {
        this.clients = clients;
    }

    public List<Client> getClients() {
        return clients;
    }
}
