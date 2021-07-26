package com.example.demo;

import java.util.List;
import java.util.Optional;
import java.util.Random;

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
import com.example.demo.group_m.GroupMRepository;
import com.example.demo.priority.Priority;
import com.example.demo.priority.PriorityRepository;
import com.example.demo.task.TaskRepository;
import com.example.demo.user.User;
import com.example.demo.user.UserRepository;

@Controller
public class UserController {

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

	//http://localhost:8080/
	//ログイン画面
	@RequestMapping("/")
	public ModelAndView task(ModelAndView mv) {
		//諸々のデフォルト設定
		categoryZero();
		groupZero();
		userZero();
		priorityZero();

		session.invalidate();//セッションを消去

		mv.setViewName("top");//ログイン画面を表示
		return mv;
	}

	//ログイン画面2
	@PostMapping("/top")
	public String top() {
		session.invalidate();//セッションを消去
		return "top1";
	}


	//ログイン処理
	@PostMapping("/login")
	public ModelAndView login(ModelAndView mv,
			@RequestParam("name") String name,
			@RequestParam("pw") String pw) {

		//未入力チェック(名前とpw)
		if ((name == null || name.length() == 0) && (pw == null || pw.length() == 0)) {
			mv.addObject("message", "名前とパスワードを入力してください");
			mv.setViewName("top");
			return mv;
		}

		//未入力チェック(名前のみ)
		if (name == null || name.length() == 0) {
			mv.addObject("message", "名前を入力してください");
			mv.setViewName("top");
			return mv;
		}

		//未入力チェック(pwのみ)
		if (pw == null || pw.length() == 0) {
			mv.addObject("message", "パスワードを入力してください");
			mv.setViewName("top");
			return mv;
		}

		//ユーザ名からユーザを検索
		List<User> user = userRepository.findByNameAndPw(name, pw);

		//ヒットしなかったら、エラー
		if (user.isEmpty()) {
			mv.addObject("message", "名前とパスワードが違います");
			mv.setViewName("top");
			return mv;
		}

		//ユーザ情報を取得
		User userInfo = user.get(0);

		//上記全てをクリアしたら、ログインを実行
		// セッションスコープにユーザ情報を格納する
		session.setAttribute("name", name);
		session.setAttribute("userInfo", userInfo);

		//タスク一覧へ遷移
		mv.setViewName("redirect:/list");
		return mv;

	}

	//新規ユーザー登録画面へ遷移
	@RequestMapping("/user")
	public ModelAndView user(ModelAndView mv) {
		session.invalidate();
		mv.setViewName("newUser");
		return mv;
	}

	//新規ユーザー登録アクション
	@PostMapping("/user/new")
	public ModelAndView newuser(
			ModelAndView mv,
			@RequestParam("name") String name,
			@RequestParam("pw") String pw,
			@RequestParam("pw1") String pw1,
			@RequestParam("himitu") String himitu,
			@RequestParam("himituCode") int himituCode,
			@RequestParam("touroku") String touroku) {

		//未入力チェック(名前、パスワード)
		if ((name == null || name.length() == 0) && (pw == null || pw.length() == 0)
				&& (pw1 == null || pw1.length() == 0)) {
			mv.addObject("message", "名前とパスワードを入力してください");
			mv.setViewName("newUser");
			return mv;
		}

		//未入力チェック(秘密の質問の答え)
		if (himitu == null || himitu.length() == 0) {
			mv.addObject("message", "秘密の質問の答えを入力してください");
			mv.setViewName("newUser");
			return mv;
		}

		//未入力チェック(登録用コード)
		if (touroku == null || touroku.length() == 0) {
			mv.addObject("message", "登録用コードを入力してください");
			mv.setViewName("newUser");
			return mv;
		}

		//入力された登録用コードがtodoLetsと一致しなければ、エラー
		if (!touroku.equals("todoLets")) {
			mv.addObject("message", "登録用コードが違います");
			mv.addObject("message2", "管理者に問い合わせてください");
			mv.setViewName("newUser");
			return mv;
		}

		//ユーザ名からユーザを検索
		List<User> user = userRepository.findByName(name);

		//同じ名前がヒットしたら、エラー
		if (!user.isEmpty()) {
			mv.addObject("message", "その名前は既に使われています");
			mv.setViewName("newUser");
			return mv;
		}

		//パスワードと確認用が一致していなければ、エラー
		if (!pw.equals(pw1)) {
			mv.addObject("message", "パスワードが一致していません");
			mv.setViewName("newUser");
			return mv;
		}

		//上記全てをクリアしていれば、登録処理を実行
		User user1 = new User(name, pw, himitu, himituCode);
		userRepository.saveAndFlush(user1);

		//登録完了画を表示
		mv.setViewName("finished");
		return mv;
	}

	//パスワード忘れた人向けの画面へ遷移
	@RequestMapping("/forget/password")
	public ModelAndView forgetPw(ModelAndView mv) {
		//名前入力ボタン表示用フラッグ
		mv.addObject("flag", false);
		//表示画面を設定
		mv.setViewName("forgetPw");
		return mv;
	}

