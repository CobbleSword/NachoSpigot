package net.minecraft.server;

import com.google.common.collect.Lists;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Callable;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CrashReport {

    private static final Logger a = LogManager.getLogger();
    private final String b;
    private final Throwable c;
    private final CrashReportSystemDetails d = new CrashReportSystemDetails(this, "System Details");
    private final List<CrashReportSystemDetails> e = Lists.newArrayList();
    private File f;
    private boolean g = true;
    private StackTraceElement[] h = new StackTraceElement[0];

    public CrashReport(String s, Throwable throwable) {
        this.b = s;
        this.c = throwable;
        this.h();
    }

    private void h() {
        this.d.a("Minecraft Version", new Callable() {
            public String a() {
                return "1.8.8";
            }

            public Object call() throws Exception {
                return this.a();
            }
        });
        this.d.a("Operating System", new Callable() {
            public String a() {
                return System.getProperty("os.name") + " (" + System.getProperty("os.arch") + ") version " + System.getProperty("os.version");
            }

            public Object call() throws Exception {
                return this.a();
            }
        });
        this.d.a("Java Version", new Callable() {
            public String a() {
                return System.getProperty("java.version") + ", " + System.getProperty("java.vendor");
            }

            public Object call() throws Exception {
                return this.a();
            }
        });
        this.d.a("Java VM Version", new Callable() {
            public String a() {
                return System.getProperty("java.vm.name") + " (" + System.getProperty("java.vm.info") + "), " + System.getProperty("java.vm.vendor");
            }

            public Object call() throws Exception {
                return this.a();
            }
        });
        this.d.a("Memory", new Callable() {
            public String a() {
                Runtime runtime = Runtime.getRuntime();
                long i = runtime.maxMemory();
                long j = runtime.totalMemory();
                long k = runtime.freeMemory();
                long l = i / 1024L / 1024L;
                long i1 = j / 1024L / 1024L;
                long j1 = k / 1024L / 1024L;

                return k + " bytes (" + j1 + " MB) / " + j + " bytes (" + i1 + " MB) up to " + i + " bytes (" + l + " MB)";
            }

            public Object call() throws Exception {
                return this.a();
            }
        });
        this.d.a("JVM Flags", new Callable() {
            public String a() {
                RuntimeMXBean runtimemxbean = ManagementFactory.getRuntimeMXBean();
                List list = runtimemxbean.getInputArguments();
                int i = 0;
                StringBuilder stringbuilder = new StringBuilder();
                Iterator iterator = list.iterator();

                while (iterator.hasNext()) {
                    String s = (String) iterator.next();

                    if (s.startsWith("-X")) {
                        if (i++ > 0) {
                            stringbuilder.append(" ");
                        }

                        stringbuilder.append(s);
                    }
                }

                return String.format("%d total; %s", new Object[] { Integer.valueOf(i), stringbuilder.toString()});
            }

            public Object call() throws Exception {
                return this.a();
            }
        });
        this.d.a("IntCache", new Callable() {
            public String a() throws Exception {
                return IntCache.b();
            }

            public Object call() throws Exception {
                return this.a();
            }
        });
        this.d.a("CraftBukkit Information", (Callable) (new org.bukkit.craftbukkit.CraftCrashReport())); // CraftBukkit
    }

    public String a() {
        return this.b;
    }

    public Throwable b() {
        return this.c;
    }

    public void a(StringBuilder stringbuilder) {
        if ((this.h == null || this.h.length <= 0) && this.e.size() > 0) {
            this.h = (StackTraceElement[]) ArrayUtils.subarray(((CrashReportSystemDetails) this.e.get(0)).a(), 0, 1);
        }

        if (this.h != null && this.h.length > 0) {
            stringbuilder.append("-- Head --\n");
            stringbuilder.append("Stacktrace:\n");
            StackTraceElement[] astacktraceelement = this.h;
            int i = astacktraceelement.length;

            for (int j = 0; j < i; ++j) {
                StackTraceElement stacktraceelement = astacktraceelement[j];

                stringbuilder.append("\t").append("at ").append(stacktraceelement.toString());
                stringbuilder.append("\n");
            }

            stringbuilder.append("\n");
        }

        Iterator iterator = this.e.iterator();

        while (iterator.hasNext()) {
            CrashReportSystemDetails crashreportsystemdetails = (CrashReportSystemDetails) iterator.next();

            crashreportsystemdetails.a(stringbuilder);
            stringbuilder.append("\n\n");
        }

        this.d.a(stringbuilder);
    }

    public String d() {
        StringWriter stringwriter = null;
        PrintWriter printwriter = null;
        Object object = this.c;

        if (((Throwable) object).getMessage() == null) {
            if (object instanceof NullPointerException) {
                object = new NullPointerException(this.b);
            } else if (object instanceof StackOverflowError) {
                object = new StackOverflowError(this.b);
            } else if (object instanceof OutOfMemoryError) {
                object = new OutOfMemoryError(this.b);
            }

            ((Throwable) object).setStackTrace(this.c.getStackTrace());
        }

        String s = ((Throwable) object).toString();

        try {
            stringwriter = new StringWriter();
            printwriter = new PrintWriter(stringwriter);
            ((Throwable) object).printStackTrace(printwriter);
            s = stringwriter.toString();
        } finally {
            IOUtils.closeQuietly(stringwriter);
            IOUtils.closeQuietly(printwriter);
        }

        return s;
    }

    public String e() {
        StringBuilder stringbuilder = new StringBuilder();

        stringbuilder.append("---- Minecraft Crash Report ----\n");
        stringbuilder.append("// ");
        stringbuilder.append(i());
        stringbuilder.append("\n\n");
        stringbuilder.append("Time: ");
        stringbuilder.append((new SimpleDateFormat()).format(new Date()));
        stringbuilder.append("\n");
        stringbuilder.append("Description: ");
        stringbuilder.append(this.b);
        stringbuilder.append("\n\n");
        stringbuilder.append(this.d());
        stringbuilder.append("\n\nA detailed walkthrough of the error, its code path and all known details is as follows:\n");

        for (int i = 0; i < 87; ++i) {
            stringbuilder.append("-");
        }

        stringbuilder.append("\n\n");
        this.a(stringbuilder);
        return stringbuilder.toString();
    }

    public boolean a(File file) {
        if (this.f != null) {
            return false;
        } else {
            if (file.getParentFile() != null) {
                file.getParentFile().mkdirs();
            }

            try {
                FileWriter filewriter = new FileWriter(file);

                filewriter.write(this.e());
                filewriter.close();
                this.f = file;
                return true;
            } catch (Throwable throwable) {
                CrashReport.a.error("Could not save crash report to " + file, throwable);
                return false;
            }
        }
    }

    public CrashReportSystemDetails g() {
        return this.d;
    }

    public CrashReportSystemDetails a(String s) {
        return this.a(s, 1);
    }

    public CrashReportSystemDetails a(String s, int i) {
        CrashReportSystemDetails crashreportsystemdetails = new CrashReportSystemDetails(this, s);

        if (this.g) {
            int j = crashreportsystemdetails.a(i);
            StackTraceElement[] astacktraceelement = this.c.getStackTrace();
            StackTraceElement stacktraceelement = null;
            StackTraceElement stacktraceelement1 = null;
            int k = astacktraceelement.length - j;

            if (k < 0) {
                System.out.println("Negative index in crash report handler (" + astacktraceelement.length + "/" + j + ")");
            }

            if (astacktraceelement != null && 0 <= k && k < astacktraceelement.length) {
                stacktraceelement = astacktraceelement[k];
                if (astacktraceelement.length + 1 - j < astacktraceelement.length) {
                    stacktraceelement1 = astacktraceelement[astacktraceelement.length + 1 - j];
                }
            }

            this.g = crashreportsystemdetails.a(stacktraceelement, stacktraceelement1);
            if (j > 0 && !this.e.isEmpty()) {
                CrashReportSystemDetails crashreportsystemdetails1 = (CrashReportSystemDetails) this.e.get(this.e.size() - 1);

                crashreportsystemdetails1.b(j);
            } else if (astacktraceelement != null && astacktraceelement.length >= j && 0 <= k && k < astacktraceelement.length) {
                this.h = new StackTraceElement[k];
                System.arraycopy(astacktraceelement, 0, this.h, 0, this.h.length);
            } else {
                this.g = false;
            }
        }

        this.e.add(crashreportsystemdetails);
        return crashreportsystemdetails;
    }

    private static String i() {
        String[] astring = new String[] { "Who set us up the TNT?", "Everything\'s going to plan. No, really, that was supposed to happen.", "Uh... Did I do that?", "Oops.", "Why did you do that?", "I feel sad now :(", "My bad.", "I\'m sorry, Dave.", "I let you down. Sorry :(", "On the bright side, I bought you a teddy bear!", "Daisy, daisy...", "Oh - I know what I did wrong!", "Hey, that tickles! Hehehe!", "I blame Dinnerbone.", "You should try our sister game, Minceraft!", "Don\'t be sad. I\'ll do better next time, I promise!", "Don\'t be sad, have a hug! <3", "I just don\'t know what went wrong :(", "Shall we play a game?", "Quite honestly, I wouldn\'t worry myself about that.", "I bet Cylons wouldn\'t have this problem.", "Sorry :(", "Surprise! Haha. Well, this is awkward.", "Would you like a cupcake?", "Hi. I\'m Minecraft, and I\'m a crashaholic.", "Ooh. Shiny.", "This doesn\'t make any sense!", "Why is it breaking :(", "Don\'t do that.", "Ouch. That hurt :(", "You\'re mean.", "This is a token for 1 free hug. Redeem at your nearest Mojangsta: [~~HUG~~]", "There are four lights!", "But it works on my machine."};

        try {
            return astring[(int) (System.nanoTime() % (long) astring.length)];
        } catch (Throwable throwable) {
            return "Witty comment unavailable :(";
        }
    }

    public static CrashReport a(Throwable throwable, String s) {
        CrashReport crashreport;

        if (throwable instanceof ReportedException) {
            crashreport = ((ReportedException) throwable).a();
        } else {
            crashreport = new CrashReport(s, throwable);
        }

        return crashreport;
    }
}
