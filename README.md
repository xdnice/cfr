# CFR - Another Java Decompiler \o/

This is the public repository for the CFR decompiler, main site hosted at <a href="https://www.benf.org/other/cfr">benf.org/other/cfr</a>

CFR will decompile modern Java features - up to and <a href="https://www.benf.org/other/cfr/java9observations.html">including much of Java <a href="java9stringconcat.html">9</a>, 10, <a href="https://www.benf.org/other/cfr/switch_expressions.html">12</a> and beyond, but is written entirely in Java 6, so will work anywhere!  (<a href="https://www.benf.org/other/cfr/faq.html">FAQ</a>) - It'll even make a decent go of turning class files from other JVM languages back into java!</p>

To use, simply run the specific version jar, with the class name(s) you want to decompile (either as a path to a class file, or as a fully qualified classname on your classpath).
(`--help` to list arguments).

Alternately, to decompile an entire jar, simply provide the jar path, and if you want to emit files (which you probably do!) add `--outputdir /tmp/putithere`.

# Getting CFR

The main site for CFR is <a href="https://www.benf.org/other/cfr">benf.org/other/cfr</a>, where releases are available with a bunch of rambling musings from the author.

Since 0.145, Binaries are published on github along with release tags.

You can also download CFR from your favourite <a href="https://mvnrepository.com/artifact/org.benf/cfr">maven</a> repo, though releases are published a few days late usually, to allow for release regret.

# Issues

If you have an issue, please **_DO NOT_** include copyright materials.  I will have to delete your issue.

