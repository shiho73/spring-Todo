package com.example.demo;

import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
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
import com.example.demo.group.Group;
import com.example.demo.group.GroupRepository;
import com.example.demo.priority.Priority;
import com.example.demo.priority.PriorityRepository;
import com.example.demo.task.Task;
import com.example.demo.task.TaskRepository;
import com.example.demo.user.User;
import com.example.demo.user.UserRepository;

@Controller
public class EditController {

	//保持用/
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

	@Autowired
	PriorityRepository priorityRepository;

	//新規作成
	@PostMapping("/list/new")
	public ModelAndView listnew(ModelAndView mv) {
		categoryZero();
		////		groupZero();

		List<Category> categoryList = categoryRepository.findAll();
		List<Priority> priorityList = priorityRepository.findAll();
		List<Group> groupList = groupRepository.findAll();
		mv.addObject("clist", categoryList);
		mv.addObject("plist", priorityList);
		mv.addObject("glist", groupList);
		mv.setViewName("addTask");
		return mv;
	}

	//新規作成アクション
	@PostMapping("/list/add")
	public ModelAndView listnew1(ModelAndView mv,
			@RequestParam("name") String name,
			@RequestParam("userId") int userId,
			@RequestParam("dline") String dline,
			@RequestParam("prtNum") int prtNum,
			@RequestParam("cgCode") int cgCode,
			@RequestParam("groupId") int groupId,
			@RequestParam("memo") String memo) {

		//未入力チェック
		if (name == null || name == "" || dline == null || dline == "") {
			String msg1 = null;
			String msg2 = null;
			if (name == null || name == "") {
				msg1 = "タスク名を入力してください";
				mv.addObject("msg1", msg1);
			}
			if (dline == null || dline == "") {
				msg2 = "期限を設定してください";
				mv.addObject("msg2", msg2);
			}
			return listnew(mv);
		}

		//期限日の型変換
		//String型の期限日(dline)をjavaのDate型(yyyy/MM/dd)に変換
		SimpleDateFormat sdFormat = new SimpleDateFormat("yyyy/MM/dd");
		java.util.Date date = null;
		try {
			date = sdFormat.parse(dline);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		//書式を(yyyy/MM/dd)から(yyyy-MM-dd)に変換し、String型に戻す
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String date2 = sdf.format(date);
		//String型をData型(SQL)dline2に変換
		Date dline2 = Date.valueOf(date2);

		//新しく追加
		Task task = new Task(name, userId, dline2, prtNum, cgCode, groupId, memo, true);
		taskRepository.saveAndFlush(task);

		//すべてのリスト取得
		List<Task> taskList = taskRepository.findByOrderByCodeAsc();

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

	//新規カテゴリ作成アクション
	@PostMapping("/option/new")
	public ModelAndView newoption(
			@RequestParam("name") String name,
			@RequestParam("code") int code,
			ModelAndView mv) {

		//カテゴリチェック
		List<Category> list = categoryRepository.findByCode(code);
		List<Category> list2 = categoryRepository.findByName(name);
		if (!list.isEmpty() || !list2.isEmpty()) {
			if (!list.isEmpty()) {
				mv.addObject("msg", "使用済みのカテゴリコードです");
			}
			if (!list.isEmpty()) {
				mv.addObject("msg", "使用済みのカテゴリ名です");
			}
			mv.setViewName("optionCategory");
			return mv;
		}

		Category category = new Category(code, name);
		categoryRepository.saveAndFlush(category);

		List<Category> categoryList = categoryRepository.findAll();
		List<Priority> priorityList = priorityRepository.findAll();
		List<Group> groupList = groupRepository.findAll();
		mv.addObject("clist", categoryList);
		mv.addObject("plist", priorityList);
		mv.addObject("glist", groupList);

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

		List<Category> categoryList = categoryRepository.findAll();
		List<Priority> priorityList = priorityRepository.findAll();
		List<Group> groupList = groupRepository.findAll();
		mv.addObject("clist", categoryList);
		mv.addObject("plist", priorityList);
		mv.addObject("glist", groupList);

		mv.addObject("task", task);

		mv.setViewName("editTask");
		return mv;
	}

	//編集アクション
	@PostMapping("/update")
	public ModelAndView edit1(
			@RequestParam(name = "code") int code,
			@RequestParam(name = "name") String name,
			@RequestParam(name = "userId") int userId,
			@RequestParam(name = "dline") String dline,
			@RequestParam(name = "prtNum") int prtNum,
			@RequestParam(name = "cgCode") int cgCode,
			@RequestParam(name = "groupId") int groupId,
			@RequestParam(name = "progress") int progress,
			@RequestParam(name = "memo") String memo,
			ModelAndView mv) {

		//未入力チェック
		if (name == null || name == "") {
			mv.addObject("msg1", "タスク名を入力してください");
			mv.setViewName("addTask");
			return mv;
		}

		//期限日の型変換
		//String型の期限日(dline)をjavaのDate型(yyyy/MM/dd)に変換
		SimpleDateFormat sdFormat = new SimpleDateFormat("yyyy/MM/dd");
		java.util.Date date = null;
		try {
			date = sdFormat.parse(dline);
		} catch (ParseException e) {
			e.printStackTrace();
		}

		if (date==null) {
			Date dline3 = Date.valueOf(dline);
			Task task = new Task(code, name, userId, dline3, prtNum, cgCode, groupId, progress, memo, true);
			taskRepository.saveAndFlush(task);
		} else {
		//書式を(yyyy/MM/dd)から(yyyy-MM-dd)に変換し、String型に戻す
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String date2 = sdf.format(date);
		//String型をData型(SQL)dline2に変換
		Date dline2 = Date.valueOf(date2);
		Task task = new Task(code, name, userId, dline2, prtNum, cgCode, groupId, progress, memo, true);
		taskRepository.saveAndFlush(task);}

		//空の表示用リストを生成
		ArrayList<Task> list = new ArrayList<Task>();

		//全てのタスクを取得
		List<Task> taskList = taskRepository.findByOrderByCodeAsc();

		//ゴミ箱に入れていなければ、表示するリストに追加
		for (Task task1 : taskList) {
			if (task1.isTrash() == true) {
				list.add(task1);
			}
		}

		List<User> userList = userRepository.findAll();
		List<Category> categoryList = categoryRepository.findAll();
		List<Priority> priorityList = priorityRepository.findAll();
		List<Group> groupList = groupRepository.findAll();
		mv.addObject("ulist", userList);
		mv.addObject("clist", categoryList);
		mv.addObject("plist", priorityList);
		mv.addObject("glist", groupList);

		mv.addObject("list", list);

		mv.setViewName("list");
		return mv;
	}

	//カテゴリコードのデフォルト設定
	private void categoryZero() {
		List<Category> list = categoryRepository.findByCode(0);
		if (list.isEmpty()) {
			Category category = new Category(0, "なし");
			categoryRepository.saveAndFlush(category);
		}
	}

//	//グループのデフォルト設定・どうさしない
//	private void groupZero() {
//		List<Group> list2 = groupRepository.findById(0);
//		if (list2.isEmpty()) {
//			Group group = new Group(0, "なし");
//			groupRepository.saveAndFlush(group);
//		}
//	}

}
