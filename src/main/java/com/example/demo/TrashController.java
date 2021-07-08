package com.example.demo;

import java.sql.Date;
import java.util.ArrayList;
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
import com.example.demo.task.Task;
import com.example.demo.task.TaskRepository;
import com.example.demo.user.UserRepository;

@Controller
public class TrashController {

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

	//編集
		@RequestMapping("/list/edit")
		public ModelAndView edit(
				@RequestParam(name = "code") int code,
				ModelAndView mv) {

			Task task = null;

			Optional<Task> recode = taskRepository.findById(code);

			if (recode.isEmpty() == false) {
				task = recode.get();
			}

			mv.addObject("task", task);

			mv.setViewName("editTask");
			return mv;
		}

		//編集アクション
		@PostMapping("/update")
		public ModelAndView edit1(
				@RequestParam(name = "code") int code,
				@RequestParam(name = "name") String name,
				@RequestParam(name = "user_id") int user_id,
				@RequestParam(name = "dline") Date dline,
				@RequestParam(name = "prt_num") int prt_num,
				@RequestParam(name = "cg_code") int cg_code,
				@RequestParam(name = "group_id") int group_id,
				@RequestParam(name = "progress") int progress,
				@RequestParam(name = "memo") String memo,
				ModelAndView mv) {

			Task task = new Task(code, name, user_id, dline, prt_num, cg_code, group_id, progress, memo, true);
			taskRepository.saveAndFlush(task);


			//空の表示用リストを生成
			ArrayList<Task> list = new ArrayList<Task>();

			//全てのタスクを取得
			List<Task> taskList = taskRepository.findAll();

			//ゴミ箱に入れていなければ、表示するリストに追加
			for (Task task1 : taskList) {
				if (task1.isTrash() == true) {
					list.add(task1);
				}
			}

			mv.addObject("list", list);

			mv.setViewName("list");
			return mv;
		}

	//ゴミ箱へ投げる
	@RequestMapping("/list/trash")
	public ModelAndView listtrash(
			@RequestParam(name = "code") int code,
			@RequestParam(name = "name") String name,
			@RequestParam(name = "user_id") int user_id,
			@RequestParam(name = "dline") Date dline,
			@RequestParam(name = "prt_num") int prt_num,
			@RequestParam(name = "cg_code") int cg_code,
			@RequestParam(name = "group_id") int group_id,
			@RequestParam(name = "progress") int progress,
			@RequestParam(name = "memo") String memo,
			ModelAndView mv) {

		Task task = new Task(code, name, user_id, dline, prt_num, cg_code, group_id, progress, memo, false);
		taskRepository.saveAndFlush(task);

		//空の表示用リストを生成
		ArrayList<Task> list = new ArrayList<Task>();

		//全てのタスクを取得
		List<Task> taskList = taskRepository.findAll();

		//ゴミ箱に入れていなければ、表示するリストに追加
		for (Task task1 : taskList) {
			if (task1.isTrash() == true) {
				list.add(task1);
			}
		}

		//リストが空であれば、メッセージを表示
		//リストの中身があれば、リストを表示
		if (list.isEmpty() == true) {
			mv.addObject("message", "リストは空です");
		} else {
			mv.addObject("list", list);
		}

		mv.setViewName("list");
		return mv;
	}

	//ゴミ箱から戻す
	@RequestMapping("/trash/recovery")
	public ModelAndView trash(
			@RequestParam(name = "code") int code,
			@RequestParam(name = "name") String name,
			@RequestParam(name = "user_id") int user_id,
			@RequestParam(name = "dline") Date dline,
			@RequestParam(name = "prt_num") int prt_num,
			@RequestParam(name = "cg_code") int cg_code,
			@RequestParam(name = "group_id") int group_id,
			@RequestParam(name = "progress") int progress,
			@RequestParam(name = "memo") String memo,
			ModelAndView mv) {

		Task task = new Task(code, name, user_id, dline, prt_num, cg_code, group_id, progress, memo, true);
		taskRepository.saveAndFlush(task);

		//空の表示用リストを生成
		ArrayList<Task> list = new ArrayList<Task>();

		//全てのタスクを取得
		List<Task> taskList = taskRepository.findAll();

		//ゴミ箱に入れていれば、表示するリストに追加
		for (Task task1 : taskList) {
			if (task1.isTrash() == false) {
				list.add(task1);
			}
		}

		//リストが空であれば、メッセージを表示
		//リストの中身があれば、リストを表示
		if (list.isEmpty() == true) {
			mv.addObject("message", "ゴミ箱は空です");
		} else {
			mv.addObject("list", list);
		}

		mv.setViewName("trash");
		return mv;
	}

	//タスク完全消去
	@RequestMapping("/delete")
	public ModelAndView delete(
			@RequestParam(name = "code") int code,
			ModelAndView mv) {

		taskRepository.deleteById(code);

		//空の表示用リストを生成
		ArrayList<Task> list = new ArrayList<Task>();

		//全てのタスクを取得
		List<Task> taskList = taskRepository.findAll();

		//ゴミ箱に入れていれば、表示するリストに追加
		for (Task task1 : taskList) {
			if (task1.isTrash() == false) {
				list.add(task1);
			}
		}

		//リストが空であれば、メッセージを表示
		//リストの中身があれば、リストを表示
		if (list.isEmpty() == true) {
			mv.addObject("message", "ゴミ箱は空です");
		} else {
			mv.addObject("list", list);
		}

		mv.setViewName("trash");
		return mv;
	}

}
