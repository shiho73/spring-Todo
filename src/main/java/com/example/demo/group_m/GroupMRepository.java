package com.example.demo.group_m;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GroupMRepository extends JpaRepository<GroupM, Integer>{
	List<GroupM> findByOrderByGroupIdAsc();
}