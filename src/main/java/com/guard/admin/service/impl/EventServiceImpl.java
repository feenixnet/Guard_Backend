package com.guard.admin.service.impl;

import com.guard.admin.database.entities.Event;
import com.guard.admin.database.entities.Site;
import com.guard.admin.database.entities.Token;
import com.guard.admin.database.entities.User;
import com.guard.admin.database.repositories.EventRepository;
import com.guard.admin.database.repositories.SiteRepository;
import com.guard.admin.database.repositories.TokenRepository;
import com.guard.admin.database.repositories.UserRepository;
import com.guard.admin.service.declaration.EventService;
import com.guard.admin.service.declaration.NotificationService;
import com.guard.admin.utils.constant.Role;

import jakarta.persistence.criteria.Predicate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class EventServiceImpl implements EventService {

    @Autowired
    EventRepository eventRepository;

    @Autowired
    SiteRepository siteRepository;

    @Autowired
    TokenRepository tokenRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    NotificationService notificationService;

    @Override
    public Event create(Event event) {

        List<Token> tokenList = tokenRepository.findAll();
        List<User> adminList = userRepository.findAllByRole(Role.admin);

       for(User admin : adminList)
       {
           Token token = tokenRepository.findByUserIdAndRole(admin.getId(), admin.getRole());
           if (token != null)
               notificationService.sendMessage(token.getToken(), "Event", event.getDescription());
       }

       int siteId = event.getSiteId();
       int clientId = siteRepository.findById(siteId).get().getClient().getId();

       Token clientToken = tokenRepository.findByUserIdAndRole(clientId, "ROLE_CLIENT");
       Map<String, String> additionalParams = new HashMap<>();
       additionalParams.put("siteId", String.valueOf(siteId));
        
       if(clientToken != null)
           notificationService.sendMessage(clientToken.getToken(), "Event", event.getDescription(), additionalParams);

        eventRepository.save(event);
        return event;
    }

    @Override
    public List<Event> getPage(Integer siteId, Integer userId, Integer clientId, Integer guardId, int pageNum, int pageSize, String search, Date startDate, Date endDate) {
        Pageable pageable = PageRequest.of(pageNum, pageSize, Sort.by(Sort.Direction.DESC, "timestamp"));
        Specification<Event> specification = (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (userId != null) {
                List<Site> siteList = siteRepository.findAllByUserId(userId);
                List<Integer> siteIds = siteList.stream()
                        .map(Site::getId)
                        .toList();
                predicates.add(root.get("siteId").in(siteIds));
                // predicates.add(root.get("site").in(siteList));
            }
            if (siteId != null)
                predicates.add(criteriaBuilder.equal(root.get("siteId"), siteId));
            if (guardId != null)
                predicates.add(criteriaBuilder.equal(root.get("guardId"), guardId));
            if(clientId != null)
            {
                List<Site> siteList = siteRepository.findAllByClientId(clientId);
                List<Integer> siteIds = siteList.stream()
                        .map(Site::getId)
                        .toList();
                predicates.add(root.get("siteId").in(siteIds));
                // predicates.add(root.get("site").in(siteList));
            }   
            if (startDate != null)
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("timestamp"), startDate));
            if (endDate != null)
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("timestamp"), endDate));
            if(search != null) {
                if (!search.isEmpty()) {
                    String likePattern = "%" + search + "%";
                    predicates.add(criteriaBuilder.or(
                            criteriaBuilder.like(criteriaBuilder.lower(root.get("action")), likePattern.toLowerCase()),
                            criteriaBuilder.like(criteriaBuilder.lower(root.get("description")), likePattern.toLowerCase())
                    ));
                }
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };

        Page<Event> page = eventRepository.findAll(specification, pageable);
        return page.stream().toList();
    }
}
