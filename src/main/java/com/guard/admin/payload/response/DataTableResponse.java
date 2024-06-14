package com.guard.admin.payload.response;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class DataTableResponse<T> {
    private int draw;
    private int recordsTotal;
    private int recordsFiltered;
    private List<T> data;
    private String orderColumnName;

    public DataTableResponse(int draw, int recordsTotal , int recoredsFiltered, List<T> data , String orderColumnName) {
        this.draw = draw;
        this.recordsTotal = recordsTotal;
        this.recordsFiltered = recoredsFiltered;
        this.data = data;
        this.orderColumnName = orderColumnName;
    }
}
