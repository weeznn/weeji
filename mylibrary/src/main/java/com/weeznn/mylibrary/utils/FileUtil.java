package com.weeznn.mylibrary.utils;

import android.content.res.AssetManager;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.os.Environment;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.PrintStream;

/**
 * Created by weeznn on 2018/4/2.
 */

public class FileUtil {
    private static final String TAG = FileUtil.class.getSimpleName();

    public static final String APPBASEPATH = Environment.getExternalStorageDirectory().getAbsolutePath() + "/WeeJi/";
    public static final String FILE_TYPE_MEETING = "MEETING";
    public static final String FILE_TYPE_DAIRY = "DAIRY";
    public static final String FILE_TYPE_NOTE = "NOTE";

    /**
     * 创建文件夹
     *
     * @param dirPath
     * @return
     */
    public static boolean makeDir(String dirPath) {
        Log.i(TAG,"makeDir :"+dirPath);
        File file = new File(dirPath);
        if (!file.exists()) {
            return file.mkdirs();
        } else {
            return true;
        }
    }

    /**
     * 删除文件
     * @param type
     * @param name
     */
    public static void deleteFile(String type,String name){
        String path=APPBASEPATH+type+"/"+name;
        Log.i(TAG,"deleteFile :"+path);
        File file=new File(path);
        if (file.exists()){
            file.delete();
        }
    }


    public static String getContentFromAssetsFile(AssetManager assets, String source) {
        InputStream is = null;
        FileOutputStream fos = null;
        String result = "";
        try {
            is = assets.open(source);
            int lenght = is.available();
            byte[] buffer = new byte[lenght];
            is.read(buffer);
            result = new String(buffer, "utf8");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 从Assets中复制文件
     *
     * @param assets
     * @param source
     * @return
     */
    public static boolean copyFromAssets(AssetManager assets, String source, String dest,
                                         boolean isCover) throws IOException {
        File file = new File(dest);
        boolean isCopyed = false;
        if (isCover || (!isCover && !file.exists())) {
            InputStream is = null;
            FileOutputStream fos = null;
            try {
                is = assets.open(source);
                String path = dest;
                fos = new FileOutputStream(path);
                byte[] buffer = new byte[1024];
                int size = 0;
                while ((size = is.read(buffer, 0, 1024)) >= 0) {
                    fos.write(buffer, 0, size);
                }
                isCopyed = true;
            } finally {
                if (fos != null) {
                    try {
                        fos.close();
                    } finally {
                        if (is != null) {
                            is.close();
                        }
                    }
                }
            }

        }
        return isCopyed;
    }

    /**
     * 读文本
     *
     * @param type
     * @param fileName
     * @return
     */
    public static String ReadText(String type, String fileName) {
        String path = APPBASEPATH + type + "/" + fileName + "/" + fileName + ".txt";
        Log.i(TAG,"read text :"+path);
        File file = new File(path);
        StringBuilder builder = new StringBuilder();
        try {
            InputStream inputStream = new FileInputStream(file);
            BufferedInputStream stream = new BufferedInputStream(inputStream);
            int readcound;
            byte[] buffer = new byte[1024];
            while ((readcound = stream.read(buffer)) != -1) {
                builder.append(buffer);
            }
            stream.close();
            inputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.i(TAG,"read text resoule:"+builder.toString());
        return builder.toString();
    }

    /**
     * 写文本
     *
     * @param type
     * @param fileName
     * @param data
     */
    public static void WriteText(String type, String fileName, String data) {
        String path = APPBASEPATH + type + "/" + fileName + "/" + fileName + ".txt";
        Log.i(TAG,"write text :"+path);
        try {
            FileWriter fileWriter = new FileWriter(path);
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
            bufferedWriter.write(data);
            bufferedWriter.flush();
            bufferedWriter.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 写入音频PCM文件
     *
     * @param type  数据类型（会议/日记/笔记）
     * @param fileName  文件名
     * @param data  数据
     * @param length    数据长度
     * @param offset    追加的位置
     * @throws FileNotFoundException
     */
    public static void WriteAudio(String type, String fileName, byte[] data, int length, int offset) throws FileNotFoundException {
        String path=APPBASEPATH + type + "/" +
                fileName + "/" +
                fileName + ".pcm";
        Log.i(TAG,"write  audio :"+path);

        File file = new File(path);
        FileOutputStream stream = null;
        if (!file.exists()) {
            try {
                file.createNewFile();
                stream = new FileOutputStream(file);
                stream.write(data);
                stream.flush();
                stream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            stream = new FileOutputStream(file, true);
            try {
                stream.write(data, offset, length);
                stream.flush();
                stream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    /**
     * 读取音频文件
     * @param path  文件路径
     * @param offset    读取开始的地方
     * @param buffer    读取的大小
     */
    public static byte[] ReadAudio(String path,long offset,int buffer){
        Log.i(TAG,"read audio :"+path);

        byte[] data=null;
        File file=new File(path);
        if (!file.exists()){
            //文件未找到
            return data;
        }

        try {

            InputStream in=new FileInputStream(file);
            in.read(data,(int) offset,buffer);
            in.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return data;
    }
}