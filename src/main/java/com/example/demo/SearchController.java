package com.example.demo;

import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.example.demo.category.Category;
import com.example.demo.category.CategoryRepository;
import com.example.demo.group.Group;
import com.example.demo.group.GroupRepository;
import com.example.demo.group_m.GroupM;
import com.example.demo.group_m.GroupMRepository;
import com.example.demo.priority.Priority;
import com.example.demo.priority.PriorityRepository;
import com.example.demo.task.Task;
import com.example.demo.task.TaskRepository;
import com.example.demo.user.User;
import com.example.demo.user.UserRepository;

@Controller
public class SearchController extends SuperController {

	//保持用
	@Autowired
	HttpSession session;
	//各テーブルのレポジトリを設定
	@Autowired
	TaskRepository taskRepository;
	@Autowired
	CategoryRepository categoryRepository;
	@Autowired
	GroupRepository groupRepository;
	@Autowired
	GroupMRepository groupMRepository;
	@Autowired
	UserRepository userRepository;
	@Autowired
	PriorityRepository priorityRepository;

	//検索
	@PostMapping("/task/search")
	public ModelAndView search(
			ModelAndView mv,
			@RequestParam("keyword") String keyword) {

		List<Task> taskList = null;

		if (keyword.equals("")) {
			taskList = taskRepository.findAll();
		} else if (!keyword.equals("")) {
			taskList = taskRepository.findByNameLike("%" + keyword + "%");
		}

		mv.addObject("list", taskList);
		mv.addObject("keyword", keyword);

		List<User> userList = userRepository.findAll();
		List<Category> categoryList = categoryRepository.findAll();
		List<Priority> priorityList = priorityRepository.findAll();
		List<Group> groupList = groupRepository.findAll();
		List<GroupM> gmList = groupMRepository.findAll();
		mv.addObject("ulist", userList);
		mv.addObject("clist", categoryList);
		mv.addObject("plist", priorityList);
		mv.addObject("glist", groupList);
		mv.addObject("gmlist", gmList);

		mv.setViewName("list");
		return sessiontest(mv);
	}

}
