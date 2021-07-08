package com.example.demo.task;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TaskRepository extends JpaRepository<Task, Integer>{
	List<Task> findByOrderByCodeAsc();
	List<Task> findByOrderByPrtNumAsc();
}
