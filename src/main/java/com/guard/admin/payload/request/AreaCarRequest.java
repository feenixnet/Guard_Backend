package com.guard.admin.payload.request;


import lombok.Getter;
import lombok.Setter;
import java.util.List;

import com.guard.admin.database.entities.Area;
import com.guard.admin.payload.dto.SiteWithHitpoint;

@Getter
@Setter
public class AreaCarRequest {
    private Integer id;
    private List<Integer> areaList;
    private List<Integer> siteList;
}
