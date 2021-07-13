package com.example.demo;

import java.sql.Date;
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
public class TaskController {

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

		mv.setViewName("list");//タスク一覧画面に遷移
		return mv;
	}

	//ゴミ箱を見る
	@RequestMapping("/look/trash")
	public ModelAndView lookTrash(ModelAndView mv) {
		//ゴミ箱内一覧を準備
		mv = listAndTrash(true, mv);

		//ゴミ箱画面に遷移
		mv.setViewName("trash");
		return mv;
	}

	//ゴミ箱へ投げる
	@RequestMapping("/list/trash")
	public ModelAndView goTrash(
			@RequestParam(name = "code") int code,
			@RequestParam(name = "name") String name,
			@RequestParam(name = "userId") int userId,
			@RequestParam(name = "dline") Date dline,
			@RequestParam(name = "prtNum") int prtNum,
			@RequestParam(name = "cgCode") int cgCode,
			@RequestParam(name = "groupId") int groupId,
			@RequestParam(name = "progress") int progress,
			@RequestParam(name = "memo") String memo,
			ModelAndView mv) {

		Task task = new Task(code, name, userId, dline, prtNum, cgCode, groupId, progress, memo, false);
		taskRepository.saveAndFlush(task);

		return lookList(mv);
	}

	//ゴミ箱から戻す
	@RequestMapping("/trash/recovery")
	public ModelAndView trash(
			@RequestParam(name = "code") int code,
			@RequestParam(name = "name") String name,
			@RequestParam(name = "userId") int userId,
			@RequestParam(name = "dline") Date dline,
			@RequestParam(name = "prtNum") int prtNum,
			@RequestParam(name = "cgCode") int cgCode,
			@RequestParam(name = "groupId") int groupId,
			@RequestParam(name = "progress") int progress,
			@RequestParam(name = "memo") String memo,
			ModelAndView mv) {

		Task task = new Task(code, name, userId, dline, prtNum, cgCode, groupId, progress, memo, true);
		taskRepository.saveAndFlush(task);

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



	//表示するための道具
	private ModelAndView listAndTrash(boolean tflag, ModelAndView mv) {
		//各テーブルから全件検索
		List<User> userList = userRepository.findAll();
		List<Category> categoryList = categoryRepository.findAll();
		List<Priority> priorityList = priorityRepository.findAll();
		List<Group> groupList = groupRepository.findAll();

		//空の表示用リストを生成
		ArrayList<Task> list = new ArrayList<Task>();

		//全てのタスクを取得
		List<Task> taskList = taskRepository.findByOrderByCodeAsc();

		//タスク一覧とゴミ箱で分岐
		if (tflag == true) {
			//ゴミ箱に入れていれば、表示するリストに追加
			for (Task task : taskList) {
				if (task.isTrash() == false) {
					list.add(task);
				}
			}
			//リストが空であれば、メッセージを表示
			if (list.isEmpty() == true) {
				mv.addObject("message", "ゴミ箱は空です");
			}
		} else if (tflag == false) {
			//ゴミ箱に入れていなければ、表示するリストに追加
			for (Task task1 : taskList) {
				if (task1.isTrash() == true) {
					list.add(task1);
				}
			}
		}

		//Thymeleafで表示する準備
		mv.addObject("ulist", userList);
		mv.addObject("clist", categoryList);
		mv.addObject("plist", priorityList);
		mv.addObject("glist", groupList);
		mv.addObject("list", list);

		return mv;
	}

}