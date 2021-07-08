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
import com.example.demo.task.Task;
import com.example.demo.task.TaskRepository;
import com.example.demo.user.UserRepository;

@Controller
public class EditController {

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


	//新規作成
	@PostMapping("/list/new")
	public ModelAndView listnew(ModelAndView mv) {

		mv.setViewName("addTask");
		return mv;
	}

	//新規作成アクション
	@PostMapping("/list/add")
	public ModelAndView listnew1(ModelAndView mv,
			@RequestParam("task") String task,
			@RequestParam("name") String name,
			@RequestParam("dline") String dline,
			@RequestParam("prt_num") int prt_num,
			@RequestParam("cg_code") int dlicg_codene,
			@RequestParam("group_id") int group_id,
			@RequestParam("memo") String memo
) {

		//Task.javaに記述する予定
		//新しく追加
		Task tasklist = new Task(task,name,dline, prt_num, dlicg_codene,group_id,memo);
		taskRepository.saveAndFlush(tasklist);

		//すべてのリスト取得
		List<Task> taskList = taskRepository.findAll();

		//Thymeleafで表示する準備
		mv.addObject("list", taskList);

		mv.setViewName("list");
		return mv;
	}

	//編集
	@RequestMapping("/list/edit")
	public ModelAndView edit(ModelAndView mv) {

		mv.setViewName("editTask");
		return mv;
	}

	//編集アクション
	@PostMapping("/list/edit")
	public ModelAndView edit1(ModelAndView mv) {

		mv.setViewName("list");
		return mv;
	}



}
