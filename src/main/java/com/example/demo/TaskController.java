package com.example.demo;

import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
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
public class TaskController extends SuperController {

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

	//タスク一覧を表示
	@RequestMapping("/list")
	public ModelAndView lookList(ModelAndView mv) {
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

	//ゴミ箱を見る
	@RequestMapping("/look/trash")
	public ModelAndView lookTrash(ModelAndView mv) {
		//ゴミ箱内一覧を準備
		mv = listAndTrash(true, mv);

		//ゴミ箱画面に遷移
		mv.setViewName("trash");
		return sessiontest(mv);
	}

	//ゴミ箱へ投げる
	@RequestMapping("/list/trash")
	public ModelAndView goTrash(
			@RequestParam(name = "code") int code,
			ModelAndView mv) {

		Optional<Task> record = taskRepository.findById(code);

		if (record.isEmpty() == false) {
			Task task = record.get();
			task.setTrash(false);
			taskRepository.saveAndFlush(task);
		}

		return lookList(mv);
	}

	//ゴミ箱から戻す
	@RequestMapping("/trash/recovery")
	public ModelAndView trash(
			@RequestParam(name = "code") int code,
			ModelAndView mv) {

		Optional<Task> record = taskRepository.findById(code);

		if (record.isEmpty() == false) {
			Task task = record.get();
			task.setTrash(true);
			taskRepository.saveAndFlush(task);
		}

		return lookTrash(mv);
	}

	//タスク完全消去
	@RequestMapping("/delete")
	public ModelAndView delete(
			@RequestParam(name = "code") int code,
			ModelAndView mv) {
		//コードで指定したタスクを消去
		taskRepository.deleteById(code);

		return lookTrash(mv);
	}

	//一括更新ボタン
	@GetMapping("/task/update")
	public ModelAndView update(
			@RequestParam(name = "code") int[] code,
			@RequestParam(name = "progress") int[] progress,
			ModelAndView mv) {

		//コードの数だけプログレスバーを取得する
		for (int i = 0; i < code.length; i++) {
			Optional<Task> record = taskRepository.findById(code[i]);

			if (record.isEmpty() == false) {
				Task task = record.get();
				task.setProgress(progress[i]);
				taskRepository.saveAndFlush(task);
			}
		}

		return lookList(mv);
	}

	//カレンダーボタン
	@GetMapping("/task/calendar")
	public ModelAndView calendar(
			@RequestParam(name = "dateText") String dateText,
			ModelAndView mv) {

		//リスト一覧を準備
		mv = listAndTrash(false, mv);

		//期限の文字色を変更
		mv = almostDeadline(mv);

		//期限日の型変換
		//String型の期限日(dline)をjavaのDate型(yyyy/MM/dd)に変換
		SimpleDateFormat sdFormat = new SimpleDateFormat("yyyy/MM/dd");
		java.util.Date date = null;
		try {
			date = sdFormat.parse(dateText);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		//書式を(yyyy/MM/dd)から(yyyy-MM-dd)に変換
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		//String型に戻す
		String date2 = sdf.format(date);

		//String型をData型(SQL)dateText2に変換
		Date dateText2 = Date.valueOf(date2);

		//検索
		ArrayList<Task> list = new ArrayList<Task>();
		List<Task> taskList = taskRepository.findAll();
		for (Task t : taskList) {
			if (t.getDline().equals(dateText2)) {
				list.add(t);
			}
		}
		mv.addObject("list", list);

		mv.setViewName("list");//タスク一覧画面に遷移
		return sessiontest(mv);
	}

}