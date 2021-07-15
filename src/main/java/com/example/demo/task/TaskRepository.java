package com.example.demo.task;

import java.sql.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TaskRepository extends JpaRepository<Task, Integer> {
	//名前からレコード取得
	List<Task> findByNameLike(String string);

	List<Task> findByGroupId(int id);
	List<Task> findByCgCode(int cgCode);
	List<Task> findByUserId(int userId);

	//コード順
	List<Task> findByOrderByCodeAsc();
	List<Task> findByOrderByCodeDesc();

	//優先度順
	List<Task> findByOrderByPrtNumAsc();
	List<Task> findByOrderByPrtNumDesc();

	//締め切り順
	List<Task> findByOrderByDlineAsc();
	List<Task> findByOrderByDlineDesc();

	//優先度順
	List<Task> findByOrderByProgressAsc();
	List<Task> findByOrderByProgressDesc();

	List<Task> findByDline(Date dline);


}