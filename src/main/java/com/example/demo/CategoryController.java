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
public class CategoryController extends SuperController{

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

	//新規カテゴリー作成
	@RequestMapping("/category/new")
	public ModelAndView newCategory(ModelAndView mv) {
		mv.setViewName("addCategory");
		return sessiontest(mv);
	}

	//新規カテゴリ作成アクション
	@PostMapping("/category/add")
	public ModelAndView addCategory(
			@RequestParam("name") String name,
			@RequestParam(name="code", defaultValue="-1") int code,
			ModelAndView mv) {

		// 空の場合にエラーとする
		if (name == null || name.length() == 0 && code == -1) {
			mv.addObject("message", "カテゴリー番号とカテゴリー名を入力してください");
			mv.setViewName("addCategory");
			return sessiontest(mv);
		}

		if (name == null || name.length() == 0) {
			mv.addObject("message", "カテゴリー名を入力してください");
			mv.setViewName("addCategory");
			return sessiontest(mv);
		}

		if (code == -1) {
			mv.addObject("message", "カテゴリー番号を入力してください");
			mv.setViewName("addCategory");
			return sessiontest(mv);
		}

		//カテゴリの重複チェック
		List<Category> list = categoryRepository.findByCode(code);
		List<Category> list2 = categoryRepository.findByName(name);
		if (!list.isEmpty() || !list2.isEmpty()) {
			if (!list.isEmpty()) {
				mv.addObject("message", "使用済みのカテゴリコードです");
			}
			if (!list2.isEmpty()) {
				mv.addObject("message", "使用済みのカテゴリ名です");
			}
			mv.setViewName("addCategory");
			return sessiontest(mv);
		}

		Category category = new Category(code, name);
		categoryRepository.saveAndFlush(category);

		mv = prepare02(mv);

		mv.setViewName("addTask");
		return sessiontest(mv);
	}

	//カテゴリ編集
	@PostMapping("/category/edit")
	public ModelAndView editCategory(
			@RequestParam(name = "cCode") int cCode,
			ModelAndView mv) {

		//編集するカテゴリの取得
		Category category = null;
		Optional<Category> recode = categoryRepository.findById(cCode);
		if (recode.isEmpty() == false) {
			category = recode.get();
		}

		mv.addObject("category", category);
		mv.setViewName("editCategory");
		return sessiontest(mv);
	}

	//カテゴリ編集アクション
	@PostMapping("/category/update")
	public ModelAndView updateCategory(
			@RequestParam(name = "code", defaultValue = "0") int code,
			@RequestParam("name") String name,
			@RequestParam("cName") String cName,
			@RequestParam(name = "cCode") int cCode,
			ModelAndView mv) {

		// 空の場合にエラーとする
		if (name == null || name.length() == 0 && code == -1) {
			mv.addObject("message", "カテゴリー番号とカテゴリー名を入力してください");
			mv.setViewName("addCategory");
			return editCategory(cCode, mv);
		}

		if (name == null || name.length() == 0) {
			mv.addObject("message", "カテゴリー名を入力してください");
			mv.setViewName("addCategory");
			return editCategory(cCode, mv);
		}

		if (code == -1) {
			mv.addObject("message", "カテゴリー番号を入力してください");
			mv.setViewName("addCategory");
			return editCategory(cCode, mv);
		}

		//グループの重複チェック/
		Optional<Category> list = categoryRepository.findById(code);
		List<Category> list2 = categoryRepository.findByName(name);

		if (!list.isEmpty()) {
			if (code != cCode) {
				mv.addObject("message", "使用済みのカテゴリ番号です");
				return editCategory(cCode, mv);
			}
		}

		if (!list2.isEmpty() && !name.equals(cName)) {
			mv.addObject("message", "使用済みのカテゴリ名です");
			return editCategory(cCode, mv);
		}

		//外部キー参照制約解消 グループ100に一時退避
		List<Task> taskList = taskRepository.findByCgCode(cCode);
		for (Task a : taskList) {
			a.setCgCode(100);
			taskRepository.saveAndFlush(a);
		}

		categoryRepository.deleteById(cCode);
		Category category = new Category(code, name);
		categoryRepository.saveAndFlush(category);

		//退避したタスクを新しいグループに再登録
		List<Task> taskList2 = taskRepository.findByCgCode(100);
		for (Task a : taskList2) {
			a.setCgCode(code);
			taskRepository.saveAndFlush(a);
		}

		mv = prepare02(mv);

		mv.setViewName("addTask");
		return sessiontest(mv);
	}

	//リスト表示の予備動作
	private ModelAndView prepare02(ModelAndView mv) {
		//各テーブルから全件検索
		List<User> userList = userRepository.findAll();
		List<Category> categoryList = categoryRepository.findAll();
		List<Priority> priorityList = priorityRepository.findAll();
		List<Group> groupList = groupRepository.findAll();

		//Thymeleafで表示する準備
		mv.addObject("ulist", userList);
		mv.addObject("clist", categoryList);
		mv.addObject("plist", priorityList);
		mv.addObject("glist", groupList);

		return mv;
	}

}
