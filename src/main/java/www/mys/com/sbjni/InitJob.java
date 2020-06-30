package www.mys.com.sbjni;

import org.springframework.stereotype.Component;
import www.mys.com.sbjni.utils.LogUtils;
import www.mys.com.sbjni.utils.file.CloseUtils;
import www.mys.com.sbjni.utils.file.FileUtils;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

@Component
public class InitJob {

    private static final HashMap<String, String> LIB_PATHS = new HashMap<String, String>() {{
        put("NativeUtils64.so", "/libs/NativeUtils64.so");
        put("NativeUtils64.dll", "/libs/NativeUtils64.dll");
    }};
    public static String ROOT_LIBS_DIR, LIB_END;

    @PostConstruct
    public void initProject() {
        initLibs();
    }

    private void initLibs() {
        File runLibPath = new File("runLibs");
        ROOT_LIBS_DIR = runLibPath.getAbsolutePath();
        FileUtils.sureDir(runLibPath.getAbsolutePath());
        String javaLibraryPath = System.getProperty("java.library.path");
        String os = System.getProperty("os.name");
        String model = System.getProperty("sun.arch.data.model");
        LogUtils.log("javaLibraryPath=" + javaLibraryPath);
        if (os.toLowerCase().startsWith("win")) {
            System.out.println("system is win" + model);
            LIB_END = model + ".dll";
            System.setProperty("java.library.path", javaLibraryPath
                    + ";" + InitJob.ROOT_LIBS_DIR);
        } else if (os.toLowerCase().startsWith("linux")) {
            System.out.println("system is linux" + model);
            LIB_END = model + ".so";
            System.setProperty("java.library.path", javaLibraryPath
                    + ":" + InitJob.ROOT_LIBS_DIR);
        } else {
            System.out.println("not support system model=" + model);
            System.exit(1);
        }
        javaLibraryPath = System.getProperty("java.library.path");
        LogUtils.log("javaLibraryPath2=" + javaLibraryPath);
        FileUtils.sureDir(runLibPath.getAbsolutePath());
        InputStream inputStream = null;
        File tempFile;
        for (Map.Entry<String, String> kv : LIB_PATHS.entrySet()) {
            try {
                inputStream = InitJob.class.getResourceAsStream(kv.getValue());
                tempFile = new File(runLibPath, kv.getKey());
                if (!tempFile.exists()) {
                    FileUtils.sureFileIsNew(tempFile.getAbsolutePath());
                    FileUtils.inputStream2File(inputStream, tempFile);
                } else {
                    LogUtils.log("file already exist.tempFile=" + tempFile);
                }
            } catch (Exception e) {
                LogUtils.log("new FileInputStream(resourcesLib) error.e=" + e);
            } finally {
                CloseUtils.closeSilently(inputStream);
            }
        }
    }

}
