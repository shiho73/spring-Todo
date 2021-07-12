package com.example.demo;

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
import com.example.demo.group_m.GroupM;
import com.example.demo.group_m.GroupMRepository;
import com.example.demo.priority.Priority;
import com.example.demo.priority.PriorityRepository;
import com.example.demo.task.TaskRepository;
import com.example.demo.user.User;
import com.example.demo.user.UserRepository;

@Controller
public class GroupController {

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

	@Autowired
	PriorityRepository priorityRepository;

	@Autowired
	GroupMRepository groupMRepository;

	//新規グループ作成
	@RequestMapping("/group/new")
	public ModelAndView newGroup(ModelAndView mv) {
		mv.setViewName("addGroup");
		return mv;
	}

	//新規グループ作成アクション
	@PostMapping("/group/add")
	public ModelAndView addGroup(
			@RequestParam(name = "name") String name,
			@RequestParam(name = "id", defaultValue = "0") Integer id,
			@RequestParam(name = "member") String member,
			ModelAndView mv) {

		//未入力チェック
		if (name == null || name == "" || id == 0) {
			mv.addObject("msg1", "未入力の項目があります");
			mv.setViewName("addGroup");
			return mv;
		}

		//グループの重複チェック
		Optional<Group> list = groupRepository.findById(id);
		List<Group> list2 = groupRepository.findByName(name);

		if (!list.isEmpty() || !list2.isEmpty()) {
			if (!list.isEmpty()) {
				mv.addObject("msg1", "使用済みのグループ番号です");
			}
			if (!list2.isEmpty()) {
				mv.addObject("msg2", "使用済みのグループ名です");
			}
			mv.setViewName("addGroup");
			return mv;
		}

		Group group = new Group(id, name);
		groupRepository.saveAndFlush(group);

		GroupM groupM = new GroupM (id, member);
		groupMRepository.saveAndFlush(groupM);

		List<User> userList = userRepository.findAll();
		List<Category> categoryList = categoryRepository.findAll();
		List<Priority> priorityList = priorityRepository.findAll();
		List<Group> groupList = groupRepository.findAll();
		mv.addObject("ulist", userList);
		mv.addObject("clist", categoryList);
		mv.addObject("plist", priorityList);
		mv.addObject("glist", groupList);

		mv.setViewName("addTask");
		return mv;
	}

	//グループ編集アクション
	@RequestMapping("/group/edit")
	public ModelAndView editGroup(
			@RequestParam(name = "id") int id,
			ModelAndView mv) {

		Group group = null;

		List<Group> recode = groupRepository.findById(id);

		if (recode.isEmpty() == false) {
			group = recode.get(0);
		}

		List<Category> categoryList = categoryRepository.findAll();
		List<Priority> priorityList = priorityRepository.findAll();
		List<Group> groupList = groupRepository.findAll();
		mv.addObject("clist", categoryList);
		mv.addObject("plist", priorityList);
		mv.addObject("glist", groupList);

		mv.addObject("group", group);

		mv.setViewName("editGroup");
		return mv;
	}

//	//編集アクション
//	@PostMapping("/group/update")
//	public ModelAndView edit1(
//			@RequestParam(name = "id") int id,
//			@RequestParam("name") String name,
//			@RequestParam("member") String member,
//			ModelAndView mv) {
//
//		//未入力チェック
//		if (name == null || name == "") {
//			mv.addObject("msg1", "グループ名を入力してください");
//			mv.setViewName("editGroup");
//			return mv;
//		}
//
//		//期限日の型変換
//		//String型の期限日(dline)をjavaのDate型(yyyy/MM/dd)に変換
//		SimpleDateFormat sdFormat = new SimpleDateFormat("yyyy/MM/dd");
//		java.util.Date date = null;
//		try {
//			date = sdFormat.parse(dline);
//		} catch (ParseException e) {
//			e.printStackTrace();
//		}
//
//		if (date == null) {
//			Date dline3 = Date.valueOf(dline);
//			Task task = new Task(code, name, userId, dline3, prtNum, cgCode, groupId, progress, memo, true);
//			taskRepository.saveAndFlush(task);
//		} else {
//			//書式を(yyyy/MM/dd)から(yyyy-MM-dd)に変換し、String型に戻す
//			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
//			String date2 = sdf.format(date);
//			//String型をData型(SQL)dline2に変換
//			Date dline2 = Date.valueOf(date2);
//			Task task = new Task(code, name, userId, dline2, prtNum, cgCode, groupId, progress, memo, true);
//			taskRepository.saveAndFlush(task);
//		}
//
//		//空の表示用リストを生成
//		ArrayList<Task> list = new ArrayList<Task>();
//
//		//全てのタスクを取得
//		List<Task> taskList = taskRepository.findByOrderByCodeAsc();
//
//		//ゴミ箱に入れていなければ、表示するリストに追加
//		for (Task task1 : taskList) {
//			if (task1.isTrash() == true) {
//				list.add(task1);
//			}
//		}
//
//		List<User> userList = userRepository.findAll();
//		List<Category> categoryList = categoryRepository.findAll();
//		List<Priority> priorityList = priorityRepository.findAll();
//		List<Group> groupList = groupRepository.findAll();
//		mv.addObject("ulist", userList);
//		mv.addObject("clist", categoryList);
//		mv.addObject("plist", priorityList);
//		mv.addObject("glist", groupList);
//
//		mv.addObject("list", list);
//
//		mv.setViewName("list");
//		return mv;
//	}

}
