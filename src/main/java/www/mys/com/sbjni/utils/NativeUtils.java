package www.mys.com.sbjni.utils;

import www.mys.com.sbjni.InitJob;

import java.io.File;

public class NativeUtils {

    static {
        try {
            System.load(InitJob.ROOT_LIBS_DIR
                    + File.separatorChar + "NativeUtils"
                    + InitJob.LIB_END);
        } catch (UnsatisfiedLinkError e) {
            LogUtils.log("System.load error.e=" + e);
            System.exit(1);
        }
    }

    public static void main(String[] args) {
        System.out.println("test");
        String jniStr = getJniStr();
        System.out.println("jniStr=" + jniStr);
    }

    public static native String getJniStr();

}
