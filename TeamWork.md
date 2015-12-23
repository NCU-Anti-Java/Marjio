Team Work Flow
---------------

## IDE
使用 [IntelliJ IDEA](https://www.jetbrains.com/idea/)，可以到 [這裏](https://www.jetbrains.com/student/) 申請學生方案，取得正式版本。

## Git
### Setting

請依照您的作業系統針對版本控制換行字元同步做設定。

#### Windows
```
git config --global core.autocrlf true
git config --global core.safecrlf true
```

#### Unix-like
```
git config --global core.autocrlf input
git config --global core.safecrlf true
```

### Github Flow and Branches

使用 Github Flow，每個人 Fork 一份回去，然後做完更改後提 Pull Request，然後經過至少一人 Code Review 後，Merge 到目標分支中。

#### 分支慣例
- `master` 是最穩定的版本。
- `develop` 開發用分支，會在穩定後，用 `merge request` 併入 `master`
- `feature/****` 開發新功能用的分支，會在開發完成時，用 `merge request` 併入 `develop`
- `fixes/****` 修正 bug 用的分支，會在修正完成時，用 `merge request` 併入 `develop`

#### 命名風格
使用 `-` 作為單字間的分隔，例如：
- `fixes/project-list-save-error-message`
- `feature/auto-login`

### Commits
- 用 `git commit` 取代 `git commit -m "commit messages"`。
- Commit message 第一行不得超過 50 個字元。之後的行數不得超過 72 個字元。
- Commit message 第一行應以大寫字母為開始，且建議為動詞。例如
  - Add
  - Change
  - Fix
  - Remove
  - Refactor
- Commit message 第一行句尾，應不含 `.`
- Commit 前，向自己發問 3 個問題：
  1. 為什麼這個改變是需要的？
  2. 他是如何解決這個問題的？
  3. 這個改變有副作用嗎？
- 將 `issue/story/card` 的連結附在 commit message 中

#### Sample
```
Redirect user to the requested page after login

https://trello.com/path/to/relevant/card

Users were being redirected to the home page after login, which is less
useful than redirecting to the page they had originally requested before
being redirected to the login form.

* Store requested path in a session variable
* Redirect to the stored location after successfully logging in the user
```

### Reference
- [5 Useful Tips For A Better Commit Message](https://robots.thoughtbot.com/5-useful-tips-for-a-better-commit-message)
- [Git Commit Good Practice](https://wiki.openstack.org/wiki/GitCommitMessages)
- [Git SCM](http://git-scm.com/)
- [Visual Studio Tools for Git 處理斷行字元 (CRLF) 的注意事項](http://blog.miniasp.com/post/2014/02/20/Visual-Studio-Tools-for-Git-Line-Ending-Conversion-Notes.aspx)
- [Git中的AutoCRLF与SafeCRLF换行符问题](http://boliquan.com/git-and-autocrlf-in-safecrlf-line-break-problem/)
- [\[git\] warning: LF will be replaced by CRLF | fatal: CRLF would be replaced by LF](http://blog.csdn.net/feng88724/article/details/11600375)