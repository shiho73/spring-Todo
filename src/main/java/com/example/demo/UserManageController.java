package com.example.demo;

import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.example.demo.category.CategoryRepository;
import com.example.demo.group.GroupRepository;
import com.example.demo.priority.PriorityRepository;
import com.example.demo.task.Task;
import com.example.demo.task.TaskRepository;
import com.example.demo.user.User;
import com.example.demo.user.UserRepository;

@Controller
public class UserManageController extends SuperController {

	//保持用/
	@Autowired
	HttpSession session;

	//Taskデータベース
	@Autowired
	TaskRepository taskRepository;

	//Userデータベース
	@Autowired
	UserRepository userRepository;

	//Groupデータベース
	@Autowired
	GroupRepository groupRepository;

	@Autowired
	PriorityRepository priorityRepository;

	//Categoryデータベース
	@Autowired
	CategoryRepository categoryRepository;

	@RequestMapping("/users/list")
	public ModelAndView usersList(
			ModelAndView mv,
			@RequestParam("password") String pw) {

		if (pw == "" || pw == null) {
			mv.addObject("msg00", "パスワードが入力されていません");
			mv = listAndTrash(false, mv);
			mv = almostDeadline(mv);
			mv.setViewName("/list");
			return mv;
		}

		if (pw != "himituToDo") {
			mv.addObject("msg00", "パスワードが違います");
			mv = listAndTrash(false, mv);
			mv = almostDeadline(mv);
			mv.setViewName("/list");
			return mv;
		}

		HashMap<Integer, String> hlist = new HashMap<Integer, String>();
		hlist.put(1, "出身地は？");
		hlist.put(2, "ペットの名前は？");
		hlist.put(3, "親の旧姓");
		mv.addObject("hlist", hlist);

		List<User> userList = userRepository.findByOrderByIdAsc();
		//管理者と削除済ユーザを非表示に
		userList.remove(0);
		userList.remove(0);

		mv.addObject("ulist", userList);

		mv.setViewName("userManage");

		return mv;
	}

	@RequestMapping("/user/delete/check")
	public ModelAndView userDeleteCheck01(
			ModelAndView mv,
			@RequestParam("id") int id) {

		List<Task> tlist = taskRepository.findByUserId(id);

		mv.addObject("tlist", tlist);
		mv.setViewName("userDelete");
		return mv;
	}

	@RequestMapping("/user/delete/cancel")
	public ModelAndView userDeleteCancel(
			ModelAndView mv) {

		String pw = "himituToDo";
		mv = usersList(mv, pw);

		return mv;
	}

}
