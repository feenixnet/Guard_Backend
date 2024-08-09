package com.guard.admin.service.impl;

import java.util.ArrayList;
import java.util.List;

import com.guard.admin.payload.response.DataTableResponse;
import com.guard.admin.service.declaration.ClientService;
import com.guard.admin.utils.constant.Role;
import jakarta.persistence.criteria.Predicate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.google.firebase.auth.FirebaseAuthException;
import com.guard.admin.database.entities.Client;
import com.guard.admin.database.repositories.ClientRepository;

@Service
public class ClientServiceImpl implements ClientService {

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    PasswordEncoder encoder;

    @Autowired
    FirebaseAuthServiceImpl firebaseAuthServiceImpl;

    @Override
    public Client get(Integer id) {
        return clientRepository.findById(id).get();
    }

    @Override
    public Client create(Client client) throws FirebaseAuthException {
        client.setPassword(encoder.encode(client.getPassword()));
        client.setRole(Role.client);
        clientRepository.save(client);

        firebaseAuthServiceImpl.createUser(client.getEmail(), client.getPassword());

        return client;
    }

    @Override
    public void update(Integer id , Client clientDetail) throws FirebaseAuthException {
        
        Client updateClient = clientRepository.findById(id).get();
        updateClient.setAddress(clientDetail.getAddress());
        updateClient.setEmail(clientDetail.getEmail());
        updateClient.setFirstname(clientDetail.getFirstname());
        updateClient.setLastname(clientDetail.getLastname());
        updateClient.setPhone(clientDetail.getPhone());
        if(!clientDetail.getPassword().isEmpty())
            updateClient.setPassword(encoder.encode(clientDetail.getPassword()));

        clientRepository.save(updateClient);

        firebaseAuthServiceImpl.updateClientUser(id, clientDetail.getEmail(), clientDetail.getPassword());
    }

    @Override
    public void delete(Integer id) throws FirebaseAuthException {
        firebaseAuthServiceImpl.deleteUser(clientRepository.findById(id).get().getEmail());
        clientRepository.deleteById(id);        
    }

    @Override
    public List<Client> getAll() {
        return clientRepository.findAll();
    }

    @Override
    public DataTableResponse<Client> getPage(Integer pageNumber, Integer pageSize, String searchKeyword) {

        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        Specification<Client> specification = (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if(searchKeyword != null) {
                if (!searchKeyword.isEmpty()) {
                    String likePattern = "%" + searchKeyword + "%";
                    predicates.add(criteriaBuilder.or(
                            criteriaBuilder.like(criteriaBuilder.lower(root.get("firstname")), likePattern.toLowerCase()),
                            criteriaBuilder.like(criteriaBuilder.lower(root.get("lastname")), likePattern.toLowerCase()),
                            criteriaBuilder.like(criteriaBuilder.lower(root.get("email")), likePattern.toLowerCase()),
                            criteriaBuilder.like(criteriaBuilder.lower(root.get("address")), likePattern.toLowerCase()),
                            criteriaBuilder.like(criteriaBuilder.lower(root.get("gender")), likePattern.toLowerCase()),
                            criteriaBuilder.like(criteriaBuilder.lower(root.get("company")), likePattern.toLowerCase()),
                            criteriaBuilder.like(criteriaBuilder.lower(root.get("position")), likePattern.toLowerCase())
                    ));
                }
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };

        Page<Client> page = clientRepository.findAll(specification, pageable);

        List<Client> data = new ArrayList<>(page.getContent());

        long pagefiltered = page.getTotalElements();
        int intpageFiltered;
        intpageFiltered = (int) pagefiltered;

        return new DataTableResponse<>(1 , (int)clientRepository.count() , intpageFiltered , data , "name");
    }
}
