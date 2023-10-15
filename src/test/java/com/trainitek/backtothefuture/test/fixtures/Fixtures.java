package com.trainitek.backtothefuture.test.fixtures;

import com.trainitek.backtothefuture.domain.Course;
import com.trainitek.backtothefuture.domain.User;

public class Fixtures {
    public static User someStudent() {
        return User.builder().firstName("John").lastName("Doe").build();
    }

    public static Course someCourse() {
        return Course.builder().courseName("Trainitek - Implementing Modern Architecture").build();
    }
}
