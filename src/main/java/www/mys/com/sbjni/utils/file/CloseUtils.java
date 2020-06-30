package www.mys.com.sbjni.utils.file;

import www.mys.com.sbjni.utils.LogUtils;

import java.io.Closeable;

public class CloseUtils {

    public static void closeSilently(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (Exception e) {
                LogUtils.log("e=" + e);
            }
        }
    }

    public static void closeReader(java.io.Reader reader) {
        if (reader != null) {
            try {
                reader.close();
            } catch (Exception e) {
                LogUtils.log("e=" + e);
            }
        }
    }

}
