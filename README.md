# Building Rich Java Applications with JavaFX and AI Tools

An interactive, offline course website for the Aptech module **Building Rich Java Applications with JavaFX and AI Tools**
(18 sessions + self-study, September 2026 curriculum). Written for beginners: every concept starts with an everyday
analogy, every code example is explained line by line, and every output shown on the pages is the real output of code
that compiles.

## Open the course

**No installation or internet connection is needed to read the course.**

1. On this GitHub page click **Code → Download ZIP**, then unzip it
   (or `git clone https://github.com/praiseunite/javaFX.git`).
2. Open the folder and double-click **`index.html`**. It opens in your web browser.
3. Use the sidebar to move between sessions. Your progress, quiz scores and lab checklists are saved in your browser.

Videos in the *Further Study* sections need internet; everything else works offline.

## What's inside each session

- Learning objectives, analogies and "New words" boxes
- Diagrams and step-by-step code examples with line-by-line explanations and real output
- Interactive demos (they run in the browser and were checked against the real JDK)
- Quick checks after each part and a full knowledge check with explanations
- A guided lab, common mistakes with the exact error messages, and practice exercises with solutions
- Assignments / lab tasks with starter projects and a **SelfCheck** program that tests your work
- Verified YouTube videos, official documentation links and a printable cheat sheet

| Status | Sessions |
|---|---|
| ✅ Ready | Java Foundations Bridge B1–B7 · Session 1 – Java Utility APIs · Session 2 – Generics · Session 3 – File Handling, Streams & Serialization · Session 4 – Threading · Session 5 – Multithreading & Concurrency · Session 6 – Try It Yourself: Sessions 1–5 · Session 7 – The JDBC API · Reference guide: H2 & MySQL |
| 🛠 Coming | Sessions 8–18, capstone project |

## Run the Java code

You need **JDK 21 or newer** (the course is tested on JDK 25) and **IntelliJ IDEA**.

1. In IntelliJ choose **File → Open** and select one of the folders inside `code/`
   (for example `code/s01-java-utility-apis` — the folder that contains `pom.xml`).
2. Open any class and click the green ▶ next to `main`.

| Folder | Contents |
|---|---|
| `code/s0N-…` | All examples, the guided lab and practice solutions for session N |
| `code/a1-…-starter`, `code/a2-…-starter`, `code/a3-…-starter`, `code/a4-…-starter` | Starter projects for the graded assignments |
| `code/l2-…-starter`, `code/l4-…-starter` | Starter projects for lab tasks L2 and L4 |
| `guides/databases.html` | Reference guide: using the two databases (embedded H2 and a MySQL server) |

Projects that need a library vendored their jar in `lib/` (Session 7 and Assignment A4 use
`h2-2.3.232.jar`), so they compile and run offline with no download. Maven users get the same
dependency from each project's `pom.xml`.


## Folder structure

```
index.html            course home page
sessions/             one page per session
assets/css, assets/js the design system and the interactive features (no external libraries)
code/                 runnable Java projects for examples, labs and assignment starters
```
