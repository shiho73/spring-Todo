package com.example.demo;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.example.demo.category.CategoryRepository;
import com.example.demo.group.GroupRepository;
import com.example.demo.task.TaskRepository;
import com.example.demo.user.UserRepository;

@Controller
public class TrashController {

	//保持用
	@Autowired
	HttpSession session;

	//Taskデータベース
	@Autowired
	TaskRepository taskRepository;

	//Categoryデータベース
	@Autowired
	CategoryRepository categoryRepository;

	//Groupデータベース
	@Autowired
	GroupRepository groupRepository;

	//Userデータベース
	@Autowired
	UserRepository userRepository;

	//ゴミ箱へ投げる
	@RequestMapping("/list/trash")
	public ModelAndView listtrash(ModelAndView mv) {

		mv.setViewName("list");
		return mv;
	}

	//ゴミ箱を見る
	@RequestMapping("/look/trash")
	public ModelAndView looktrash(ModelAndView mv) {

		mv.setViewName("trash");
		return mv;
	}

	//ゴミ箱から戻す
	@RequestMapping("/trash/recovery")
	public ModelAndView trash(ModelAndView mv) {

		mv.setViewName("trash");
		return mv;
	}

	//タスク完全消去
	@RequestMapping("/delete")
	public ModelAndView delete(ModelAndView mv) {

		mv.setViewName("trash");
		return mv;
	}

}
