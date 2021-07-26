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

	//Categoryデータベース/
	@Autowired
	CategoryRepository categoryRepository;

	//ユーザ管理画面へ遷移
	@PostMapping("/users/list")
	public ModelAndView usersList(
			ModelAndView mv,
			@RequestParam("pw") String pw) {

		//未入力チェック
		if (pw == "" || pw == null) {
			mv = lookList02(mv);
			mv.addObject("msg00", "パスワードが入力されていません");
			return sessiontest(mv);
		}

		//パスワード(固定)照合
		if (!pw.equals("himituToDo")) {
			mv = lookList02(mv);
			mv.addObject("msg00", "パスワードが違います");
			return sessiontest(mv);
		}

		//各ユーザの「秘密の質問」表示準備
		mv.addObject("h1", "出身地は？");
		mv.addObject("h2", "ペットの名前は？");
		mv.addObject("h3", "親の旧姓は？");

		//全ユーザの情報を取得
		List<User> userList = userRepository.findByOrderByIdAsc();

		//管理者と削除済ユーザを非表示に
		userList.remove(0);
		userList.remove(0);

		mv.addObject("ulist", userList);//ユーザ一覧表示準備
		mv.setViewName("userManage");//遷移先を指定
		return sessiontest(mv);
	}

	//ユーザ削除画面へ遷移
	@RequestMapping("/user/delete/check")
	public ModelAndView userDeleteCheck01(
			ModelAndView mv,
			@RequestParam("id") int id) {
		//対象のユーザのレコードを取得
		Optional<User> record = userRepository.findById(id);
		//レコードが空でなければ、Userを作成
		User user = new User();
		if (record.isEmpty() == false) {
			user = record.get();
		}

		//対象のユーザが作成したタスクのリストを取得
		List<Task> tlist = taskRepository.findByUserId(id);

		//表示準備
		mv.addObject("user", user);
		mv.addObject("tlist", tlist);

		mv.addObject("flag0", true);//削除確認(第一段階)の表示
		mv.setViewName("userDelete");//遷移先の指定
		return sessiontest(mv);
	}

	//削除確認
	@RequestMapping("/user/delete/check02")
	public ModelAndView userDeleteCheck02(
			ModelAndView mv,
			@RequestParam("id") int id) {
		//対象のユーザのレコードを取得
		Optional<User> record = userRepository.findById(id);
		//レコードが空でなければ、Userを作成
		User user = new User();
		if (record.isEmpty() == false) {
			user = record.get();
		}

		//対象のユーザが作成したタスクのリストを取得
		List<Task> tlist = taskRepository.findByUserId(id);

		//表示準備
		mv.addObject("tlist", tlist);
		mv.addObject("user", user);

		mv.addObject("flag0", false);//削除確認(第一段階)の表示
		mv.addObject("flag", true);//削除確認(第二段階)の表示

		mv.setViewName("userDelete");//遷移先の指定
		return sessiontest(mv);
	}

	//削除対象ユーザが作ったタスクを消去
	@RequestMapping("/user/delete/all")
	public ModelAndView userDeleteAll(
			ModelAndView mv,
			@RequestParam("id") int id) {

		//削除対象ユーザが作成したタスクを取得
		List<Task> tlist = taskRepository.findByUserId(id);
		//タスクの削除を実行
		for (Task t : tlist) {
			taskRepository.deleteById(t.getCode());
		}

		//ユーザデータの削除を実行
		userRepository.deleteById(id);

		//ユーザ管理画面へ遷移
		String pw = "himituToDo";
		mv = usersList(mv, pw);

		mv.addObject("finish", "正常に削除されました");//メッセージを表示

		return sessiontest(mv);
	}

	//削除対象ユーザが作ったタスクを残す
	@RequestMapping("/user/delete/taihi")
	public ModelAndView userDeleteTaihi(
			ModelAndView mv,
			@RequestParam("id") int id) {

		//削除対象ユーザが作成したタスクを取得
		List<Task> tlist = taskRepository.findByUserId(id);
		//タスクの作成者を「削除済のユーザ(id==2)」に変更
		for (Task t : tlist) {
			t.setUserId(2);
			taskRepository.saveAndFlush(t);
		}

		//ユーザデータの削除を実行
		userRepository.deleteById(id);

		//ユーザ管理画面へ遷移
		String pw = "himituToDo";
		mv = usersList(mv, pw);

		mv.addObject("finish", "正常に削除されました");//メッセージを表示

		return sessiontest(mv);
	}

	@RequestMapping("/user/delete/cancel")
	public ModelAndView userDeleteCancel(
			ModelAndView mv) {

		//ユーザ管理画面へ遷移
		String pw = "himituToDo";
		mv = usersList(mv, pw);

		return sessiontest(mv);
	}

	//ユーザ情報更新ページへ
	@PostMapping("/userInfo")
	public ModelAndView userInfo(ModelAndView mv) {
		mv.setViewName("changeUserInfo");
		return sessiontest(mv);
	}

	//ユーザネーム更新
	@PostMapping("/userInfo/change/name")
	public ModelAndView changeUserName(
			@RequestParam("id") Integer id,
			@RequestParam("name") String name,
			ModelAndView mv) {
		Optional<User> record = userRepository.findById(id);
		User user = new User();
		if (!record.isEmpty()) {
			user.setName(name);
			userRepository.saveAndFlush(user);
		}
		mv.setViewName("changeUserInfo");
		return sessiontest(mv);
	}

	//ユーザpw更新
	@PostMapping("/userInfo/change/pw")
	public ModelAndView changeUserPw(
			@RequestParam("id") Integer id,
			@RequestParam("pw") String pw,
			@RequestParam("pw2") String pw2,
			ModelAndView mv) {
		//パスワードと確認用が一致していなければ、エラー
		if (!pw.equals(pw2)) {
			mv.addObject("message", "パスワードが一致していません");
			return mv;
		}

		Optional<User> record = userRepository.findById(id);
		User user = new User();
		if (!record.isEmpty()) {
			user.setPw(pw);
			userRepository.saveAndFlush(user);
			mv.addObject("message", "パスワードが変更されました");
		} else {
			mv.addObject("message", "エラーが発生しました。もう一度操作を行ってください");
		}
		mv.setViewName("changeUserInfo");
		return sessiontest(mv);
	}

	//秘密の質問更新
	@PostMapping("/userInfo/change/himitu")
	public ModelAndView changeUserHimitu(
			@RequestParam("id") Integer id,
			@RequestParam("himitu") String himitu,
			@RequestParam("himituCode") Integer himituCode,
			ModelAndView mv) {
		Optional<User> record = userRepository.findById(id);
		User user = new User();
		if (!record.isEmpty()) {
			user.setHimitu(himitu);
			user.setHimituCode(himituCode);
			userRepository.saveAndFlush(user);
		}
		mv.setViewName("changeUserInfo");
		return sessiontest(mv);
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
