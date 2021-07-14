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
import com.example.demo.priority.PriorityRepository;
import com.example.demo.task.TaskRepository;
import com.example.demo.user.User;
import com.example.demo.user.UserRepository;

@Controller
public class UserController {

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

	//http://localhost:8080/
	//ログイン画面
	@RequestMapping("/")
	public ModelAndView task(ModelAndView mv) {
		session.invalidate();
		mv.setViewName("top");
		return mv;
	}

	//ログイン
	@PostMapping("/login")
	public ModelAndView login(ModelAndView mv,
			@RequestParam("name") String name,
			@RequestParam("pw") String pw) {

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

			if (!pw.equals(userInfo.getPw())) {
				mv.addObject("message", "パスワードが違います");
				mv.setViewName("top");
				return mv;
			}

			// セッションスコープにカテゴリ情報を格納する
			session.setAttribute("name", name);
			session.setAttribute("userInfo", userInfo);

			mv.setViewName("redirect:/list");

		} else {
			//見つからなかった場合ログインNG
			mv.addObject("message", "入力された情報は登録されていません");
			mv.setViewName("top");
		}
		return mv;
	}

	//新規ユーザー登録
	@RequestMapping("/user")
	public ModelAndView user(ModelAndView mv) {
		session.invalidate();
		mv.setViewName("newUser");
		return mv;
	}

	//新規ユーザー登録アクション
	@PostMapping("/user/new")
	public ModelAndView newuser(ModelAndView mv,
			@RequestParam("name") String name,
			@RequestParam("pw") String pw,
			@RequestParam("pw1") String pw1,
			@RequestParam("himitu") String himitu,
			@RequestParam("himituCode") int himituCode) {

		// 名前とパスワードが空の場合にエラーとする
		if (name == null || name.length() == 0 || pw == null || pw.length() == 0 || pw1 == null || pw1.length() == 0) {
			mv.addObject("message", "名前とパスワードを入力してください");
			mv.setViewName("newUser");
			return mv;
		}

		//秘密の質問の答えが空の場合にエラーとする
		if (himitu == null || himitu.length() == 0) {
			mv.addObject("message", "秘密の質問の答えを入力してください");
			mv.setViewName("newUser");
			return mv;
		}

		List<User> user = userRepository.findByName(name);

		//同じ名前がヒットしたら
		if (!user.isEmpty()) {
			mv.addObject("message", "既にその名前は登録されています");
			mv.setViewName("newUser");
			return mv;
		} else {

			if (pw.equals(pw1)) {
				//t_user新しく追加
				User user1 = new User(name, pw, himitu, himituCode);
				userRepository.saveAndFlush(user1);

				List<User> userList = userRepository.findAll();
				mv.addObject("user", userList);

				mv.setViewName("finished");
				return mv;

			} else {

				mv.addObject("message", "パスワードが一致していません");
				mv.setViewName("newUser");
			}
		}
		return mv;
	}

	//パスワード忘れた人
	@RequestMapping("/change")
	public ModelAndView change(ModelAndView mv) {
		mv.addObject("flag", false);
		mv.setViewName("changePw");
		return mv;
	}

	//秘密の質問取得
	@PostMapping("/himitu/get")
	public ModelAndView himitu(
			@RequestParam("name") String name,
			ModelAndView mv) {

		List<User> user = userRepository.findByName(name);

		if (user.isEmpty()) {
			//見つからなかった場合取得NG
			mv.addObject("message", "入力された情報は登録されていません");
			return change(mv);
		}

		User user1 = user.get(0);
		Integer code = user1.getHimituCode();

		if (code == 1) {
			mv.addObject("quiz", "出身地は？");
		} else if (code == 2) {
			mv.addObject("quiz", "ペットの名前は？");
		} else if (code == 3) {
			mv.addObject("quiz", "親の旧姓は？");
		}

		mv.addObject("userName", user1.getName());
		mv.addObject("flag", true);
		mv.setViewName("changePw");

		return mv;
	}

	//パスワード忘れた人アクション
	@PostMapping("/change/pw")
	public ModelAndView changepw(
			@RequestParam("userName") String name,
			@RequestParam("himitu") String himitu,
			ModelAndView mv) {
		// 名前と秘密が空の場合にエラーとする
		if (name == null || name.length() == 0 || himitu == null || himitu.length() == 0) {
			mv.addObject("message", "名前と秘密の言葉を入力してください");
			return change(mv);
		}

		List<User> user = userRepository.findByName(name);

		//ヒットしたら
		if (!user.isEmpty()) {

			User userInfo = user.get(0); //一致した名前を含むリスト取得

			if(himitu.equals(userInfo.getHimitu())) {
				mv.addObject("pw", userInfo.getPw());
				mv.setViewName("change");
			} else if (!himitu.equals(userInfo.getHimitu())) {
				mv.addObject("message", "秘密の言葉が違います");
				return himitu(userInfo.getName(), mv);
			} else {
				//見つからなかった場合ログインNG
				mv.addObject("message", "入力された情報は登録されていません");
				return change(mv);
			}
		}

		return mv;

	}

	//ログアウト
	@RequestMapping("/logout")
	public ModelAndView logout(ModelAndView mv) {
		session.invalidate();
		mv.setViewName("top");
		return mv;
	}

}
