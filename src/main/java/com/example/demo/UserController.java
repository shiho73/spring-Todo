package com.example.demo;

import java.util.ArrayList;
import java.util.List;

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

			//空の表示用リストを生成
			ArrayList<Task> list = new ArrayList<Task>();

			//全てのタスクを取得
			List<Task> taskList = taskRepository.findAll();

			//ゴミ箱に入れていなければ、表示するリストに追加
			for (Task task : taskList) {
				if (task.isTrash() == true) {
					list.add(task);
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

		} else {
			//見つからなかった場合ログインNG
			mv.addObject("message", "入力された情報は登録されていません");
			mv.setViewName("top");
		}
		return mv;
	}

	//新規ユーザー登録
	//ログイン画面
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
			@RequestParam("pw1") String pw1) {

		// 名前とパスワードが空の場合にエラーとする
		if (name == null || name.length() == 0 || pw == null || pw.length() == 0 || pw1 == null || pw1.length() == 0) {
			mv.addObject("message", "名前とパスワードを入力してください");
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
				User user1 = new User(name, pw);
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

	//ログアウト
	@RequestMapping("/logout")
	public ModelAndView logout(ModelAndView mv) {
		session.invalidate();
		mv.setViewName("top");
		return mv;
	}

}
