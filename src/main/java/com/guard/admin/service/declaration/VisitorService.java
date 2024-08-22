package com.guard.admin.service.declaration;

import com.guard.admin.payload.response.VisitorResponse;
import org.springframework.data.domain.Page;

import java.util.Date;
import java.util.List;

public interface VisitorService {
    List<VisitorResponse> getPage(Integer siteId, Integer guardId, int pageNum, int pageSize, String search, Date startDate, Date endDate);
}
