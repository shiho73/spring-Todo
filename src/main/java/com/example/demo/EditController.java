package com.example.demo;

import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Optional;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.example.demo.category.CategoryRepository;
import com.example.demo.group.GroupRepository;
import com.example.demo.priority.PriorityRepository;
import com.example.demo.task.Task;
import com.example.demo.task.TaskRepository;
import com.example.demo.user.UserRepository;

@Controller
public class EditController extends SuperController {

	//セッションのレポジトリをセット
	@Autowired
	HttpSession session;
	//各テーブルのレポジトリをセット
	@Autowired
	TaskRepository taskRepository;
	@Autowired
	CategoryRepository categoryRepository;
	@Autowired
	GroupRepository groupRepository;
	@Autowired
	UserRepository userRepository;
	@Autowired
	PriorityRepository priorityRepository;

	//新規作成
	@RequestMapping("/list/new")
	public ModelAndView listnew(ModelAndView mv) {
		mv = listAndTrash(false, mv);//表示の準備
		mv.setViewName("addTask");//遷移先(タスク作成ページ)を指定
		return sessiontest(mv);
	}

	//新規作成アクション
	@PostMapping("/list/add")
	public ModelAndView listnew1(ModelAndView mv,
			@RequestParam("name") String name,
			@RequestParam("userId") int userId,
			@RequestParam("dline") String dline,
			@RequestParam("prtNum") int prtNum,
			@RequestParam("cgCode") int cgCode,
			@RequestParam("groupId") int groupId,
			@RequestParam("memo") String memo) {

		//未入力チェック
		if (name == null || name == "" || dline == null || dline == "") {
			if ((name == null || name == "") && (dline == null || dline == "")) {
				mv.addObject("message", "タスク名と期限を入力してください");
			} else if (name == null || name == "") {
				mv.addObject("message", "タスク名を設定してください");
			} else if (dline == null || dline == "") {
				mv.addObject("message", "期限を設定してください");
			}
			return listnew(mv);//編集ページに戻る
		}

		//期限日の型を変換し、タスクを登録
		int code = 0;
		int progress = 0;
		dateExchange(code, name, userId, dline, prtNum, cgCode, groupId,
				progress, memo);

		mv.setViewName("redirect:/list");
		return sessiontest(mv);

	}

	//編集
	@RequestMapping("/list/edit")
	public ModelAndView edit(
			@RequestParam(name = "code") int code,
			ModelAndView mv) {
		//タスクのレコードを取得
		Optional<Task> recode = taskRepository.findById(code);

		//変数taskの初期化
		Task task = null;
		//レコードが存在すれば、レコードからタスクを取得
		if (recode.isEmpty() == false) {
			task = recode.get();
		}
		mv.addObject("task", task);//表示の準備

		mv = listAndTrash(false, mv);//編集ページ表示の準備
		mv.setViewName("editTask");//遷移先(編集ページ)を指定
		return sessiontest(mv);
	}

	//編集アクション
	@PostMapping("/update")
	public ModelAndView edit1(
			@RequestParam(name = "code") int code,
			@RequestParam(name = "name") String name,
			@RequestParam(name = "userId") int userId,
			@RequestParam(name = "dline") String dline,
			@RequestParam(name = "prtNum") int prtNum,
			@RequestParam(name = "cgCode") int cgCode,
			@RequestParam(name = "groupId") int groupId,
			@RequestParam(name = "progress") int progress,
			@RequestParam(name = "memo") String memo,
			ModelAndView mv) {

		//未入力チェック
		if (name == null || name == "") {
			mv.addObject("message", "タスク名を入力してください");
			edit(code, mv);//編集画面を再表示
			return sessiontest(mv);
		}

		//期限日の型を変換し、タスクを更新
		dateExchange(code, name, userId, dline, prtNum, cgCode, groupId,
				progress, memo);

		mv.setViewName("redirect:/list");
		return sessiontest(mv);
	}

	//カテゴリ作成・編集からタスク編集へ戻る
	@RequestMapping("/list/edit{tcode}")
	public ModelAndView returnEditTask(
			@PathVariable(name = "tcode") int tcode,
			ModelAndView mv) {
		mv = listAndTrash(false, mv);//遷移先の表示準備
		//タスクのレコードを取得
		Optional<Task> recode = taskRepository.findById(tcode);
		//変数taskの初期化
		Task task = null;
		//レコードが存在すれば、レコードからタスクを取得
		if (recode.isEmpty() == false) {
			task = recode.get();
		}
		mv.addObject("task", task);//表示の準備
		mv.setViewName("editTask");//遷移先(編集ページ)を指定
		return sessiontest(mv);
	}

	//期限日型変換
	private void dateExchange(int code, String name, int userId, String dline, int prtNum, int cgCode, int groupId,
			int progress, String memo) {

		//期限日の型変換
		//String型の期限日(dline)をjavaのDate型(yyyy/MM/dd)に変換
		SimpleDateFormat sdFormat = new SimpleDateFormat("yyyy/MM/dd");
		java.util.Date date = null;
		try {
			date = sdFormat.parse(dline);
		} catch (ParseException e) {
			e.printStackTrace();
		}

		//文字列が元々sqlの書式だった場合(編集時のみ存在)
		if (date == null) {
			//String型からsqlのDate型に変換
			Date dline3 = Date.valueOf(dline);
			//Taskを編集
			Task task = new Task(code, name, userId, dline3, prtNum, cgCode, groupId, progress, memo, true);
			taskRepository.saveAndFlush(task);
		} else {
			//文字列dlineのjava・Date型への変換が行われていた場合
			//書式を(yyyy/MM/dd)から(yyyy-MM-dd)に変換
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			//String型に戻す
			String date2 = sdf.format(date);

			//String型をData型(SQL)dline2に変換
			Date dline2 = Date.valueOf(date2);

			//作成と編集を分岐させ、それぞれ実行
			Task task = null;
			if (code == 0) {
				task = new Task(name, userId, dline2, prtNum, cgCode, groupId, memo, true);
			} else {
				task = new Task(code, name, userId, dline2, prtNum, cgCode, groupId, progress, memo, true);
			}
			taskRepository.saveAndFlush(task);
		}

	}

}
