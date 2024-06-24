package com.guard.admin.service.impl;

import java.io.File;
import java.io.IOException;
import java.util.*;

import com.guard.admin.database.repositories.*;
import com.google.firebase.auth.FirebaseAuthException;
import com.guard.admin.database.entities.*;
import com.guard.admin.payload.request.GuardRequest;
import com.guard.admin.payload.response.DataTableResponse;
import com.guard.admin.service.declaration.GuardService;
import com.guard.admin.service.declaration.ScheduleService;
import jakarta.persistence.criteria.Predicate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.transaction.annotation.Transactional;

@Service
public class GuardServiceImpl implements GuardService {

    @Autowired
    private GuardRepository guardRepository;

    @Autowired
    private SiteRepository siteRepository;

    @Autowired
    private ScheduleRepository scheduleRepository;

    @Autowired
    private ReportRepository reportRepository;


    @Autowired
    private ScheduleService scheduleService;

    @Autowired
    PasswordEncoder encoder;

    @Autowired
    FirebaseAuthServiceImpl firebaseAuthServiceImpl;

    private final String uploadDir = System.getProperty("user.dir") + File.separator + "upload/public/images";

    @Override
    public Guard get(Integer id) {
        return guardRepository.findById(id).get();
    }

    @Override
    public Guard analyzeRequest(GuardRequest guardRequest) throws IOException {
        Guard guard = new Guard(guardRequest);

        File upload = new File(uploadDir);
        if(!upload.exists()) {
            upload.mkdir();
        }

        if(guardRequest.getDriverImage() != null && !guardRequest.getDriverImage().isEmpty()) {
            MultipartFile file = guardRequest.getDriverImage();
            String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
            File destination = new File(uploadDir + File.separator + fileName);
            file.transferTo(destination);
            guard.setDriverLicenseUrl(fileName);
        }

        if(guardRequest.getSecurityImage() != null && !guardRequest.getSecurityImage().isEmpty()) {
            MultipartFile file = guardRequest.getSecurityImage();
            String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
            File destination = new File(uploadDir + File.separator + fileName);
            file.transferTo(destination);
            guard.setSecurityLicenseUrl(fileName);
        }

        if(guardRequest.getFirearmsImage() != null && !guardRequest.getFirearmsImage().isEmpty()) {
            MultipartFile file = guardRequest.getFirearmsImage();
            String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
            File destination = new File(uploadDir + File.separator + fileName);
            file.transferTo(destination);
            guard.setFirearmsLicenseUrl(fileName);
        }

        if(guardRequest.getExpiryDateForDriverLicense() != null)
            guard.setExpiryDateForDriverLicense(guardRequest.getExpiryDateForDriverLicense());

        if(guardRequest.getExpiryDateForSecurityLicense() != null)
            guard.setExpiryDateForSecurityLicense(guardRequest.getExpiryDateForSecurityLicense());

        if(guardRequest.getExpiryDateForFirearmsLicense() != null)
            guard.setExpiryDateForFirearmsLicense(guardRequest.getExpiryDateForFirearmsLicense());

        guard.setPassword(encoder.encode(guardRequest.getPassword()));

        return guard;
    }

    @Override
    public Guard create(GuardRequest guardRequest) throws IOException, FirebaseAuthException {
        Guard guard = analyzeRequest(guardRequest);
        guardRepository.save(guard);
        firebaseAuthServiceImpl.createUser(guardRequest.getEmail(),guardRequest.getPassword());
        return guard;
    }

