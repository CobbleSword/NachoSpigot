package me.elier.nachospigot.config;

import dev.cobblesword.nachospigot.OldNachoConfig;
import dev.cobblesword.nachospigot.commons.FileUtils;
import org.junit.Test;
import org.objectweb.asm.*;
import org.sugarcanemc.sugarcane.util.yaml.YamlCommenter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Objects;

public class ConfigurationTests {
    private static class TestUtils {
        private static class ConfigClassVisitor extends ClassVisitor {
            public ConfigClassVisitor(int api) {
                super(api);
            }

            @Override
            public MethodVisitor visitMethod(int access, String methodName, String descriptor, String signature, String[] exceptions) {
                if ((access & Opcodes.ACC_PRIVATE) != 0) {
                    MethodVisitor mv = super.visitMethod(access, methodName, descriptor, signature, exceptions);
                    return new MethodVisitor(Opcodes.ASM9, mv) {
                        @Override
                        public void visitMethodInsn(int opcode, String owner, String name, String descriptor, boolean isInterface) {
                            if (opcode == Opcodes.INVOKEVIRTUAL && Objects.equals(owner, Type.getType(YamlCommenter.class).getInternalName())) {
                                // This method contains an invokevirtual call to YamlCommenter, this shouldn't happen!
                                throw new IllegalStateException(String.format(
                                        "Method %s contains a call to YamlCommenter, which shouldn't happen! Put your comments in the loadComments function.",
                                        methodName
                                ));
                            } else {
                                super.visitMethodInsn(opcode, owner, name, descriptor, isInterface);
                            }
                        }
                    };
                }

                return super.visitMethod(access, methodName, descriptor, signature, exceptions);
            }
        }
    }

    @Test
    public void migrateFullConfig() {
        FileUtils.toFile(new OldNachoConfig(), new File("nacho.json"));
        NachoConfig.init(new File("nacho.yml"));
    }

    @Test
    public void migratePartialModifiedConfig() throws IOException {
        File file = new File("nacho.json");
        file.createNewFile();
        FileWriter writer = new FileWriter(file);
        writer.write("{ \"usePandaWire\": false }");
        writer.close();
        NachoConfig.init(new File("nacho.yml"));
        assert !NachoConfig.usePandaWire;
    }

    @Test
    public void migrateEmptyConfig() throws IOException {
        new File("nacho.json").createNewFile();
        NachoConfig.init(new File("nacho.yml"));
    }

    @Test
    public void loadConfig() {
        NachoConfig.init(new File("nacho.yml"));
    }

    // Makes sure that c.addComment is not called in any config init functions.
    @Test
    public void noAddCommentInConfigInit() throws IOException {
        ClassReader cr = new ClassReader(NachoConfig.class.getName());
        cr.accept(new TestUtils.ConfigClassVisitor(Opcodes.ASM9), ClassReader.SKIP_FRAMES);
    }
}
