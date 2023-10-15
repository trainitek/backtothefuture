package com.trainitek.backtothefuture;

import com.trainitek.backtothefuture.domain.Course;
import com.trainitek.backtothefuture.domain.Enrollment;
import com.trainitek.backtothefuture.domain.User;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;

@Slf4j
public class Quiz1 {

    public static void main(String[] args) {
        Date date = new Date();
        log.info("Date: {}", date);

        User student = User.builder().build();
        User enroller = User.builder().build();
        Enrollment enrollment = Enrollment.builder()
                .enroller(enroller)
                .enroller(student)
                .course(someCourse())
                .build();
//        enrollment.startAt(clo);
    }

    private static Course someCourse() {
        return Course.builder().build();
    }
}
