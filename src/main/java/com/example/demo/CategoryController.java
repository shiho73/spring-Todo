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

import com.example.demo.category.Category;
import com.example.demo.category.CategoryRepository;
import com.example.demo.group.GroupRepository;
import com.example.demo.priority.PriorityRepository;
import com.example.demo.task.Task;
import com.example.demo.task.TaskRepository;
import com.example.demo.user.UserRepository;

@Controller
public class CategoryController extends SuperController {

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

	//タスク登録から新規カテゴリー作成
	@RequestMapping("/category/new")
	public ModelAndView newCategory(ModelAndView mv) {
		mv.addObject("tcode", 0);
		mv = listAndTrash(false, mv);
		mv.setViewName("addCategory");
		return sessiontest(mv);
	}

	//タスク編集から新規カテゴリー作成
	@RequestMapping("/editTask{tcode}/category/new")
	public ModelAndView newCategory02(
			@PathVariable(name = "tcode") int tcode,
			ModelAndView mv) {
		mv.addObject("tcode", tcode);
		mv = listAndTrash(false, mv);
		mv.setViewName("addCategory");
		return sessiontest(mv);
	}

	//新規カテゴリ作成アクション
	@PostMapping("/category/add")
	public ModelAndView addCategory(
			@RequestParam("tcode") int tcode,
			@RequestParam("name") String name,
			@RequestParam(name = "code", defaultValue = "-1") int code,
			ModelAndView mv) {

		// 空の場合にエラーとする(やばい)
		if (name == null || name.length() == 0 && code == -1) {
			mv.addObject("message", "カテゴリー番号とカテゴリー名を入力してください");
			if (tcode == 0) {
				return newCategory(mv);
			} else {
				return newCategory02(tcode, mv);
			}
		}

		if (name == null || name.length() == 0) {
			mv.addObject("message", "カテゴリー名を入力してください");
			if (tcode == 0) {
				return newCategory(mv);
			} else {
				return newCategory02(tcode, mv);
			}
		}

		if (code == -1) {
			mv.addObject("message", "カテゴリー番号を入力してください");
			if (tcode == 0) {
				return newCategory(mv);
			} else {
				return newCategory02(tcode, mv);
			}
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
			if (tcode == 0) {
				return newCategory(mv);
			} else {
				return newCategory02(tcode, mv);
			}
		}

		Category category = new Category(code, name);
		categoryRepository.saveAndFlush(category);

		mv = listAndTrash(false, mv);

		if (tcode == 0) {
			mv = listAndTrash(false, mv);
			mv.setViewName("addTask");
			return sessiontest(mv);
		} else {
			//タスクのレコードを取得
			Optional<Task> recode = taskRepository.findById(tcode);
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

	}

	//カテゴリ編集
	@PostMapping("/category/edit")
	public ModelAndView editCategory(
			@RequestParam(name = "cCode") int cCode,
			ModelAndView mv) {
		mv = listAndTrash(false, mv);

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

		mv = listAndTrash(false, mv);

		mv.setViewName("addTask");
		return sessiontest(mv);
	}

	//カテゴリ削除確認
	@PostMapping("/category/delete/check")
	public ModelAndView deleteCategoryCheck(
			@RequestParam(name = "cCode") int id,
			ModelAndView mv) {
		mv.addObject("check", "本当に削除しますか？");
		mv.addObject("check2", "登録されたタスクの作業者グループは「なし」に分類されます");
		mv.addObject("flag", true);

		return editCategory(id, mv);
	}

	//カテゴリ削除アクション
	@PostMapping("/category/delete")
	public ModelAndView deleteGroup(
			@RequestParam(name = "cCode") int id,
			ModelAndView mv) {

		//外部キー参照制約解消 カテゴリ0に移動
		List<Task> taskList = taskRepository.findByCgCode(id);
		for (Task a : taskList) {
			a.setCgCode(0);
			taskRepository.saveAndFlush(a);
		}

		//消去
		categoryRepository.deleteById(id);

		mv = listAndTrash(false, mv);

		mv.setViewName("addTask");
		return sessiontest(mv);
	}

}
