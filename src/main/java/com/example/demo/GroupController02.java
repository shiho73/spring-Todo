package com.example.demo;

import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.example.demo.category.CategoryRepository;
import com.example.demo.group.Group;
import com.example.demo.group.GroupRepository;
import com.example.demo.group_m.GroupM;
import com.example.demo.group_m.GroupMRepository;
import com.example.demo.priority.PriorityRepository;
import com.example.demo.task.Task;
import com.example.demo.task.TaskRepository;
import com.example.demo.user.UserRepository;

@Controller
public class GroupController02 extends SuperController {

	//タスク編集画面からグループを登録・編集・削除するコントローラー

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
	@Autowired
	GroupMRepository groupMRepository;

	//タスク登録から新規グループ作成
	@RequestMapping("/editTask{code}/group/new")
	public ModelAndView newGroup02(
			@PathVariable(name = "code") int code,
			ModelAndView mv) {
		mv.addObject("code", code);
		mv = listAndTrash(false, mv);
		mv.setViewName("addGroup02");
		return sessiontest(mv);
	}

	//新規グループ作成アクション
	@PostMapping("/editTask/group/add")
	public ModelAndView addGroup02(
			@RequestParam(name = "code") int code,
			@RequestParam(name = "name") String name,
			@RequestParam(name = "id", defaultValue = "0") Integer id,
			@RequestParam(name = "member", defaultValue = "") String member,
			ModelAndView mv) {

		//未入力チェック(グループ名、グループ番号)
		if (name == null || name == "" && id == 0) {
			mv.addObject("message", "グループ名とグループ番号を入力してください");
			return newGroup02(code, mv);
		}

		//未入力チェック(グループ名のみ)
		if (name == null || name == "") {
			mv.addObject("message", "グループ名を入力してください");
			return newGroup02(code, mv);
		}

		//未入力チェック(グループ番号のみ)
		if (id == 0) {
			mv.addObject("message", "グループ番号を入力してください");
			return newGroup02(code, mv);
		}

		//グループの重複チェック/
		Optional<Group> list = groupRepository.findById(id);
		List<Group> list2 = groupRepository.findByName(name);

		if (!list.isEmpty() || !list2.isEmpty()) {
			if (!list.isEmpty()) {
				mv.addObject("message", "使用済みのグループ番号です");
			}
			if (!list2.isEmpty()) {
				mv.addObject("message", "使用済みのグループ名です");
			}
			return newGroup02(code, mv);
		}

		//上記をクリアしていれば、グループを作成しテーブルに登録
		Group group = new Group(id, name);
		groupRepository.saveAndFlush(group);

		//グループメンバーをテーブルに登録
		GroupM groupM = new GroupM(id, member);
		groupMRepository.saveAndFlush(groupM);

		mv = listAndTrash(false, mv);//タスク編集・登録ページの表示準備

		//タスクのレコードを取得
		Optional<Task> recode = taskRepository.findById(code);

		//変数taskの初期化
		Task task = null;
		//レコードが存在すれば、レコードからタスクを取得
		if (recode.isEmpty() == false) {
			task = recode.get();
		}
		mv.addObject("task", task);//表示の準備

		mv = listAndTrash(false, mv);//編集ページ表示の準備
		mv.setViewName("editTask");//遷移先(編集ページ)を指定
		return sessiontest(mv);
	}

	//グループ編集
	@PostMapping("/editTask/group/edit")
	public ModelAndView editGroup02(
			@RequestParam(name = "gid") int gid,
			ModelAndView mv) {
		mv = listAndTrash(false, mv);//グループ編集ページの表示準備

		//編集するグループの取得
		Group group = null;
		Optional<Group> recode = groupRepository.findById(gid);
		if (recode.isEmpty() == false) {
			group = recode.get();
		}

		//編集するグループのメンバーを取得
		GroupM groupM = null;
		Optional<GroupM> recode2 = groupMRepository.findById(group.getId());
		if (recode2.isEmpty() == false) {
			groupM = recode2.get();
		}

		//表示準備
		mv.addObject("group", group);
		mv.addObject("groupM", groupM);

		mv.setViewName("editGroup");//グループ編集ページへ遷移
		return sessiontest(mv);
	}

