package com.jizhi.learn.asm4.rewriteclass;

import jdk.internal.org.objectweb.asm.ClassReader;

import java.io.IOException;

public class RewriteClassTest {

    public void rewriteClass(){
        try {
            ClassReader classReader = new ClassReader("UserService");

            ClassReWriter classReWriter = new ClassReWriter(0);

//            classReader.accept(classReWriter,0);

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static void main(String[] args) {

    }
}
