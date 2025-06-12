package com.project.spring.pawple.app.manage;

import lombok.Data;

@Data
public class MonthlySalesDto {
    private String month;
    private Long totalSales;

    public MonthlySalesDto(String month, Long totalSales) {
        this.month = month;
        this.totalSales = totalSales;
    }
    
    public String getMonth() {
        return month;
    }

    public Long getTotalSales() {
        return totalSales;
    }
}