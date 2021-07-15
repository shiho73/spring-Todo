package com.example.demo;

import java.util.List;
import java.util.Optional;

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

	//保持用
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
			@RequestParam("pw") String pw) {

		if (pw == "" || pw == null) {
			mv = lookList02(mv);
			mv.addObject("msg00", "パスワードが入力されていません");
			return mv;
		}

		if (!pw.equals("himituToDo")) {
			mv = lookList02(mv);
			mv.addObject("msg00", "パスワードが違います");
			return mv;
		}

		mv.addObject("h1", "出身地は？");
		mv.addObject("h2", "ペットの名前は？");
		mv.addObject("h3", "親の旧姓は？");

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
		Optional<User> record = userRepository.findById(id);

		User user = new User();
		if (record.isEmpty() == false) {
			user = record.get();
		}

		mv.addObject("tlist", tlist);
		mv.addObject("user", user);
		mv.addObject("flag0", true);
		mv.setViewName("userDelete");
		return mv;
	}

	@RequestMapping("/user/delete/check02")
	public ModelAndView userDeleteCheck02(
			ModelAndView mv,
			@RequestParam("id") int id) {

		List<Task> tlist = taskRepository.findByUserId(id);
		Optional<User> record = userRepository.findById(id);

		User user = new User();
		if (record.isEmpty() == false) {
			user = record.get();
		}

		mv.addObject("tlist", tlist);
		mv.addObject("user", user);
		mv.addObject("flag0", false);
		mv.addObject("flag", true);
		mv.setViewName("userDelete");
		return mv;
	}

	@RequestMapping("/user/delete/all")
	public ModelAndView userDeleteAll(
			ModelAndView mv,
			@RequestParam("id") int id) {

		List<Task> tlist = taskRepository.findByUserId(id);
		for (Task t : tlist) {
			taskRepository.deleteById(t.getCode());
		}

		userRepository.deleteById(id);

		String pw = "himituToDo";
		mv = usersList(mv, pw);
		mv.addObject("finish", "正常に削除されました");

		return mv;
	}

	@RequestMapping("/user/delete/taihi")
	public ModelAndView userDeleteTaihi(
			ModelAndView mv,
			@RequestParam("id") int id) {

		List<Task> tlist = taskRepository.findByUserId(id);
		for (Task t : tlist) {
			t.setUserId(2);
			taskRepository.saveAndFlush(t);
		}

		userRepository.deleteById(id);

		String pw = "himituToDo";
		mv = usersList(mv, pw);
		mv.addObject("finish", "正常に削除されました");

		return mv;
	}

	@RequestMapping("/user/delete/cancel")
	public ModelAndView userDeleteCancel(
			ModelAndView mv) {

		String pw = "himituToDo";
		mv = usersList(mv, pw);

		return mv;
	}

	//タスク一覧を表示
	@RequestMapping("/list02")
	public ModelAndView lookList02(ModelAndView mv) {
		//リスト一覧を準備
		mv = listAndTrash(false, mv);

		//期限の文字色を変更
		mv = almostDeadline(mv);

		User login = (User) session.getAttribute("userInfo");
		if (login == null) {
			session.invalidate();
			mv.addObject("message", "セッションがタイムアウトしました");
			mv.setViewName("top");
		} else if (login.getId() == 1) {
			mv.addObject("flag", true);
		}

		mv.setViewName("list");//タスク一覧画面に遷移
		return sessiontest(mv);
	}

}