    @Override
    public Guard update(Integer id , GuardRequest request) throws IOException, FirebaseAuthException {
        
        Guard updateGuard = guardRepository.findById(id).get();
        updateGuard.setEmail(request.getEmail());
        updateGuard.setFirstname(request.getFirstname());
        updateGuard.setLastname(request.getLastname());
        updateGuard.setGender(request.getGender());
        updateGuard.setPhone(request.getPhone());
        updateGuard.setType(request.getType());
        updateGuard.setSin(request.getSin());
        updateGuard.setTransportation(request.getTransportation());
        updateGuard.setBirthday(request.getBirthday());
        updateGuard.setDateHired(request.getDateHired());
        updateGuard.setSystemPenalties(request.getSystemPenalties());
        updateGuard.setStatus(request.getStatus());
        updateGuard.setFirstAddress(request.getFirstAddress());
        updateGuard.setSecondAddress(request.getSecondAddress());
        updateGuard.setCity(request.getCity());
        updateGuard.setProvince(request.getProvince());
        updateGuard.setPostal(request.getPostal());
        updateGuard.setCountry(request.getCountry());

        if(request.getExpiryDateForDriverLicense() != null)
            updateGuard.setExpiryDateForDriverLicense(request.getExpiryDateForDriverLicense());

        if(request.getExpiryDateForSecurityLicense() != null)
            updateGuard.setExpiryDateForSecurityLicense(request.getExpiryDateForSecurityLicense());

        if(request.getExpiryDateForFirearmsLicense() != null)
            updateGuard.setExpiryDateForFirearmsLicense(request.getExpiryDateForFirearmsLicense());

        if(request.getDriverImage() != null && !request.getDriverImage().isEmpty()) {
            MultipartFile file = request.getDriverImage();
            String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
            File destination = new File(uploadDir + File.separator + fileName);
            file.transferTo(destination);
            updateGuard.setDriverLicenseUrl(fileName);
        }

        if(request.getSecurityImage() != null && !request.getSecurityImage().isEmpty()) {
            MultipartFile file = request.getSecurityImage();
            String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
            File destination = new File(uploadDir + File.separator + fileName);
            file.transferTo(destination);
            updateGuard.setSecurityLicenseUrl(fileName);
        }

        if(request.getFirearmsImage() != null && !request.getFirearmsImage().isEmpty()) {
            MultipartFile file = request.getFirearmsImage();
            String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
            File destination = new File(uploadDir + File.separator + fileName);
            file.transferTo(destination);
            updateGuard.setFirearmsLicenseUrl(fileName);
        }

        if(!request.getPassword().isEmpty())
            updateGuard.setPassword(encoder.encode(request.getPassword()));

        guardRepository.save(updateGuard);

        firebaseAuthServiceImpl.updateGuardUser(id, request.getEmail(),request.getPassword());

        return updateGuard;
    }

    @Override
    @Transactional
    public void delete(Integer id) throws FirebaseAuthException {    

        firebaseAuthServiceImpl.deleteUser(guardRepository.findById(id).get().getEmail());
        scheduleRepository.deleteAllByGuardId(id);
        reportRepository.deleteAllByGuardId(id);
        guardRepository.deleteById(id);        

    }

    @Override
    public List<Guard> getByUserId(Integer id){
        List<Site> siteList = siteRepository.findAllByUserId(id);
        Set<Guard> guards= new HashSet<>(scheduleService.findGuardsSiteList(siteList));

        return new ArrayList<>(guards);
    }

    @Override
    public List<Guard> getAll() {
        return guardRepository.findAll();
    }

    @Override
    public List<Guard> getBySiteId(Integer id) {
        return new ArrayList<>(scheduleService.findGuardsBySiteId(id));
    }

    @Override
    public List<Guard> getByType(String type) {
        return guardRepository.findByTypeAndStatus(type , true);
    }

    @Override
    public DataTableResponse<Guard> getPage(Integer pageNum, Integer pageLength, String search) {

        int pageNumber = pageNum;
        int pageSize = pageLength;

        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        Specification<Guard> specification = (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if(search !=null) {
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

        Page<Guard> page = guardRepository.findAll(specification, pageable);

        List<Guard> data = new ArrayList<>(page.getContent());

        long pagefiltered = page.getTotalElements();
        int intpageFiltered;
        intpageFiltered = (int) pagefiltered;

        return new DataTableResponse<>(1 , (int)guardRepository.count() , intpageFiltered , data , "name");
    }
}
