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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.example.demo.category.CategoryRepository;
import com.example.demo.deadline.AlmostD;
import com.example.demo.group.GroupRepository;
import com.example.demo.priority.PriorityRepository;
import com.example.demo.task.Task;
import com.example.demo.task.TaskRepository;
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
	        calendar.add(Calendar.DATE, -3);
	        // Calendar型の日時をDate型に戻す
	        java.util.Date d1 = calendar.getTime();
	        //比較
	        boolean flag = false;
	        if(dline3.after(d1)) {
	        	flag = false;
	        } else {
	        	flag = true;
	        }
			AlmostD a = new AlmostD(d.getCode(), dline, flag);
			almost.add(a);
		}

		mv.addObject("almost", almost);

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

}