package com.example.demo;

import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.example.demo.category.CategoryRepository;
import com.example.demo.group.GroupRepository;
import com.example.demo.task.Task;
import com.example.demo.task.TaskRepository;
import com.example.demo.user.UserRepository;

@Controller
public class TaskController {

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



	//タスク一覧
	@RequestMapping("/list")
	public ModelAndView list(ModelAndView mv) {

		List<Task> taskList = taskRepository.findAll();
		mv.addObject("list", taskList);
		mv.setViewName("list");
		return mv;
	}

	//新規作成
	@RequestMapping("/list/new")
	public ModelAndView listnew(ModelAndView mv) {

		mv.setViewName("addTask");
		return mv;
	}

	//編集
	@RequestMapping("/list/edit")
	public ModelAndView edit(ModelAndView mv) {

		mv.setViewName("editTask");
		return mv;
	}

	//ゴミ箱
	@RequestMapping("/list/trash")
	public ModelAndView trash(ModelAndView mv) {

		mv.setViewName("trash");
		return mv;
	}

	//カレンダー
	@RequestMapping("/list/calender")
	public ModelAndView calender(ModelAndView mv) {

		mv.setViewName("calender");
		return mv;
	}



}
