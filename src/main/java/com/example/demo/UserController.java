package com.example.demo;

import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.example.demo.task.TaskRepository;
import com.example.demo.user.User;
import com.example.demo.user.UserRepository;

@Controller
public class UserController {

	//保持用
	@Autowired
	HttpSession session;

	//Userデータベース
	@Autowired
	UserRepository userRepository;

	//Taskデータベース
	@Autowired
	TaskRepository taskRepository;

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
		mv.addObject("list", taskRepository.findAll());
		mv.setViewName("list");

	} else {
		//見つからなかった場合ログインNG
		mv.addObject("message", "入力された情報は登録されていません");
		mv.setViewName("top");
	}
	return mv;
}

	//ログアウト
	@RequestMapping("/logout")
	public ModelAndView logout(ModelAndView mv) {

		mv.setViewName("top");
		return mv;
	}

}
