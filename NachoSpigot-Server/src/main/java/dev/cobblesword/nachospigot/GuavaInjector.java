package dev.cobblesword.nachospigot;

import org.apache.commons.lang3.JavaVersion;
import org.apache.commons.lang3.SystemUtils;
import org.bukkit.Bukkit;
import org.objectweb.asm.*;

import java.lang.reflect.Method;
import java.util.logging.Level;

import static org.objectweb.asm.Opcodes.*;

public class GuavaInjector {

    @SuppressWarnings({"SameParameterValue", "JavaReflectionInvocation"})
    private static void defineClass(String name, byte[] b) throws ReflectiveOperationException {
        if (SystemUtils.isJavaVersionAtLeast(JavaVersion.JAVA_9)) {
            Class<?> moduleClazz = Class.forName("java.lang.Module");
            Method getModule = Class.class.getDeclaredMethod("getModule");
            Method addOpens = moduleClazz.getDeclaredMethod("addOpens", String.class, moduleClazz);
            // Module.class.getModule().addOpens("java.lang", GuavaInjector.class.getModule);
            addOpens.invoke(getModule.invoke(moduleClazz), "java.lang", getModule.invoke(GuavaInjector.class));
        }
        // GuavaInjector.class.getClassLoader().defineCLass(name, b, 0, b.length);
        Method defineClass = ClassLoader.class.getDeclaredMethod("defineClass", String.class, byte[].class, int.class, int.class);
        defineClass.setAccessible(true);
        defineClass.invoke(GuavaInjector.class.getClassLoader(), name, b, 0, b.length);
    }

    static {
        // MoreExecutors.sameThreadExecutors
        try {
            ClassReader cr = new ClassReader("com.google.common.util.concurrent.MoreExecutors");
            ClassWriter cw = new ClassWriter(cr, 0);
            cr.accept(new MoreExecutorsVisitor(cw), 0);
            defineClass("com.google.common.util.concurrent.MoreExecutors", cw.toByteArray());
        } catch (Exception e) {
            Bukkit.getLogger().log(Level.SEVERE, "Error creating method com.google.common.util.concurrent.MoreExecutors.sameThreadExecutor", e);
        }
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