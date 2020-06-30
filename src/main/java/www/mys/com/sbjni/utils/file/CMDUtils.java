package www.mys.com.sbjni.utils.file;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class CMDUtils {
    private static boolean mHaveRoot = false;

    public static boolean haveRoot() {
        if (!mHaveRoot) {
            mHaveRoot = execRootCmdSilent("echo test") != -1;
        }
        return mHaveRoot;
    }

    public static String runRoot(String cmd) {
        StringBuilder stringBuilder = new StringBuilder();
        DataOutputStream dos = null;
        DataInputStream dis = null;
        try {
            Process p = Runtime.getRuntime().exec("su");
            dos = new DataOutputStream(p.getOutputStream());
            dis = new DataInputStream(p.getInputStream());
            dos.writeBytes(cmd + "\n");
            dos.flush();
            dos.writeBytes("exit\n");
            dos.flush();
            String line;
            while ((line = dis.readLine()) != null) {
                stringBuilder.append(line).append("\n");
            }
            p.waitFor();
        } catch (Exception e) {
            System.out.println("e=" + e);
        } finally {
            closeSilently(dos);
            closeSilently(dis);
        }
        System.out.println("run root cmd=" + cmd + "\n" + stringBuilder);
        return stringBuilder.toString();
    }

    private static int execRootCmdSilent(String cmd) {
        int result = -1;
        DataOutputStream dos = null;
        try {
            Process p = Runtime.getRuntime().exec("su");
            dos = new DataOutputStream(p.getOutputStream());
            dos.writeBytes(cmd + "\n");
            dos.flush();
            dos.writeBytes("exit\n");
            dos.flush();
            p.waitFor();
            result = p.exitValue();
        } catch (Exception e) {
            System.out.println("e=" + e);
        } finally {
            closeSilently(dos);
        }
        return result;
    }

    public static String run(final String str) {
        return run(new ArrayList<String>() {{
            add(str);
        }});
    }

    public static String run(List<String> strs) {
        StringBuilder result = new StringBuilder();
        if (strs != null && strs.size() > 0) {
            Runtime runtime = Runtime.getRuntime();
            for (String str : strs) {
                commonRun(result, runtime, str);
            }
        }
        return result.toString();
    }

    private static void commonRun(StringBuilder result, Runtime runtime, String str) {
        Process process;
        try {
            process = runtime.exec(str);
        } catch (Exception e) {
            System.out.println("e=" + e);
            return;
        }
        InputStream inputStream = process.getInputStream();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byte[] temp = new byte[8192];
        int len;
        try {
            while ((len = inputStream.read(temp)) > 0) {
                byteArrayOutputStream.write(temp, 0, len);
            }
            result.append(byteArrayOutputStream).append("\n");
        } catch (Exception e) {
            System.out.println("e=" + e);
        } finally {
            closeSilently(inputStream);
        }
        if (result.length() == 0) {
            inputStream = process.getErrorStream();
            byteArrayOutputStream = new ByteArrayOutputStream();
            try {
                while ((len = inputStream.read(temp)) > 0) {
                    byteArrayOutputStream.write(temp, 0, len);
                }
                result.append(byteArrayOutputStream).append("\n");
            } catch (Exception e) {
                System.out.println("e=" + e);
            } finally {
                closeSilently(inputStream);
            }
        }
    }

    private static void closeSilently(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (Exception e) {
                System.out.println("e=" + e);
            }
        }
    }

}
