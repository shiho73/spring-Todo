package com.example.demo;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
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
public class SuperController {

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

	//表示するための道具
		protected ModelAndView listAndTrash(boolean tflag, ModelAndView mv) {
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
