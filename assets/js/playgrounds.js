/* ==========================================================================
   playgrounds.js — Interactive demos for the JavaFX course.
   Any element with  data-playground="<name>"  is turned into a live demo.

   The JavaSim object reproduces the REAL algorithms used by the JDK
   (String.hashCode, HashMap bucket order + resizing, PriorityQueue heap,
   Arrays.binarySearch), so what students see here matches what Java prints.
   It is verified against a real JVM by tools/verify-playgrounds (see README).
   ========================================================================== */
(function (global) {
  "use strict";

  /* ======================================================================
     1. JavaSim — pure logic, no DOM (also loadable from Node for testing)
     ====================================================================== */
  var JavaSim = {};

  /** Java's String.hashCode(): s[0]*31^(n-1) + ... with 32-bit int overflow. */
  JavaSim.hashCode = function (s) {
    var h = 0;
    for (var i = 0; i < s.length; i++) h = (Math.imul(31, h) + s.charCodeAt(i)) | 0;
    return h;
  };

  /** HashMap.hash(): spreads the high bits down, then index = hash & (capacity-1). */
  JavaSim.bucketIndex = function (key, capacity) {
    var h = JavaSim.hashCode(key);
    return (h ^ (h >>> 16)) & (capacity - 1);
  };

  /**
   * Emulates java.util.HashMap (and HashSet, which is a HashMap inside) for String keys.
   * Iteration order = bucket index ascending, then insertion order inside a bucket.
   */
  JavaSim.HashTable = function () {
    this.capacity = 0;          // table is created lazily on the first put, like the JDK
    this.threshold = 0;
    this.seq = 0;
    this.entries = {};          // key -> { seq, value }
    this.keysList = [];
    this.resizes = [];          // history, for teaching ("resized from 16 to 32")
  };
  JavaSim.HashTable.prototype._resize = function () {
    var old = this.capacity;
    this.capacity = old === 0 ? 16 : old * 2;
    this.threshold = Math.floor(this.capacity * 0.75);
    if (old) this.resizes.push(old + " → " + this.capacity);
  };
  JavaSim.HashTable.prototype.has = function (k) {
    return Object.prototype.hasOwnProperty.call(this.entries, "$" + k);
  };
  JavaSim.HashTable.prototype.get = function (k) {
    return this.has(k) ? this.entries["$" + k].value : null;
  };
  /** Returns the previous value, or null if the key is new (exactly like Map.put). */
  JavaSim.HashTable.prototype.put = function (k, v) {
    if (this.capacity === 0) this._resize();
    if (this.has(k)) {
      var prev = this.entries["$" + k].value;
      this.entries["$" + k].value = v;
      return prev;
    }
    // How many nodes are already in the target bucket? (JDK treeifyBin -> resize when table < 64)
    var idx = JavaSim.bucketIndex(k, this.capacity), inBucket = 0, self = this;
    this.keysList.forEach(function (x) { if (JavaSim.bucketIndex(x, self.capacity) === idx) inBucket++; });
    this.entries["$" + k] = { seq: this.seq++, value: v };
    this.keysList.push(k);
    if (inBucket >= 8 && this.capacity < 64) this._resize();
    if (this.keysList.length > this.threshold) this._resize();
    return null;
  };
  JavaSim.HashTable.prototype.remove = function (k) {
    if (!this.has(k)) return null;
    var prev = this.entries["$" + k].value;
    delete this.entries["$" + k];
    this.keysList.splice(this.keysList.indexOf(k), 1);
    return prev;
  };
  JavaSim.HashTable.prototype.size = function () { return this.keysList.length; };
  /** Keys in the order Java's iterator / toString() would visit them. */
  JavaSim.HashTable.prototype.keys = function () {
    var cap = this.capacity, e = this.entries;
    return this.keysList.slice().sort(function (a, b) {
      var d = JavaSim.bucketIndex(a, cap) - JavaSim.bucketIndex(b, cap);
      return d !== 0 ? d : e["$" + a].seq - e["$" + b].seq;
    });
  };
  /** Bucket view for the visualiser: [{index, keys:[...]}] for non-empty buckets. */
  JavaSim.HashTable.prototype.buckets = function () {
    var out = {}, cap = this.capacity;
    this.keys().forEach(function (k) {
      var i = JavaSim.bucketIndex(k, cap);
      (out[i] = out[i] || []).push(k);
    });
    return Object.keys(out).map(Number).sort(function (a, b) { return a - b; })
      .map(function (i) { return { index: i, keys: out[i] }; });
  };

  /** String.compareTo — compares UTF-16 code units, same as JS < for strings. */
  JavaSim.compareStrings = function (a, b) { return a < b ? -1 : a > b ? 1 : 0; };

  /** AbstractCollection.toString() / AbstractMap.toString() */
  JavaSim.listToString = function (arr) { return "[" + arr.join(", ") + "]"; };
  JavaSim.mapToString = function (pairs) {
    return "{" + pairs.map(function (p) { return p[0] + "=" + p[1]; }).join(", ") + "}";
  };

  /**
   * java.util.PriorityQueue — same siftUp / siftDown as the JDK, so the printed
   * (internal heap) order matches Java exactly. cmp(a,b) works like Comparator.compare.
   */
  JavaSim.PriorityQueue = function (cmp) {
    this.q = [];
    this.cmp = cmp || function (a, b) { return a < b ? -1 : a > b ? 1 : 0; };
  };
  JavaSim.PriorityQueue.prototype.offer = function (x) {
    var q = this.q, k = q.length;
    while (k > 0) {
      var parent = (k - 1) >>> 1, e = q[parent];
      if (this.cmp(x, e) >= 0) break;
      q[k] = e;
      k = parent;
    }
    q[k] = x;
    return true;
  };
  JavaSim.PriorityQueue.prototype.peek = function () { return this.q.length ? this.q[0] : null; };
  JavaSim.PriorityQueue.prototype.poll = function () {
    var q = this.q;
    if (!q.length) return null;
    var result = q[0], n = q.length - 1, x = q[n];
    q.length = n;
    if (n > 0) {
      var k = 0, half = n >>> 1;
      while (k < half) {
        var child = 2 * k + 1, c = q[child], right = child + 1;
        if (right < n && this.cmp(c, q[right]) > 0) c = q[child = right];
        if (this.cmp(x, c) <= 0) break;
        q[k] = c;
        k = child;
      }
      q[k] = x;
    }
    return result;
  };
  JavaSim.PriorityQueue.prototype.toArray = function () { return this.q.slice(); };
  JavaSim.PriorityQueue.prototype.size = function () { return this.q.length; };

  /** Arrays.binarySearch(int[], key) with a record of every step. */
  JavaSim.binarySearch = function (a, key) {
    var low = 0, high = a.length - 1, steps = [];
    while (low <= high) {
      var mid = (low + high) >>> 1, v = a[mid];
      if (v < key) { steps.push({ low: low, high: high, mid: mid, move: "right" }); low = mid + 1; }
      else if (v > key) { steps.push({ low: low, high: high, mid: mid, move: "left" }); high = mid - 1; }
      else { steps.push({ low: low, high: high, mid: mid, move: "found" }); return { result: mid, steps: steps }; }
    }
    return { result: -(low + 1), steps: steps, insertionPoint: low };
  };

  /** Word splitting used by the word-frequency demo (mirrors the Java code shown on the page). */
  JavaSim.words = function (text) {
    return text.toLowerCase().split(/[^a-z']+/).filter(function (w) { return w.length > 0; });
  };

  /* ---- Generics rules (Session 2), verified against javac by tools/verify-playgrounds ---- */
  // Tiny type world: Integer, Double -> Number -> Object ; String -> Object
  JavaSim.isSubtype = function (a, b) {
    if (a === b || b === "Object") return true;
    return (a === "Integer" || a === "Double") && b === "Number";
  };
  /** Parses "List<? extends Number>" into {kind, bound}. kinds: exact | extends | super | any */
  JavaSim.parseListType = function (t) {
    var m = t.match(/^List<(.*)>$/), inner = m ? m[1].trim() : t;
    if (inner === "?") return { kind: "any", bound: "Object" };
    var e = inner.match(/^\? extends (\w+)$/); if (e) return { kind: "extends", bound: e[1] };
    var s = inner.match(/^\? super (\w+)$/);   if (s) return { kind: "super", bound: s[1] };
    return { kind: "exact", bound: inner };
  };
  /** Can a List<arg> be passed to a parameter of type param?  (arg is a plain type name) */
  JavaSim.acceptsList = function (param, arg) {
    var p = JavaSim.parseListType(param);
    if (p.kind === "exact") return arg === p.bound;
    if (p.kind === "extends") return JavaSim.isSubtype(arg, p.bound);
    if (p.kind === "super") return JavaSim.isSubtype(p.bound, arg);
    return true;
  };
  /** The type you get back from list.get(0) inside the method. */
  JavaSim.readType = function (param) {
    var p = JavaSim.parseListType(param);
    return p.kind === "exact" || p.kind === "extends" ? p.bound : "Object";
  };
  /** Does  X x = list.get(0);  compile? */
  JavaSim.canReadAs = function (param, x) { return JavaSim.isSubtype(JavaSim.readType(param), x); };
  /** Does  list.add(valueOfType x);  compile? */
  JavaSim.canAdd = function (param, x) {
    var p = JavaSim.parseListType(param);
    if (p.kind === "exact") return JavaSim.isSubtype(x, p.bound);
    if (p.kind === "super") return JavaSim.isSubtype(x, p.bound);
    return false;                                   // ? and ? extends: only null may be added
  };
  /** Literals used by the Box<T> demo, and whether  box.put(literal)  compiles for a Box<T>. */
  JavaSim.LITERALS = [
    { code: '"Ada"', prim: "String", wrapper: "String", runtime: "java.lang.String" },
    { code: "95", prim: "int", wrapper: "Integer", runtime: "java.lang.Integer" },
    { code: "3.5", prim: "double", wrapper: "Double", runtime: "java.lang.Double" },
    { code: "true", prim: "boolean", wrapper: "Boolean", runtime: "java.lang.Boolean" }
  ];
  JavaSim.canPut = function (literalWrapper, T) {
    if (literalWrapper === "Boolean") return T === "Object";
    return JavaSim.isSubtype(literalWrapper, T);
  };

  /* ---- I/O rules (Session 3), verified against the JDK by tools/verify-playgrounds ---- */
  /** UTF-8 bytes (0..255, as FileInputStream.read() returns them) of a Java String. */
  JavaSim.utf8Bytes = function (s) {
    if (typeof TextEncoder !== "undefined") return Array.from(new TextEncoder().encode(s));
    return Array.from(Buffer.from(s, "utf8"));
  };
  /** Java chars = UTF-16 code units, exactly what FileReader.read() returns one by one. */
  JavaSim.javaChars = function (s) {
    var out = [];
    for (var i = 0; i < s.length; i++) out.push(s.charCodeAt(i));
    return out;
  };
  /** How many times the FILE is asked for data when reading n bytes one at a time.
      bufferSize 0 = FileInputStream alone; otherwise BufferedInputStream(bufferSize). */
  JavaSim.diskReads = function (n, bufferSize) {
    if (!bufferSize) return n + 1;                     // one call per byte, plus the final -1
    return Math.ceil(n / bufferSize) + 1;              // one call per full buffer, plus the final -1
  };
  /** The recommended "decorator chain" for a job. kind: bytes|text|data|objects, dir: read|write */
  JavaSim.streamChain = function (kind, dir, buffered) {
    var R = dir === "read";
    var file = R ? 'new FileInputStream("f")' : 'new FileOutputStream("f")';
    if (kind === "bytes") {
      return buffered ? (R ? "new BufferedInputStream(" + file + ")" : "new BufferedOutputStream(" + file + ")") : file;
    }
    if (kind === "text") {
      var base = R ? 'new FileReader("f")' : 'new FileWriter("f")';
      return buffered ? (R ? "new BufferedReader(" + base + ")" : "new BufferedWriter(" + base + ")") : base;
    }
    var inner = buffered ? (R ? "new BufferedInputStream(" + file + ")" : "new BufferedOutputStream(" + file + ")") : file;
    if (kind === "data") return (R ? "new DataInputStream(" : "new DataOutputStream(") + inner + ")";
    return (R ? "new ObjectInputStream(" : "new ObjectOutputStream(") + inner + ")";
  };
  JavaSim.streamChainType = function (kind, dir, buffered) {
    var R = dir === "read";
    if (kind === "bytes") return buffered ? (R ? "BufferedInputStream" : "BufferedOutputStream") : (R ? "FileInputStream" : "FileOutputStream");
    if (kind === "text") return buffered ? (R ? "BufferedReader" : "BufferedWriter") : (R ? "FileReader" : "FileWriter");
    if (kind === "data") return R ? "DataInputStream" : "DataOutputStream";
    return R ? "ObjectInputStream" : "ObjectOutputStream";
  };

  if (typeof module !== "undefined" && module.exports) { module.exports = JavaSim; return; }
  global.JavaSim = JavaSim;

  /* ======================================================================
     2. Small DOM helpers
     ====================================================================== */
  function el(tag, attrs, kids) {
    var n = document.createElement(tag);
    if (attrs) Object.keys(attrs).forEach(function (k) {
      if (k === "text") n.textContent = attrs[k];
      else if (k === "html") n.innerHTML = attrs[k];
      else if (k === "class") n.className = attrs[k];
      else if (k.indexOf("on") === 0) n.addEventListener(k.slice(2), attrs[k]);
      else n.setAttribute(k, attrs[k]);
    });
    (kids || []).forEach(function (c) { if (c != null) n.appendChild(typeof c === "string" ? document.createTextNode(c) : c); });
    return n;
  }
  function btn(label, onclick, secondary) {
    return el("button", { type: "button", class: "playground__btn" + (secondary ? " playground__btn--secondary" : ""), text: label, onclick: onclick });
  }
  function shell(root, title) {
    root.classList.add("playground");
    root.innerHTML = "";
    var head = el("div", { class: "playground__header", text: "⚙️ " + title });
    var body = el("div", { class: "playground__body" });
    root.appendChild(head);
    root.appendChild(body);
    return body;
  }
  function chip(text, cls) { return el("span", { class: "viz-item" + (cls ? " " + cls : ""), text: text }); }
  function q(s) { return '"' + s + '"'; }
  function logLine(out, text, cls) {
    var line = el("div", { class: "viz-log__line" + (cls ? " " + cls : ""), text: text });
    out.appendChild(line);
    out.scrollTop = out.scrollHeight;
  }

  /* ======================================================================
     3. Playground: collections — ArrayList vs HashSet vs LinkedHashSet vs TreeSet
     ====================================================================== */
  function pgCollections(root) {
    var body = shell(root, "Collection Visualizer — one input, four collections");
    var list, hash, linked, tree;

    var input = el("input", { type: "text", class: "viz-input", placeholder: "Type a name, e.g. Ada", maxlength: "14", "aria-label": "Value to add or remove" });
    var controls = el("div", { class: "playground__controls" }, [
      input,
      btn("add()", function () { doAdd(input.value.trim()); input.select(); }),
      btn("remove()", function () { doRemove(input.value.trim()); input.select(); }, true),
      btn("Load sample", sample, true),
      btn("Reset", reset, true)
    ]);
    input.addEventListener("keydown", function (e) { if (e.key === "Enter") { doAdd(input.value.trim()); input.select(); } });
    body.appendChild(controls);
    body.appendChild(el("p", { class: "text-sm text-faint", html: "Add the same name twice. Watch which collections accept the duplicate and how each one orders the items. The HashSet order is computed with Java&rsquo;s real <code>hashCode()</code>, so it matches what your program prints." }));

    function panel(title, decl, rule) {
      var items = el("div", { class: "viz-items" });
      var extra = el("div", { class: "viz-extra" });
      var printed = el("div", { class: "viz-print" });
      var p = el("div", { class: "viz-panel" }, [
        el("div", { class: "viz-panel__title", text: title }),
        el("code", { class: "viz-panel__decl", text: decl }),
        el("div", { class: "viz-panel__rule", text: rule }),
        items, extra, printed
      ]);
      return { root: p, items: items, extra: extra, printed: printed };
    }
    var pList = panel("ArrayList", "List<String> list = new ArrayList<>();", "Keeps insertion order · allows duplicates · has index numbers");
    var pHash = panel("HashSet", "Set<String> hash = new HashSet<>();", "No duplicates · order decided by hash buckets");
    var pLinked = panel("LinkedHashSet", "Set<String> linked = new LinkedHashSet<>();", "No duplicates · remembers insertion order");
    var pTree = panel("TreeSet", "Set<String> tree = new TreeSet<>();", "No duplicates · always sorted (A→Z)");
    body.appendChild(el("div", { class: "viz-grid" }, [pList.root, pHash.root, pLinked.root, pTree.root]));
    var out = el("div", { class: "viz-log", "aria-live": "polite" });
    body.appendChild(el("div", { class: "viz-log__label", text: "What Java would report" }));
    body.appendChild(out);

    function reset() {
      list = []; hash = new JavaSim.HashTable(); linked = []; tree = [];
      out.innerHTML = "";
      logLine(out, "// empty collections created — try adding a name", "is-muted");
      render();
    }
    function sample() {
      reset();
      ["Zara", "Musa", "Ada", "Musa", "Ben", "Zara"].forEach(doAdd);
    }
    function doAdd(v) {
      if (!v) return;
      list.push(v);
      var hashNew = hash.put(v, true) === null;
      var linkedNew = linked.indexOf(v) < 0; if (linkedNew) linked.push(v);
      var treeNew = tree.indexOf(v) < 0; if (treeNew) { tree.push(v); tree.sort(JavaSim.compareStrings); }
      logLine(out, "add(" + q(v) + ")  →  list: true   hash: " + hashNew + "   linked: " + linkedNew + "   tree: " + treeNew,
        hashNew ? "" : "is-warn");
      if (!hashNew) logLine(out, "   ↳ Sets returned false: " + q(v) + " is already inside, so it was ignored. The List accepted it again.", "is-muted");
      render(v);
    }
    function doRemove(v) {
      if (!v) return;
      var i = list.indexOf(v), inList = i >= 0; if (inList) list.splice(i, 1);
      var inHash = hash.remove(v) !== null;
      var li = linked.indexOf(v); if (li >= 0) linked.splice(li, 1);
      var ti = tree.indexOf(v); if (ti >= 0) tree.splice(ti, 1);
      logLine(out, "remove(" + q(v) + ")  →  list: " + inList + "   sets: " + inHash + (inList && list.indexOf(v) >= 0 ? "   (list.remove removes only the FIRST match)" : ""), inList ? "" : "is-warn");
      render();
    }
    function render(flash) {
      pList.items.innerHTML = "";
      list.forEach(function (v, i) {
        var c = el("span", { class: "viz-item viz-item--indexed" + (v === flash && i === list.length - 1 ? " is-new" : "") }, [
          el("small", { text: String(i) }), v
        ]);
        pList.items.appendChild(c);
      });
      var hk = hash.keys();
      pHash.items.innerHTML = "";
      hk.forEach(function (v) { pHash.items.appendChild(chip(v, v === flash ? "is-new" : "")); });
      pHash.extra.innerHTML = "";
      if (hash.capacity) {
        var tbl = el("div", { class: "viz-buckets" });
        hash.buckets().forEach(function (b) {
          tbl.appendChild(el("div", { class: "viz-bucket" }, [el("b", { text: "bucket " + b.index }), " " + b.keys.join(" → ")]));
        });
        pHash.extra.appendChild(tbl);
        pHash.extra.appendChild(el("div", { class: "viz-note", text: "table size " + hash.capacity + " · grows after " + hash.threshold + " items" + (hash.resizes.length ? " · resized " + hash.resizes.join(", ") : "") }));
      }
      pLinked.items.innerHTML = "";
      linked.forEach(function (v, i) {
        if (i) pLinked.items.appendChild(el("span", { class: "viz-arrow", text: "→" }));
        pLinked.items.appendChild(chip(v, v === flash ? "is-new" : ""));
      });
      pTree.items.innerHTML = "";
      tree.forEach(function (v) { pTree.items.appendChild(chip(v, v === flash ? "is-new" : "")); });

      [[pList, list], [pHash, hk], [pLinked, linked], [pTree, tree]].forEach(function (pair) {
        if (!pair[1].length) pair[0].items.appendChild(el("span", { class: "viz-empty", text: "(empty)" }));
      });
      pList.printed.textContent = "println → " + JavaSim.listToString(list) + "   size " + list.length;
      pHash.printed.textContent = "println → " + JavaSim.listToString(hk) + "   size " + hk.length;
      pLinked.printed.textContent = "println → " + JavaSim.listToString(linked) + "   size " + linked.length;
      pTree.printed.textContent = "println → " + JavaSim.listToString(tree) + "   size " + tree.length;
    }
    reset();
  }

  /* ======================================================================
     4. Playground: wordcount — HashMap vs LinkedHashMap vs TreeMap
     ====================================================================== */
  function pgWordCount(root) {
    var body = shell(root, "Word Counter — three kinds of Map, same data");
    var ta = el("textarea", { class: "viz-textarea", rows: "3", "aria-label": "Text to count" });
    ta.value = root.getAttribute("data-text") || "the cat and the dog and the bird";
    body.appendChild(ta);
    body.appendChild(el("div", { class: "playground__controls" }, [
      btn("Count words", run),
      btn("Try a proverb", function () { ta.value = "When the music changes so does the dance and when the dance changes so does the music"; run(); }, true),
      btn("Clear", function () { ta.value = ""; run(); }, true)
    ]));
    var outWrap = el("div");
    body.appendChild(outWrap);

    function run() {
      var words = JavaSim.words(ta.value);
      var hm = new JavaSim.HashTable(), order = [], counts = {};
      words.forEach(function (w) {
        var cur = hm.get(w);
        hm.put(w, (cur || 0) + 1);
        if (!counts["$" + w]) { counts["$" + w] = 0; order.push(w); }
        counts["$" + w]++;
      });
      function pairs(keys) { return keys.map(function (k) { return [k, counts["$" + k]]; }); }
      var treeKeys = order.slice().sort(JavaSim.compareStrings);
      outWrap.innerHTML = "";
      if (!words.length) { outWrap.appendChild(el("p", { class: "text-faint", text: "Type some words above." })); return; }

      var lines = el("div", { class: "viz-log" });
      logLine(lines, "HashMap       → " + JavaSim.mapToString(pairs(hm.keys())));
      logLine(lines, "LinkedHashMap → " + JavaSim.mapToString(pairs(order)));
      logLine(lines, "TreeMap       → " + JavaSim.mapToString(pairs(treeKeys)));
      logLine(lines, "// " + words.length + " words, " + order.length + " unique keys. Same pairs in all three maps; only the ORDER differs.", "is-muted");
      outWrap.appendChild(lines);

      var max = 0; order.forEach(function (k) { max = Math.max(max, counts["$" + k]); });
      var bars = el("div", { class: "viz-bars" });
      treeKeys.forEach(function (k) {
        var n = counts["$" + k];
        bars.appendChild(el("div", { class: "viz-bar" }, [
          el("code", { text: k }),
          el("span", { class: "viz-bar__track" }, [el("span", { class: "viz-bar__fill", style: "width:" + Math.max(6, Math.round(n / max * 100)) + "%" })]),
          el("b", { text: String(n) })
        ]));
      });
      outWrap.appendChild(el("div", { class: "viz-log__label", text: "Key → value (TreeMap order)" }));
      outWrap.appendChild(bars);
    }
    run();
  }

  /* ======================================================================
     5. Playground: queues — Queue (FIFO) vs Stack (LIFO) vs PriorityQueue
     ====================================================================== */
  function pgQueues(root) {
    var body = shell(root, "Waiting Room — Queue vs Stack vs PriorityQueue");
    var fifo, stack, pq, arrivals;
    var nameIn = el("input", { type: "text", class: "viz-input", placeholder: "Patient name", maxlength: "10", "aria-label": "Patient name" });
    var sev = el("select", { class: "viz-input viz-input--sm", "aria-label": "Severity" });
    [["1", "1 – critical"], ["2", "2 – serious"], ["3", "3 – moderate"], ["4", "4 – minor"], ["5", "5 – check-up"]]
      .forEach(function (o) { sev.appendChild(el("option", { value: o[0], text: o[1] })); });
    sev.value = "3";
    body.appendChild(el("div", { class: "playground__controls" }, [
      nameIn, sev,
      btn("Arrive (offer / push)", function () { arrive(nameIn.value.trim() || ("P" + (arrivals + 1)), +sev.value); nameIn.value = ""; nameIn.focus(); }),
      btn("Serve next (poll / pop)", serve),
      btn("Load sample", sample, true),
      btn("Reset", reset, true)
    ]));
    nameIn.addEventListener("keydown", function (e) { if (e.key === "Enter") { arrive(nameIn.value.trim() || ("P" + (arrivals + 1)), +sev.value); nameIn.value = ""; } });
    body.appendChild(el("p", { class: "text-sm text-faint", html: "The same patients arrive at three different waiting rooms. Press <b>Serve next</b> and compare who each room serves. A <b>lower severity number means more urgent</b>." }));

    var cQ = el("div", { class: "viz-items" }), cS = el("div", { class: "viz-items viz-items--stack" }), cP = el("div", { class: "viz-items" });
    var pQ = el("div", { class: "viz-print" }), pS = el("div", { class: "viz-print" }), pP = el("div", { class: "viz-print" });
    var tree = el("div", { class: "viz-heap" });
    body.appendChild(el("div", { class: "viz-grid viz-grid--3" }, [
      el("div", { class: "viz-panel" }, [el("div", { class: "viz-panel__title", text: "Queue (FIFO)" }), el("code", { class: "viz-panel__decl", text: "Queue<Patient> q = new ArrayDeque<>();" }), el("div", { class: "viz-panel__rule", text: "First in, first out — like a bank line" }), cQ, pQ]),
      el("div", { class: "viz-panel" }, [el("div", { class: "viz-panel__title", text: "Stack (LIFO)" }), el("code", { class: "viz-panel__decl", text: "Deque<Patient> s = new ArrayDeque<>();" }), el("div", { class: "viz-panel__rule", text: "Last in, first out — like a pile of plates" }), cS, pS]),
      el("div", { class: "viz-panel" }, [el("div", { class: "viz-panel__title", text: "PriorityQueue" }), el("code", { class: "viz-panel__decl", text: "new PriorityQueue<>(comparingInt(Patient::getSeverity))" }), el("div", { class: "viz-panel__rule", text: "Most urgent first — like a hospital ER" }), cP, tree, pP])
    ]));
    var out = el("div", { class: "viz-log", "aria-live": "polite" });
    body.appendChild(el("div", { class: "viz-log__label", text: "Log" }));
    body.appendChild(out);

    function label(p) { return p.name + "(" + p.sev + ")"; }
    function reset() {
      fifo = []; stack = []; arrivals = 0;
      pq = new JavaSim.PriorityQueue(function (a, b) { return a.sev - b.sev; });
      out.innerHTML = "";
      logLine(out, "// three empty waiting rooms", "is-muted");
      render();
    }
    function sample() {
      reset();
      [["Ada", 4], ["Ben", 1], ["Chi", 3], ["Dan", 2], ["Efe", 5]].forEach(function (p) { arrive(p[0], p[1]); });
    }
    function arrive(name, s) {
      var p = { name: name, sev: s };
      arrivals++;
      fifo.push(p); stack.unshift(p); pq.offer(p);
      logLine(out, "arrive " + label(p) + "   → q.offer(), s.push(), pq.offer()");
      render();
    }
    function serve() {
      if (!fifo.length) { logLine(out, "Everyone has been served. poll() on an empty queue returns null; pop() on an empty stack throws NoSuchElementException.", "is-warn"); return; }
      var a = fifo.shift(), b = stack.shift(), c = pq.poll();
      logLine(out, "q.poll() → " + label(a) + "    s.pop() → " + label(b) + "    pq.poll() → " + label(c), "is-good");
      render();
    }
    function render() {
      cQ.innerHTML = ""; cS.innerHTML = ""; cP.innerHTML = ""; tree.innerHTML = "";
      fifo.forEach(function (p, i) { cQ.appendChild(chip(label(p), i === 0 ? "is-front" : "")); });
      stack.forEach(function (p, i) { cS.appendChild(chip(label(p), i === 0 ? "is-front" : "")); });
      var arr = pq.toArray();
      arr.forEach(function (p, i) { cP.appendChild(chip(label(p), i === 0 ? "is-front" : "")); });
      // draw the heap as levels: 1, 2, 4, 8 ...
      for (var start = 0, width = 1; start < arr.length; start += width, width *= 2) {
        var row = el("div", { class: "viz-heap__row" });
        arr.slice(start, start + width).forEach(function (p) { row.appendChild(chip(label(p))); });
        tree.appendChild(row);
      }
      if (!fifo.length) [cQ, cS, cP].forEach(function (c) { c.appendChild(el("span", { class: "viz-empty", text: "(empty)" })); });
      pQ.textContent = "println → " + JavaSim.listToString(fifo.map(label)) + "   next: " + (fifo[0] ? label(fifo[0]) : "null");
      pS.textContent = "println → " + JavaSim.listToString(stack.map(label)) + "   top: " + (stack[0] ? label(stack[0]) : "null");
      pP.textContent = "println → " + JavaSim.listToString(arr.map(label)) + "   next: " + (arr[0] ? label(arr[0]) : "null");
    }
    reset();
  }

  /* ======================================================================
     6. Playground: listcost — ArrayList vs LinkedList, counting the work
     ====================================================================== */
  function pgListCost(root) {
    var body = shell(root, "ArrayList vs LinkedList — count the work");
    var arr, work, busy = false, nextLetter;
    body.appendChild(el("div", { class: "playground__controls" }, [
      btn("add(0, x) — insert at front", function () { run("front"); }),
      btn("add(x) — append at end", function () { run("end"); }),
      btn("get(6)", function () { run("get"); }),
      btn("remove(0)", function () { run("remove"); }),
      btn("Reset", reset, true)
    ]));
    var rowA = el("div", { class: "viz-items viz-items--array" }), rowL = el("div", { class: "viz-items" });
    var wA = el("b"), wL = el("b");
    body.appendChild(el("div", { class: "viz-panel" }, [el("div", { class: "viz-panel__title" }, ["ArrayList — work done: ", wA]), el("div", { class: "viz-panel__rule", text: "One block of numbered boxes. Jumping to any index is instant, but inserting near the front shifts everything after it." }), rowA]));
    body.appendChild(el("div", { class: "viz-panel" }, [el("div", { class: "viz-panel__title" }, ["LinkedList — work done: ", wL]), el("div", { class: "viz-panel__rule", text: "A chain of nodes. Adding or removing at either end only relinks 2 arrows, but reaching index 6 means walking node by node." }), rowL]));
    var out = el("div", { class: "viz-log", "aria-live": "polite" });
    body.appendChild(out);

    function reset() {
      arr = ["A", "B", "C", "D", "E", "F", "G", "H"]; work = { a: 0, l: 0 }; nextLetter = 0;
      out.innerHTML = ""; logLine(out, "// both lists hold the same 8 items", "is-muted");
      draw([], []);
    }
    function draw(hiA, hiL) {
      rowA.innerHTML = ""; rowL.innerHTML = "";
      arr.forEach(function (v, i) {
        rowA.appendChild(el("span", { class: "viz-item viz-item--indexed" + (hiA.indexOf(i) >= 0 ? " is-hot" : "") }, [el("small", { text: String(i) }), v]));
        if (i) rowL.appendChild(el("span", { class: "viz-arrow", text: "⇄" }));
        rowL.appendChild(el("span", { class: "viz-item" + (hiL.indexOf(i) >= 0 ? " is-hot" : ""), text: v }));
      });
      wA.textContent = work.a + " steps"; wL.textContent = work.l + " steps";
    }
    function animate(framesA, framesL, done) {
      busy = true;
      var n = Math.max(framesA.length, framesL.length), i = 0;
      (function tick() {
        if (i >= n) { busy = false; draw([], []); done(); return; }
        draw(framesA[i] || [], framesL[i] || []);
        i++;
        setTimeout(tick, 170);
      })();
    }
    function run(op) {
      if (busy) return;
      var fA = [], fL = [], msg;
      var x = "xyzwvutsrqponmlkjihgfedcba".charAt(nextLetter++ % 26);
      if (op === "front") {
        for (var i = arr.length - 1; i >= 0; i--) fA.push([i]);
        fL.push([0]);
        arr.unshift(x);
        work.a += arr.length - 1 + 1; work.l += 2;
        msg = "add(0, " + q(x) + "): ArrayList shifted " + (arr.length - 1) + " items right, then wrote 1. LinkedList relinked 2 arrows.";
      } else if (op === "end") {
        fA.push([arr.length]); fL.push([arr.length]);
        arr.push(x);
        work.a += 1; work.l += 2;
        msg = "add(" + q(x) + "): both are fast at the end. ArrayList writes into the next free box (it only occasionally grows its array); LinkedList relinks the tail.";
      } else if (op === "get") {
        if (arr.length < 7) { logLine(out, "get(6) needs at least 7 items; this would throw IndexOutOfBoundsException.", "is-warn"); return; }
        fA.push([6]);
        for (var j = 0; j <= 6; j++) fL.push([j]);
        work.a += 1; work.l += 7;
        msg = "get(6): ArrayList jumped straight to box 6 (1 step). LinkedList walked 7 nodes from the head (it starts from whichever end is closer).";
      } else {
        if (!arr.length) { logLine(out, "The list is empty; remove(0) would throw IndexOutOfBoundsException.", "is-warn"); return; }
        for (var k = 1; k < arr.length; k++) fA.push([k]);
        fL.push([0]);
        arr.shift();
        work.a += arr.length; work.l += 2;
        msg = "remove(0): ArrayList shifted " + arr.length + " items left to close the gap. LinkedList just moved the head arrow.";
      }
      animate(fA, fL, function () { logLine(out, msg); });
    }
    reset();
  }

  /* ======================================================================
     7. Playground: binarysearch — Arrays.binarySearch step by step
     ====================================================================== */
  function pgBinarySearch(root) {
    var body = shell(root, "Arrays.binarySearch() — step by step");
    var SORTED = [3, 8, 12, 17, 21, 26, 30, 34, 41, 47, 52, 58, 63, 70, 77];
    var arr, res, stepIdx, sorted;
    var target = el("input", { type: "number", class: "viz-input viz-input--sm", value: "58", "aria-label": "Number to search for" });
    body.appendChild(el("div", { class: "playground__controls" }, [
      el("label", { class: "text-sm", text: "key =" }), target,
      btn("Start search", start),
      btn("Next step", step),
      btn("Shuffle (break it!)", function () { shuffle(); start(); }, true),
      btn("Arrays.sort()", function () { arr = SORTED.slice(); sorted = true; start(); }, true)
    ]));
    var row = el("div", { class: "viz-items viz-items--array" });
    body.appendChild(row);
    var out = el("div", { class: "viz-log", "aria-live": "polite" });
    body.appendChild(out);

    function shuffle() {
      // deterministic "shuffle" so the lesson is reproducible
      arr = [41, 3, 70, 17, 58, 26, 8, 63, 34, 12, 77, 21, 47, 30, 52];
      sorted = false;
    }
    function draw(s) {
      row.innerHTML = "";
      arr.forEach(function (v, i) {
        var cls = "viz-item viz-item--indexed";
        if (s) {
          if (i < s.low || i > s.high) cls += " is-out";
          if (i === s.mid) cls += " is-hot";
        }
        var tags = [];
        if (s && i === s.low) tags.push("low");
        if (s && i === s.mid) tags.push("mid");
        if (s && i === s.high) tags.push("high");
        row.appendChild(el("span", { class: cls, title: tags.join(" ") }, [el("small", { text: String(i) }), String(v), tags.length ? el("em", { text: tags.join("/") }) : null]));
      });
    }
    function start() {
      var key = parseInt(target.value, 10);
      if (isNaN(key)) { target.value = "58"; key = 58; }
      res = JavaSim.binarySearch(arr, key); stepIdx = 0;
      out.innerHTML = "";
      logLine(out, "int[] arr = " + JavaSim.listToString(arr).replace("[", "{").replace("]", "}") + ";" + (sorted ? "" : "   // NOT sorted!"), sorted ? "is-muted" : "is-warn");
      logLine(out, "Arrays.binarySearch(arr, " + key + ")  — press Next step", "is-muted");
      draw(null);
    }
    function step() {
      if (!res) start();
      if (stepIdx >= res.steps.length) { finish(); return; }
      var s = res.steps[stepIdx++], key = parseInt(target.value, 10);
      draw(s);
      var v = arr[s.mid];
      var msg = "Step " + stepIdx + ": low=" + s.low + " high=" + s.high + " → mid=(" + s.low + "+" + s.high + ")/2=" + s.mid + ", arr[mid]=" + v + ". ";
      if (s.move === "found") msg += v + " == " + key + " → FOUND at index " + s.mid + ".";
      else if (s.move === "right") msg += v + " < " + key + " → the key must be to the RIGHT. Throw away the left half (low = " + (s.mid + 1) + ").";
      else msg += v + " > " + key + " → the key must be to the LEFT. Throw away the right half (high = " + (s.mid - 1) + ").";
      logLine(out, msg, s.move === "found" ? "is-good" : "");
      if (stepIdx >= res.steps.length) finish();
    }
    function finish() {
      if (res.done) return;
      res.done = true;
      var key = parseInt(target.value, 10);
      if (res.result >= 0) logLine(out, "returns " + res.result + " after only " + res.steps.length + " comparisons (a linear search could need up to " + arr.length + ").", "is-good");
      else {
        logLine(out, "low > high → nothing left to check. returns " + res.result + "  = -(insertionPoint) - 1, where insertionPoint = " + res.insertionPoint + " (where " + key + " WOULD go).", "is-warn");
      }
      var real = arr.indexOf(key);
      if (!sorted && ((res.result < 0 && real >= 0) || (res.result >= 0 && arr[res.result] !== key))) {
        logLine(out, "⚠ WRONG ANSWER: " + key + " really is in the array at index " + real + ". Binary search assumes the array is sorted. Always call Arrays.sort() first!", "is-bad");
      }
    }
    arr = SORTED.slice(); sorted = true; start();
  }

  /* ======================================================================
     8. Playground: chooser — "Which collection should I use?" decision tree
     ====================================================================== */
  var CHOOSER = {
    start: { q: "Do you need to look things up by a KEY (e.g. find a student by ID, a price by product name)?", a: [["Yes — key → value pairs", "map"], ["No — just a group of items", "unique"]] },
    map: { q: "When you print or loop over the map, what order do you want the keys in?", a: [["Don't care — just be fast", "r:HashMap"], ["The order I added them", "r:LinkedHashMap"], ["Sorted (A→Z, 1→9)", "r:TreeMap"]] },
    unique: { q: "Must every item be UNIQUE (no duplicates allowed)?", a: [["Yes — no duplicates", "setorder"], ["No — duplicates are fine", "line"]] },
    setorder: { q: "What order do you want the items in?", a: [["Don't care — just be fast", "r:HashSet"], ["The order I added them", "r:LinkedHashSet"], ["Sorted", "r:TreeSet"]] },
    line: { q: "Are the items waiting to be PROCESSED one at a time (a waiting line)?", a: [["Yes — first come, first served", "r:QueueFIFO"], ["Yes — last in, first out (undo / back button)", "r:Stack"], ["Yes — most urgent first", "r:PriorityQueue"], ["No — it's just a list I read and update", "list"]] },
    list: { q: "Where do most of your insertions and removals happen?", a: [["At the end, and I often use get(index)", "r:ArrayList"], ["At the very front (and the end)", "r:Deque"]] }
  };
  var RESULTS = {
    HashMap: ["HashMap", "Map<String, Student> byId = new HashMap<>();", "The default Map. Very fast put/get; iteration order is not guaranteed.", "Student ID → Student record, username → password hash"],
    LinkedHashMap: ["LinkedHashMap", "Map<String, Integer> cart = new LinkedHashMap<>();", "Fast like HashMap, but remembers the order keys were first inserted.", "Shopping cart item → quantity, shown in the order added"],
    TreeMap: ["TreeMap", "Map<String, Double> grades = new TreeMap<>();", "Keeps keys sorted and can find the nearest key (floorKey, ceilingKey). A little slower.", "A dictionary, a report sorted by name, a timetable"],
    HashSet: ["HashSet", "Set<String> emails = new HashSet<>();", "The default Set. Fastest way to ask \"have I seen this before?\"", "Registered email addresses, visited web pages"],
    LinkedHashSet: ["LinkedHashSet", "Set<String> tags = new LinkedHashSet<>();", "Removes duplicates while keeping the original order.", "Removing duplicate names from a list without shuffling it"],
    TreeSet: ["TreeSet", "TreeSet<Integer> scores = new TreeSet<>();", "Sorted, unique elements with first(), last(), floor(), ceiling().", "Leaderboard of unique scores, sorted list of courses"],
    QueueFIFO: ["ArrayDeque used as a Queue", "Queue<Order> orders = new ArrayDeque<>();", "offer() at the back, poll() from the front. (LinkedList also works.)", "Print jobs, customer orders, a ticket line"],
    Stack: ["ArrayDeque used as a Stack", "Deque<String> history = new ArrayDeque<>();", "push() and pop() at the top. Preferred over the old Stack class.", "Undo history, the browser back button, checking brackets"],
    PriorityQueue: ["PriorityQueue", "PriorityQueue<Task> tasks = new PriorityQueue<>(comparator);", "poll() always returns the smallest / most urgent element.", "Hospital triage, scheduling jobs by deadline"],
    ArrayList: ["ArrayList", "List<String> names = new ArrayList<>();", "The default List and the most-used collection in Java. Fast get(index) and add at the end.", "A class register, search results, items on screen"],
    Deque: ["ArrayDeque (or LinkedList)", "Deque<String> d = new ArrayDeque<>();", "Adding/removing at both ends is cheap. Use LinkedList only if you also need List methods.", "A playlist where songs are added to the front and back"]
  };
  function pgChooser(root) {
    var body = shell(root, "Which collection should I use? — answer the questions");
    var path = el("div", { class: "viz-path" });
    var box = el("div", { class: "viz-chooser" });
    body.appendChild(path);
    body.appendChild(box);
    var trail = [];
    function show(id) {
      box.innerHTML = "";
      path.innerHTML = "";
      trail.forEach(function (t, i) {
        path.appendChild(el("span", { class: "viz-path__step", text: (i + 1) + ". " + t }));
      });
      if (id.indexOf("r:") === 0) {
        var r = RESULTS[id.slice(2)];
        box.appendChild(el("div", { class: "viz-result" }, [
          el("div", { class: "viz-result__k", text: "Use this" }),
          el("div", { class: "viz-result__name", text: r[0] }),
          el("pre", {}, [el("code", { class: "lang-java", text: r[1] })]),
          el("p", { text: r[2] }),
          el("p", { class: "text-sm" }, [el("b", { text: "Real-world example: " }), r[3]])
        ]));
        box.appendChild(btn("Start again", function () { trail = []; show("start"); }, true));
        if (global.Course && global.Course.highlight) global.Course.highlight();
        return;
      }
      var node = CHOOSER[id];
      box.appendChild(el("p", { class: "viz-chooser__q", text: node.q }));
      var opts = el("div", { class: "viz-chooser__opts" });
      node.a.forEach(function (a) {
        opts.appendChild(btn(a[0], function () { trail.push(a[0]); show(a[1]); }, true));
      });
      box.appendChild(opts);
    }
    show("start");
  }

  /* ======================================================================
     9. Playground: typebox — Box<T> checked at compile time vs a raw Box
     ====================================================================== */
  var LITERALS = JavaSim.LITERALS;
  function pgTypeBox(root) {
    var body = shell(root, "The Box Type-Checker — generic Box<T> vs raw Box");
    var T = "String", raw = [], generic = null;
    var seg = el("div", { class: "playground__controls" });
    var label = el("span", { class: "text-sm", text: "Choose T:" });
    seg.appendChild(label);
    var tBtns = {};
    ["String", "Integer", "Double", "Number", "Object"].forEach(function (t) {
      var b = btn("Box<" + t + ">", function () { T = t; generic = null; paint(); log("// new Box<" + t + ">() created — now try putting values in", "is-muted"); }, true);
      tBtns[t] = b; seg.appendChild(b);
    });
    body.appendChild(seg);
    var putRow = el("div", { class: "playground__controls" });
    putRow.appendChild(el("span", { class: "text-sm", text: "box.put(" }));
    LITERALS.forEach(function (lit) { putRow.appendChild(btn(lit.code, function () { put(lit); })); });
    putRow.appendChild(el("span", { class: "text-sm", text: ")" }));
    putRow.appendChild(btn("String s = (String) rawBox.get()", readRaw, true));
    putRow.appendChild(btn("Reset", reset, true));
    body.appendChild(putRow);

    var gState = el("div", { class: "viz-items" }), rState = el("div", { class: "viz-items" });
    var gDecl = el("code", { class: "viz-panel__decl" });
    body.appendChild(el("div", { class: "viz-grid" }, [
      el("div", { class: "viz-panel" }, [el("div", { class: "viz-panel__title", text: "Generic Box<T>" }), gDecl,
        el("div", { class: "viz-panel__rule", text: "The compiler checks every put() BEFORE the program runs. A wrong type is a red underline in IntelliJ." }), gState]),
      el("div", { class: "viz-panel" }, [el("div", { class: "viz-panel__title", text: "Raw Box (no generics)" }), el("code", { class: "viz-panel__decl", text: "Box rawBox = new Box();   // old style" }),
        el("div", { class: "viz-panel__rule", text: "Accepts anything. Mistakes are only discovered when the program is running and you cast." }), rState])
    ]));
    var out = el("div", { class: "viz-log", "aria-live": "polite" });
    body.appendChild(el("div", { class: "viz-log__label", text: "Compiler / run-time messages" }));
    body.appendChild(out);
    function log(t, c) { logLine(out, t, c); }

    function paint() {
      Object.keys(tBtns).forEach(function (t) { tBtns[t].classList.toggle("playground__btn--secondary", t !== T); });
      gDecl.textContent = "Box<" + T + "> box = new Box<>();";
      gState.innerHTML = "";
      gState.appendChild(generic ? chip(generic.code + "  (" + generic.wrapper + ")", "is-new") : el("span", { class: "viz-empty", text: "(empty)" }));
      rState.innerHTML = "";
      if (!raw.length) rState.appendChild(el("span", { class: "viz-empty", text: "(empty)" }));
      else rState.appendChild(chip(raw[raw.length - 1].code + "  (" + raw[raw.length - 1].wrapper + ")", "is-hot"));
    }
    function put(lit) {
      raw.push(lit);
      if (JavaSim.canPut(lit.wrapper, T)) {
        generic = lit;
        log("box.put(" + lit.code + ")    ✓ compiles" + (lit.prim !== lit.wrapper ? "  (autoboxing: " + lit.prim + " → " + lit.wrapper + ")" : ""), "is-good");
      } else {
        log("box.put(" + lit.code + ")    ✗ COMPILE ERROR: incompatible types: " + lit.prim + " cannot be converted to " + T, "is-bad");
      }
      log("rawBox.put(" + lit.code + ") ✓ compiles (a raw box takes anything — nobody checks)", "is-muted");
      paint();
    }
    function readRaw() {
      if (!raw.length) { log("rawBox is empty: get() returns null, and (String) null is allowed — s is null.", "is-muted"); return; }
      var last = raw[raw.length - 1];
      if (last.wrapper === "String") log("String s = (String) rawBox.get();  ✓ works this time, s = " + last.code, "is-good");
      else log("String s = (String) rawBox.get();  ✗ RUN-TIME CRASH: ClassCastException: class " + last.runtime + " cannot be cast to class java.lang.String", "is-bad");
    }
    function reset() { raw = []; generic = null; out.innerHTML = ""; log("// empty boxes", "is-muted"); paint(); }
    reset();
  }

  /* ======================================================================
     10. Playground: wildcards — what a List<?...> parameter accepts and allows
     ====================================================================== */
  function pgWildcards(root) {
    var body = shell(root, "Wildcard Explorer — what compiles?");
    var PARAMS = ["List<Integer>", "List<Number>", "List<Object>", "List<? extends Number>", "List<? super Integer>", "List<?>"];
    var ARGS = ["Integer", "Double", "Number", "Object", "String"];
    var EXPLAIN = {
      "List<Integer>": "An exact type: ONLY a List<Integer> fits. Reading gives Integer; you may add Integers.",
      "List<Number>": "An exact type: ONLY a List<Number> fits — not List<Integer>, even though Integer is a Number! You may add any Number.",
      "List<Object>": "An exact type: ONLY a List<Object> fits. You may add anything, but you only read back Object.",
      "List<? extends Number>": "\"A list of SOME kind of Number.\" Accepts List<Integer>, List<Double>, List<Number>. Safe to READ as Number. You can't add, because Java doesn't know which kind it really is (it might be a List<Double> and you add an Integer!). A PRODUCER: it gives you values.",
      "List<? super Integer>": "\"A list that can HOLD Integers.\" Accepts List<Integer>, List<Number>, List<Object>. Safe to ADD Integers. Reading only gives Object, because Java doesn't know the exact type. A CONSUMER: it takes your values.",
      "List<?>": "\"A list of anything.\" Accepts every list. You can read elements as Object and call size(), but you can't add (except null)."
    };
    var cur = PARAMS[3];
    var seg = el("div", { class: "playground__controls" });
    var btns = {};
    PARAMS.forEach(function (p) { var b = btn(p, function () { cur = p; paint(); }, true); btns[p] = b; seg.appendChild(b); });
    body.appendChild(el("p", { class: "text-sm text-faint", text: "Pick the parameter type of a method. The tables show what the real Java compiler says (every answer here was checked with javac)." }));
    body.appendChild(seg);
    var sig = el("pre", {}, [el("code", { class: "lang-java" })]);
    body.appendChild(sig);
    var explain = el("div", { class: "callout callout--concept" });
    body.appendChild(explain);
    var grid = el("div", { class: "viz-grid" });
    body.appendChild(grid);

    function table(title, rows) {
      var t = el("table", {}, [el("thead", {}, [el("tr", {}, [el("th", { text: title }), el("th", { text: "Compiles?" })])])]);
      var tb = el("tbody");
      rows.forEach(function (r) {
        tb.appendChild(el("tr", {}, [el("td", {}, [el("code", { text: r[0] })]), el("td", { class: r[1] ? "yes" : "no", text: r[1] ? "✓ yes" : "✗ no" })]));
      });
      t.appendChild(tb);
      return el("div", { class: "viz-panel" }, [t]);
    }
    function paint() {
      Object.keys(btns).forEach(function (p) { btns[p].classList.toggle("playground__btn--secondary", p !== cur); });
      var code = sig.querySelector("code");
      code.removeAttribute("data-hl-done");
      code.textContent = "static void process(" + cur + " list) {\n    // what may I do with 'list' in here?\n}";
      if (global.Course && global.Course.highlight) global.Course.highlight();
      explain.innerHTML = "";
      explain.appendChild(el("span", { class: "callout__i", text: "💡" }));
      explain.appendChild(el("div", { class: "callout__b" }, [el("b", { text: cur }), el("p", { text: EXPLAIN[cur] })]));
      grid.innerHTML = "";
      grid.appendChild(table("Call process(...) with", ARGS.map(function (a) { return ["List<" + a + ">", JavaSim.acceptsList(cur, a)]; })));
      grid.appendChild(table("Inside: read", ["Integer", "Number", "Object"].map(function (x) { return [x + " v = list.get(0);", JavaSim.canReadAs(cur, x)]; })));
      grid.appendChild(table("Inside: add", [["Integer", "Integer.valueOf(1)"], ["Double", "1.5"], ["Number", "someNumber"], ["Object", "new Object()"]].map(function (x) { return ["list.add(" + x[1] + ");", JavaSim.canAdd(cur, x[0])]; })));
    }
    paint();
  }

  /* ======================================================================
     11. Playground: bytes — the same text as BYTES (InputStream) and CHARS (Reader)
     ====================================================================== */
  function pgBytes(root) {
    var body = shell(root, "Bytes vs Characters — what the two kinds of stream see");
    var input = el("input", { type: "text", class: "viz-input", value: "₦500 café", "aria-label": "Text to analyse", style: "width:16rem" });
    body.appendChild(el("div", { class: "playground__controls" }, [
      el("span", { class: "text-sm", text: "Text written to the file:" }), input,
      btn("Hi!", function () { input.value = "Hi!"; paint(); }, true),
      btn("₦500 café", function () { input.value = "₦500 café"; paint(); }, true),
      btn("😀 ok", function () { input.value = "😀 ok"; paint(); }, true)
    ]));
    input.addEventListener("input", paint);
    var summary = el("div", { class: "viz-grid" });
    var table = el("div", { class: "viz-panel" });
    body.appendChild(summary);
    body.appendChild(table);
    function paint() {
      var s = input.value, bytes = JavaSim.utf8Bytes(s), chars = JavaSim.javaChars(s);
      summary.innerHTML = "";
      summary.appendChild(el("div", { class: "viz-panel" }, [
        el("div", { class: "viz-panel__title", text: "FileInputStream (bytes)" }),
        el("div", { class: "viz-panel__rule", text: "read() returns " + bytes.length + " numbers from 0–255, then -1" }),
        el("div", { class: "viz-print", text: bytes.concat([-1]).join(" ") })
      ]));
      summary.appendChild(el("div", { class: "viz-panel" }, [
        el("div", { class: "viz-panel__title", text: "FileReader (characters)" }),
        el("div", { class: "viz-panel__rule", text: "read() returns " + chars.length + " characters, then -1   (s.length() = " + s.length + ")" }),
        el("div", { class: "viz-print", text: chars.map(function (c) { return String.fromCharCode(c); }).join(" · ") + "  · -1" })
      ]));
      // per-character breakdown (by code point, so emoji show as one symbol)
      table.innerHTML = "";
      var t = el("table", {}, [el("thead", {}, [el("tr", {}, [el("th", { text: "Symbol" }), el("th", { text: "Java chars" }), el("th", { text: "UTF-8 bytes in the file" })])])]);
      var tb = el("tbody");
      Array.from(s).forEach(function (sym) {
        var b = JavaSim.utf8Bytes(sym);
        tb.appendChild(el("tr", {}, [
          el("td", {}, [el("code", { text: sym === " " ? "(space)" : sym })]),
          el("td", { text: String(sym.length) }),
          el("td", {}, [el("code", { text: b.join(" ") + "   (" + b.length + " byte" + (b.length > 1 ? "s" : "") + ")" })])
        ]));
      });
      t.appendChild(tb);
      table.appendChild(t);
    }
    paint();
  }

  /* ======================================================================
     12. Playground: buffering — how many trips to the disk?
     ====================================================================== */
  function pgBuffering(root) {
    var body = shell(root, "Buffering — count the trips to the disk");
    var sizes = [1000, 10000, 100000, 1000000, 10000000];
    var sizeSel = el("select", { class: "viz-input viz-input--sm", "aria-label": "File size" });
    ["1 KB", "10 KB", "100 KB", "1 MB", "10 MB"].forEach(function (l, i) { sizeSel.appendChild(el("option", { value: String(sizes[i]), text: l })); });
    sizeSel.value = "100000";
    sizeSel.addEventListener("change", paint);
    body.appendChild(el("div", { class: "playground__controls" }, [el("span", { class: "text-sm", text: "Read a file of" }), sizeSel, el("span", { class: "text-sm", text: "one byte at a time with read()" })]));
    var rows = el("div", { class: "viz-bars" });
    body.appendChild(rows);
    var note = el("p", { class: "text-sm text-faint" });
    body.appendChild(note);
    var OPTS = [["FileInputStream (no buffer)", 0], ["BufferedInputStream(in, 512)", 512], ["BufferedInputStream(in) — 8192", 8192], ["BufferedInputStream(in, 65536)", 65536]];
    function fmt(n) { return n.toLocaleString("en-US"); }
    function paint() {
      var n = +sizeSel.value;
      rows.innerHTML = "";
      var max = Math.log10(JavaSim.diskReads(n, 0));
      OPTS.forEach(function (o) {
        var trips = JavaSim.diskReads(n, o[1]);
        rows.appendChild(el("div", { class: "viz-bar viz-bar--wide" }, [
          el("code", { text: o[0] }),
          el("span", { class: "viz-bar__track" }, [el("span", { class: "viz-bar__fill", style: "width:" + Math.max(3, Math.round(Math.log10(trips) / max * 100)) + "%" })]),
          el("b", { text: fmt(trips) })
        ]));
      });
      note.textContent = "Trips = how many times the program asks the operating system for data (the numbers match the counting stream in Example 4). " +
        "If every trip cost just 0.01 ms, the unbuffered read of this file would spend about " + (JavaSim.diskReads(n, 0) * 0.01 / 1000).toFixed(2) +
        " s on trips; with the default 8192 buffer, " + (JavaSim.diskReads(n, 8192) * 0.01).toFixed(2) + " ms. (Bar lengths use a log scale.)";
    }
    paint();
  }

  /* ======================================================================
     13. Playground: streamchain — build the right stream for the job
     ====================================================================== */
  function pgStreamChain(root) {
    var body = shell(root, "Stream Builder — which classes do I combine?");
    var kind = "text", dir = "read", buffered = true;
    function seg(label, opts, get, set) {
      var wrap = el("div", { class: "playground__controls" }, [el("span", { class: "text-sm", style: "min-width:8rem", text: label })]);
      var btns = [];
      opts.forEach(function (o) {
        var b = btn(o[1], function () { set(o[0]); paint(); }, true);
        b.dataset.v = String(o[0]);
        btns.push(b);
        wrap.appendChild(b);
      });
      wrap.paintSel = function () { btns.forEach(function (b) { b.classList.toggle("playground__btn--secondary", b.dataset.v !== String(get())); }); };
      return wrap;
    }
    var s1 = seg("What is in the file?", [["bytes", "raw bytes (image, any file)"], ["text", "text (characters)"], ["data", "Java values (int, double…)"], ["objects", "whole objects"]], function () { return kind; }, function (v) { kind = v; });
    var s2 = seg("Reading or writing?", [["read", "reading"], ["write", "writing"]], function () { return dir; }, function (v) { dir = v; });
    var s3 = seg("Add a buffer?", [[true, "yes (recommended)"], [false, "no"]], function () { return buffered; }, function (v) { buffered = v; });
    [s1, s2, s3].forEach(function (s) { body.appendChild(s); });
    var code = el("pre", {}, [el("code", { class: "lang-java" })]);
    body.appendChild(code);
    var layers = el("div", { class: "viz-layers" });
    body.appendChild(layers);
    var why = el("p", { class: "text-sm" });
    body.appendChild(why);
    function paint() {
      [s1, s2, s3].forEach(function (s) { s.paintSel(); });
      var chain = JavaSim.streamChain(kind, dir, buffered), type = JavaSim.streamChainType(kind, dir, buffered);
      var c = code.querySelector("code");
      c.removeAttribute("data-hl-done");
      c.textContent = "try (" + type + " s =\n         " + chain + ") {\n    // ... use s here ...\n}   // closed automatically";
      if (global.Course && global.Course.highlight) global.Course.highlight();
      // draw the nesting: outermost first
      layers.innerHTML = "";
      var names = chain.match(/new (\w+)/g).map(function (x) { return x.slice(4); });
      var holder = layers;
      names.forEach(function (n, i) {
        var box = el("div", { class: "viz-layer" + (i === names.length - 1 ? " viz-layer--file" : "") }, [el("b", { text: n })]);
        holder.appendChild(box);
        holder = box;
      });
      var tips = {
        bytes: "Byte streams copy anything exactly: images, videos, zip files.",
        text: "Readers/Writers turn bytes into characters for you (UTF-8). " + (buffered ? (dir === "read" ? "BufferedReader adds readLine()." : "BufferedWriter adds newLine().") : "Without a buffer every single character is a trip to the disk!"),
        data: "DataInput/DataOutput streams add readInt()/writeInt(), readUTF()/writeUTF()… Read in exactly the order you wrote.",
        objects: "Object streams add readObject()/writeObject(). The class must implement Serializable."
      };
      why.textContent = "Each class WRAPS the one inside it and adds a skill (this is called the Decorator pattern). " + tips[kind];
    }
    paint();
  }

  /* ======================================================================
     14. Playground: serialize — what survives a save & load?
     ====================================================================== */
  function pgSerialize(root) {
    var body = shell(root, "Serialization Lab — save an object, load it back");
    var fields = [
      { name: "owner", type: "String", value: '"Ada"', def: "null", transient: false },
      { name: "balance", type: "double", value: "2500.0", def: "0.0", transient: false },
      { name: "pin", type: "String", value: '"1234"', def: "null", transient: true },
      { name: "loggedIn", type: "boolean", value: "true", def: "false", transient: false }
    ];
    var serializable = true, savedVersion = null, version = 1;
    var classBox = el("pre", {}, [el("code", { class: "lang-java" })]);
    var toggles = el("div", { class: "playground__controls" });
    body.appendChild(el("p", { class: "text-sm text-faint", text: "Tick fields to make them transient, then Save and Load. Try turning Serializable off, or changing the version after saving." }));
    body.appendChild(toggles);
    body.appendChild(classBox);
    var out = el("div", { class: "viz-log", "aria-live": "polite" });
    body.appendChild(el("div", { class: "playground__controls" }, [
      btn("writeObject() — save", save),
      btn("readObject() — load", load),
      btn("Toggle implements Serializable", function () { serializable = !serializable; paint(); log("// class " + (serializable ? "now implements" : "no longer implements") + " Serializable", "is-muted"); }, true),
      btn("Change serialVersionUID", function () { version++; paint(); log("// serialVersionUID is now " + version + "L (as if you edited the class)", "is-muted"); }, true)
    ]));
    body.appendChild(out);
    function log(t, c) { logLine(out, t, c); }
    function paint() {
      toggles.innerHTML = "";
      fields.forEach(function (f) {
        var cb = el("input", { type: "checkbox", "aria-label": "transient " + f.name });
        cb.checked = f.transient;
        cb.addEventListener("change", function () { f.transient = cb.checked; paint(); });
        toggles.appendChild(el("label", { class: "text-sm" }, [cb, " transient " + f.name]));
      });
      var lines = ["class Account" + (serializable ? " implements Serializable" : "") + " {"];
      if (serializable) lines.push("    private static final long serialVersionUID = " + version + "L;");
      fields.forEach(function (f) { lines.push("    " + (f.transient ? "transient " : "") + f.type + " " + f.name + " = " + f.value + ";"); });
      lines.push("}");
      var c = classBox.querySelector("code");
      c.removeAttribute("data-hl-done");
      c.textContent = lines.join("\n");
      if (global.Course && global.Course.highlight) global.Course.highlight();
    }
    function save() {
      if (!serializable) { log("out.writeObject(account)  ✗ java.io.NotSerializableException: Account", "is-bad"); return; }
      savedVersion = version;
      var kept = fields.filter(function (f) { return !f.transient; }).map(function (f) { return f.name; });
      log("out.writeObject(account)  ✓ saved fields: " + kept.join(", ") + (kept.length < fields.length ? "   (transient fields are NOT written)" : ""), "is-good");
      saved = fields.map(function (f) { return { name: f.name, value: f.transient ? f.def : f.value, skipped: f.transient }; });
    }
    var saved = null;
    function load() {
      if (!saved) { log("Nothing saved yet — in a real program: FileNotFoundException", "is-warn"); return; }
      if (!serializable) { log("in.readObject()  ✗ java.io.InvalidClassException: Account; class invalid for deserialization", "is-bad"); return; }
      if (savedVersion !== version) {
        log("in.readObject()  ✗ java.io.InvalidClassException: Account; local class incompatible: stream classdesc serialVersionUID = " + savedVersion + ", local class serialVersionUID = " + version, "is-bad");
        return;
      }
      log("in.readObject()  ✓ a NEW Account object: " + saved.map(function (f) { return f.name + "=" + f.value + (f.skipped ? " (default!)" : ""); }).join(", "), "is-good");
    }
    paint();
    log("// an Account object is in memory, ready to save", "is-muted");
  }

  /* ======================================================================
     15. Playground: threadstates — click actions, watch Thread.getState()
     ====================================================================== */
  var STATE_ACTIONS = {
    NEW: [["start()", "RUNNABLE", "The thread is started. From now on the scheduler may run it at any time."]],
    RUNNABLE: [
      ["Thread.sleep(1000)", "TIMED_WAITING", "Sleeping for a fixed time. It uses no CPU while it sleeps."],
      ["other.join()", "WAITING", "Waiting, with no time limit, for another thread to finish."],
      ["other.join(500)", "TIMED_WAITING", "Waiting for another thread, but at most 500 ms."],
      ["enter synchronized block (lock is taken)", "BLOCKED", "Another thread holds the lock, so this thread must wait at the door."],
      ["run() finishes", "TERMINATED", "The run() method returned (or threw an exception). The thread is finished for ever."]
    ],
    TIMED_WAITING: [
      ["time runs out", "RUNNABLE", "The sleep/timeout is over, so it's ready to run again."],
      ["interrupt()", "RUNNABLE", "Woken early: sleep() throws InterruptedException inside the thread, and the thread decides what to do."]
    ],
    WAITING: [
      ["the other thread finishes", "RUNNABLE", "join() returns, and the thread continues."],
      ["interrupt()", "RUNNABLE", "Woken early: join() throws InterruptedException."]
    ],
    BLOCKED: [["the lock is released", "RUNNABLE", "It gets the lock and enters the synchronized block."]],
    TERMINATED: [["start() again", "TERMINATED", "!java.lang.IllegalThreadStateException: a thread can only be started ONCE. Create a new Thread object instead."]]
  };
  function pgThreadStates(root) {
    var body = shell(root, "Thread State Machine — click an action, watch getState()");
    var state = "NEW";
    var POS = { NEW: [70, 60], RUNNABLE: [300, 60], TIMED_WAITING: [530, 30], WAITING: [530, 95], BLOCKED: [530, 160], TERMINATED: [300, 160] };
    var NS = "http://www.w3.org/2000/svg";
    var svg = document.createElementNS(NS, "svg");
    svg.setAttribute("viewBox", "0 0 660 200");
    svg.setAttribute("class", "dg");
    svg.setAttribute("width", "660");
    svg.setAttribute("height", "200");
    var nodes = {};
    Object.keys(POS).forEach(function (s) {
      var g = document.createElementNS(NS, "g");
      var r = document.createElementNS(NS, "rect");
      r.setAttribute("x", POS[s][0] - 62); r.setAttribute("y", POS[s][1] - 17);
      r.setAttribute("width", "124"); r.setAttribute("height", "34"); r.setAttribute("rx", "17");
      var t = document.createElementNS(NS, "text");
      t.setAttribute("x", POS[s][0]); t.setAttribute("y", POS[s][1] + 5);
      t.setAttribute("text-anchor", "middle"); t.setAttribute("class", "t-mono t-b");
      t.textContent = s;
      g.appendChild(r); g.appendChild(t); svg.appendChild(g);
      nodes[s] = r;
    });
    body.appendChild(el("div", { class: "viz-states" }, [svg]));
    var now = el("p", { class: "viz-chooser__q" });
    var acts = el("div", { class: "viz-chooser__opts" });
    body.appendChild(now);
    body.appendChild(acts);
    var out = el("div", { class: "viz-log", "aria-live": "polite" });
    body.appendChild(out);
    function paint() {
      Object.keys(nodes).forEach(function (s) { nodes[s].setAttribute("class", s === state ? "impl" : "muted"); });
      now.textContent = "t.getState() → " + state;
      acts.innerHTML = "";
      STATE_ACTIONS[state].forEach(function (a) {
        acts.appendChild(btn(a[0], function () {
          var msg = a[2];
          if (msg.charAt(0) === "!") { logLine(out, a[0] + "  ✗ " + msg.slice(1), "is-bad"); return; }
          logLine(out, state + "  --" + a[0] + "-->  " + a[1] + "    " + msg, a[1] === "TERMINATED" ? "is-warn" : "");
          state = a[1];
          paint();
        }, true));
      });
      acts.appendChild(btn("Reset (new Thread)", function () { state = "NEW"; out.innerHTML = ""; logLine(out, "Thread t = new Thread(job);   // state NEW", "is-muted"); paint(); }));
    }
    logLine(out, "Thread t = new Thread(job);   // state NEW", "is-muted");
    paint();
  }

  /* ======================================================================
     16. Playground: interleave — the scheduler decides the order
     ====================================================================== */
  function pgInterleave(root) {
    var body = shell(root, "Who Prints First? — a simulated thread scheduler");
    var useJoin = false, seen = {};
    var joinBox = el("input", { type: "checkbox", "aria-label": "Use join" });
    joinBox.addEventListener("change", function () { useJoin = joinBox.checked; seen = {}; run(); });
    body.appendChild(el("p", { class: "text-sm text-faint", html: "Two threads each print 3 lines, like Example 2. The real scheduler picks who runs next, and you can't control it. This demo imitates that with random choices. Press <b>Run again</b> a few times." }));
    body.appendChild(el("div", { class: "playground__controls" }, [
      btn("Run again", run),
      el("label", { class: "text-sm" }, [joinBox, " main calls ada.join() before starting Ben"])
    ]));
    var cols = el("div", { class: "viz-items viz-items--stack" });
    body.appendChild(cols);
    var stat = el("p", { class: "text-sm" });
    body.appendChild(stat);
    function run() {
      var a = 0, b = 0, order = [];
      while (a < 3 || b < 3) {
        var pickA = useJoin ? a < 3 : (b >= 3 || (a < 3 && Math.random() < 0.5));
        if (pickA) { a++; order.push("Ada says " + a); } else { b++; order.push("Ben says " + b); }
      }
      cols.innerHTML = "";
      order.forEach(function (line) { cols.appendChild(chip(line, line.indexOf("Ada") === 0 ? "is-front" : "is-new")); });
      var key = order.map(function (l) { return l.charAt(0); }).join("");
      seen[key] = true;
      var n = Object.keys(seen).length;
      stat.innerHTML = useJoin
        ? "With <code>join()</code> there is only <b>1</b> possible order: Ada finishes completely before Ben starts."
        : "Different orders seen so far: <b>" + n + "</b> of the <b>20</b> possible orders. (Choosing which 3 of the 6 lines are Ada's gives 6!/(3!·3!) = 20.) Your real program can print any of them.";
    }
    run();
  }

  /* ======================================================================
     17. Playground: daemon — does the program end?
     ====================================================================== */
  function pgDaemon(root) {
    var body = shell(root, "Daemon or Not? — does the program end when main ends?");
    var daemon = true;
    var box = el("input", { type: "checkbox", "aria-label": "daemon" });
    box.checked = true;
    box.addEventListener("change", function () { daemon = box.checked; paint(); });
    body.appendChild(el("div", { class: "playground__controls" }, [el("label", { class: "text-sm" }, [box, " autosave.setDaemon(true) before start()"])]));
    var tl = el("div", { class: "viz-timeline" });
    body.appendChild(tl);
    var msg = el("p", { class: "text-sm" });
    body.appendChild(msg);
    function lane(label, cells) {
      var row = el("div", { class: "viz-lane" }, [el("span", { class: "viz-lane__label", text: label })]);
      cells.forEach(function (c) { row.appendChild(el("span", { class: "viz-lane__cell " + c })); });
      return row;
    }
    function paint() {
      tl.innerHTML = "";
      var mainCells = [], bgCells = [];
      for (var i = 0; i < 12; i++) {
        mainCells.push(i < 4 ? "on" : "off");
        bgCells.push(daemon ? (i < 4 ? "on" : "off") : "on");
      }
      tl.appendChild(lane("main", mainCells));
      tl.appendChild(lane("autosave", bgCells));
      tl.appendChild(el("div", { class: "viz-lane__axis", text: "time →   (main finishes after the 4th block)" }));
      msg.innerHTML = daemon
        ? "✅ <b>Daemon thread:</b> when main (the last non-daemon thread) finishes, the JVM exits and switches the daemon off. The program <b>ends</b>. This is Example 7."
        : "⚠️ <b>Normal (user) thread:</b> the JVM waits for <em>all</em> non-daemon threads. autosave loops forever, so the program <b>never ends</b>, and you'd have to press the red ■ Stop button in IntelliJ.";
    }
    paint();
  }

  /* ======================================================================
     18. Playground: virtual — waiting tasks on platform vs virtual threads
     ====================================================================== */
  JavaSim.idealWaitTime = function (tasks, taskMs, workers) {
    return Math.ceil(tasks / workers) * taskMs;          // each "round" of workers waits taskMs
  };
  function pgVirtual(root) {
    var body = shell(root, "Platform vs Virtual Threads — tasks that mostly WAIT");
    var tasksSel = el("select", { class: "viz-input viz-input--sm", "aria-label": "number of tasks" });
    [100, 1000, 10000, 100000].forEach(function (n) { tasksSel.appendChild(el("option", { value: String(n), text: n.toLocaleString("en-US") + " tasks" })); });
    tasksSel.value = "10000";
    var poolSel = el("select", { class: "viz-input viz-input--sm", "aria-label": "platform pool size" });
    [20, 100, 500].forEach(function (n) { poolSel.appendChild(el("option", { value: String(n), text: "pool of " + n + " platform threads" })); });
    poolSel.value = "100";
    [tasksSel, poolSel].forEach(function (s) { s.addEventListener("change", paint); });
    body.appendChild(el("div", { class: "playground__controls" }, [tasksSel, el("span", { class: "text-sm", text: "each waiting 1 second, run on a" }), poolSel]));
    var bars = el("div", { class: "viz-bars" });
    body.appendChild(bars);
    var note = el("p", { class: "text-sm text-faint" });
    body.appendChild(note);
    function fmt(ms) { return ms >= 60000 ? (ms / 60000).toFixed(1) + " min" : (ms / 1000).toFixed(ms < 10000 ? 1 : 0) + " s"; }
    function paint() {
      var n = +tasksSel.value, pool = +poolSel.value;
      var p = JavaSim.idealWaitTime(n, 1000, pool), v = JavaSim.idealWaitTime(n, 1000, n);
      bars.innerHTML = "";
      [["Platform pool (" + pool + ")", p], ["Virtual threads (one per task)", v]].forEach(function (r) {
        bars.appendChild(el("div", { class: "viz-bar viz-bar--wide" }, [
          el("code", { text: r[0] }),
          el("span", { class: "viz-bar__track" }, [el("span", { class: "viz-bar__fill", style: "width:" + Math.max(2, Math.round(r[1] / p * 100)) + "%" })]),
          el("b", { text: fmt(r[1]) })
        ]));
      });
      note.textContent = "Ideal waiting time: a pool of " + pool + " finishes " + pool + " tasks per second, so it needs " + Math.ceil(n / pool).toLocaleString("en-US") +
        " rounds. Virtual threads are so cheap that every task can wait at the same time. Real runs add a little overhead (Example 8 measured about 1 s for 10,000 virtual threads and about 12 s for 1,000 tasks on 100 platform threads). " +
        "Remember: this only helps tasks that WAIT (network, database, files). For pure calculation, virtual threads give no speed-up.";
    }
    paint();
  }

  /* ======================================================================
     19. Boot
     ====================================================================== */
  var REGISTRY = {
    collections: pgCollections,
    wordcount: pgWordCount,
    queues: pgQueues,
    listcost: pgListCost,
    binarysearch: pgBinarySearch,
    chooser: pgChooser,
    typebox: pgTypeBox,
    wildcards: pgWildcards,
    bytes: pgBytes,
    buffering: pgBuffering,
    streamchain: pgStreamChain,
    serialize: pgSerialize,
    threadstates: pgThreadStates,
    interleave: pgInterleave,
    daemon: pgDaemon,
    virtual: pgVirtual
  };
  function boot() {
    document.querySelectorAll("[data-playground]").forEach(function (node) {
      var fn = REGISTRY[node.getAttribute("data-playground")];
      if (!fn) return;
      try { fn(node); }
      catch (e) { node.textContent = "This demo could not start: " + e.message; }
    });
  }
  if (document.readyState === "loading") document.addEventListener("DOMContentLoaded", boot);
  else boot();
  global.Playgrounds = REGISTRY;
})(typeof window !== "undefined" ? window : this);
