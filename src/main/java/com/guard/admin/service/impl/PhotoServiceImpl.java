package com.guard.admin.service.impl;

import com.guard.admin.database.entities.Photo;
import com.guard.admin.database.entities.Site;
import com.guard.admin.database.repositories.PhotoRepository;
import com.guard.admin.database.repositories.SiteRepository;
import com.guard.admin.payload.response.PhotoResponse;
import com.guard.admin.service.declaration.PhotoService;
import jakarta.persistence.criteria.Join;
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
public class PhotoServiceImpl implements PhotoService {

    @Autowired
    PhotoRepository photoRepository;

    @Autowired
    SiteRepository siteRepository;

    @Override
    public List<PhotoResponse> getPage(Integer siteId, Integer guardId, int pageNum, int pageSize, String search Date startDate, Date endDate) {
        Pageable pageable = PageRequest.of(pageNum, pageSize, Sort.by(Sort.Direction.DESC, "timestamp"));
        Specification<Photo> specification = (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (siteId != null) {
                Join<Photo, Site> siteJoin = root.join("site");
                predicates.add(criteriaBuilder.equal(siteJoin.get("id"), siteId));
            }
            if (guardId != null) {
                predicates.add(criteriaBuilder.equal(root.get("guard").get("id"), guardId));
            }
            if (startDate != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("timestamp"), startDate));
            }
            if (endDate != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("timestamp"), endDate));
            }
            if(search != null) {
                if (!search.isEmpty()) {
                    String likePattern = "%" + search + "%";
                    predicates.add(criteriaBuilder.or(
                            criteriaBuilder.like(criteriaBuilder.lower(root.get("timestamp")), likePattern.toLowerCase()),
                            criteriaBuilder.like(criteriaBuilder.lower(root.get("title")), likePattern.toLowerCase())
                    ));
                }
            }
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };

        
        Page<Photo> page = photoRepository.findAll(specification, pageable);

        List<PhotoResponse> photoResponses = new ArrayList<>();
        for(Photo photo : page)
        {
            if(!photo.getUrl().contains("Tsignature")){
                PhotoResponse photoResponse = new PhotoResponse();
                photoResponse.setPhoto(photo);
                photoResponses.add(photoResponse);
            }
        }
        return photoResponses;
    }

}
