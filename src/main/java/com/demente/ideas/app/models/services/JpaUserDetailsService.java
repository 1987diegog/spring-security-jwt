package com.demente.ideas.app.models.services;

import com.demente.ideas.app.models.entity.Client;
import com.demente.ideas.app.models.entity.Role;
import com.demente.ideas.app.models.repository.IClientRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service("jpaUserDetailsService")
public class JpaUserDetailsService implements UserDetailsService {

    private Logger logger = LoggerFactory.getLogger(JpaUserDetailsService.class);

    @Autowired
    private IClientRepository clientRepository;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        Client client = clientRepository.findByUsername(username);
        if (client == null) {
            logger.error("Error login: Client not exist: " + username);
            throw new UsernameNotFoundException("Client not exist: " + username);
        }

        List<GrantedAuthority> authorities = new ArrayList<>();
        for (Role role : client.getRoles()) {
            logger.info("Role:".concat(role.getAuthority()));
            authorities.add(new SimpleGrantedAuthority(role.getAuthority()));
        }

        if (authorities.isEmpty()) {
            logger.error("Error login: User " + username + ", dont have any roles");
            throw new UsernameNotFoundException("User " + username + ", dont have any roles");
        }

        return new User(client.getUsername(), client.getPassword(),
                client.getEnabled(), true, true, true, authorities);
    }
}
