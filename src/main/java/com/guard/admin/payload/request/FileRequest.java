package com.guard.admin.payload.request;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class FileRequest {
    private MultipartFile[] image;
    private Integer siteId;
}