	//秘密の質問取得
	@PostMapping("/himitu/get")
	public ModelAndView himitu(
			@RequestParam("name") String name,
			ModelAndView mv) {
		//未入力チェック
		if (name == null || name.length() == 0) {
			mv.addObject("message", "名前を入力してください");
			return forgetPw(mv);
		}

		//ユーザ名からユーザを検索
		List<User> user = userRepository.findByName(name);

		Integer code;

		//ユーザが見つからなかった場合、ランダムで質問を出力
		if(user.isEmpty()) {
			Random r = new Random();
			code = r.nextInt(3) + 1;
		} else {
			//ユーザが見つかっていれば、秘密の質問コードを取得
			User user1 = user.get(0);
			code = user1.getHimituCode();
		}

		//コード番号に応じて秘密の質問を表示
		if (code == 1) {
			mv.addObject("quiz", "出身地は？");
		} else if (code == 2) {
			mv.addObject("quiz", "ペットの名前は？");
		} else if (code == 3) {
			mv.addObject("quiz", "親の旧姓は？");
		}

		mv.addObject("name", name);
		mv.addObject("flag", true);
		mv.setViewName("forgetPw");

		return mv;
	}

	//パスワード変更ページへ遷移
	@PostMapping("/change/pw")
	public ModelAndView changePw(
			@RequestParam("name") String name,
			@RequestParam("himitu") String himitu,
			ModelAndView mv) {
		//未入力チェック
		if (name == null || name.length() == 0 || himitu == null || himitu.length() == 0) {
			mv.addObject("message", "名前と秘密の言葉を入力してください");
			return forgetPw(mv);
		}

		//ユーザ名からユーザを検索
		List<User> user = userRepository.findByNameAndHimitu(name, himitu);

		//ヒットしなかった場合、エラー
		if (user.isEmpty()) {
			mv.addObject("message", "名前と秘密の言葉が間違っています");
			return forgetPw(mv);
		}

		//ヒットした場合、ユーザ情報を取得
		User userInfo = user.get(0);

		//一致すれば、パスワード変更画面へ遷移
		mv.addObject("user", userInfo);
		mv.setViewName("changePw");
		return mv;
	}

	//パスワード変更
	@PostMapping("/change/pw/action")
	public ModelAndView changePwAction(
			@RequestParam("userId") Integer userId,
			@RequestParam("pw") String pw,
			@RequestParam("pw2") String pw2,
			ModelAndView mv) {
		Optional<User> record = userRepository.findById(userId);

		if(record.isEmpty()) {
			mv.addObject("message", "エラーが発生しました");
			mv.setViewName("/top");
			return task(mv);
		}

		User user = record.get();

		//パスワードと確認用が一致していなければ、エラー
		if (!pw.equals(pw2)) {
			mv.addObject("message", "パスワードが一致していません");
			mv.addObject("user", user);
			return mv;
		}

		//パスワードと確認用が一致して入れば、パスワードを更新
		user.setPw(pw);
		userRepository.saveAndFlush(user);

		mv.addObject("message", "パスワードが変更されました");
		return task(mv);
	}

	//ログアウト
	@RequestMapping("/logout")
	public ModelAndView logout(ModelAndView mv) {
		session.invalidate();//セッションを消去
		mv.setViewName("top");//ログイン画面へ遷移
		return mv;
	}

	//諸々のデフォルト設定・dbの初回起動時にのみ発生
	//カテゴリコードのデフォルト設定
	private void categoryZero() {
		//カテゴリテーブルに0番・なしが存在しなければ、作成
		List<Category> list = categoryRepository.findByCode(0);
		if (list.isEmpty()) {
			Category category = new Category(0, "なし");
			categoryRepository.saveAndFlush(category);
		}
		//カテゴリテーブルに100番・退避用が存在しなければ、作成
		List<Category> list1 = categoryRepository.findByCode(100);
		if (list1.isEmpty()) {
			Category category = new Category(100, "退避用");
			categoryRepository.saveAndFlush(category);
		}
	}

	//グループのデフォルト設定
	private void groupZero() {
		//グループテーブルが空であれば、「0番・なし」と「100番・退避用」作成
		List<Group> list2 = groupRepository.findAll();
		if (list2.isEmpty()) {
			Group group = new Group(0, "なし");
			groupRepository.saveAndFlush(group);
			Group group2 = new Group(100, "退避用");
			groupRepository.saveAndFlush(group2);
		}
	}

	//管理者と削除済のユーザのデフォルト設定
	private void userZero() {
		//ユーザテーブルに「1番・管理者」が存在しなければ、作成
		Optional<User> kanri = userRepository.findById(1);
		if (kanri.isEmpty()) {
			User user = new User("管理者", "himitu", "sv7awe#78d$%dert6&%(&560", 1);
			userRepository.saveAndFlush(user);
		}
		//ユーザテーブルに「2番・削除済のユーザ」が存在しなければ、作成
		Optional<User> taihi = userRepository.findById(2);
		if (taihi.isEmpty()) {
			User user = new User("削除済のユーザ", "himitu", "yt75yt8yury6bfx#$%78&&", 1);
			userRepository.saveAndFlush(user);
		}
	}

	//優先度の設定
	private void priorityZero() {
		//優先度テーブルに「1番・高」が存在しなければ、作成
		List<Priority> kou = priorityRepository.findByNum(1);
		if (kou.isEmpty()) {
			Priority high = new Priority(1, "高");
			priorityRepository.saveAndFlush(high);
		}
		//優先度テーブルに「2番・中」が存在しなければ、作成
		List<Priority> chuu = priorityRepository.findByNum(2);
		if (chuu.isEmpty()) {
			Priority mid = new Priority(2, "中");
			priorityRepository.saveAndFlush(mid);
		}
		//優先度テーブルに「3番・低」が存在しなければ、作成
		List<Priority> hiku = priorityRepository.findByNum(3);
		if (hiku.isEmpty()) {
			Priority low = new Priority(3, "低");
			priorityRepository.saveAndFlush(low);
		}
	}


}


