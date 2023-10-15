package com.trainitek.backtothefuture.domain;

import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

public interface CourseRepository extends CrudRepository<Course, UUID> {
}
