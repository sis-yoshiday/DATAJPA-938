package com.example;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * Created by yukiyoshida on 2016/08/03.
 */
@Repository
public interface DepartmentRepository extends JpaRepository<Department, Integer> {

    @Query("select new com.example.Pair(d.id, d.name) " +
            "from Department d join d.persons p " +
            "where p.name like ?1")
    List<Pair> findByPersonName1(String name);

    @Query("select distinct new com.example.Pair(d.id, d.name) " +
            "from Department d join d.persons p " +
            "where p.name like ?1")
    List<Pair> findByPersonName2(String name);
}
