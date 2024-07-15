package com.guard.admin.service.impl;

import com.guard.admin.database.entities.Event;
import com.guard.admin.database.entities.Site;
import com.guard.admin.database.repositories.EventRepository;
import com.guard.admin.database.repositories.SiteRepository;
import com.guard.admin.service.declaration.EventService;
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
import java.util.List;

@Service
public class EventServiceImpl implements EventService {

    @Autowired
    EventRepository eventRepository;

    @Autowired
    SiteRepository siteRepository;

    @Override
    public Event create(Event event) {
        eventRepository.save(event);
        return event;
    }

    @Override
    public List<Event> getPage(Integer siteId, Integer userId, Integer guardId, int pageNum, int pageSize, Date startDate, Date endDate) {
        Pageable pageable = PageRequest.of(pageNum, pageSize, Sort.by(Sort.Direction.DESC, "timestamp"));
        Specification<Event> specification = (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (userId != null) {
                List<Site> siteList = siteRepository.findAllByUserId(userId);
                List<Integer> siteIds = siteList.stream()
                        .map(Site::getId)
                        .toList();
                predicates.add(root.get("siteId").in(siteIds));
                predicates.add(root.get("site").in(siteList));
            }
            if (siteId != null)
                predicates.add(criteriaBuilder.equal(root.get("siteId"), siteId));
            if (guardId != null)
                predicates.add(criteriaBuilder.equal(root.get("guardId"), guardId));
            if (startDate != null)
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("timestamp"), startDate));
            if (endDate != null)
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("timestamp"), endDate));

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };

        Page<Event> page = eventRepository.findAll(specification, pageable);
        return page.stream().toList();
    }
}
