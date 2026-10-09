/* ==========================================================================
   course.js — Single source of truth for the JavaFX course platform
   Powers: topbar, sidebar, progress, pager, table of contents, quiz engine,
   step checklists, theme toggle, teacher-mode toggle, glossary filter,
   YouTube click-to-load, copy buttons, syntax highlighting, line numbers.

   Every page sets  window.PAGE = "<slug>"  before loading this file.
   Paths in MANIFEST are relative to the course ROOT folder; the root is
   worked out from where this script was loaded, so links work from any
   sub-folder and straight from disk (file://).
   ========================================================================== */
(function () {
  "use strict";

  /* ---- Course manifest (order = learning path) ----------------------------- */
  var MANIFEST = [
    { group: "Java Foundations Bridge" },
    { slug: "b1", file: "bridge/b1-setup.html",         unit: "Bridge", title: "Setup: JDK & IntelliJ" },
    { slug: "b2", file: "bridge/b2-basics.html",        unit: "Bridge", title: "Variables, Types & Operators" },
    { slug: "b3", file: "bridge/b3-flow.html",          unit: "Bridge", title: "Decisions & Loops" },
    { slug: "b4", file: "bridge/b4-methods.html",       unit: "Bridge", title: "Methods & Arrays" },
    { slug: "b5", file: "bridge/b5-oop1.html",          unit: "Bridge", title: "Classes & Objects" },
    { slug: "b6", file: "bridge/b6-oop2.html",          unit: "Bridge", title: "Inheritance & Interfaces" },
    { slug: "b7", file: "bridge/b7-essentials.html",    unit: "Bridge", title: "Strings, Exceptions & Packages" },

    { group: "Week 1 — Core Java Foundations" },
    { slug: "s01", file: "sessions/s01-java-utility-apis.html",  unit: "Session 1",  title: "Java Utility APIs" },
    { slug: "s02", file: "sessions/s02-generics.html",           unit: "Session 2",  title: "Generics" },
    { slug: "s03", file: "sessions/s03-file-handling.html",      unit: "Session 3",  title: "Files & Streams" },
    { slug: "s04", file: "sessions/s04-threading.html",          unit: "Session 4",  title: "Threading" },

    { group: "Week 2 — Advanced Java & Review" },
    { slug: "s05", file: "sessions/s05-multithreading.html",     unit: "Session 5",  title: "Multithreading & Concurrency" },
    { slug: "s06", file: "sessions/s06-review-1.html",           unit: "Review",     title: "Try It Yourself: Sessions 1–5" },
    { slug: "s07", file: "sessions/s07-jdbc-api.html",           unit: "Session 7",  title: "JDBC API" },
    { slug: "s08", file: "sessions/s08-advanced-jdbc.html",      unit: "Session 8",  title: "Advanced JDBC" },

    { group: "Week 3 — Design & Testing" },
    { slug: "s09", file: "sessions/s09-design-patterns.html",    unit: "Session 9",  title: "Design Patterns & Advanced Features", coming: true },
    { slug: "s10", file: "sessions/s10-unit-testing-ai.html",    unit: "Session 10", title: "Unit Testing & AI Tools", coming: true },
    { slug: "s11", file: "sessions/s11-data-structures.html",    unit: "Session 11", title: "Java Data Structures", coming: true },
    { slug: "s12", file: "sessions/s12-review-2.html",           unit: "Review",     title: "Try It Yourself: Sessions 6–10", coming: true },

    { group: "Week 4 — JavaFX Foundations" },
    { slug: "s13", file: "sessions/s13-intro-javafx.html",       unit: "Session 13", title: "Introduction to JavaFX", coming: true },
    { slug: "s14", file: "sessions/s14-javafx-text-shapes.html", unit: "Session 14", title: "Text, Transforms & Shapes", coming: true },
    { slug: "s15", file: "sessions/s15-javafx-layouts-ui.html",  unit: "Session 15", title: "Layouts, CSS, Controls & Charts", coming: true },
    { slug: "s16", file: "sessions/s16-javafx-events.html",      unit: "Session 16", title: "Event Handling", coming: true },

    { group: "Week 5 — JavaFX Advanced & Capstone" },
    { slug: "s17", file: "sessions/s17-javafx-media.html",       unit: "Session 17", title: "Media with JavaFX", coming: true },
    { slug: "s18", file: "sessions/s18-review-3.html",           unit: "Review",     title: "Try It Yourself: Sessions 11–15", coming: true },

    { group: "Resources" },
    { slug: "schedule",    file: "schedule.html",      unit: "Ref", title: "5-Week Schedule" },
    { slug: "assignments", file: "assignments.html",   unit: "Ref", title: "Assignments (A1–A6)" },
    { slug: "project",     file: "project.html",       unit: "Ref", title: "Capstone Project" },
    { slug: "resources",   file: "resources.html",     unit: "Ref", title: "Further Study & Videos" },
    { slug: "glossary",    file: "glossary.html",      unit: "Ref", title: "Glossary" },
    { slug: "databases",   file: "guides/databases.html", unit: "Ref", title: "Databases: H2 & MySQL" }
  ];

  var PAGES = MANIFEST.filter(function (m) { return m.slug; });
  // Pages that actually exist. Progress and "of N" counts use these only, so a
  // student is never told they are 30% done when half the course is unwritten.
  var AVAILABLE = PAGES.filter(function (p) { return !p.coming; });
  var SLUG  = window.PAGE || "index";
  var idx   = PAGES.findIndex(function (p) { return p.slug === SLUG; });

  /* ---- Work out the course root from this script's own URL ----------------- */
  var ROOT = (function () {
    var scripts = document.getElementsByTagName("script");
    for (var i = scripts.length - 1; i >= 0; i--) {
      var src = scripts[i].getAttribute("src") || "";
      var m = src.match(/^(.*?)assets\/js\/course\.js(\?.*)?$/);
      if (m) return m[1];
    }
    return "";
  })();
  function href(file) { return ROOT + file; }

  /* ---- localStorage helpers (private-mode safe) --------------------------- */
  var STORE_PROGRESS = "javafx.progress.v1";
  var STORE_QUIZ     = "javafx.quizscores.v1";
  var STORE_STEPS    = "javafx.steps.v1";
  var STORE_THEME    = "javafx.theme";
  var STORE_TEACHER  = "javafx.teacher";
  var STORE_LAST     = "javafx.lastpage";

  function safeGet(key, fallback) {
    try { var v = JSON.parse(localStorage.getItem(key)); return v == null ? fallback : v; }
    catch (e) { return fallback; }
  }
  function safeSet(key, val) {
    try { localStorage.setItem(key, JSON.stringify(val)); } catch (e) {}
  }
  function rawGet(key) { try { return localStorage.getItem(key); } catch (e) { return null; } }

  var done = safeGet(STORE_PROGRESS, {});
  if (idx >= 0) safeSet(STORE_LAST, SLUG);

  /* ---- Theme --------------------------------------------------------------- */
  function applyTheme(t) {
    if (t === "light" || t === "dark") document.documentElement.setAttribute("data-theme", t);
    else document.documentElement.removeAttribute("data-theme");
  }
  applyTheme(rawGet(STORE_THEME));

  function cycleTheme() {
    var cur = rawGet(STORE_THEME);
    var next = cur === "dark" ? "light" : cur === "light" ? null : "dark";
    try {
      if (next) localStorage.setItem(STORE_THEME, next);
      else localStorage.removeItem(STORE_THEME);
    } catch (e) {}
    applyTheme(next);
    updateThemeBtn();
  }
  function updateThemeBtn() {
    var btn = document.getElementById("themeBtn");
    if (!btn) return;
    var cur = rawGet(STORE_THEME);
    btn.textContent = cur === "dark" ? "☀️" : cur === "light" ? "🌙" : "💻";
    btn.title = cur === "dark" ? "Theme: dark (click for light)" : cur === "light" ? "Theme: light (click for system)" : "Theme: system (click for dark)";
    btn.setAttribute("aria-label", btn.title);
  }

  /* ---- Teacher mode -------------------------------------------------------- */
  // TEACHER_EDITION is true in the teacher's own copy. tools/publish.js sets it to false in the
  // student edition (which also has every teacher note removed), so no teacher button appears there.
  var TEACHER_EDITION = false;
  var teacherOn = TEACHER_EDITION && safeGet(STORE_TEACHER, false) === true;
  function applyTeacher(on) { document.body.classList.toggle("teacher-mode", on); }
  function toggleTeacher() {
    teacherOn = !teacherOn;
    safeSet(STORE_TEACHER, teacherOn);
    applyTeacher(teacherOn);
    var btn = document.getElementById("teacherBtn");
    if (btn) {
      btn.classList.toggle("is-active", teacherOn);
      btn.title = teacherOn ? "Teacher mode ON (click to hide notes)" : "Teacher mode OFF (click to show notes)";
      btn.setAttribute("aria-pressed", String(teacherOn));
    }
  }

  /* ---- Topbar -------------------------------------------------------------- */
  function buildTopbar() {
    var doneCount = AVAILABLE.filter(function (p) { return done[p.slug]; }).length;
    var pct = Math.round((doneCount / AVAILABLE.length) * 100);

    var bar = document.createElement("header");
    bar.className = "topbar";
    bar.innerHTML =
      '<button class="topbar__burger" id="burgerBtn" aria-label="Open course menu">&#9776;</button>' +
      '<a class="topbar__title" href="' + href("index.html") + '">Building Rich Java Applications with JavaFX</a>' +
      '<div class="topbar__progress" title="' + doneCount + ' of ' + PAGES.length + ' pages complete" role="progressbar" aria-valuenow="' + pct + '" aria-valuemin="0" aria-valuemax="100">' +
        '<div class="topbar__progress-fill" style="width:' + pct + '%"></div>' +
      '</div>' +
      '<div class="topbar__actions">' +
        (TEACHER_EDITION ? '<button class="topbar__btn' + (teacherOn ? ' is-active' : '') + '" id="teacherBtn" aria-pressed="' + teacherOn + '" title="Teacher mode">👨‍🏫</button>' : '') +
        '<button class="topbar__btn" id="themeBtn"></button>' +
      '</div>';
    document.body.prepend(bar);
    updateThemeBtn();

    document.getElementById("themeBtn").addEventListener("click", cycleTheme);
    if (TEACHER_EDITION) document.getElementById("teacherBtn").addEventListener("click", toggleTeacher);
    document.getElementById("burgerBtn").addEventListener("click", function () {
      var sb = document.querySelector(".sidebar");
      if (sb) sb.classList.toggle("is-open");
    });
  }

  /* ---- Sidebar ------------------------------------------------------------- */
  function buildSidebar() {
    var nav = document.createElement("nav");
    nav.className = "sidebar";
    nav.setAttribute("aria-label", "Course navigation");

    var home = document.createElement("a");
    home.className = "sidebar__link" + (SLUG === "index" ? " is-current" : "");
    home.href = href("index.html");
    home.textContent = "🏠 Course Home";
    nav.appendChild(home);

    MANIFEST.forEach(function (m) {
      if (m.group) {
        var g = document.createElement("div");
        g.className = "sidebar__group";
        g.textContent = m.group;
        nav.appendChild(g);
        return;
      }
      var a = document.createElement(m.coming ? "span" : "a");
      a.className = "sidebar__link" + (m.slug === SLUG ? " is-current" : "") +
                    (done[m.slug] ? " is-done" : "") + (m.coming ? " is-coming" : "");
      if (!m.coming) a.href = href(m.file);
      if (m.slug === SLUG) a.setAttribute("aria-current", "page");
      var tag = document.createElement("span");
      tag.className = "sidebar__tag";
      tag.textContent = m.unit;
      a.appendChild(tag);
      a.appendChild(document.createTextNode(" " + m.title));
      if (m.coming) {
        var soon = document.createElement("span");
        soon.className = "sidebar__tag sidebar__tag--soon";
        soon.textContent = "soon";
        soon.title = "This page has not been written yet";
        a.appendChild(soon);
      }
      nav.appendChild(a);
    });

    document.body.prepend(nav);
    nav.addEventListener("click", function (e) {
      if (e.target.closest(".sidebar__link") && window.innerWidth <= 900) nav.classList.remove("is-open");
    });
    var cur = nav.querySelector(".is-current");
    if (cur && cur.scrollIntoView) cur.scrollIntoView({ block: "center" });
  }

  /* ---- Pager (previous / mark complete / next) ----------------------------- */
  function pagerLink(page, dir) {
    var a = document.createElement("a");
    a.className = "pager__link" + (dir === "next" ? " pager__link--next" : "");
    a.href = href(page.file);
    a.innerHTML = '<span class="pager__dir"></span><span class="pager__title"></span>';
    a.firstChild.textContent = dir === "next" ? "Next →" : "← Previous";
    a.lastChild.textContent = page.title;
    return a;
  }
  function buildPager() {
    if (idx < 0) return;
    // Skip pages that are not written yet — a "Next" button into a 404 is worse
    // than no button at all.
    var prev = null, next = null;
    for (var i = idx - 1; i >= 0; i--) { if (!PAGES[i].coming) { prev = PAGES[i]; break; } }
    for (var j = idx + 1; j < PAGES.length; j++) { if (!PAGES[j].coming) { next = PAGES[j]; break; } }

    var pager = document.createElement("div");
    pager.className = "pager";
    pager.appendChild(prev ? pagerLink(prev, "prev") : document.createElement("span"));

    var btn = document.createElement("button");
    btn.type = "button";
    function paint() {
      btn.className = "mark-complete" + (done[SLUG] ? " is-done" : "");
      btn.textContent = done[SLUG] ? "✓ Completed" : "Mark Complete";
    }
    paint();
    btn.addEventListener("click", function () {
      done[SLUG] = !done[SLUG];
      safeSet(STORE_PROGRESS, done);
      paint();
      var sb = document.querySelector(".sidebar__link.is-current");
      if (sb) sb.classList.toggle("is-done", !!done[SLUG]);
      var fill = document.querySelector(".topbar__progress-fill");
      if (fill) fill.style.width = Math.round(AVAILABLE.filter(function (p) { return done[p.slug]; }).length / AVAILABLE.length * 100) + "%";
    });
    pager.appendChild(btn);
    if (next) pager.appendChild(pagerLink(next, "next"));

    var main = document.querySelector(".page");
    if (main) main.appendChild(pager);
  }

  /* ---- Heading anchors + auto table of contents ---------------------------- */
  function slugify(t) { return t.toLowerCase().replace(/[^a-z0-9]+/g, "-").replace(/^-|-$/g, "").slice(0, 60); }
  function buildToc() {
    var used = {};
    document.querySelectorAll(".page h2, .page h3").forEach(function (h) {
      if (!h.id) {
        var base = slugify(h.textContent) || "section", id = base, n = 2;
        while (used[id] || document.getElementById(id)) id = base + "-" + n++;
        h.id = id;
      }
      used[h.id] = true;
    });
    document.querySelectorAll("[data-toc]").forEach(function (box) {
      var ol = document.createElement("ol");
      document.querySelectorAll(".page h2").forEach(function (h) {
        if (h.closest("[data-no-toc]") || h.hasAttribute("data-no-toc")) return;
        var li = document.createElement("li"), a = document.createElement("a");
        a.href = "#" + h.id;
        a.textContent = h.textContent.replace(/^[^\w(]+/, "");
        li.appendChild(a);
        ol.appendChild(li);
      });
      box.appendChild(ol);
    });
  }

  /* ---- Quiz engine ----------------------------------------------------------
     Markup (per question):  <div class="quiz__q" data-answer="b"> ... </div>
       radio / checkbox inputs  -> data-answer lists the right value(s), "|" separated
       .fill-blank inputs       -> each input has data-accept="a|b" (any match is right)
       select.quiz__match       -> each select has data-correct="value"
     A .quiz with a .quiz__score shows a total, saves it, and offers "Try again".
     -------------------------------------------------------------------------- */
  function norm(s) { return String(s).trim().replace(/\s+/g, " ").replace(/;$/, "").toLowerCase(); }

  function gradeQuestion(q) {
    var answer = q.getAttribute("data-answer");
    var ok = true, any = false;
    var boxes = q.querySelectorAll("input[type=radio], input[type=checkbox]");
    if (boxes.length) {
      any = true;
      var want = (answer || "").split("|").map(norm).sort().join("|");
      var got = [];
      boxes.forEach(function (b) { if (b.checked) got.push(norm(b.value)); });
      if (!got.length) return null;
      if (got.sort().join("|") !== want) ok = false;
      q.querySelectorAll(".quiz__opt").forEach(function (opt) {
        var inp = opt.querySelector("input");
        if (!inp) return;
        var right = want.split("|").indexOf(norm(inp.value)) >= 0;
        if (right) opt.classList.add("is-correct-answer");
        else if (inp.checked) opt.classList.add("is-wrong-answer");
      });
    }
    var blanks = q.querySelectorAll(".fill-blank");
    if (blanks.length) {
      any = true;
      var empty = false;
      blanks.forEach(function (b) { if (!b.value.trim()) empty = true; });
      if (empty) return null;
      blanks.forEach(function (b, i) {
        var accept = (b.getAttribute("data-accept") || (answer || "").split("|")[i] || "").split("|").map(norm);
        var right = accept.indexOf(norm(b.value)) >= 0;
        b.classList.add(right ? "is-correct" : "is-wrong");
        if (!right) ok = false;
      });
    }
    var selects = q.querySelectorAll("select.quiz__match");
    if (selects.length) {
      any = true;
      var blank = false;
      selects.forEach(function (s) { if (!s.value) blank = true; });
      if (blank) return null;
      selects.forEach(function (s) {
        var right = norm(s.value) === norm(s.getAttribute("data-correct"));
        s.classList.add(right ? "is-correct" : "is-wrong");
        if (!right) ok = false;
      });
    }
    return any ? ok : null;
  }

  function initQuizzes() {
    var scores = safeGet(STORE_QUIZ, {});
    document.querySelectorAll(".quiz").forEach(function (quiz, qi) {
      var questions = quiz.querySelectorAll(".quiz__q");
      var scoreEl = quiz.querySelector(".quiz__score");
      var key = SLUG + (quiz.id ? ":" + quiz.id : ":" + qi);
      var state = { correct: 0, answered: 0 };

      questions.forEach(function (q, n) {
        q.querySelectorAll("input[type=radio]").forEach(function (r) { r.name = "q-" + qi + "-" + n; });
        var btn = q.querySelector(".quiz__check");
        if (!btn) return;
        btn.type = "button";
        btn.addEventListener("click", function () {
          if (q.classList.contains("is-answered")) return;
          var ok = gradeQuestion(q);
          if (ok === null) {
            btn.textContent = "Choose / type an answer first";
            setTimeout(function () { btn.textContent = "Check Answer"; }, 1600);
            return;
          }
          q.classList.add("is-answered", ok ? "is-correct" : "is-wrong");
          q.querySelectorAll("input, select").forEach(function (i) { i.disabled = true; });
          var verdict = document.createElement("div");
          verdict.className = "quiz__verdict " + (ok ? "is-good" : "is-bad");
          verdict.textContent = ok ? "✓ Correct!" : "✗ Not quite — read the explanation below.";
          btn.after(verdict);
          btn.disabled = true;
          state.answered++;
          if (ok) state.correct++;
          if (scoreEl && state.answered === questions.length) showScore();
        });
      });

      function showScore() {
        var total = questions.length, pct = Math.round(state.correct / total * 100);
        var msg = pct >= 80 ? "Excellent — you're ready for the next part." :
                  pct >= 50 ? "Good start. Re-read the sections for the questions you missed, then try again." :
                              "Go back through the lesson (especially the walkthrough tables) and try again. That's normal!";
        scoreEl.innerHTML = "You scored <strong>" + state.correct + "/" + total + " (" + pct + "%)</strong><br><span class='text-sm'>" + msg + "</span><br>";
        var retry = document.createElement("button");
        retry.type = "button";
        retry.className = "quiz__check";
        retry.textContent = "↻ Try again";
        retry.addEventListener("click", resetQuiz);
        scoreEl.appendChild(retry);
        scoreEl.style.display = "block";
        var best = scores[key];
        if (!best || pct >= best.pct) { scores[key] = { correct: state.correct, total: total, pct: pct }; safeSet(STORE_QUIZ, scores); }
      }
      function resetQuiz() {
        state.correct = 0; state.answered = 0;
        questions.forEach(function (q) {
          q.classList.remove("is-answered", "is-correct", "is-wrong");
          q.querySelectorAll(".is-correct-answer, .is-wrong-answer").forEach(function (o) { o.classList.remove("is-correct-answer", "is-wrong-answer"); });
          q.querySelectorAll("input, select").forEach(function (i) {
            i.disabled = false; i.classList.remove("is-correct", "is-wrong");
            if (i.type === "radio" || i.type === "checkbox") i.checked = false; else i.value = "";
          });
          q.querySelectorAll(".quiz__verdict").forEach(function (v) { v.remove(); });
          var b = q.querySelector(".quiz__check"); if (b) b.disabled = false;
        });
        scoreEl.style.display = "none";
        quiz.scrollIntoView({ behavior: "smooth", block: "start" });
      }
      if (scoreEl && scores[key]) {
        var note = document.createElement("div");
        note.className = "text-sm text-faint";
        note.textContent = "Your best score so far: " + scores[key].correct + "/" + scores[key].total + " (" + scores[key].pct + "%)";
        quiz.insertBefore(note, quiz.children[1] || null);
      }
    });
  }

  /* ---- Step checklists ----------------------------------------------------- */
  function initSteps() {
    var saved = safeGet(STORE_STEPS, {});
    var pageSteps = saved[SLUG] || {};
    document.querySelectorAll(".step").forEach(function (step, i) {
      var key = "step-" + i;
      if (pageSteps[key]) step.classList.add("is-done");
      var check = step.querySelector(".step__check");
      if (!check) return;
      check.setAttribute("role", "checkbox");
      check.setAttribute("tabindex", "0");
      check.setAttribute("aria-label", "Mark step " + (i + 1) + " done");
      check.setAttribute("aria-checked", String(!!pageSteps[key]));
      function flip() {
        step.classList.toggle("is-done");
        pageSteps[key] = step.classList.contains("is-done");
        check.setAttribute("aria-checked", String(pageSteps[key]));
        saved[SLUG] = pageSteps;
        safeSet(STORE_STEPS, saved);
      }
      check.addEventListener("click", flip);
      check.addEventListener("keydown", function (e) { if (e.key === " " || e.key === "Enter") { e.preventDefault(); flip(); } });
    });
  }

  /* ---- Syntax highlighting (tokenizer: never re-colours its own markup) ---- */
  var JAVA_KW = "abstract assert break case catch class const continue default do else enum extends final finally for if implements import instanceof interface native new package private protected public return static strictfp super switch synchronized this throw throws transient try void volatile while var record sealed permits yield true false null".split(" ");
  var JAVA_PRIM = "int long double float boolean char byte short".split(" ");
  var SQL_KW = "SELECT FROM WHERE INSERT INTO VALUES UPDATE SET DELETE CREATE TABLE DROP ALTER ADD JOIN INNER LEFT RIGHT ON AND OR NOT NULL IS IN LIKE ORDER BY GROUP HAVING LIMIT OFFSET AS DISTINCT COUNT SUM AVG MAX MIN PRIMARY KEY FOREIGN REFERENCES INDEX UNIQUE DEFAULT CHECK CONSTRAINT CASCADE BEGIN COMMIT ROLLBACK TRANSACTION INTEGER TEXT REAL VARCHAR AUTOINCREMENT PROCEDURE CALL".split(" ");

  function esc(s) { return s.replace(/&/g, "&amp;").replace(/</g, "&lt;").replace(/>/g, "&gt;"); }

  function tokenize(src, lang) {
    var rules;
    if (lang === "java") rules = [
      [/^\/\/[^\n]*/, "cmt"], [/^\/\*[\s\S]*?\*\//, "cmt"], [/^"""[\s\S]*?"""/, "str"],
      [/^"(?:[^"\\\n]|\\.)*"/, "str"], [/^'(?:[^'\\\n]|\\.)'/, "str"], [/^@\w+/, "ann"],
      [/^\d[\d_]*(?:\.\d+)?[fFdDlL]?\b/, "num"], [/^[A-Za-z_$][\w$]*/, "word"], [/^\s+/, null], [/^[\s\S]/, null]
    ];
    else if (lang === "sql") rules = [
      [/^--[^\n]*/, "cmt"], [/^'(?:[^'\\]|\\.)*'/, "str"], [/^\d+(?:\.\d+)?/, "num"],
      [/^[A-Za-z_]\w*/, "sqlword"], [/^\s+/, null], [/^[\s\S]/, null]
    ];
    else if (lang === "xml" || lang === "html") rules = [
      [/^<!--[\s\S]*?-->/, "cmt"], [/^<\/?[\w:.-]+/, "tag"], [/^\/?>/, "tag"],
      [/^[\w:.-]+(?==)/, "type"], [/^"[^"]*"/, "str"], [/^\s+/, null], [/^[\s\S]/, null]
    ];
    else if (lang === "css") rules = [
      [/^\/\*[\s\S]*?\*\//, "cmt"], [/^#[\da-fA-F]{3,8}\b/, "num"], [/^-?[\w-]+(?=\s*:)/, "type"],
      [/^\d+(?:\.\d+)?(?:px|em|rem|%)?/, "num"], [/^"[^"]*"/, "str"], [/^\s+/, null], [/^[\s\S]/, null]
    ];
    else if (lang === "bash" || lang === "shell") rules = [
      [/^#[^\n]*/, "cmt"], [/^"[^"]*"/, "str"], [/^'[^']*'/, "str"], [/^--?[\w-]+/, "kw"], [/^\s+/, null], [/^[^\s"'#]+/, null]
    ];
    else return [[src, null]];

    var out = [], i = 0, prevWord = "";
    while (i < src.length) {
      var rest = src.slice(i), matched = false;
      for (var r = 0; r < rules.length; r++) {
        var m = rest.match(rules[r][0]);
        if (!m) continue;
        var text = m[0], kind = rules[r][1];
        if (kind === "word") {
          if (JAVA_KW.indexOf(text) >= 0) kind = "kw";
          else if (JAVA_PRIM.indexOf(text) >= 0) kind = "type";
          else if (/^[A-Z]/.test(text)) kind = "type";
          else if (/^\s*\(/.test(src.slice(i + text.length)) && prevWord !== "new") kind = "fn";
          else kind = null;
          prevWord = text;
        } else if (kind === "sqlword") {
          kind = SQL_KW.indexOf(text.toUpperCase()) >= 0 ? "kw" : null;
        } else if (text.trim()) prevWord = "";
        out.push([text, kind]);
        i += text.length;
        matched = true;
        break;
      }
      if (!matched) { out.push([src[i], null]); i++; }
    }
    return out;
  }

  function parseRanges(spec) {
    var set = {};
    (spec || "").split(",").forEach(function (part) {
      var m = part.trim().match(/^(\d+)(?:-(\d+))?$/);
      if (!m) return;
      for (var n = +m[1]; n <= +(m[2] || m[1]); n++) set[n] = true;
    });
    return set;
  }

  function highlightCode() {
    document.querySelectorAll("pre code[class*='lang-']").forEach(function (block) {
      if (block.getAttribute("data-hl-done")) return;
      var lang = (block.className.match(/lang-(\w+)/) || [])[1];
      var src = block.textContent.replace(/^\n/, "").replace(/\s+$/, "");
      var tokens = tokenize(src, lang);
      var pre = block.parentElement;
      var numbered = pre.classList.contains("numbered");
      var hl = parseRanges(pre.getAttribute("data-hl"));

      // Split tokens into lines so every line can be wrapped (needed for numbering / highlighting)
      var lines = [[]];
      tokens.forEach(function (t) {
        var parts = t[0].split("\n");
        parts.forEach(function (p, k) {
          if (k > 0) lines.push([]);
          if (p) lines[lines.length - 1].push([p, t[1]]);
        });
      });
      var html = lines.map(function (line, n) {
        var inner = line.map(function (t) {
          return t[1] ? '<span class="hl-' + t[1] + '">' + esc(t[0]) + "</span>" : esc(t[0]);
        }).join("");
        if (numbered) return '<span class="line' + (hl[n + 1] ? " hl" : "") + '">' + (inner || " ") + "</span>";
        return inner;
      }).join(numbered ? "" : "\n");
      block.innerHTML = html;
      block.setAttribute("data-hl-done", "1");
      block.setAttribute("data-src", src);
    });
  }

  /* ---- Code file labels + copy buttons ------------------------------------- */
  function copyText(text, done) {
    function fallback() {
      var ta = document.createElement("textarea");
      ta.value = text; ta.setAttribute("readonly", ""); ta.style.position = "fixed"; ta.style.opacity = "0";
      document.body.appendChild(ta); ta.select();
      try { document.execCommand("copy"); done(true); } catch (e) { done(false); }
      ta.remove();
    }
    if (navigator.clipboard && window.isSecureContext) navigator.clipboard.writeText(text).then(function () { done(true); }, fallback);
    else fallback();
  }
  function initCodeChrome() {
    document.querySelectorAll("pre").forEach(function (pre) {
      if (pre.closest(".terminal-output")) return;
      var file = pre.getAttribute("data-file");
      if (file && !(pre.previousElementSibling && pre.previousElementSibling.classList.contains("code-file"))) {
        var lab = document.createElement("div");
        lab.className = "code-file";
        lab.textContent = "📄 " + file;
        pre.parentNode.insertBefore(lab, pre);
        pre.classList.add("has-file");
      }
      if (pre.querySelector(".code-copy")) return;
      var btn = document.createElement("button");
      btn.type = "button";
      btn.className = "code-copy";
      btn.textContent = "Copy";
      btn.setAttribute("aria-label", "Copy code to clipboard");
      btn.addEventListener("click", function () {
        var code = pre.querySelector("code");
        var text = code ? (code.getAttribute("data-src") || code.textContent) : pre.textContent;
        copyText(text, function (ok) {
          btn.textContent = ok ? "Copied!" : "Press Ctrl+C";
          setTimeout(function () { btn.textContent = "Copy"; }, 1500);
        });
      });
      pre.appendChild(btn);
    });
  }

  /* ---- YouTube click-to-load ----------------------------------------------- */
  function initVideos() {
    document.querySelectorAll("[data-yt]").forEach(function (card) {
      var id = card.getAttribute("data-yt");
      var title = card.getAttribute("data-title") || "Watch on YouTube";
      var embed = card.querySelector(".video-card__embed");
      if (!embed || !/^[\w-]{11}$/.test(id)) return;
      var ph = document.createElement("button");
      ph.type = "button";
      ph.className = "video-card__placeholder";
      ph.innerHTML = '<span class="play-icon">▶</span><span class="ph-title"></span><span class="ph-note">Click to load (needs internet)</span>';
      ph.querySelector(".ph-title").textContent = title;
      embed.appendChild(ph);
      ph.addEventListener("click", function () {
        var iframe = document.createElement("iframe");
        iframe.src = "https://www.youtube-nocookie.com/embed/" + id + "?autoplay=1&rel=0";
        iframe.setAttribute("allowfullscreen", "");
        iframe.setAttribute("allow", "autoplay; encrypted-media; picture-in-picture");
        iframe.title = title;
        embed.innerHTML = "";
        embed.appendChild(iframe);
      });
      var info = card.querySelector(".video-card__info");
      if (info && !info.querySelector(".video-card__link")) {
        var a = document.createElement("a");
        a.className = "video-card__link";
        a.href = "https://www.youtube.com/watch?v=" + id;
        a.target = "_blank";
        a.rel = "noopener";
        a.textContent = "Open on YouTube ↗";
        info.appendChild(a);
      }
    });
  }

  /* ---- Glossary filter ----------------------------------------------------- */
  function initGlossary() {
    var input = document.querySelector(".glossary-filter");
    if (!input) return;
    var items = document.querySelectorAll(".glossary-item");
    input.addEventListener("input", function () {
      var q = input.value.toLowerCase();
      items.forEach(function (item) { item.classList.toggle("hidden", q.length > 0 && item.textContent.toLowerCase().indexOf(q) < 0); });
    });
  }

  /* ---- Print buttons ------------------------------------------------------- */
  function initPrint() {
    document.querySelectorAll("[data-print]").forEach(function (b) {
      b.addEventListener("click", function () {
        document.querySelectorAll("details").forEach(function (d) { d.setAttribute("data-was-open", d.open ? "1" : ""); });
        window.print();
      });
    });
  }

  /* ---- Init ---------------------------------------------------------------- */
  function init() {
    applyTeacher(teacherOn);
    buildTopbar();
    buildSidebar();
    buildToc();
    if (idx >= 0) buildPager();
    highlightCode();          // must run before copy buttons are added
    initCodeChrome();
    initQuizzes();
    initSteps();
    initVideos();
    initGlossary();
    initPrint();

    window.Course = {
      ROOT: ROOT,
      PAGES: PAGES,
      MANIFEST: MANIFEST,
      href: href,
      done: function () { return safeGet(STORE_PROGRESS, {}); },
      scores: function () { return safeGet(STORE_QUIZ, {}); },
      lastPage: function () { return safeGet(STORE_LAST, null); },
      highlight: highlightCode
    };
    document.dispatchEvent(new Event("course:ready"));
  }

  if (document.readyState === "loading") document.addEventListener("DOMContentLoaded", init);
  else init();
})();
