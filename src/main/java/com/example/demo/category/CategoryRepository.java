package com.example.demo.category;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Integer>{
	List<Category> findByCode(int code);
	List<Category> findByName(String name);
	List<Category> findByOrderByCodeAsc();
}
