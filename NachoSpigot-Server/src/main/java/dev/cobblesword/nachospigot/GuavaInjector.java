package dev.cobblesword.nachospigot;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.PluginClassLoader;
import org.objectweb.asm.*;

import java.lang.reflect.Method;
import java.util.logging.Level;

import static org.objectweb.asm.Opcodes.*;

public class GuavaInjector {

    private static byte[] moreExecutorsBytes;

    private static void defineClass(String name, byte[] b) {
        try {
            // GuavaInjector.class.getClassLoader().defineCLass(name, b, 0, b.length);
            Method defineClass = ClassLoader.class.getDeclaredMethod("defineClass", String.class, byte[].class, int.class, int.class);
            defineClass.setAccessible(true);
            defineClass.invoke(GuavaInjector.class.getClassLoader(), name, b, 0, b.length);
        } catch (Exception e) {
            Bukkit.getLogger().log(Level.SEVERE, "Exception while defining class", e);
        }
    }

    static {
        // MoreExecutors.sameThreadExecutors
        try {
            ClassReader cr = new ClassReader("com.google.common.util.concurrent.MoreExecutors");
            ClassWriter cw = new ClassWriter(cr, 0);
            cr.accept(new MoreExecutorsVisitor(cw), 0);
            moreExecutorsBytes = cw.toByteArray();
        } catch (Exception e) {
            Bukkit.getLogger().log(Level.SEVERE, "Exception creating method com.google.common.util.concurrent.MoreExecutors.sameThreadExecutor", e);
        }
        defineClass("com.google.common.util.concurrent.MoreExecutors", moreExecutorsBytes);
    }

    public static void load() {}

    private static class MoreExecutorsVisitor extends ClassVisitor {
        public MoreExecutorsVisitor(ClassVisitor cv) {
            super(Opcodes.ASM9, cv);
        }

        @Override
        public void visitEnd() {
            addMethod();
            super.visitEnd();
        }

        private void addMethod() {
            MethodVisitor mv = cv.visitMethod(ACC_PUBLIC | ACC_STATIC, "sameThreadExecutor", "()Lcom/google/common/util/concurrent/ListeningExecutorService;", null, null);
            mv.visitCode();
            Label label0 = new Label();
            mv.visitLabel(label0);
            mv.visitLineNumber(5, label0);
            mv.visitMethodInsn(INVOKESTATIC, "com/google/common/util/concurrent/MoreExecutors", "newDirectExecutorService", "()Lcom/google/common/util/concurrent/ListeningExecutorService;", false);
            mv.visitInsn(ARETURN);
            mv.visitMaxs(1, 0);
            mv.visitEnd();
        }
    }
}