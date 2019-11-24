package asm;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.LineNumberReader;

import org.redkale.util.Utility;


public class AsmTest {

    public static void main(String[] args) throws Throwable {
        boolean realasm = false; //从http://forge.ow2.org/projects/asm/ 下载最新asm的src放在 srcasmroot 目录下
        File srcasmroot = new File("/home/fk/eclipse-workspace/source/redkale/asm/src/org/redkale/asm");
        if (realasm) srcasmroot = new File("/home/fk/eclipse-workspace/source/redkale/asm/src/org/redkale/asm");
        File destasmroot = new File("/home/fk/eclipse-workspace/source/redkale/asm/src/org/redkale/asm");
        String line = null;
        LineNumberReader txtin = new LineNumberReader(new FileReader(new File(destasmroot, "asm.txt")));
        while ((line = txtin.readLine()) != null) {
            line = line.trim();
            if (!line.endsWith(".java")) continue;
            File srcfile = new File(srcasmroot, line);
            if (!srcfile.isFile()) continue;
            File destfile = new File(destasmroot, line);
            String content = Utility.readThenClose(new FileInputStream(srcfile));
            FileOutputStream out = new FileOutputStream(destfile);
            out.write(content.replace("jdk.internal.org.objectweb", "org.redkale").replace("org.objectweb", "org.redkale").getBytes());
            out.close();
        }
        //需要屏蔽ClassReader中判断checks the class version的部分
    }
}
