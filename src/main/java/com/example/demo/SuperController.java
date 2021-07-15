package com.example.demo;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.servlet.ModelAndView;

import com.example.demo.category.Category;
import com.example.demo.category.CategoryRepository;
import com.example.demo.deadline.AlmostD;
import com.example.demo.group.Group;
import com.example.demo.group.GroupRepository;
import com.example.demo.group_m.GroupM;
import com.example.demo.group_m.GroupMRepository;
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
	GroupMRepository groupMRepository;
	@Autowired
	UserRepository userRepository;
	@Autowired
	PriorityRepository priorityRepository;

	//表示するための道具
	protected ModelAndView listAndTrash(boolean tflag, ModelAndView mv) {
		//各テーブルから全件検索
		List<User> userList = userRepository.findAll();
		List<Category> categoryList = categoryRepository.findByOrderByCodeAsc();
		List<Priority> priorityList = priorityRepository.findAll();
		List<Group> groupList = groupRepository.findByOrderByIdAsc();
		List<GroupM> gmList = groupMRepository.findByOrderByGroupIdAsc();

		//退避用のグループ100を非表示に
		int x = -1;
		for (Group y : groupList) {
			x++;
		}
		groupList.remove(x);

		//退避用のグループ100を非表示に
		int a = -1;
		for (Category b : categoryList) {
			a++;
		}
		categoryList.remove(a);

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
		mv.addObject("gmlist", gmList);
		mv.addObject("list", list);

		return mv;
	}

	protected ModelAndView sessiontest(ModelAndView mv) {
		User login = (User) session.getAttribute("userInfo");
		if (login == null) {
			session.invalidate();
			mv.addObject("message", "セッションがタイムアウトしました");
			mv.setViewName("top");
		}
		return mv;
	}

	protected ModelAndView almostDeadline(ModelAndView mv) {
		//期限フラッグを設定
		List<Task> taskList = taskRepository.findByOrderByCodeAsc();
		ArrayList<AlmostD> almost = new ArrayList<>();
		for (Task d : taskList) {
			Date dline = d.getDline();//sqlDate型
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			String date2 = sdf.format(dline);//String型に
			java.util.Date dline3 = Date.valueOf(date2);//java.utilのDate型
			java.util.Date date = new java.util.Date();
			// Date型の日時をCalendar型に変換
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(date);
			// 日時を加算する
			calendar.add(Calendar.DATE, 2);
			// Calendar型の日時をDate型に戻す
			java.util.Date d1 = calendar.getTime();
			//比較
			boolean flag = false;
			if (d1.after(dline3)) {
				flag = false;
			} else {
				flag = true;
			}
			AlmostD a = new AlmostD(d.getCode(), dline, flag);
			almost.add(a);
		}

		mv.addObject("almost", almost);
		return mv;
	}

}
