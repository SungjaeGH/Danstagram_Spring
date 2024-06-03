package com.project.danstagram.global.time;

import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
public class TimeUtil {

    public String getCurrTime(String timeFormat) {

        LocalDateTime nowDate = LocalDateTime.now();
        DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern(timeFormat);

        return nowDate.format(dateFormat);
    }
}
