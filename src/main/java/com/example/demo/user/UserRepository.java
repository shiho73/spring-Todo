package com.example.demo.user;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Integer>{

	List<User> findByName(String name);
	List<User> findByOrderByIdAsc();

	List<User> findByNameAndPw(String name, String pw);
	List<User> findByNameAndHimitu(String name, String himitu);

}