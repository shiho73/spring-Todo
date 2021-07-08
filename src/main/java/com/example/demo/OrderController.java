package com.example.demo;

import java.util.ArrayList;
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
public class OrderController {

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

	//並べ替え関係
	@RequestMapping("/order/code")
	public ModelAndView orderCode(ModelAndView mv) {
		List<Task> taskList = taskRepository.findByOrderByCodeAsc();
		return showList(mv, taskList);
	}

	@RequestMapping("/order/taskName")
	public ModelAndView orderTaskName(ModelAndView mv) {
		List<Task> taskList = taskRepository.findByOrderByNameAsc();
		return showList(mv, taskList);
	}

	@RequestMapping("/order/userId")
	public ModelAndView orderUserId(ModelAndView mv) {
		List<Task> taskList = taskRepository.findByOrderByUserIdAsc();
		return showList(mv, taskList);
	}

	@RequestMapping("/order/deadline")
	public ModelAndView orderDline(ModelAndView mv) {
		List<Task> taskList = taskRepository.findByOrderByDlineAsc();
		return showList(mv, taskList);
	}

	@RequestMapping("/order/priority")
	public ModelAndView orderPriority(ModelAndView mv) {
		List<Task> taskList = taskRepository.findByOrderByPrtNumAsc();
		return showList(mv, taskList);
	}

	@RequestMapping("/order/category")
	public ModelAndView orderCategory(ModelAndView mv) {
		List<Task> taskList = taskRepository.findByOrderByCgCodeAsc();
		return showList(mv, taskList);
	}

	@RequestMapping("/order/groupId")
	public ModelAndView orderGroupId(ModelAndView mv) {
		List<Task> taskList = taskRepository.findByOrderByGroupIdAsc();
		return showList(mv, taskList);
	}

	@RequestMapping("/order/progress")
	public ModelAndView orderProgress(ModelAndView mv) {
		List<Task> taskList = taskRepository.findByOrderByProgressAsc();
		return showList(mv, taskList);
	}

	//表示用関数
	public ModelAndView showList(ModelAndView mv, List<Task> taskList) {
		ArrayList<Task> list = new ArrayList<Task>();
		for (Task task : taskList) {
			if (task.isTrash() == true) {
				list.add(task);
			}
		}
		mv.addObject("list", list);
		mv.setViewName("list");
		return mv;
	}
}
