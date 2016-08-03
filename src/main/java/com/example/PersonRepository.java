package com.example;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by yukiyoshida on 2016/08/03.
 */
@Repository
public interface PersonRepository extends JpaRepository<Person, Integer> {
}
