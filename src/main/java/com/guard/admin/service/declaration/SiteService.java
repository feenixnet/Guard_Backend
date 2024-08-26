package com.guard.admin.service.declaration;

import java.util.*;

import com.guard.admin.database.entities.Area;
import com.guard.admin.database.entities.Guard;
import com.guard.admin.database.entities.HitPoints;
import com.guard.admin.database.entities.Shift;
import com.guard.admin.payload.response.DataTableResponse;
import com.guard.admin.payload.dto.SiteWithHitpoint;
import com.guard.admin.payload.request.AreaCarRequest;
import com.guard.admin.database.entities.Site;

public interface SiteService {

    SiteWithHitpoint getFull(Integer id) ;

    Site getSite(Integer id) ;

    SiteWithHitpoint create(SiteWithHitpoint site) ;

    Area createArea(Area area);

    void deleteArea(Integer id);

    List<Area> getAllArea();

    Area updateArea(Area area);

    SiteWithHitpoint update(Integer id, SiteWithHitpoint site);

    void delete(Integer id);

    void changeCar(AreaCarRequest arCar);

    List<HitPoints> getHitPoints(Integer id);

    List<Shift> getShifts(Integer id);

    List<SiteWithHitpoint> getByUser(Integer id) ;

    List<SiteWithHitpoint> getByAdmin() ;

    List<SiteWithHitpoint> getByClient(Integer id) ;

    List<SiteWithHitpoint> getBySites(List<Site> siteList);

    DataTableResponse<SiteWithHitpoint> getPage(Integer pageNum, Integer pageLength, String search, Integer userId) ;
}
