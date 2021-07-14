package com.example.demo;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.example.demo.category.CategoryRepository;
import com.example.demo.group.GroupRepository;
import com.example.demo.group_m.GroupMRepository;
import com.example.demo.priority.PriorityRepository;
import com.example.demo.task.Task;
import com.example.demo.task.TaskRepository;
import com.example.demo.user.UserRepository;

@Controller
public class NarrowController extends SuperController {

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

	//カテゴリ検索
	@RequestMapping("/list/{category}/category")
	public ModelAndView narrowCategory(
			ModelAndView mv,
			@PathVariable(name = "category") int code) {

		mv = listAndTrash(false, mv);
		mv = almostDeadline(mv);

		List<Task> taskList = taskRepository.findAll();
		ArrayList<Task> taskList2 = new ArrayList<>();

		for (Task t : taskList) {
			if (t.getCgCode() == code) {
				taskList2.add(t);
			}
		}

		mv.addObject("list", taskList2);

		mv.setViewName("list");
		return sessiontest(mv);
	}

	//優先度検索
	@RequestMapping("/list/{priority}/priority")
	public ModelAndView narrowPriority(
			ModelAndView mv,
			@PathVariable(name = "priority") int code) {

		mv = listAndTrash(false, mv);
		mv = almostDeadline(mv);

		List<Task> taskList = taskRepository.findAll();
		ArrayList<Task> taskList2 = new ArrayList<>();

		for (Task t : taskList) {
			if (t.getPrtNum() == code) {
				taskList2.add(t);
			}
		}

		mv.addObject("list", taskList2);

		mv.setViewName("list");
		return sessiontest(mv);
	}

}
