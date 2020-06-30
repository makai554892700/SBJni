package www.mys.com.sbjni.utils.file;

import www.mys.com.sbjni.utils.LogUtils;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.concurrent.ConcurrentHashMap;

public class AppendFileUtils {

    private static ConcurrentHashMap<String, AppendFileUtils> concurrentHashMap = new ConcurrentHashMap<>();

    private java.io.File fileName;
    private FileWriter fileWriter;
    private PrintWriter printWriter;
    private boolean isInited;

    private AppendFileUtils(java.io.File file) {
        this.fileName = file;
        try {
            fileWriter = new FileWriter(file, true);
        } catch (IOException e) {
            LogUtils.log("e=" + e);
            return;
        }
        printWriter = new PrintWriter(fileWriter);
        isInited = true;
    }

    public static synchronized AppendFileUtils getInstance(java.io.File file) {
        if (file == null || !file.exists()) {
            return null;
        }
        if (concurrentHashMap.containsKey(file.getAbsolutePath())) {
            return concurrentHashMap.get(file.getAbsolutePath());
        } else {
            AppendFileUtils appendFileUtils = new AppendFileUtils(file);
            concurrentHashMap.put(file.getAbsolutePath(), appendFileUtils);
            return appendFileUtils;
        }
    }

    //直接叠加数据
    public void appendString(String data) {
        if (isInited) {
            printWriter.print(data);
            printWriter.flush();
        }
    }

    //叠加一行数据
    public void appendLineString(String data) {
        if (isInited) {
            printWriter.println(data);
            printWriter.flush();
        }
    }

    //是否正确初始化
    public boolean isInited() {
        return isInited;
    }

    //每次用完一定要关闭
    public void endAppendFile() {
        if (isInited) {
            try {
                fileWriter.flush();
            } catch (Exception e) {
                LogUtils.log("e=" + e);
            }
            CloseUtils.closeSilently(printWriter);
            CloseUtils.closeSilently(fileWriter);
            concurrentHashMap.remove(fileName.getAbsolutePath());
            isInited = false;
        }
    }

}
