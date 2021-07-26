package com.example.demo;

import java.util.List;
import java.util.Optional;
import java.util.Random;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.example.demo.category.CategoryRepository;
import com.example.demo.group.GroupRepository;
import com.example.demo.group_m.GroupMRepository;
import com.example.demo.priority.PriorityRepository;
import com.example.demo.task.TaskRepository;
import com.example.demo.user.User;
import com.example.demo.user.UserRepository;

@Controller
public class UserController02 extends SuperController {

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

	//ログイン画面2
	@PostMapping("/top")
	public String top1() {
		session.invalidate();//セッションを消去
		return "top1";
	}

	//ログイン処理
	@PostMapping("/login1")
	public ModelAndView login1(ModelAndView mv,
			@RequestParam("name") String name,
			@RequestParam("pw") String pw) {

		//未入力チェック(名前とpw)
		if ((name == null || name.length() == 0) && (pw == null || pw.length() == 0)) {
			mv.addObject("message", "名前とパスワードを入力してください");
			mv.setViewName("top1");
			return mv;
		}

		//未入力チェック(名前のみ)
		if (name == null || name.length() == 0) {
			mv.addObject("message", "名前を入力してください");
			mv.setViewName("top1");
			return mv;
		}

		//未入力チェック(pwのみ)
		if (pw == null || pw.length() == 0) {
			mv.addObject("message", "パスワードを入力してください");
			mv.setViewName("top1");
			return mv;
		}

		//ユーザ名からユーザを検索
		List<User> record = userRepository.findByName(name);

		//ヒットしなかったら、エラー
		if (record.isEmpty()) {
			mv.addObject("message", "名前とパスワードが違います");
			mv.setViewName("top1");
			return mv;
		}

		User user = record.get(0);
		BCryptPasswordEncoder bcrypt = new BCryptPasswordEncoder();
		if (!bcrypt.matches(pw, user.getPw())) {
			mv.addObject("message", "名前とパスワードが違います");
			mv.setViewName("top1");
			return mv;
		}

		//上記全てをクリアしたら、ログインを実行
		// セッションスコープにユーザ情報を格納する
		session.setAttribute("name", name);
		session.setAttribute("userInfo", user);

		//タスク一覧へ遷移
		mv.setViewName("redirect:/list");
		return mv;

	}

	//新規ユーザー登録画面へ遷移
	@RequestMapping("/user1")
	public ModelAndView user1(ModelAndView mv) {
		session.invalidate();
		mv.setViewName("newUser1");
		return mv;
	}

	//新規ユーザー登録アクション
	@PostMapping("/user/new1")
	public ModelAndView newuser1(
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
			mv.setViewName("newUser1");
			return mv;
		}

		//未入力チェック(秘密の質問の答え)
		if (himitu == null || himitu.length() == 0) {
			mv.addObject("message", "秘密の質問の答えを入力してください");
			mv.setViewName("newUser1");
			return mv;
		}

		//未入力チェック(登録用コード)
		if (touroku == null || touroku.length() == 0) {
			mv.addObject("message", "登録用コードを入力してください");
			mv.setViewName("newUser1");
			return mv;
		}

		//入力された登録用コードがtodoLetsと一致しなければ、エラー
		if (!touroku.equals("todoLets")) {
			mv.addObject("message", "登録用コードが違います");
			mv.addObject("message2", "管理者に問い合わせてください");
			mv.setViewName("newUser1");
			return mv;
		}

		//ユーザ名からユーザを検索
		List<User> user = userRepository.findByName(name);

		//同じ名前がヒットしたら、エラー
		if (!user.isEmpty()) {
			mv.addObject("message", "その名前は既に使われています");
			mv.setViewName("newUser1");
			return mv;
		}

		//パスワードと確認用が一致していなければ、エラー
		if (!pw.equals(pw1)) {
			mv.addObject("message", "パスワードが一致していません");
			mv.setViewName("newUser1");
			return mv;
		}

		//PWをハッシュ化して保存
		BCryptPasswordEncoder bcrypt = new BCryptPasswordEncoder();
		String hash = bcrypt.encode(pw);

		//上記全てをクリアしていれば、登録処理を実行
		User user1 = new User(name, hash, himitu, himituCode);
		userRepository.saveAndFlush(user1);

		//登録完了画を表示
		mv.setViewName("finished1");
		return mv;
	}

	//パスワード忘れた人向けの画面へ遷移
	@RequestMapping("/forget/password1")
	public ModelAndView forgetPw1(ModelAndView mv) {
		//名前入力ボタン表示用フラッグ
		mv.addObject("flag", false);
		//表示画面を設定
		mv.setViewName("forgetPw1");
		return mv;
	}

	//秘密の質問取得
	@PostMapping("/himitu/get1")
	public ModelAndView himitu1(
			@RequestParam("name") String name,
			ModelAndView mv) {
		//未入力チェック
		if (name == null || name.length() == 0) {
			mv.addObject("message", "名前を入力してください");
			return forgetPw1(mv);
		}

		//ユーザ名からユーザを検索
		List<User> user = userRepository.findByName(name);

		Integer code;

		//ユーザが見つからなかった場合、ランダムで質問を出力
		if (user.isEmpty()) {
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
		mv.setViewName("forgetPw1");

		return mv;
	}

	//パスワード変更ページへ遷移
	@PostMapping("/change/pw1")
	public ModelAndView changePw1(
			@RequestParam("name") String name,
			@RequestParam("himitu") String himitu,
			ModelAndView mv) {
		//未入力チェック
		if (name == null || name.length() == 0 || himitu == null || himitu.length() == 0) {
			mv.addObject("message", "名前と秘密の言葉を入力してください");
			return forgetPw1(mv);
		}

		//ユーザ名からユーザを検索
		List<User> user = userRepository.findByNameAndHimitu(name, himitu);

		//ヒットしなかった場合、エラー
		if (user.isEmpty()) {
			mv.addObject("message", "名前と秘密の言葉が間違っています");
			return forgetPw1(mv);
		}

		//ヒットした場合、ユーザ情報を取得
		User userInfo = user.get(0);

		//一致すれば、パスワード変更画面へ遷移
		mv.addObject("user", userInfo);
		mv.setViewName("changePw1");
		return mv;
	}

	//パスワード変更
	@PostMapping("/change/pw/action1")
	public ModelAndView changePwAction1(
			@RequestParam("userId") Integer userId,
			@RequestParam("pw") String pw,
			@RequestParam("pw2") String pw2,
			ModelAndView mv) {
		Optional<User> record = userRepository.findById(userId);

		if (record.isEmpty()) {
			mv.addObject("message", "エラーが発生しました");
			mv.setViewName("top1");
			return mv;
		}

		User user = record.get();

		//パスワードと確認用が一致していなければ、エラー
		if (!pw.equals(pw2)) {
			mv.addObject("message", "パスワードが一致していません");
			mv.addObject("user", user);
			return mv;
		}

		//パスワードと確認用が一致して入れば、パスワードを更新
		//PWをハッシュ化して保存
		BCryptPasswordEncoder bcrypt = new BCryptPasswordEncoder();
		String hash = bcrypt.encode(pw);

		user.setPw(hash);
		userRepository.saveAndFlush(user);

		mv.addObject("message", "パスワードが変更されました");
		mv.setViewName("top1");
		return mv;
	}
}
