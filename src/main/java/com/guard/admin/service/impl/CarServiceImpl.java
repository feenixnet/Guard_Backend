package com.guard.admin.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.guard.admin.database.entities.Car;
import com.guard.admin.database.repositories.CarRepository;
import com.guard.admin.service.declaration.CarService;

import jakarta.persistence.criteria.Predicate;

import com.guard.admin.payload.response.DataTableResponse;

@Service
public class CarServiceImpl implements CarService{

    @Autowired
    CarRepository carRepository;


    @Override
    public Car get(Integer id)
    {
        return carRepository.findById(id).get();
    }
    
    public Car create(Car client)
    {
        return carRepository.save(client);
    }

    public void update(Integer id , Car carDetail)
    {
        Car carOriginal = carRepository.findById(id).get();
        carOriginal.setName(carDetail.getName());
        carOriginal.setMake(carDetail.getMake());
        carOriginal.setModel(carDetail.getModel());
        carOriginal.setYear(carDetail.getYear());
        carOriginal.setLicense_number(carDetail.getLicense_number());
        carRepository.save(carOriginal);
    }

    public void delete(Integer id)
    {
        carRepository.deleteById(id);
    }

    public List<Car> getAll()
    {
        return carRepository.findAll();
    }

    public DataTableResponse<Car> getPage(Integer pageNumber, Integer pageSize, String searchKeyword)
    {
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        Specification<Car> specification = (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if(searchKeyword != null) {
                if (!searchKeyword.isEmpty()) {
                    String likePattern = "%" + searchKeyword + "%";
                    predicates.add(criteriaBuilder.or(
                            criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), likePattern.toLowerCase()),
                            criteriaBuilder.like(criteriaBuilder.lower(root.get("model")), likePattern.toLowerCase()),
                            criteriaBuilder.like(criteriaBuilder.lower(root.get("make")), likePattern.toLowerCase()),
                            criteriaBuilder.like(criteriaBuilder.lower(root.get("year")), likePattern.toLowerCase()),
                            criteriaBuilder.like(criteriaBuilder.lower(root.get("license_number")), likePattern.toLowerCase())
                    ));
                }
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };

        Page<Car> page = carRepository.findAll(specification, pageable);

        List<Car> data = new ArrayList<>(page.getContent());

        long pagefiltered = page.getTotalElements();
        int intpageFiltered;
        intpageFiltered = (int) pagefiltered;

        return new DataTableResponse<>(1 , (int)carRepository.count() , intpageFiltered , data , "name");
    }
}
