package www.mys.com.sbjni;

import org.springframework.stereotype.Component;
import org.springframework.util.ResourceUtils;
import www.mys.com.sbjni.utils.LogUtils;
import www.mys.com.sbjni.utils.file.CloseUtils;
import www.mys.com.sbjni.utils.file.FileUtils;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarInputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

@Component
public class InitJob {

    @PostConstruct
    public void initProject() {
        initLibs();
    }

    private void initLibs() {
        String ROOT_LIBS_DIR = new File("runLibs").getAbsolutePath();
        String os = System.getProperty("os.name");
        String model = System.getProperty("sun.arch.data.model");
        String LIB_DIR;
        //在idea下运行需要修改这个值
        boolean isIdea = false;
        if (os.toLowerCase().startsWith("win")) {
            System.out.println("system is win" + model);
            LIB_DIR = isIdea ? "classpath:libs/win" : "libs/win";
            addLibraryDir("", true);
        } else if (os.toLowerCase().startsWith("linux")) {
            System.out.println("system is linux" + model);
            LIB_DIR = isIdea ? "classpath:libs/lnx" : "libs/lnx";
            addLibraryDir(ROOT_LIBS_DIR, false);
        } else {
            System.out.println("not support system model=" + model);
            System.exit(1);
            return;
        }
        LogUtils.log("end java.library.path=" + System.getProperty("java.library.path"));
        FileUtils.sureDir(ROOT_LIBS_DIR);
        if (isIdea) {
            copyFilesIdea(ROOT_LIBS_DIR, LIB_DIR);
        } else {
            copyFiles(ROOT_LIBS_DIR, LIB_DIR);
        }
    }

    private void copyFiles(String ROOT_LIBS_DIR, String LIB_DIR) {
        URL url = InitJob.class.getResource("/");
        URLConnection con;
        try {
            con = url.openConnection();
        } catch (Exception e) {
            LogUtils.log("url.openConnection error.e=" + e);
            return;
        }
        if (con instanceof JarURLConnection) {
            JarURLConnection result = (JarURLConnection) con;
            JarFile jarFile;
            try {
                jarFile = result.getJarFile();
            } catch (Exception e) {
                LogUtils.log("url.openConnection error.e=" + e);
                return;
            }
            Enumeration<JarEntry> e = jarFile.entries();
            String filePath;
            LogUtils.log("start list files.");
            while (e.hasMoreElements()) {
                JarEntry entry = e.nextElement();
                filePath = entry.getName();
                if (filePath.startsWith(LIB_DIR) && !entry.isDirectory()) {
                    copyFile(ROOT_LIBS_DIR, filePath);
                }
            }
            LogUtils.log("end list files.");
        } else {
            LogUtils.log("con not instanceof JarURLConnection.con=" + con);
        }
        try {
        } catch (Exception e) {
            LogUtils.log("list files error.e=" + e);
        }
    }

    private void copyFile(String ROOT_LIBS_DIR, String filePath) {
        String[] tempStrs = filePath.split("/");
        String childName = tempStrs[tempStrs.length - 1];
        File tempFile = new File(ROOT_LIBS_DIR, childName);
        if (tempFile.exists()) {
            LogUtils.log("file already exist.tempFile=" + tempFile);
        } else {
            if (!filePath.startsWith("/")) {
                filePath = "/" + filePath;
            }
            InputStream inputStream;
            try {
                inputStream = InitJob.class.getResourceAsStream(filePath);
            } catch (Exception e) {
                LogUtils.log("getResourceAsStream error.e=" + e);
                return;
            }
            FileUtils.sureFileIsNew(tempFile.getAbsolutePath());
            FileUtils.inputStream2File(inputStream, tempFile);
            CloseUtils.closeSilently(inputStream);
        }
    }

    private void copyFilesIdea(String ROOT_LIBS_DIR, String LIB_DIR) {
        File file = null;
        try {
            file = ResourceUtils.getFile(LIB_DIR);
        } catch (Exception e) {
            LogUtils.log("getResourceFile error.e=" + e);
        }
        if (file != null && file.exists()) {
            File[] files = file.listFiles();
            if (files != null) {
                File tempFile;
                InputStream inputStream;
                for (File childFile : files) {
                    LogUtils.log("childFile=" + childFile);
                    tempFile = new File(ROOT_LIBS_DIR, childFile.getName());
                    if (!tempFile.exists()) {
                        try {
                            inputStream = new FileInputStream(childFile);
                        } catch (Exception e) {
                            LogUtils.log("new FileInputStream error.e=" + e);
                            continue;
                        }
                        FileUtils.sureFileIsNew(tempFile.getAbsolutePath());
                        FileUtils.inputStream2File(inputStream, tempFile);
                        CloseUtils.closeSilently(inputStream);
                    } else {
                        LogUtils.log("file already exist.tempFile=" + tempFile);
                    }
                }
            }
        } else {
            LogUtils.log("file not exist.");
        }
    }

    private void addLibraryDir(String libraryPath, boolean isWin) {
        Field userPathsField;
        try {
            userPathsField = ClassLoader.class.getDeclaredField("usr_paths");
        } catch (Exception e) {
            LogUtils.log("ClassLoader.class.getDeclaredField(\"usr_paths\") error.e=" + e);
            return;
        }
        userPathsField.setAccessible(true);
        String[] paths;
        try {
            paths = (String[]) userPathsField.get(null);
        } catch (Exception e) {
            LogUtils.log("userPathsField.get(null) error.e=" + e);
            return;
        }
        StringBuilder sb = new StringBuilder();
        for (String path : paths) {
            if (libraryPath.equals(path)) {
                continue;
            }
            sb.append(path).append(isWin ? ';' : ':');
        }
        sb.append(libraryPath);
        System.setProperty("java.library.path", sb.toString());
        final Field sysPathsField;
        try {
            sysPathsField = ClassLoader.class.getDeclaredField("sys_paths");
        } catch (Exception e) {
            LogUtils.log("ClassLoader.class.getDeclaredField(\"sys_paths\") error.e=" + e);
            return;
        }
        sysPathsField.setAccessible(true);
        try {
            sysPathsField.set(null, null);
        } catch (Exception e) {
            LogUtils.log("sysPathsField.set(null, null) error.e=" + e);
        }
    }

}
