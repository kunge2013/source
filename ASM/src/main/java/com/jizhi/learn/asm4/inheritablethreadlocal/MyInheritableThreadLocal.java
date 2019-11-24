package com.jizhi.learn.asm4.inheritablethreadlocal;

public class MyInheritableThreadLocal<T> extends InheritableThreadLocal<T> {

    @Override
    protected T childValue(T parentValue) {

        System.out.println("MyInheritableThreadLocal -> " + parentValue);

        if(parentValue instanceof Context){
            Context parentContext = (Context) parentValue;
            Context subContext = (Context) parentContext.clone();
            return (T) subContext;
        }

        return parentValue;
    }

}
