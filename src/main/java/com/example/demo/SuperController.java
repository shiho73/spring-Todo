package com.example.demo;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Optional;

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

	//セッション情報保持用
	@Autowired
	HttpSession session;

	//m_taskテーブル
	@Autowired
	TaskRepository taskRepository;

	//t_userテーブル
	@Autowired
	UserRepository userRepository;

	//t_group_mテーブル
	@Autowired
	GroupMRepository groupMRepository;

	//t_groupテーブル
	@Autowired
	GroupRepository groupRepository;

	//t_priorityテーブル
	@Autowired
	PriorityRepository priorityRepository;

	//t_categoryテーブル
	@Autowired
	CategoryRepository categoryRepository;

	//各テーブルのリストを表示するための準備
	protected ModelAndView listAndTrash(boolean tflag, ModelAndView mv) {
		//各テーブルから全件検索し、主キーの順に並べる
		List<User> userList = userRepository.findByOrderByIdAsc();
		List<Category> categoryList = categoryRepository.findByOrderByCodeAsc();
		List<Priority> priorityList = priorityRepository.findByOrderByNumAsc();
		List<Group> groupList = groupRepository.findByOrderByIdAsc();
		List<GroupM> gmList = groupMRepository.findByOrderByGroupIdAsc();

		//退避用のグループ100(id順で最後尾)を非表示に
		int x = groupList.size() - 1;
		groupList.remove(x);

		//退避用のカテゴリ100(コード順で最後尾)を非表示に
		int y = categoryList.size() -1;
		categoryList.remove(y);

		//空のタスク表示用リストを生成
		ArrayList<Task> list = new ArrayList<Task>();

		//全てのタスクを取得
		List<Task> taskList = taskRepository.findByOrderByCodeAsc();

		//「タスク一覧」(tflag==false)と「ゴミ箱」(tflag==true)で分岐
		if (tflag == true) {
			//ゴミ箱に入れていれば、表示用リストに追加
			for (Task task : taskList) {
				if (!task.isTrash()) {
					list.add(task);
				}
			}
			//リストが空であれば、メッセージを表示
			if (list.isEmpty() == true) {
				mv.addObject("message", "ゴミ箱は空です");
			}
		} else if (tflag == false) {
			//ゴミ箱に入れていなければ、表示用リストに追加
			for (Task task1 : taskList) {
				if (task1.isTrash()) {
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

	//ページ遷移時、セッションが切れていたらログイン画面に遷移
	protected ModelAndView sessiontest(ModelAndView mv) {
		//ログイン情報をセッションから取得
		User login = (User) session.getAttribute("userInfo");
		//ログイン情報が空であれば、ログイン画面へ
		if (login == null) {
			session.invalidate();
			mv.addObject("message", "セッションがタイムアウトしました");
			mv.setViewName("top");
		}
		return mv;
	}

	//期限が近付いたら文字色を変更する
	//「タスク一覧」の表示に使用
	protected ModelAndView almostDeadline(ModelAndView mv) {
		//全てのタスクを取得
		List<Task> taskList = taskRepository.findByOrderByCodeAsc();
		//空の表示用リストを用意
		ArrayList<AlmostD> almost = new ArrayList<>();

		//全てのタスクに対して処理を実行
		for (Task d : taskList) {
			//sqlDate型のdlineを取得
			Date dline = d.getDline();
			//dlineをsqlDate型の書式のままString型のdline2に変換
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			String dline2 = sdf.format(dline);
			//dline2をjava.utilのDate型dline3に変換
			java.util.Date dline3 = Date.valueOf(dline2);

			//現在の日付dateをjava.utilのDate型で取得
			java.util.Date date = new java.util.Date();
			//dateをCalendar型に変換
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(date);
			//現在日時から2日後の日付を計算
			calendar.add(Calendar.DATE, 2);
			//dateをDate型date2に変換
			java.util.Date date2 = calendar.getTime();

			//dline3とdate2と比較
			boolean flag = false;
			if (date2.after(dline3)) {
				flag = false;//期限が3日以内なら、flagをfalseに
			} else {
				flag = true;//期限がまだであれば、flagをtrueに
			}
			AlmostD a = new AlmostD(d.getCode(), dline, flag);
			almost.add(a);
		}

		//Thymeleafで表示する準備
		mv.addObject("almost", almost);
		return mv;
	}


	//タスク編集ページの表示準備
	public ModelAndView returnTaskEdit(int tcode, ModelAndView mv) {
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
		return mv;
	}
}
