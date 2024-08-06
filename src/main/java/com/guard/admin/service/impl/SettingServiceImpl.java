package com.guard.admin.service.impl;

import com.guard.admin.database.entities.Setting;
import com.guard.admin.database.repositories.SettingRepository;
import com.guard.admin.service.declaration.SettingService;

import com.guard.admin.payload.request.SettingRequest;

import java.io.File;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class SettingServiceImpl implements SettingService {
    @Autowired
    SettingRepository settingRepository;

    private final String uploadDir = System.getProperty("user.dir") + File.separator + "upload/public/images";

    @Override
    public Setting put(SettingRequest settingUpdate) {
        Setting settingOriginal = settingRepository.findById(1).get();
        settingOriginal.setTimezone(settingUpdate.getTimezone());
        settingOriginal.setCompany_name(settingUpdate.getCompany_name());
        settingOriginal.setCompany_email(settingUpdate.getCompany_email());
        settingOriginal.setCompany_phone(settingUpdate.getCompany_phone());
        settingOriginal.setCompany_address(settingUpdate.getCompany_address());


        if(settingUpdate.getCompany_logo_file() != null && !settingUpdate.getCompany_logo_file().isEmpty()) {
            MultipartFile file = settingUpdate.getCompany_logo_file();
            String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
            File destination = new File(uploadDir + File.separator + fileName);
            try{
                file.transferTo(destination);
            }catch(Exception e){
                System.out.println(e);
            }
            
            settingOriginal.setCompany_logo(fileName);
        }

        settingRepository.save(settingOriginal);

        return settingOriginal;
    }

    @Override
    public Setting get() {
        return settingRepository.findById(1).get();
    }
}
