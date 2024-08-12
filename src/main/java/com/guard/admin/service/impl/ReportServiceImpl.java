package com.guard.admin.service.impl;

import com.guard.admin.database.entities.*;
import com.guard.admin.database.repositories.*;
import com.guard.admin.payload.request.ReportRequest;
import com.guard.admin.payload.response.ReportResponse;
import com.guard.admin.service.declaration.*;
import com.guard.admin.service.impl.*;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import com.guard.admin.utils.constant.Role;


import java.io.File;
import java.io.IOException;
import java.util.*;

@Service
public class ReportServiceImpl implements ReportService {

    @Autowired
    ReportRepository reportRepository;

    @Autowired
    SiteRepository siteRepository;

    @Autowired
    GuardService guardService;

    @Autowired
    ReportPhotoRepository reportPhotoRepository;

    @Autowired
    TokenRepository tokenRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    NotificationService notificationService;

    private static final Logger logger = LoggerFactory.getLogger(ScheduleService.class);

    private final String uploadDir = System.getProperty("user.dir") + File.separator + "upload/public/images";

    @Override
    public Report create(ReportRequest reportRequest, Integer guardId) throws IOException {

        File upload = new File(uploadDir);
        if(!upload.exists()) {
            upload.mkdir();
        }

        Report report = new Report();
        report.setGuard(guardService.get(guardId) );
        report.setSite(siteRepository.findById(reportRequest.getSiteId()).get());
        report.setNature(reportRequest.getNature());
        report.setDescription(reportRequest.getDescription());
        report.setTimestamp(new Date());
        report.setOtherInvolved(reportRequest.getOtherInvolved());
        report.setPoliceCaseNumber(reportRequest.getPoliceCaseNumber());
        report.setPoliceOfficerContactDetails(reportRequest.getPoliceOfficerContactDetails());
        report.setSignature(reportRequest.getSignature());
        Report updateReport = reportRepository.save(report);

       List<Token> tokenList = tokenRepository.findAll();
       List<User> adminList = userRepository.findAllByRole(Role.admin);

       for(User admin : adminList)
       {
           Token token = tokenRepository.findByUserIdAndRole(admin.getId(), admin.getRole());
           if(token != null)
               notificationService.sendMessage(token.getToken(), report.getNature(), report.getDescription());
       }

       Token areaToken = tokenRepository.findByUserIdAndRole(report.getSite().getUser().getId(), report.getSite().getUser().getRole());
       if(areaToken != null)
           notificationService.sendMessage(areaToken.getToken(), report.getNature(), report.getDescription());

       Token clientToken = tokenRepository.findByUserIdAndRole(report.getSite().getClient().getId(),
               report.getSite().getClient().getRole());
        
       Map<String, String> additionalParams = new HashMap<>();
       additionalParams.put("siteId", String.valueOf(report.getSite().getId()));
        
       if(clientToken != null)
           notificationService.sendMessage(clientToken.getToken(), report.getNature(), report.getDescription(), additionalParams);

    //    for(Token token : tokenList)
    //    {
    //        notificationService.sendMessage(token.getToken(), report.getNature(), report.getDescription());
    //    }

        for(String url: reportRequest.getUrls())
        { 
            ReportPhoto reportPhoto = new ReportPhoto();
            reportPhoto.setReport(reportRepository.findById(updateReport.getId()).get());
            reportPhoto.setUrl(url);
            reportPhotoRepository.save(reportPhoto);
        }
        return updateReport;
    }

    @Override
    public List<ReportResponse> getPage(Integer siteId, Integer userId, int pageNum, int pageSize, Date startDate, Date endDate) {
        Pageable pageable = PageRequest.of(pageNum, pageSize, Sort.by(Sort.Direction.DESC, "timestamp"));
        Specification<Report> specification = (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            if(siteId != null) {
                Join<Report, Site> siteJoin = root.join("site");
                predicates.add(criteriaBuilder.equal(siteJoin.get("id"), siteId));
            }
            if(userId != null) {
                List<Site> siteList = siteRepository.findAllByUserId(userId);
                predicates.add(root.get("site").in(siteList));
            }
            if(startDate != null)
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("timestamp"),startDate));
            if(endDate != null)
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("timestamp"),endDate));
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
        Page<Report> page = reportRepository.findAll(specification, pageable);

        List<ReportResponse> reportResponses = new ArrayList<>();
        for(Report report : page)
        {
            ReportResponse reportResponse = new ReportResponse();
            reportResponse.setReport(report);
            List<ReportPhoto> photoList = reportPhotoRepository.findByReportId(report.getId());
            reportResponse.setPhotoList(photoList);

            reportResponses.add(reportResponse);
        }
        return reportResponses;
    }
}
