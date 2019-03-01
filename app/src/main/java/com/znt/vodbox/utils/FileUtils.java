package com.znt.vodbox.utils;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;
import android.util.Log;

import com.znt.vodbox.R;
import com.znt.vodbox.application.AppCache;
import com.znt.vodbox.model.Music;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * 文件工具类
 * Created by wcy on 2016/1/3.
 */
public class FileUtils {
    private static final String MP3 = ".mp3";
    private static final String LRC = ".lrc";

    /**
     * 复制文件到SD卡
     * @param context
     * @param fileName 复制的文件名
     * @param path  保存的目录路径
     * @return
     */
    public static Uri copyAssetsFile(Context context, String fileName, String path) {
        try {
            InputStream mInputStream = context.getAssets().open(fileName);
            File file = new File(path);
            if (!file.exists()) {
                file.mkdir();
            }
            File mFile = new File(path + File.separator + "DianYinGuanJia.apk");
            if(mFile.exists())
                mFile.delete();

            if(!mFile.exists())
                mFile.createNewFile();
            long s = mFile.length();
            FileOutputStream mFileOutputStream = new FileOutputStream(mFile);
            byte[] mbyte = new byte[1024];
            int i = 0;
            while((i = mInputStream.read(mbyte)) > 0){
                mFileOutputStream.write(mbyte, 0, i);
            }
            mInputStream.close();
            mFileOutputStream.close();
            Uri uri = null;
            try{
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
                    //包名.fileprovider
                    String pkgName = context.getPackageName();
                    uri = FileProvider.getUriForFile(context, pkgName + ".fileprovider", mFile);
                }else{
                    uri = Uri.fromFile(mFile);
                }
            }catch (ActivityNotFoundException anfe){

            }
            MediaScannerConnection.scanFile(context, new String[]{mFile.getAbsolutePath()}, null, null);

            return uri;
        } catch (IOException e) {
            e.printStackTrace();

            return null;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 安装apk
     * @param uri apk存放的路径
     * @param context
     */
    public static void openApk(Uri uri, Context context) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        if (Build.VERSION.SDK_INT >= 24) {
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.setDataAndType(uri, "application/vnd.android.package-archive");
        } else {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        intent.setDataAndType(uri, "application/vnd.android.package-archive");
        context.startActivity(intent);
    }

    private static String getAppDir() {
        return Environment.getExternalStorageDirectory() + "/PonyMusic";
    }

    public static String getMusicDir() {
        String dir = getAppDir() + "/Music/";
        return mkdirs(dir);
    }

    public static String getLrcDir() {
        String dir = getAppDir() + "/Lyric/";
        return mkdirs(dir);
    }

    public static String getAlbumDir() {
        String dir = getAppDir() + "/Album/";
        return mkdirs(dir);
    }

    public static String getLogDir() {
        String dir = getAppDir() + "/Log/";
        return mkdirs(dir);
    }

    public static String getSplashDir(Context context) {
        String dir = context.getFilesDir() + "/splash/";
        return mkdirs(dir);
    }

    public static String getRelativeMusicDir() {
        String dir = "PonyMusic/Music/";
        return mkdirs(dir);
    }

    public static String getCorpImagePath(Context context) {
        return context.getExternalCacheDir() + "/corp.jpg";
    }

    /**
     * 获取歌词路径<br>
     * 先从已下载文件夹中查找，如果不存在，则从歌曲文件所在文件夹查找。
     *
     * @return 如果存在返回路径，否则返回null
     */
    public static String getLrcFilePath(Music music) {
        if (music == null) {
            return null;
        }

        String lrcFilePath = getLrcDir() + getLrcFileName(music.getArtist(), music.getTitle());
        if (!exists(lrcFilePath)) {
            lrcFilePath = music.getPath().replace(MP3, LRC);
            if (!exists(lrcFilePath)) {
                lrcFilePath = null;
            }
        }
        return lrcFilePath;
    }

    private static String mkdirs(String dir) {
        File file = new File(dir);
        if (!file.exists()) {
            file.mkdirs();
        }
        return dir;
    }

    private static boolean exists(String path) {
        File file = new File(path);
        return file.exists();
    }

    public static String getMp3FileName(String artist, String title) {
        return getFileName(artist, title) + MP3;
    }

    public static String getLrcFileName(String artist, String title) {
        return getFileName(artist, title) + LRC;
    }

    public static String getAlbumFileName(String artist, String title) {
        return getFileName(artist, title);
    }

    public static String getFileName(String artist, String title) {
        artist = stringFilter(artist);
        title = stringFilter(title);
        if (TextUtils.isEmpty(artist)) {
            artist = AppCache.get().getContext().getString(R.string.unknown);
        }
        if (TextUtils.isEmpty(title)) {
            title = AppCache.get().getContext().getString(R.string.unknown);
        }
        return artist + " - " + title;
    }

    public static String getArtistAndAlbum(String artist, String album) {
        if (TextUtils.isEmpty(artist) && TextUtils.isEmpty(album)) {
            return "";
        } else if (!TextUtils.isEmpty(artist) && TextUtils.isEmpty(album)) {
            return artist;
        } else if (TextUtils.isEmpty(artist) && !TextUtils.isEmpty(album)) {
            return album;
        } else {
            return artist + " - " + album;
        }
    }

    /**
     * 过滤特殊字符(\/:*?"<>|)
     */
    private static String stringFilter(String str) {
        if (str == null) {
            return null;
        }
        String regEx = "[\\/:*?\"<>|]";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(str);
        return m.replaceAll("").trim();
    }

    public static float b2mb(int b) {
        String mb = String.format(Locale.getDefault(), "%.2f", (float) b / 1024 / 1024);
        return Float.valueOf(mb);
    }

    public static void saveLrcFile(String path, String content) {
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(path));
            bw.write(content);
            bw.flush();
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * @Description: 创建文件
     * @param @param fileUrl
     * @param @return
     * @return int
     * @throws
     */
    public static int createFile(String fileUrl)
    {
        String tempStr = fileUrl;
        if(tempStr.contains("/"))
            tempStr = StringUtils.getHeadByTag("/", tempStr);
        File tempDir = new File(tempStr);
        if(!tempDir.exists())
        {
            if(!tempDir.mkdirs())
                return 1;
        }

        File tempFile = new File(fileUrl);
        if(tempFile.exists())
            tempFile.delete();
        try
        {
            if(tempFile.createNewFile())
                return 0;
        } catch (IOException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return 1;
    }

    public static void copyAssetFileToLocal(Context context, String assetsPath, String desPath) throws IOException {
        String[] srcFiles = context.getAssets().list(assetsPath);//for directory
        for (String srcFileName : srcFiles) {
            String outFileName = desPath + File.separator + srcFileName;
            String inFileName = assetsPath + File.separator + srcFileName;
            if (assetsPath.equals("")) {// for first time
                inFileName = srcFileName;
            }
            Log.e("tag","========= assets: "+ assetsPath+"  filename: "+srcFileName +" infile: "+inFileName+" outFile: "+outFileName);
            try {
                InputStream inputStream = context.getAssets().open(inFileName);
                copyAndClose(inputStream, new FileOutputStream(outFileName));
            } catch (IOException e) {//if directory fails exception
                e.printStackTrace();
                new File(outFileName).mkdir();
                copyAssetFileToLocal(context,inFileName, outFileName);
            }

        }
    }

    private static void closeQuietly(OutputStream out){
        try{
            if(out != null) out.close();;
        }catch (IOException ex){
            ex.printStackTrace();
        }
    }

    private static void closeQuietly(InputStream is){
        try{
            if(is != null){
                is.close();
            }
        }catch (IOException ex){
            ex.printStackTrace();
        }
    }

    private static void copyAndClose(InputStream is, OutputStream out) throws IOException{
        copy(is,out);
        closeQuietly(is);
        closeQuietly(out);
    }

    private static void copy(InputStream is, OutputStream out) throws IOException{
        byte[] buffer = new byte[1024];
        int n = 0;
        while(-1 != (n = is.read(buffer))){
            out.write(buffer,0,n);
        }
    }

    /**
     * @Description: MP4文件
     * @param @param path
     * @param @return
     * @return boolean
     * @throws
     */
    public static boolean isVideo(String path)
    {
        if(path == null || path.length() == 0)
            return false;
        if(path.toLowerCase().endsWith(".mp4") ||
                path.toLowerCase().endsWith(".264") ||
                path.toLowerCase().endsWith(".3gp") ||
                path.toLowerCase().endsWith(".wmv") ||
                path.toLowerCase().endsWith(".263") ||
                path.toLowerCase().endsWith(".h264") ||
                path.toLowerCase().endsWith(".rmvb") ||
                path.toLowerCase().endsWith(".mts") ||
                path.toLowerCase().endsWith(".flv"))
        {
            return true;
        }
        return false;
    }

    /**
     * @Description: MP3文件
     * @param @param path
     * @param @return
     * @return boolean
     * @throws
     */
    public static boolean isMusic(String path)
    {
        if(path == null || path.length() == 0)
            return false;
        if(path.toLowerCase().endsWith(".mp3") ||
                path.toLowerCase().endsWith(".wav") ||
                path.toLowerCase().endsWith(".pcm") ||
                path.toLowerCase().endsWith(".wma") ||
                path.toLowerCase().endsWith(".flac") ||
                path.toLowerCase().endsWith(".aac"))
        {
            return true;
        }
        return false;
    }

    /**
     * @Description: 图片文件
     * @param @param path
     * @param @return
     * @return boolean
     * @throws
     */
    public static boolean isPicture(String path)
    {
        if(path == null || path.length() == 0)
            return false;
        if(path.toLowerCase().endsWith(".jpg") ||
                path.toLowerCase().endsWith(".png") ||
                path.toLowerCase().endsWith(".bmp") ||
                path.toLowerCase().endsWith(".gif"))
        {
            return true;
        }
        return false;
    }

}
