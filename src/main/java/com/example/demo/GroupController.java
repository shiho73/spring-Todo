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
			@RequestParam(name = "member", defaultValue = "") String member,
			ModelAndView mv) {

		//未入力チェック
		if (name == null || name == "" || id == 0) {
			mv.addObject("msg1", "未入力の項目があります");
			mv.setViewName("addGroup");
			return mv;
		}

		//グループの重複チェック/
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

		GroupM groupM = new GroupM(id, member);
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
	@PostMapping("/group/edit")
	public ModelAndView editGroup(
			@RequestParam(name = "gid") int gid,
			ModelAndView mv) {

		Group group = null;

		List<Group> recode = groupRepository.findById(gid);

		if (recode.isEmpty() == false) {
			group = recode.get(0);
		}

		GroupM groupM = null;
		Optional<GroupM> recode2 = groupMRepository.findById(group.getId());
		if (recode2.isEmpty() == false) {
			groupM = recode2.get();
		}

		mv.addObject("group", group);
		mv.addObject("groupM", groupM);

		mv.setViewName("editGroup");
		return mv;
	}

	//編集アクション
	@PostMapping("/group/update")
	public ModelAndView updateGroup(
			@RequestParam(name = "id") int id,
			@RequestParam("name") String name,
			@RequestParam("gname") String gname,
			@RequestParam(name="member", defaultValue="") String member,
			@RequestParam(name = "gid") int gid,
			ModelAndView mv) {

		//未入力チェック
		if (name == null || name == "" || id == 0) {
			mv.addObject("msg1", "未入力の項目があります");

			Group group = null;

			List<Group> recode = groupRepository.findById(gid);

			if (recode.isEmpty() == false) {
				group = recode.get(0);
			}

			GroupM groupM = null;
			Optional<GroupM> recode2 = groupMRepository.findById(group.getId());
			if (recode2.isEmpty() == false) {
				groupM = recode2.get();
			}

			mv.addObject("group", group);
			mv.addObject("groupM", groupM);

			mv.setViewName("editGroup");

			return mv;
		}

		//グループの重複チェック/
		List<Group> list = groupRepository.findById(id);
		List<Group> list2 = groupRepository.findByName(name);

		if ((!list.isEmpty() && id != gid) || (!list2.isEmpty()  && !name.equals(gname))) {
			if (!list.isEmpty() && id != gid) {
				mv.addObject("msg1", "使用済みのグループ番号です");
			}
			if (!list2.isEmpty() && !name.equals(gname)) {
				mv.addObject("msg2", "使用済みのグループ名です");
			}

			Group group = null;

			List<Group> recode = groupRepository.findById(gid);

			if (recode.isEmpty() == false) {
				group = recode.get(0);
			}

			GroupM groupM = null;
			Optional<GroupM> recode2 = groupMRepository.findById(group.getId());
			if (recode2.isEmpty() == false) {
				groupM = recode2.get();
			}

			mv.addObject("group", group);
			mv.addObject("groupM", groupM);

			mv.setViewName("editGroup");
			return mv;
		}

		Group group = new Group(id, name);
		groupRepository.saveAndFlush(group);

		GroupM groupM = new GroupM(id, member);
		groupMRepository.saveAndFlush(groupM);

		List<User> userList = userRepository.findAll();
		List<Category> categoryList = categoryRepository.findAll();
		List<Priority> priorityList = priorityRepository.findAll();
		List<Group> groupList = groupRepository.findAll();
		mv.addObject("ulist", userList);
		mv.addObject("clist", categoryList);
		mv.addObject("plist", priorityList);
		mv.addObject("glist", groupList);

		mv.addObject("list", list);

		mv.setViewName("addTask");
		return mv;
	}

}
