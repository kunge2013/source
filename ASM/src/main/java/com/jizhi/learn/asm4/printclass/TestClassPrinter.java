package com.jizhi.learn.asm4.printclass;

import org.objectweb.asm.ClassReader;

import java.io.IOException;

public class TestClassPrinter {

    public void testClassPrinter(){
        try {
            ClassReader classReader = new ClassReader("UserService");
            classReader.accept(new ClassPrinter(),0);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        TestClassPrinter testClassPrinter = new TestClassPrinter();
        testClassPrinter.testClassPrinter();
    }
}
