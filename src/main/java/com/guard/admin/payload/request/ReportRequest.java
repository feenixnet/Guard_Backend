package com.guard.admin.payload.request;

import com.guard.admin.database.entities.Guard;
import com.guard.admin.database.entities.Site;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class ReportRequest {
    private MultipartFile[] image;
    private Integer siteId;
    private String nature;
    private String description;
}
