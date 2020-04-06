package com.demente.ideas.app.models.services;

import com.demente.ideas.app.models.entity.Client;
import com.demente.ideas.app.models.repository.IClientRepository;
import javassist.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


// Service es un patron del estilo facade, nos sirve para acceder a distintos DAO
@Service
@Primary
// @Primary indica que la implementacion concreta por defecto que debe inyectar Spring
// es esta clase marcada con @Primary, esto es necesario cuando se tienen varias
// implementaciones de una determinada interface, al momento de inyectar la interface
// Spring no sabe cual inyectar a no ser que se le indique.
public class ClientService implements IClientService {

    /*
    private final IClientRepository clientRepository;

    public ClientService(IClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }*/

    @Autowired
    private IClientRepository clientRepository;

    // Es necesario que todos los @Componte tenga un constructor por defecto para que pueda
    // ser inyectado DI (inyeccion de dependencias). En caso de no tener un constructor
    // con parametros no es necesario indicar el mismo (ya que se encuentra implicito)
    // pero si se tiene un constructor con parametros, es necesario indicar el constructor
    // por defecto.

    /**
     * @param client
     * @return
     */
    @Override
    @Transactional
    public Client save(Client client) {
        return this.clientRepository.save(client);
    }

    @Override
    public Client getMockClient() {
        Client client = new Client("1987Diegog","Diego",
                "Gonzalez", "1987diegog@gmail.com");
        return client;
    }

    /**
     * @return
     */
    @Override
    @Transactional(readOnly=true)
    public List<Client> findAll() {
        List<Client> clientList = this.clientRepository.findAll();
        return clientList;
    }

    @Override
    @Transactional(readOnly=true)
    public Client find(Long id) throws NotFoundException {
        return this.clientRepository.findById(id).orElseThrow(() -> new NotFoundException("Client not found"));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        clientRepository.deleteById(id);
    }
}
