package com.jizhi.learn.asm4.writeclass;

public class SubClassLoader extends ClassLoader {

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        if(name.endsWith("_Stub")){
            byte[] bytes = new ClassWriterDemo().createClass();
            return defineClass(name,bytes,0, bytes.length);
        }
        return super.findClass(name);
    }

}
