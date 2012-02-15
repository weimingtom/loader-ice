package com.ebensz.games.utils;

import android.content.Context;
import android.os.Environment;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class FileUtil {
    public static final String EBEN_APP_PATH = "/AppFiles/";
    private static final String EBEN_DOC_PATH = "/我的E人E本文档/";
    private static String currFile = "";

    public static File homePath(Context context) {

        File storageDir = Environment.getExternalStorageDirectory();
        String packageName = context.getPackageName();

        return new File(storageDir, EBEN_APP_PATH + packageName);
    }

    public static File ebenDocPath() {

        File storageDir = Environment.getExternalStorageDirectory();

        File file = new File(storageDir, EBEN_DOC_PATH);
        if (!file.exists()) file.mkdir();

        return file;
    }

    public static boolean writeData(File file, String content) {

        PrintWriter writer = null;

        try {
            writer = new PrintWriter(file);
            writer.println(content);
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        }
        finally {
            if (writer != null) writer.close();
        }

        return true;
    }

    public static List<String> readLines(Context context, String filePath) {

        BufferedReader reader = null;
        List<String> lines = new ArrayList<String>();

        try {
            reader = new BufferedReader(
                    new InputStreamReader(
                            context.getAssets().open(filePath)
                    )
            );

            String line = reader.readLine();
            while (line != null) {
                lines.add(line);
                line = reader.readLine();
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            if (reader != null) try {
                reader.close();
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }

        return lines;
    }

    /**
     * 获取机器ID
     *
     * @return
     */
    public static String getTerminateSn() {
        final String cpuInfoFilePath = "/proc/cpuinfo";
        BufferedReader reader = null;
        String pcId = "";
        try {
            reader = new BufferedReader(new FileReader(cpuInfoFilePath));
            StringBuffer buffer = asStringBuffer(reader);

            pcId = buffer.toString();
            pcId = pcId.substring(pcId.lastIndexOf(":") + 1);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            if (reader != null)
                try {
                    reader.close();
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
        }

        return pcId.trim();
    }

    private static StringBuffer asStringBuffer(BufferedReader reader) {
        StringBuffer buffer = new StringBuffer();

        try {
            String line = reader.readLine();
            while (line != null) {
                buffer.append(line).append('\n');
                line = reader.readLine();
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return buffer;
    }

    public static String getContent(Context context, String filePath) {
        currFile = filePath;
        byte[] buffer = new byte[819200];

        int length = readBuffer(context, buffer);
        if (length <= 0) return null;

        return loadString(buffer, length);
    }

    private static int readBuffer(Context context, byte[] buffer) {

        InputStream is = null;

        try {
            is = context.getAssets().open(currFile);
            return is.read(buffer);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            if (is != null) try {
                is.close();
            }
            catch (IOException ignored) {
            }
        }

        return 0;
    }

    private static String loadString(byte[] buffer, int length) {

        try {
            return new String(
                    buffer,
                    0,
                    length,
                    "GBK"
            );
        }
        catch (UnsupportedEncodingException ignored) {
        }

        return null;
    }
}
