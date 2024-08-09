package com.guard.admin.service.declaration;

import java.util.List;

import com.guard.admin.payload.dto.SiteWithHitpoint;
import com.guard.admin.payload.response.DataTableResponse;


import com.guard.admin.database.entities.Car;
import com.guard.admin.database.entities.Site;

public interface CarService {
    Car get(Integer id);
    
    Car create(Car client);

    void update(Integer id , Car carDetail);

    void delete(Integer id);

    List<Car> getAll();

    DataTableResponse<Car> getPage(Integer pageNumber, Integer pageSize, String searchKeyword);

    List<SiteWithHitpoint> getSites(Integer carId);
}
