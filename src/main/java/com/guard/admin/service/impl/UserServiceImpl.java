package com.guard.admin.service.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.google.firebase.auth.FirebaseAuthException;
import com.guard.admin.database.entities.*;
import com.guard.admin.database.repositories.*;
import com.guard.admin.payload.dto.ChatUser;
import com.guard.admin.service.declaration.ScheduleService;
import com.guard.admin.service.declaration.UserService;
import com.guard.admin.utils.constant.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.guard.admin.payload.response.DataTableResponse;

import jakarta.persistence.criteria.Predicate;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private GuardRepository guardRepository;

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private SiteRepository siteRepository;

    @Autowired
    ScheduleService scheduleService;

    @Autowired
    PasswordEncoder encoder;

    @Autowired
    FirebaseAuthServiceImpl firebaseAuthServiceImpl;

    @Override
    public User getByEmail(String email) {
        return userRepository.findByEmail(email).get();
    }

    @Override
    public User get(Integer id)
    {
        return userRepository.findById(id).get();
    }

    @Override
    public User create(User user) throws FirebaseAuthException {
        user.setPassword(encoder.encode(user.getPassword()));
        userRepository.save(user);

        firebaseAuthServiceImpl.createUser(user.getEmail(), user.getPassword());
        return user;
    }

    @Override
    public User update(Integer id, User userDetail) throws FirebaseAuthException {
        User user = userRepository.findById(id).get();
        user.setEmail(userDetail.getEmail());
        user.setAddress(userDetail.getAddress());
        user.setFirstname(userDetail.getFirstname());
        user.setLastname(userDetail.getLastname());
        user.setRole(userDetail.getRole());
        user.setPhone(userDetail.getPhone());
        if(!userDetail.getPassword().isEmpty())
            user.setPassword(encoder.encode(userDetail.getPassword()));
        userRepository.save(user);

        firebaseAuthServiceImpl.updateClientUser(id, userDetail.getEmail(), userDetail.getPassword());
        return user;
    }

    @Override
    public void delete(Integer id) throws FirebaseAuthException {        

        firebaseAuthServiceImpl.deleteUser(userRepository.findById(id).get().getEmail());
        userRepository.deleteById(id);
    }

    @Override
    public List<User> getAreaManagers() {
        List<User> areaManagers = userRepository.findAllByRole(Role.area);
        List<User> branchManagers = userRepository.findAllByRole(Role.branch);
        List<User> mergedList = new ArrayList<>(branchManagers);
        mergedList.addAll(areaManagers);

        return mergedList;
    }

    @Override
    public List<User> getAll() { return userRepository.findAll(); }

    @Override
    public List<ChatUser> getChatUsersForStaff(Integer id) {
        User currentUser = userRepository.findById(id).get();

        List<ChatUser> finalUsers = new ArrayList<>();

        List<User> admins = userRepository.findAllByRole(Role.admin);
        List<User> branches = userRepository.findAllByRole(Role.branch);
        List<User> areas = userRepository.findAllByRole(Role.area);
        List<User> dispatchers = userRepository.findAllByRole(Role.dispatch);

        for(User user : admins) {
            if(!user.getId().equals(id)) {
                ChatUser chatUser = new ChatUser();
                chatUser.setEmail(user.getEmail());
                chatUser.setFirstname(user.getFirstname());
                chatUser.setLastname(user.getLastname());
                chatUser.setRole("Admin");
                chatUser.setId(user.getId());
                finalUsers.add(chatUser);
            }
        }
        for(User user : branches) {
            if(!user.getId().equals(id)) {
                ChatUser chatUser = new ChatUser();
                chatUser.setEmail(user.getEmail());
                chatUser.setFirstname(user.getFirstname());
                chatUser.setLastname(user.getLastname());
                chatUser.setRole("Branch Manager");
                chatUser.setId(user.getId());
                finalUsers.add(chatUser);
            }
        }

        if(currentUser.getRole().equals(Role.admin) || currentUser.getRole().equals(Role.branch)) {
            for(User user : areas) {
                if(!user.getId().equals(id)) {
                    ChatUser chatUser = new ChatUser();
                    chatUser.setEmail(user.getEmail());
                    chatUser.setFirstname(user.getFirstname());
                    chatUser.setLastname(user.getLastname());
                    chatUser.setRole("Area Manager");
                    chatUser.setId(user.getId());
                    finalUsers.add(chatUser);
                }
            }
            for(User user : dispatchers) {
                if(!user.getId().equals(id)) {
                    ChatUser chatUser = new ChatUser();
                    chatUser.setEmail(user.getEmail());
                    chatUser.setFirstname(user.getFirstname());
                    chatUser.setLastname(user.getLastname());
                    chatUser.setRole("Dispatcher");
                    chatUser.setId(user.getId());
                    finalUsers.add(chatUser);
                }
            }

            List<Client> clientList = clientRepository.findAll();
            for(Client client : clientList) {
                ChatUser chatUser = new ChatUser();
                chatUser.setEmail(client.getEmail());
                chatUser.setFirstname(client.getFirstname());
                chatUser.setLastname(client.getLastname());
                chatUser.setRole("Client");
                chatUser.setId(client.getId());
                finalUsers.add(chatUser);
            }

            List<Guard> guardList = guardRepository.findAll();
            for(Guard guard : guardList) {
                ChatUser chatUser = new ChatUser();
                chatUser.setEmail(guard.getEmail());
                chatUser.setFirstname(guard.getFirstname());
                chatUser.setLastname(guard.getLastname());
                chatUser.setRole("Guard");
                chatUser.setId(guard.getId());
                finalUsers.add(chatUser);
            }
        }
        else {

            List<Site> siteList = siteRepository.findAllByUserId(currentUser.getId());
            Set<Guard> guards= scheduleService.findGuardsSiteList(siteList);
            Set<String> emailSet = new HashSet<>();
            for(Site site : siteList) {
                if(!emailSet.contains(site.getClient().getEmail()))
                {
                    emailSet.add(site.getClient().getEmail());
                    ChatUser chatUser = new ChatUser();
                    chatUser.setEmail(site.getClient().getEmail());
                    chatUser.setFirstname(site.getClient().getFirstname());
                    chatUser.setLastname(site.getClient().getLastname());
                    chatUser.setRole("Client");
                    chatUser.setId(site.getClient().getId());
                    finalUsers.add(chatUser);
                }
            }

            for(Guard guard : guards) {
                ChatUser chatUser = new ChatUser();
                chatUser.setEmail(guard.getEmail());
                chatUser.setFirstname(guard.getFirstname());
                chatUser.setLastname(guard.getLastname());
                chatUser.setRole("Guard");
                chatUser.setId(guard.getId());
                finalUsers.add(chatUser);
            }
        }

        return finalUsers;
    }

    @Override
    public List<ChatUser> getChatUsersForGuards(Integer guardId) {
        Guard currentGuard = guardRepository.findById(guardId).get();

        List<ChatUser> finalUsers = new ArrayList<>();

        List<User> admins = userRepository.findAllByRole(Role.admin);
        List<User> branches = userRepository.findAllByRole(Role.branch);

        for(User user : admins) {
            ChatUser chatUser = new ChatUser();
            chatUser.setEmail(user.getEmail());
            chatUser.setFirstname(user.getFirstname());
            chatUser.setLastname(user.getLastname());
            chatUser.setRole("Admin");
            chatUser.setId(user.getId());
            finalUsers.add(chatUser);
        }

        for(User user : branches) {
            ChatUser chatUser = new ChatUser();
            chatUser.setEmail(user.getEmail());
            chatUser.setFirstname(user.getFirstname());
            chatUser.setLastname(user.getLastname());
            chatUser.setRole("Branch Manager");
            chatUser.setId(user.getId());
            finalUsers.add(chatUser);
        }

        Set<Site> sites = scheduleService.findSitesByGuardId(currentGuard.getId());

        for(Site site : sites)
        {
            ChatUser chatUser = new ChatUser();
            chatUser.setEmail(site.getUser().getEmail());
            chatUser.setFirstname(site.getUser().getFirstname());
            chatUser.setLastname(site.getUser().getLastname());
            chatUser.setRole("Area Manager");
            chatUser.setId(site.getUser().getId());
            finalUsers.add(chatUser);
        }

        for(Site site : sites)
        {
                ChatUser chatUser = new ChatUser();
                chatUser.setEmail(site.getClient().getEmail());
                chatUser.setFirstname(site.getClient().getFirstname());
                chatUser.setLastname(site.getClient().getLastname());
                chatUser.setRole("Client");
                chatUser.setId(site.getId());
                finalUsers.add(chatUser);
        }

        return finalUsers;
    }

    @Override
    public List<ChatUser> getChatUsersForClients(Integer clientId) {
        Client currentClient = clientRepository.findById(clientId).get();

        List<ChatUser> finalUsers = new ArrayList<>();

        List<User> admins = userRepository.findAllByRole(Role.admin);
        List<User> branches = userRepository.findAllByRole(Role.branch);

        for(User user : admins) {
            ChatUser chatUser = new ChatUser();
            chatUser.setEmail(user.getEmail());
            chatUser.setFirstname(user.getFirstname());
            chatUser.setLastname(user.getLastname());
            chatUser.setRole("Admin");
            chatUser.setId(user.getId());
            finalUsers.add(chatUser);
        }

        for(User user : branches) {
            ChatUser chatUser = new ChatUser();
            chatUser.setEmail(user.getEmail());
            chatUser.setFirstname(user.getFirstname());
            chatUser.setLastname(user.getLastname());
            chatUser.setRole("Branch Manager");
            chatUser.setId(user.getId());
            finalUsers.add(chatUser);
        }

        List<Site> siteList = siteRepository.findAllByClientId(currentClient.getId());

        for(Site site : siteList)
        {
            ChatUser chatUser = new ChatUser();
            chatUser.setEmail(site.getUser().getEmail());
            chatUser.setFirstname(site.getUser().getFirstname());
            chatUser.setLastname(site.getUser().getLastname());
            chatUser.setRole("Area Manager");
            chatUser.setId(site.getUser().getId());
            finalUsers.add(chatUser);
        }

        Set<Guard> guards = scheduleService.findGuardsSiteList(siteList);

        for(Guard guard : guards)
        {
            ChatUser chatUser = new ChatUser();
            chatUser.setEmail(guard.getEmail());
            chatUser.setFirstname(guard.getFirstname());
            chatUser.setLastname(guard.getLastname());
            chatUser.setRole("Guard");
            chatUser.setId(guard.getId());
            finalUsers.add(chatUser);
        }

        return finalUsers;
    }

    @Override
    public List<ChatUser> getChatUsersWithRole(Integer id, String role) {
        User currentUser = userRepository.findById(id).get();

        List<ChatUser> finalUsers = new ArrayList<>();

        if(role.equals(Role.admin))
        {
            List<User> admins = userRepository.findAllByRole(Role.admin);
            for(User user : admins) {
                if(!user.getId().equals(id)) {
                    ChatUser chatUser = new ChatUser();
                    chatUser.setEmail(user.getEmail());
                    chatUser.setFirstname(user.getFirstname());
                    chatUser.setLastname(user.getLastname());
                    chatUser.setRole("Admin");
                    chatUser.setId(user.getId());
                    finalUsers.add(chatUser);
                }
            }
        }
        else if(role.equals(Role.branch) || role.equals(Role.area) || role.equals(Role.dispatch))
        {
            List<User> branches = userRepository.findAllByRole(Role.branch);
            List<User> areas = userRepository.findAllByRole(Role.area);
            List<User> dispatchers = userRepository.findAllByRole(Role.dispatch);

            for(User user : branches) {
                if(!user.getId().equals(id)) {
                    ChatUser chatUser = new ChatUser();
                    chatUser.setEmail(user.getEmail());
                    chatUser.setFirstname(user.getFirstname());
                    chatUser.setLastname(user.getLastname());
                    chatUser.setRole("Branch Manager");
                    chatUser.setId(user.getId());
                    finalUsers.add(chatUser);
                }
            }

            if(role.equals(Role.branch)) {
                for (User user : areas) {
                    if (!user.getId().equals(id)) {
                        ChatUser chatUser = new ChatUser();
                        chatUser.setEmail(user.getEmail());
                        chatUser.setFirstname(user.getFirstname());
                        chatUser.setLastname(user.getLastname());
                        chatUser.setRole("Area Manager");
                        chatUser.setId(user.getId());
                        finalUsers.add(chatUser);
                    }
                }
                for (User user : dispatchers) {
                    if (!user.getId().equals(id)) {
                        ChatUser chatUser = new ChatUser();
                        chatUser.setEmail(user.getEmail());
                        chatUser.setFirstname(user.getFirstname());
                        chatUser.setLastname(user.getLastname());
                        chatUser.setRole("Dispatcher");
                        chatUser.setId(user.getId());
                        finalUsers.add(chatUser);
                    }
                }
            }
        }
        else if(role.equals(Role.client))
        {
            if(currentUser.getRole().equals(Role.admin) || currentUser.getRole().equals(Role.branch)) {

                List<Client> clientList = clientRepository.findAll();
                for(Client client : clientList) {
                    ChatUser chatUser = new ChatUser();
                    chatUser.setEmail(client.getEmail());
                    chatUser.setFirstname(client.getFirstname());
                    chatUser.setLastname(client.getLastname());
                    chatUser.setRole("Client");
                    chatUser.setId(client.getId());
                    finalUsers.add(chatUser);
                }
            }
            else {

                List<Site> siteList = siteRepository.findAllByUserId(currentUser.getId());
                Set<String> emailSet = new HashSet<>();
                for(Site site : siteList) {
                    if(!emailSet.contains(site.getClient().getEmail()))
                    {
                        emailSet.add(site.getClient().getEmail());
                        ChatUser chatUser = new ChatUser();
                        chatUser.setEmail(site.getClient().getEmail());
                        chatUser.setFirstname(site.getClient().getFirstname());
                        chatUser.setLastname(site.getClient().getLastname());
                        chatUser.setRole("Client");
                        chatUser.setId(site.getClient().getId());
                        finalUsers.add(chatUser);
                    }
                }
            }
        }
        else if(role.equals(Role.guard))
        {
            if(currentUser.getRole().equals(Role.admin) || currentUser.getRole().equals(Role.branch)) {

                List<Guard> guardList = guardRepository.findAll();
                for(Guard guard : guardList) {
                    ChatUser chatUser = new ChatUser();
                    chatUser.setEmail(guard.getEmail());
                    chatUser.setFirstname(guard.getFirstname());
                    chatUser.setLastname(guard.getLastname());
                    chatUser.setRole("Guard");
                    chatUser.setId(guard.getId());
                    finalUsers.add(chatUser);
                }
            }
            else {

                List<Site> siteList = siteRepository.findAllByUserId(currentUser.getId());
                Set<Guard> guards= scheduleService.findGuardsSiteList(siteList);
                Set<String> emailSet = new HashSet<>();

                for(Guard guard : guards) {
                    ChatUser chatUser = new ChatUser();
                    chatUser.setEmail(guard.getEmail());
                    chatUser.setFirstname(guard.getFirstname());
                    chatUser.setLastname(guard.getLastname());
                    chatUser.setRole("Guard");
                    chatUser.setId(guard.getId());
                    finalUsers.add(chatUser);
                }
            }
        }

        return finalUsers;
    }

    @Override
    public List<ChatUser> getChatUsersForGuardsWithRole(Integer guardId, String role) {
        Guard currentGuard = guardRepository.findById(guardId).get();

        List<ChatUser> finalUsers = new ArrayList<>();

        Set<Site> sites = scheduleService.findSitesByGuardId(currentGuard.getId());

        if(role.equals(Role.admin))
        {
            List<User> admins = userRepository.findAllByRole(Role.admin);
            for(User user : admins) {
                ChatUser chatUser = new ChatUser();
                chatUser.setEmail(user.getEmail());
                chatUser.setFirstname(user.getFirstname());
                chatUser.setLastname(user.getLastname());
                chatUser.setRole("Admin");
                chatUser.setId(user.getId());
                finalUsers.add(chatUser);
            }
        }
        else if(role.equals(Role.branch) || role.equals(Role.area) || role.equals(Role.dispatch))
        {
            List<User> branches = userRepository.findAllByRole(Role.branch);
            for(User user : branches) {
                ChatUser chatUser = new ChatUser();
                chatUser.setEmail(user.getEmail());
                chatUser.setFirstname(user.getFirstname());
                chatUser.setLastname(user.getLastname());
                chatUser.setRole("Branch Manager");
                chatUser.setId(user.getId());
                finalUsers.add(chatUser);
            }

            for(Site site : sites)
            {
                ChatUser chatUser = new ChatUser();
                chatUser.setEmail(site.getUser().getEmail());
                chatUser.setFirstname(site.getUser().getFirstname());
                chatUser.setLastname(site.getUser().getLastname());
                chatUser.setRole("Area Manager");
                chatUser.setId(site.getUser().getId());
                finalUsers.add(chatUser);
            }
        }
        else if(role.equals(Role.client))
        {
            for(Site site : sites)
            {
                ChatUser chatUser = new ChatUser();
                chatUser.setEmail(site.getClient().getEmail());
                chatUser.setFirstname(site.getClient().getFirstname());
                chatUser.setLastname(site.getClient().getLastname());
                chatUser.setRole("Client");
                chatUser.setId(site.getId());
                finalUsers.add(chatUser);
            }
        }

        return finalUsers;
    }

    @Override
    public List<ChatUser> getChatUsersForClientsWithRole(Integer clientId, String role) {
        Client currentClient = clientRepository.findById(clientId).get();

        List<ChatUser> finalUsers = new ArrayList<>();

        List<Site> siteList = siteRepository.findAllByClientId(currentClient.getId());

        if(role.equals(Role.admin))
        {
            List<User> admins = userRepository.findAllByRole(Role.admin);
            for(User user : admins) {
                ChatUser chatUser = new ChatUser();
                chatUser.setEmail(user.getEmail());
                chatUser.setFirstname(user.getFirstname());
                chatUser.setLastname(user.getLastname());
                chatUser.setRole("Admin");
                chatUser.setId(user.getId());
                finalUsers.add(chatUser);
            }
        }
        else if(role.equals(Role.branch) || role.equals(Role.area) || role.equals(Role.dispatch))
        {
            List<User> branches = userRepository.findAllByRole(Role.branch);
            for(User user : branches) {
                ChatUser chatUser = new ChatUser();
                chatUser.setEmail(user.getEmail());
                chatUser.setFirstname(user.getFirstname());
                chatUser.setLastname(user.getLastname());
                chatUser.setRole("Branch Manager");
                chatUser.setId(user.getId());
                finalUsers.add(chatUser);
            }
            for(Site site : siteList)
            {
                ChatUser chatUser = new ChatUser();
                chatUser.setEmail(site.getUser().getEmail());
                chatUser.setFirstname(site.getUser().getFirstname());
                chatUser.setLastname(site.getUser().getLastname());
                chatUser.setRole("Area Manager");
                chatUser.setId(site.getUser().getId());
                finalUsers.add(chatUser);
            }
        }
        else if(role.equals(Role.guard))
        {
            Set<Guard> guards = scheduleService.findGuardsSiteList(siteList);

            for(Guard guard : guards)
            {
                ChatUser chatUser = new ChatUser();
                chatUser.setEmail(guard.getEmail());
                chatUser.setFirstname(guard.getFirstname());
                chatUser.setLastname(guard.getLastname());
                chatUser.setRole("Guard");
                chatUser.setId(guard.getId());
                finalUsers.add(chatUser);
            }
        }

        return finalUsers;
    }

    @Override
    public DataTableResponse<User> getPage(Integer pageNumber, Integer pageSize, String search) {

        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        Specification<User> specification = (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if(search != null) {
                if (!search.isEmpty()) {
                    String likePattern = "%" + search + "%";
                    predicates.add(criteriaBuilder.or(
                            criteriaBuilder.like(criteriaBuilder.lower(root.get("firstname")), likePattern.toLowerCase()),
                            criteriaBuilder.like(criteriaBuilder.lower(root.get("lastname")), likePattern.toLowerCase()),
                            criteriaBuilder.like(criteriaBuilder.lower(root.get("email")), likePattern.toLowerCase()),
                            criteriaBuilder.like(criteriaBuilder.lower(root.get("address")), likePattern.toLowerCase()),
                            criteriaBuilder.like(criteriaBuilder.lower(root.get("gender")), likePattern.toLowerCase())
                    ));
                }
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };

        Page<User> page = userRepository.findAll(specification, pageable);

        List<User> data = new ArrayList<>(page.getContent());

        long pagefiltered = page.getTotalElements();
        int intpageFiltered;
        intpageFiltered = (int) pagefiltered;

        return new DataTableResponse<>(1 , (int)userRepository.count() , intpageFiltered , data , "name");
    }
}
