package com.example.demo;

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
import com.example.demo.group.Group;
import com.example.demo.group.GroupRepository;
import com.example.demo.group_m.GroupM;
import com.example.demo.group_m.GroupMRepository;
import com.example.demo.priority.PriorityRepository;
import com.example.demo.task.Task;
import com.example.demo.task.TaskRepository;
import com.example.demo.user.UserRepository;

@Controller
public class GroupController extends SuperController {

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

	//新規グループ作成
	@RequestMapping("/group/new")
	public ModelAndView newGroup(ModelAndView mv) {
		mv = listAndTrash(false, mv);
		mv.setViewName("addGroup");
		return sessiontest(mv);
	}

	//新規グループ作成アクション
	@PostMapping("/group/add")
	public ModelAndView addGroup(
			@RequestParam(name = "name") String name,
			@RequestParam(name = "id", defaultValue = "0") Integer id,
			@RequestParam(name = "member", defaultValue = "") String member,
			ModelAndView mv) {

		//未入力チェック
		if (name == null || name == "" && id == 0) {
			mv.addObject("message", "グループ名とグループ番号を入力してください");
			mv.setViewName("addGroup");
			return sessiontest(mv);
		}

		if (name == null || name == "") {
			mv.addObject("message", "グループ名を入力してください");
			mv.setViewName("addGroup");
			return sessiontest(mv);
		}

		if (id == 0) {
			mv.addObject("message", "グループ番号を入力してください");
			mv.setViewName("addGroup");
			return sessiontest(mv);
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
			mv.setViewName("addGroup");
			return sessiontest(mv);
		}

		Group group = new Group(id, name);
		groupRepository.saveAndFlush(group);

		GroupM groupM = new GroupM(id, member);
		groupMRepository.saveAndFlush(groupM);

		mv = listAndTrash(false, mv);
		mv.setViewName("addTask");
		return sessiontest(mv);
	}

	//グループ編集
	@PostMapping("/group/edit")
	public ModelAndView editGroup(
			@RequestParam(name = "gid") int gid,
			ModelAndView mv) {
		mv = listAndTrash(false, mv);

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

		mv.addObject("group", group);
		mv.addObject("groupM", groupM);

		mv.setViewName("editGroup");
		return sessiontest(mv);
	}

	//グループ編集アクション
	@PostMapping("/group/update")
	public ModelAndView updateGroup(
			@RequestParam(name = "id", defaultValue = "0") int id,
			@RequestParam("name") String name,
			@RequestParam("gname") String gname,
			@RequestParam(name = "member", defaultValue = "") String member,
			@RequestParam(name = "gid") int gid,
			ModelAndView mv) {

		//未入力チェック
		if (name == null || name == "" && id == 0) {
			mv.addObject("message", "グループ名とグループ番号を入力してください");
			mv.setViewName("addGroup");
			return sessiontest(mv);
		}

		if (name == null || name == "") {
			mv.addObject("message", "グループ名を入力してください");
			mv.setViewName("addGroup");
			return sessiontest(mv);
		}

		if (id == 0) {
			mv.addObject("message", "グループ番号を入力してください");
			mv.setViewName("addGroup");
			return sessiontest(mv);
		}

		//グループの重複チェック/
		Optional<Group> list = groupRepository.findById(id);
		List<Group> list2 = groupRepository.findByName(name);

		if (!list.isEmpty()) {
			if (id != gid) {
				mv.addObject("message", "使用済みのグループ番号です");
				return editGroup(gid, mv);
			}
		}

		if (!list2.isEmpty() && !name.equals(gname)) {
			mv.addObject("message", "使用済みのグループ名です");
			return editGroup(gid, mv);
		}

		//外部キー参照制約解消 グループ100に一時退避
		List<Task> taskList = taskRepository.findByGroupId(gid);
		for (Task a : taskList) {
			a.setGroupId(100);
			taskRepository.saveAndFlush(a);
		}

		//外部キー参照制約解消 メンバーデータ消去
		groupMRepository.deleteById(gid);

		groupRepository.deleteById(gid);
		Group group = new Group(id, name);
		groupRepository.saveAndFlush(group);

		//退避したタスクを新しいグループに再登録
		List<Task> taskList2 = taskRepository.findByGroupId(100);
		for (Task a : taskList2) {
			a.setGroupId(id);
			taskRepository.saveAndFlush(a);
		}

		GroupM groupM = new GroupM(id, member);
		groupMRepository.saveAndFlush(groupM);

		mv = listAndTrash(false, mv);

		mv.setViewName("addTask");
		return sessiontest(mv);
	}

	//グループ削除確認
	@PostMapping("/group/delete/check")
	public ModelAndView deleteGroupCheck(
			@RequestParam(name = "gid", defaultValue = "0") int id,
			ModelAndView mv
			) {
		mv.addObject("check", "本当に削除しますか？");
		mv.addObject("check2", "登録されたタスクの作業者グループは「なし」に分類されます");
		mv.addObject("flag", true);

		return editGroup(id, mv);
	}

	//グループ削除アクション
	@PostMapping("/group/delete")
	public ModelAndView deleteGroup(
			@RequestParam(name = "gid", defaultValue = "0") int id,
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
