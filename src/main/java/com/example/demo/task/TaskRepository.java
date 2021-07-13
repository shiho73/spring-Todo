package com.example.demo.task;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TaskRepository extends JpaRepository<Task, Integer> {
	//名前からレコード取得
	List<Task> findByNameLike(String string);

	List<Task> findByGroupId(int id);
	List<Task> findByCgCode(int cgCode);

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

	//作成者順(いらなくね？)
	List<Task> findByOrderByUserIdAsc();

	//コード順(いらなくなるはず)
	List<Task> findByOrderByCgCodeAsc();

	//名前順(いらなくね？)
	List<Task> findByOrderByNameAsc();

	//グループ順(これもいらない気がする)
	List<Task> findByOrderByGroupIdAsc();

}