package com.jizhi.learn.asm4.writeclass;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;

import static org.objectweb.asm.Opcodes.*;

public class ClassWriterDemo {

    public byte[] createClass(){
        ClassWriter classWriter = new ClassWriter(0);

        //visit(final int version, final int access,final String name, final String signature, final String superName,final String[] interfaces)
        classWriter.visit(V1_5, ACC_PUBLIC,"UserService_Stub", null, "java/lang/Object",null);

        //visitField(final int access, final String name,final String desc, final String signature, final Object value)
        classWriter.visitField(ACC_PUBLIC + ACC_FINAL + ACC_STATIC,"LESS","I",null,new Integer(-1)).visitEnd();

        //final int access, final String name,final String desc, final String signature, final String[] exceptions
        MethodVisitor methodVisitor = classWriter.visitMethod(ACC_PUBLIC + ACC_ABSTRACT , "sayHello","(Ljava/lang/String;)Ljava/lang/String;", null, null);
        methodVisitor.visitCode();

        classWriter.visitEnd();

        byte[] bytes = classWriter.toByteArray();
        return bytes;
    }

    public static void main(String[] args) {
        SubClassLoader subClassLoader = new SubClassLoader();
        try {
            Class clazz = subClassLoader.findClass("UserService_Stub");
            System.out.println(clazz.toString());
//            Object obj = clazz.newInstance();

//            Method method = clazz.getMethod("sayHello");
//            Object result = method.invoke(obj);
//            System.out.println(clazz.toString() + " sayHello:"+ result);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