	//グループ編集アクション
	@PostMapping("/editTask/group/update")
	public ModelAndView updateGroup02(
			@RequestParam(name = "id", defaultValue = "0") int id,
			@RequestParam("name") String name,
			@RequestParam("gname") String gname,
			@RequestParam(name = "member", defaultValue = "") String member,
			@RequestParam(name = "gid") int gid,
			ModelAndView mv) {

		//未入力チェック(グループ名、グループ番号)
		if (name == null || name == "" && id == 0) {
			mv.addObject("message", "グループ名とグループ番号を入力してください");
			mv.setViewName("addGroup");
			return sessiontest(mv);
		}

		//未入力チェック(グループ名のみ)
		if (name == null || name == "") {
			mv.addObject("message", "グループ名を入力してください");
			mv.setViewName("addGroup");
			return sessiontest(mv);
		}

		//未入力チェック(グループ番号のみ)
		if (id == 0) {
			mv.addObject("message", "グループ番号を入力してください");
			mv.setViewName("addGroup");
			return sessiontest(mv);
		}

		//グループの重複チェック
		Optional<Group> list = groupRepository.findById(id);
		List<Group> list2 = groupRepository.findByName(name);

		if (!list.isEmpty()) {
			if (id != gid) {
				mv.addObject("message", "使用済みのグループ番号です");
				return editGroup02(gid, mv);
			}
		}

		if (!list2.isEmpty() && !name.equals(gname)) {
			mv.addObject("message", "使用済みのグループ名です");
			return editGroup02(gid, mv);
		}

		//外部キー参照制約解消 グループ100に一時退避
		List<Task> taskList = taskRepository.findByGroupId(gid);
		for (Task a : taskList) {
			a.setGroupId(100);
			taskRepository.saveAndFlush(a);
		}

		//外部キー参照制約解消 メンバーデータ削除
		groupMRepository.deleteById(gid);

		//グループ削除(id変更時のため)
		groupRepository.deleteById(gid);
		//グループ更新
		Group group = new Group(id, name);
		groupRepository.saveAndFlush(group);

		//グループ100に退避したタスクを新しいグループに再登録
		List<Task> taskList2 = taskRepository.findByGroupId(100);
		for (Task a : taskList2) {
			a.setGroupId(id);
			taskRepository.saveAndFlush(a);
		}

		//グループメンバー更新
		GroupM groupM = new GroupM(id, member);
		groupMRepository.saveAndFlush(groupM);

		mv = listAndTrash(false, mv);//タスク編集ページの表示準備

		mv.setViewName("addTask");
		return sessiontest(mv);
	}

	//グループ削除確認
	@PostMapping("/editTask/group/delete/check")
	public ModelAndView deleteGroupCheck02(
			@RequestParam(name = "gid", defaultValue = "0") int id,
			ModelAndView mv) {
		mv.addObject("check", "本当に削除しますか？");
		mv.addObject("check2", "登録されたタスクの作業者グループは「なし」に分類されます");
		mv.addObject("flag", true);

		return editGroup02(id, mv);
	}

	//グループ削除アクション
	@PostMapping("/editTask/group/delete")
	public ModelAndView deleteGroup02(
			@RequestParam(name = "gid") int id,
			ModelAndView mv) {

		//外部キー参照制約解消 グループ100に一時退避
		List<Task> taskList = taskRepository.findByGroupId(id);
		for (Task a : taskList) {
			a.setGroupId(0);
			taskRepository.saveAndFlush(a);
		}

		//消去
		groupMRepository.deleteById(id);
		groupRepository.deleteById(id);

		mv = listAndTrash(false, mv);

		mv.setViewName("addTask");
		return sessiontest(mv);
	}

}
