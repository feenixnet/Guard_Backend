package com.guard.admin.service.impl;

import java.util.*;

import com.guard.admin.database.entities.HitPoints;
import com.guard.admin.database.entities.Report;
import com.guard.admin.database.entities.Shift;
import com.guard.admin.database.entities.User;
import com.guard.admin.database.repositories.HitPointRepository;
import com.guard.admin.database.repositories.PhotoRepository;
import com.guard.admin.database.repositories.ReportPhotoRepository;
import com.guard.admin.database.repositories.ReportRepository;
import com.guard.admin.database.repositories.ScheduleRepository;
import com.guard.admin.database.repositories.ShiftRepository;
import com.guard.admin.payload.response.DataTableResponse;
import com.guard.admin.payload.dto.SiteWithHitpoint;
import com.guard.admin.service.declaration.SiteService;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import jakarta.validation.OverridesAttribute;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.guard.admin.database.entities.Site;
import com.guard.admin.database.repositories.SiteRepository;
import com.guard.admin.database.repositories.VisitorRepository;

import org.springframework.transaction.annotation.Transactional;

@Service
public class SiteServiceImpl implements SiteService {

    @Autowired
    private SiteRepository siteRepository;

    @Autowired
    HitPointRepository hitPointRepository;

    @Autowired
    ShiftRepository shiftRepository;

    @Autowired
    ReportRepository reportRepository;

    @Autowired
    ScheduleRepository scheduleRepository;

    @Autowired
    PhotoRepository photoRepository;

    @Autowired
    VisitorRepository visitorRepository;

    @Autowired
    ReportPhotoRepository reportPhotoRepository;

    @Override
    public SiteWithHitpoint getFull(Integer id) {
        SiteWithHitpoint siteWithHitpoint = new SiteWithHitpoint();
        siteWithHitpoint.setSite(siteRepository.findById(id).get());
        siteWithHitpoint.setHitPointsList(getHitPoints(id));
        siteWithHitpoint.setShiftList(getShifts(id));

        return siteWithHitpoint;
    }

    @Override
    public Site getSite(Integer id) {
        return siteRepository.findById(id).get();
    }

    @Override
    public SiteWithHitpoint create(SiteWithHitpoint site) {
        Site updateSite = siteRepository.save(site.getSite());

        for(Shift shift : site.getShiftList()) {
            shift.setSiteId(updateSite.getId());
            shiftRepository.save(shift);
        }

        return site;
    }

    @Override
    @Transactional
    public SiteWithHitpoint update(Integer id, SiteWithHitpoint site) {
        Site siteDetail = site.getSite();
        Site updateSite = siteRepository.findById(id).get();
        updateSite.setName(siteDetail.getName());
        updateSite.setType(siteDetail.getType());
        updateSite.setIndustry(siteDetail.getIndustry());
        updateSite.setAddress(siteDetail.getAddress());
        updateSite.setStartDate(siteDetail.getStartDate());
        updateSite.setClient(siteDetail.getClient());
        updateSite.setUser(siteDetail.getUser());
        updateSite.setLat(siteDetail.getLat());
        updateSite.setLng(siteDetail.getLng());
        updateSite.setRadius(siteDetail.getRadius());
        updateSite.setStatus(siteDetail.getStatus());
        updateSite.setRule(siteDetail.getRule());
        siteRepository.save(updateSite);

        hitPointRepository.deleteAllBySiteId(id);

        for(HitPoints hitPoints : site.getHitPointsList()) {
            hitPoints.setSiteId(id);
            hitPointRepository.save(hitPoints);
        }

        shiftRepository.deleteAllBySiteId(id);

        for(Shift shift : site.getShiftList()) {
            shift.setSiteId(id);
            shiftRepository.save(shift);
        }

        SiteWithHitpoint siteWithHitpoint = new SiteWithHitpoint();
        siteWithHitpoint.setSite(updateSite);
        siteWithHitpoint.setHitPointsList(site.getHitPointsList());
        siteWithHitpoint.setShiftList(site.getShiftList());

        return siteWithHitpoint;
    }

    @Override
    public List<HitPoints> getHitPoints(Integer id) {
        return hitPointRepository.findAllBySiteId(id);
    }

    @Override
    public List<Shift> getShifts(Integer id) {
        return shiftRepository.findAllBySiteId(id);
    }

    @Override
    @Transactional
    public void delete(Integer id) {
        System.out.println(id);
        siteRepository.deleteById(id);
        hitPointRepository.deleteAllBySiteId(id);
        shiftRepository.deleteAllBySiteId(id);
        reportRepository.deleteAllBySiteId(id);
        scheduleRepository.deleteAllBySiteId(id);
        photoRepository.deleteAllBySiteId(id);
        visitorRepository.deleteAllBySiteId(id);
        List<Report> results = reportRepository.findAllBySiteId(id);
        for(Report report : results) {
            Integer targetReportId = report.getId();
            reportPhotoRepository.deleteByReportId(targetReportId);
        }
    }

    @Override
    public List<SiteWithHitpoint> getByUser(Integer id) {
        return getBySites(siteRepository.findAllByUserId(id));
    }

    @Override
    public List<SiteWithHitpoint> getByAdmin() {
        return getBySites(siteRepository.findAll());
    }

    @Override
    public List<SiteWithHitpoint> getByClient(Integer id) {
        return getBySites(siteRepository.findAllByClientId(id));
    }

    @Override
    public List<SiteWithHitpoint> getBySites(List<Site> siteList) {
        List<SiteWithHitpoint> siteWithHitpoints = new ArrayList<>();
        for(Site site : siteList) {
            siteWithHitpoints.add(getFull(site.getId()));
        }
        return siteWithHitpoints;
    }

    @Override
    public DataTableResponse<SiteWithHitpoint> getPage(Integer pageNum, Integer pageLength, String search, Integer userId) {

        int pageNumber = pageNum;
        int pageSize = pageLength;

        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        Specification<Site> specification = (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if(search != null) {
                if (!search.isEmpty()) {
                    String likePattern = "%" + search + "%";
                    predicates.add(criteriaBuilder.or(
                            criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), likePattern.toLowerCase()),
                            criteriaBuilder.like(criteriaBuilder.lower(root.get("status")), likePattern.toLowerCase()),
                            criteriaBuilder.like(criteriaBuilder.lower(root.get("type")), likePattern.toLowerCase()),
                            criteriaBuilder.like(criteriaBuilder.lower(root.get("industry")), likePattern.toLowerCase()),
                            criteriaBuilder.like(criteriaBuilder.lower(root.get("address")), likePattern.toLowerCase())
                    ));
                }
            }
            if(!userId.equals(0)) {
                Join<Site, User> userJoin = root.join("user");
                predicates.add(criteriaBuilder.equal(userJoin.get("id"), userId));
            }
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };

        Page<Site> page = siteRepository.findAll(specification, pageable);

        List<Site> data = new ArrayList<>(page.getContent());

        List<SiteWithHitpoint> bigData = new ArrayList<>();

        for(Site site : data) {
            bigData.add(getFull(site.getId()));
        }

        long pagefiltered = page.getTotalElements();
        int intpageFiltered;
        intpageFiltered = (int) pagefiltered;

        return new DataTableResponse<>( 1 , (int)siteRepository.countByUserId(userId) , intpageFiltered , bigData , "name");
    }
}