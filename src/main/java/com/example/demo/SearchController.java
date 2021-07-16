package com.example.demo;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.example.demo.category.CategoryRepository;
import com.example.demo.group.GroupRepository;
import com.example.demo.group_m.GroupMRepository;
import com.example.demo.priority.PriorityRepository;
import com.example.demo.task.Task;
import com.example.demo.task.TaskRepository;
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
	@PostMapping("/task/search/name")
	public ModelAndView searchName(
			ModelAndView mv,
			@RequestParam("keyword") String keyword) {

		mv = listAndTrash(false, mv);
		mv = almostDeadline(mv);

		ArrayList<Task> list = new ArrayList<Task>();
		List<Task> taskList = null;

		if (keyword.equals("")) {
			taskList = taskRepository.findAll();
		} else if (!keyword.equals("")) {
			taskList = taskRepository.findByNameLike("%" + keyword + "%");
		}

		for (Task task1 : taskList) {
			if (task1.isTrash() == true) {
				list.add(task1);
			}
		}

		mv.addObject("list", list);
		mv.addObject("keyword", keyword);

		mv.setViewName("list");
		return sessiontest(mv);
	}

	//カテゴリ検索
		@RequestMapping("/task/search/category")
		public ModelAndView searchCategory(
				ModelAndView mv,
				@RequestParam(name = "category") int code) {

			mv = listAndTrash(false, mv);
			mv = almostDeadline(mv);

			List<Task> taskList = taskRepository.findAll();
			ArrayList<Task> taskList2 = new ArrayList<>();

			for (Task t : taskList) {
				if (t.getCgCode() == code) {
					taskList2.add(t);
				}
			}

			ArrayList<Task> list = new ArrayList<Task>();
			for (Task task1 : taskList2) {
				if (task1.isTrash() == true) {
					list.add(task1);
				}
			}

			mv.addObject("list", list);

			mv.setViewName("list");
			return sessiontest(mv);
		}

		//優先度検索
		@RequestMapping("/task/search/priority")
		public ModelAndView searchPriority(
				ModelAndView mv,
				@RequestParam(name = "priority") int code) {

			mv = listAndTrash(false, mv);
			mv = almostDeadline(mv);

			List<Task> taskList = taskRepository.findAll();
			ArrayList<Task> taskList2 = new ArrayList<>();

			for (Task t : taskList) {
				if (t.getPrtNum() == code) {
					taskList2.add(t);
				}
			}

			ArrayList<Task> list = new ArrayList<Task>();
			for (Task task1 : taskList2) {
				if (task1.isTrash() == true) {
					list.add(task1);
				}
			}

			mv.addObject("list", list);

			mv.setViewName("list");
			return sessiontest(mv);
		}

}
