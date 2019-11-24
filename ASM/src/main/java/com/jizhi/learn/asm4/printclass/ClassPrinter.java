package com.jizhi.learn.asm4.printclass;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;

import static org.objectweb.asm.Opcodes.ASM4;

public class ClassPrinter extends ClassVisitor {
    public ClassPrinter() {
        super(ASM4);
    }

    @Override
    public void visit(int version,int access,String name,String signature,String superName,String[] interfaces){
        System.out.println(name + " extends " + superName + " {");
    }

    public MethodVisitor visitMethod(int access, String name,String desc, String signature, String[] exceptions) {
        System.out.println(" " + name + desc);
        return null;
    }
}
