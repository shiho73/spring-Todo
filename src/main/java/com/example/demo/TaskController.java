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

	//ゴミ箱へ投げる
		@RequestMapping("/list/trash")
		public ModelAndView listtrash(ModelAndView mv) {

			mv.setViewName("list");
			return mv;
		}


}
