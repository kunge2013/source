package com.jizhi.learn.asm4.inheritablethreadlocal;

import java.util.LinkedList;

public class Context implements Cloneable{
    LinkedList<String> cache = new LinkedList<String>();

    @Override
    public Object clone(){
        Context context = null;
        try {
            context = (Context) super.clone();
            context.cache = (LinkedList<String>) cache.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return context;
    }
}
