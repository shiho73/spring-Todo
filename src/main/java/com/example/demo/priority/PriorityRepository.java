package com.example.demo.priority;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PriorityRepository extends JpaRepository<Priority, Integer>{
	List<Priority> findByNum(int num);
	List<Priority> findByOrderByNumAsc();
}
