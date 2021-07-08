package com.example.demo;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.example.demo.category.CategoryRepository;
import com.example.demo.group.GroupRepository;
import com.example.demo.task.Task;
import com.example.demo.task.TaskRepository;
import com.example.demo.user.UserRepository;

@Controller
public class TaskController {

	//保持用/
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

	//タスク一覧
	@RequestMapping("/list")
	public ModelAndView list(ModelAndView mv) {
		//空の表示用リストを生成
		ArrayList<Task> list = new ArrayList<Task>();

		//全てのタスクを取得
		List<Task> taskList = taskRepository.findAll();

		//ゴミ箱に入れていなければ、表示するリストに追加
		for(Task task : taskList) {
			if(task.isTrash() == true) {
				list.add(task);
			}
		}


			mv.addObject("list", list);

		//タスク一覧画面に遷移
		mv.setViewName("list");
		return mv;
	}

	//ゴミ箱を見る
	@RequestMapping("/look/trash")
	public ModelAndView looktrash(ModelAndView mv) {
		//空の表示用リストを生成
		ArrayList<Task> list = new ArrayList<Task>();

		//全てのタスクを取得
		List<Task> taskList = taskRepository.findAll();

		//ゴミ箱に入れていれば、表示するリストに追加
		for(Task task : taskList) {
			if(task.isTrash() == false) {
				list.add(task);
			}
		}

		//リストが空であれば、メッセージを表示
		//リストの中身があれば、リストを表示
		if(list == null) {
			mv.addObject("message", "ゴミ箱は空です");
		} else {
			mv.addObject("list", list);
		}

		//ゴミ箱画面に遷移
		mv.setViewName("trash");
		return mv;
	}

}
