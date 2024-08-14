package com.guard.admin.service.declaration;

import com.guard.admin.database.entities.Report;
import com.guard.admin.payload.request.ReportRequest;
import com.guard.admin.payload.response.ReportResponse;

import java.io.IOException;
import java.util.*;

public interface ReportService {

    Report create(ReportRequest reportRequest, Integer guardId) throws IOException ;

    List<ReportResponse> getPage(Integer siteId, Integer userId, int pageNum, int pageSize, Date startDate, Date endDate, Integer clientId);

    boolean approveReport(Integer reportId, Integer userId, boolean isApporved);
}
