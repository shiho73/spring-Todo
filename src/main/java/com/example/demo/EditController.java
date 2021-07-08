package com.example.demo;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.example.demo.category.Category;
import com.example.demo.category.CategoryRepository;
import com.example.demo.group.GroupRepository;
import com.example.demo.task.Task;
import com.example.demo.task.TaskRepository;
import com.example.demo.user.UserRepository;

@Controller
public class EditController {

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

	//新規作成
	@PostMapping("/list/new")
	public ModelAndView listnew(ModelAndView mv) {

		mv.setViewName("addTask");
		return mv;
	}

	//新規作成アクション
	@PostMapping("/list/add")
	public ModelAndView listnew1(ModelAndView mv,
			@RequestParam("name") String name,
			@RequestParam("userId") int userId,
			@RequestParam("dline") Date dline,
			@RequestParam("prtNum") int prtNum,
			@RequestParam("cgCode") int cgCode,
			@RequestParam("groupId") int groupId,
			@RequestParam("memo") String memo) {

		//新しく追加
		Task tasklist = new Task(name, userId, dline, prtNum, cgCode, groupId, memo, true);
		taskRepository.saveAndFlush(tasklist);

		//すべてのリスト取得
		List<Task> taskList = taskRepository.findAll();

		//Thymeleafで表示する準備
		mv.addObject("list", taskList);

		mv.setViewName("list");
		return mv;
	}

	//新規カテゴリー作成
	@RequestMapping("/option")
	public ModelAndView option(ModelAndView mv) {
		mv.setViewName("optionCategory");
		return mv;
	}

	//新規カテゴリー作成アクション
	@PostMapping("/option/new")
	public ModelAndView newoption(
			@RequestParam("name") String name,
			ModelAndView mv
			) {
		Category categoryList = new Category(name);

		List<Category> categoryList1 = categoryRepository.findAll();
		mv.addObject("list", categoryList);

		mv.setViewName("addTask");
		return mv;
	}


	//編集
	@RequestMapping("/list/edit")
	public ModelAndView edit(
			@RequestParam(name = "code") int code,
			ModelAndView mv) {

		Task task = null;

		Optional<Task> recode = taskRepository.findById(code);

		if (recode.isEmpty() == false) {
			task = recode.get();
		}

		mv.addObject("task", task);

		mv.setViewName("editTask");
		return mv;
	}

	//編集アクション
	@RequestMapping("/update")
	public ModelAndView edit1(
			@RequestParam(name = "code") int code,
			@RequestParam(name = "name") String name,
			@RequestParam(name = "userId") int userId,
			@RequestParam(name = "dline") Date dline,
			@RequestParam(name = "prtNum") int prtNum,
			@RequestParam(name = "cgCode") int cgCode,
			@RequestParam(name = "groupId") int groupId,
			@RequestParam(name = "progress") int progress,
			@RequestParam(name = "memo") String memo,
			ModelAndView mv) {

		Task task = new Task(code, name, userId, dline, prtNum, cgCode, groupId, progress, memo, true);
		taskRepository.saveAndFlush(task);

		//空の表示用リストを生成
		ArrayList<Task> list = new ArrayList<Task>();

		//全てのタスクを取得
		List<Task> taskList = taskRepository.findAll();

		//ゴミ箱に入れていなければ、表示するリストに追加
		for (Task task1 : taskList) {
			if (task1.isTrash() == true) {
				list.add(task1);
			}
		}

		mv.addObject("list", list);

		mv.setViewName("list");
		return mv;
	}

}
