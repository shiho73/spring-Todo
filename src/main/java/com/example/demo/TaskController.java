package com.example.demo;

import java.sql.Date;
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
		mv = listAndTrash(false, mv);//リスト一覧を準備
		mv = almostDeadline(mv);//期限の文字色を変更

		//セッションチェック(null回避)・管理者(id==1)かをチェック
		User login = (User) session.getAttribute("userInfo");
		if (login == null) {
			session.invalidate();
			mv.addObject("message", "セッションがタイムアウトしました");
			mv.setViewName("top");
		} else if (login.getId() == 1) {
			//管理者であれば、ユーザ管理ページへのリンクを出現
			mv.addObject("flag", true);
		}

		mv.setViewName("list");//タスク一覧画面に遷移
		return sessiontest(mv);
	}

	//ゴミ箱を見る
	@RequestMapping("/look/trash")
	public ModelAndView lookTrash(ModelAndView mv) {
		mv = listAndTrash(true, mv);//ゴミ箱内一覧を準備

		mv.setViewName("trash");//ゴミ箱画面に遷移
		return sessiontest(mv);
	}

	//ゴミ箱へ投げる
	@GetMapping("/list/trash")
	public ModelAndView goTrash(
			@RequestParam(name = "code") int[] code,
			ModelAndView mv) {

		//コードの数だけチェックボックスを取得する
		for (int i = 0; i < code.length; i++) {
			//タスクのレコードを取得
			Optional<Task> record = taskRepository.findById(code[i]);

			//レコードが存在すれば、レコードからタスクを取得
			if (record.isEmpty() == false) {
				Task task = record.get();
				//タスクの進捗度を更新
				task.setTrash(false);
				taskRepository.saveAndFlush(task);
			}
		}

		return lookList(mv);//リスト一覧を再度表示
	}

	//ゴミ箱から戻す
	@RequestMapping("/trash/recovery")
	public ModelAndView trash(
			@RequestParam(name = "code") int code,
			ModelAndView mv) {
		//復元対象のタスクのレコードを取得
		Optional<Task> record = taskRepository.findById(code);

		//レコードが存在すれば、レコードからタスクを取得
		if (record.isEmpty() == false) {
			Task task = record.get();
			//表示場所(一覧/ゴミ箱)の判定フラッグを更新
			task.setTrash(true);
			taskRepository.saveAndFlush(task);
		}

		return lookTrash(mv);//ゴミ箱を再度表示
	}

	//タスク完全消去
	@RequestMapping("/delete")
	public ModelAndView delete(
			@RequestParam(name = "code") int code,
			ModelAndView mv) {
		//コードで指定したタスクを消去
		taskRepository.deleteById(code);

		return lookTrash(mv);//ゴミ箱を再度表示
	}

	//一括更新ボタン
	@GetMapping("/task/update")
	public ModelAndView update(
			@RequestParam(name = "code") int[] code,
			@RequestParam(name = "progress") int[] progress,
			ModelAndView mv) {

		//コードの数だけプログレスバーを取得する
		for (int i = 0; i < code.length; i++) {
			//タスクのレコードを取得
			Optional<Task> record = taskRepository.findById(code[i]);

			//レコードが存在すれば、レコードからタスクを取得
			if (record.isEmpty() == false) {
				Task task = record.get();
				//タスクの進捗度を更新
				task.setProgress(progress[i]);
				taskRepository.saveAndFlush(task);
			}
		}

		return lookList(mv);//リスト一覧を再度表示
	}

	//カレンダーボタン
	@GetMapping("/task/calendar")
	public ModelAndView calendar(
			@RequestParam(name = "dateText") String dateText,
			ModelAndView mv) {
		mv = listAndTrash(false, mv);//リスト一覧を準備
		mv = almostDeadline(mv);//期限の文字色を変更

		Date date = Date.valueOf(dateText);

		//指定された日付で検索
		List<Task> taskList = taskRepository.findByDline(date);

		mv.addObject("list", taskList);

		mv.setViewName("list");//タスク一覧画面に遷移
		return sessiontest(mv);
	}

}