package com.guard.admin.service.declaration;

import com.guard.admin.payload.response.PhotoResponse;


import java.util.Date;
import java.util.List;

public interface PhotoService {
    List<PhotoResponse> getPage(Integer siteId, Integer guardId, int pageNum, int pageSize, Date startDate, Date endDate);
}
