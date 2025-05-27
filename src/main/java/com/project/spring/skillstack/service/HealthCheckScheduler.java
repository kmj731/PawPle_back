package com.project.spring.skillstack.service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class HealthCheckScheduler {

    public static LocalDate calculateNextCheckupDate(LocalDate lastCheckDate, int petAge) {
        if (petAge < 1) {
            return lastCheckDate.plusWeeks(2);  // 2주
        } else if (petAge <= 7) {
            return lastCheckDate.plusYears(1);  // 1년
        } else if (petAge <= 12) {
            return lastCheckDate.plusMonths(6); // 6개월
        } else {
            return lastCheckDate.plusMonths(3); // 3개월
        }
    }

    public static long calculateDday(LocalDate nextCheckDate) {
        return ChronoUnit.DAYS.between(LocalDate.now(), nextCheckDate);
    }
}
