package com.example.jeremysun.temptest;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by jeremysun on 2018/4/27.
 */

public class AssetsUtils {

    /**
     * 从Assets中获取Zip文件的文件名
     *
     * @param context
     * @return 文件名list
     */
    public static List<String> getZipFileNameListFromAssets(Context context) {
        List<String> list = new ArrayList<>();
        AssetManager assets = context.getAssets();
        try {
            String[] assetsList = assets.list("");
            if (assetsList.length > 0) {
                for (String filename : assetsList) {
                    if (filename.endsWith("zip")) {
                        list.add(filename);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            return list;
        }
    }

    /**
     * 根据文件名，从Assets中将Zip文件拷贝到File目录下
     *
     * @param context
     * @param fileName
     * @return Zip文件目录
     */
    public static String copyZipFileFromAssetsToFile(Context context, String fileName) {
        Log.d("sunwillfly", "copyZipFileFromAssetsToFile " + fileName);
        AssetManager assets = context.getAssets();
        if (fileName == null || fileName == "") {
            return "";
        }
        File src = new File(context.getFilesDir(), fileName);
        String path = "";
        // 如果旧的ZIP包存在，删除
        if (src.exists()) {
            src.delete();
        }
        try {
            src.createNewFile();
            InputStream open = assets.open(fileName);
            if (inputStreamToFile(open, src)) {
                path = src.getPath();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            return path;
        }
    }

    /**
     * io流转换成file
     *
     * @param ins
     * @param file
     * @return
     */
    private static boolean inputStreamToFile(InputStream ins, File file) {
        OutputStream os = null;
        boolean result = false;
        try {
            if (file != null && file.exists()) {
                os = new FileOutputStream(file);
                int bytesRead = 0;
                byte[] buffer = new byte[8192];
                while ((bytesRead = ins.read(buffer, 0, 8192)) != -1) {
                    os.write(buffer, 0, bytesRead);
                }
                result = true;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (os != null)
                    os.close();
                ins.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return result;
        }
    }



}
