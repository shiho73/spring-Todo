package com.example.demo;

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
import com.example.demo.task.TaskRepository;
import com.example.demo.user.User;
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
	public ModelAndView task(ModelAndView mv) {
		session.invalidate();
		mv.setViewName("top");
		return mv;
	}

	//ログインボタン押したとき
	@PostMapping("/login")
	public ModelAndView login(ModelAndView mv,
			@RequestParam("name") String name,
			@RequestParam("pw") String pw
		) {

		// 名前とパスワードが空の場合にエラーとする
	if (name == null || name.length() == 0 || pw == null || pw.length() == 0) {
		mv.addObject("message", "名前とパスワードを入力してください");
		mv.setViewName("top");
		return mv;
	}

	List<User> user = userRepository.findByName(name);

	//ヒットしたら
	if (!user.isEmpty()) {

		User userInfo = user.get(0); //一致した名前を含むリスト取得

		if (!pw.equals(userInfo.getPw())){
			mv.addObject("message", "パスワードが違います");
			mv.setViewName("top");
			return mv;
		}

		// セッションスコープにカテゴリ情報を格納する
		session.setAttribute("name", name);
		session.setAttribute("userInfo", userInfo);
		session.setAttribute("task", taskRepository.findAll());
		mv.setViewName("list");

	} else {
		//見つからなかった場合ログインNG
		mv.addObject("message", "入力された情報は登録されていません");
		mv.setViewName("top");
	}
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
