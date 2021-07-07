package com.example.demo;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.example.demo.category.CategoryRepository;
import com.example.demo.group.GroupRepository;
import com.example.demo.task.TaskRepository;
import com.example.demo.user.UserRepository;

@Controller
public class TaskController {

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


	//http://localhost:8080/
	//ログイン画面
	@RequestMapping("/")
	public String task() {
		// セッション情報はクリアする
		session.invalidate();
		return "top";
	}

	//ログイン実行
	@PostMapping("/login")
	public ModelAndView login(
			@RequestParam("name") String name,
			@RequestParam("pw") String pw,
			ModelAndView mv) {

		// 名前が空の場合にエラーとする
		return mv;
	}


	//タスク一覧
	@RequestMapping("/list")
	public ModelAndView list(ModelAndView mv) {

		mv.setViewName("list");
		return mv;
	}

	//新規作成
	@RequestMapping("/list/new")
	public ModelAndView listnew(ModelAndView mv) {

		mv.setViewName("addTask");
		return mv;
	}

	//編集
	@RequestMapping("/list/edit")
	public ModelAndView edit(ModelAndView mv) {

		mv.setViewName("editTask");
		return mv;
	}

	//ゴミ箱
	@RequestMapping("/list/trash")
	public ModelAndView trash(ModelAndView mv) {

		mv.setViewName("trash");
		return mv;
	}

	//カレンダー
	@RequestMapping("/list/calender")
	public ModelAndView calender(ModelAndView mv) {

		mv.setViewName("calender");
		return mv;
	}

	//カレンダー
	@RequestMapping("/logout")
	public ModelAndView logout(ModelAndView mv) {

		mv.setViewName("top");
		return mv;
	}


}
