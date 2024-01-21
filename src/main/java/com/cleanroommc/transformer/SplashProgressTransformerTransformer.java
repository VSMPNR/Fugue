package com.cleanroommc.transformer;

import com.cleanroommc.Fugue;
import com.cleanroommc.FugueLoadingPlugin;
import net.minecraft.launchwrapper.IClassTransformer;
import net.minecraft.launchwrapper.Launch;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

public class SplashProgressTransformerTransformer implements IClassTransformer {
    
    public SplashProgressTransformerTransformer() {
        FugueLoadingPlugin.registerToKnownTransformer("pl.asie.splashanimation.SplashAnimationRenderer", this);
    }
    @Override
    public byte[] transform(String s, String s1, byte[] bytes) {
        if (bytes == null)
        {
            return null;
        }

        if (!s1.equals("pl.asie.splashanimation.core.SplashProgressTransformer"))
        {
            return bytes;
        }

        ClassNode classNode = new ClassNode();
        ClassReader classReader = new ClassReader(bytes);
        classReader.accept(classNode, 0);
        boolean modified = false;
        if (classNode.methods != null)
        {
            for (MethodNode methodNode : classNode.methods)
            {
                if (methodNode.name.equals("patch")) {
                    InsnList instructions = methodNode.instructions;
                    if (instructions != null)
                    {
                        for (AbstractInsnNode insnNode : instructions)
                        {
                            if (insnNode.getOpcode() == Opcodes.SIPUSH && insnNode instanceof IntInsnNode intInsnNode)
                            {
                                if (intInsnNode.operand == Opcodes.INVOKESPECIAL)
                                {
                                    intInsnNode.operand = Opcodes.INVOKEVIRTUAL;
                                    modified = true;
                                }
                            }
                        }
                    }
                }
            }
        }
        if (modified)
        {
            Launch.classLoader.unRegisterSuperTransformer(this);
            ClassWriter classWriter = new ClassWriter(0);

            classNode.accept(classWriter);
            return classWriter.toByteArray();
        }
        return bytes;
    }
}
