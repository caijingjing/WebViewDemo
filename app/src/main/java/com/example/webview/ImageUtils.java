package com.example.webview;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.text.TextUtils;
import android.widget.ImageView;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public class ImageUtils {

    private ImageUtils() {
    }

    public static Boolean isLoaded(ImageView imageView) {
        Boolean loaded = (Boolean) imageView.getTag(imageView.getId());
        if (loaded != null && loaded)
            return true;
        return false;

    }

    public static void clear(ImageView imageView) {
        imageView.setImageBitmap(null);
        imageView.setTag(imageView.getId(), false);
    }
//获取网络图片
    public static Bitmap readBitmapFromNetwork(URL url) {
        InputStream is = null;
        BufferedInputStream bis = null;
        Bitmap bmp = null;
        try {
            URLConnection conn = url.openConnection();
            conn.connect();
            is = conn.getInputStream();
            bis = new BufferedInputStream(is);
            bmp = BitmapFactory.decodeStream(bis);
        } catch (MalformedURLException e) {
        } catch (IOException e) {
        } finally {
            try {
                if (is != null)
                    is.close();
                if (bis != null)
                    bis.close();
            } catch (IOException e) {
            }
        }
        return bmp;
    }

    public static void resampleImageAndSaveToNewLocation(String pathInput, String pathOutput) throws Exception {
        // 1600
        resampleImageAndSaveToNewLocation(pathInput, pathOutput, 1600, 100);
    }
 //压缩图片
    public static void resampleImageAndSaveToNewLocation(String pathInput, String pathOutput, final int maxDim, final int quality)
            throws Exception {
        if (TextUtils.isEmpty(pathInput) || TextUtils.isEmpty(pathOutput))
            return;

        Bitmap bmp = resampleImage(pathInput, maxDim);
        if (bmp == null)
            return;

        File outputFile = new File(pathOutput);
        File dir = outputFile.getParentFile();
        if (dir != null && !dir.exists())
            dir.mkdirs();
        if (outputFile.exists())
            outputFile.delete();
        OutputStream out = new FileOutputStream(outputFile);
        bmp.compress(Bitmap.CompressFormat.JPEG, quality, out);
        out.flush();
        out.close();
        if (bmp != null && !bmp.isRecycled()) {
            bmp.recycle();
            bmp = null;
        }
    }

    public static Bitmap resampleImage(String path, int maxDim) throws Exception {

        BitmapFactory.Options bfo = new BitmapFactory.Options();
        bfo.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, bfo);

        BitmapFactory.Options optsDownSample = new BitmapFactory.Options();
        optsDownSample.inSampleSize = getClosestResampleSize(bfo.outWidth, bfo.outHeight, maxDim);

        Bitmap bmpt;
        try {
            bmpt = BitmapFactory.decodeFile(path, optsDownSample);
        }catch (OutOfMemoryError error)
        {
            error.printStackTrace();
            //出现异常就再缩放0.5
            return resampleImage(path, maxDim / 2);
        }
        catch (Exception e) 
        {
            e.printStackTrace();
            return resampleImage(path, maxDim / 2);
        }

        Matrix m = new Matrix();

        if (bmpt.getWidth() > maxDim || bmpt.getHeight() > maxDim) {
            BitmapFactory.Options optsScale = getResampling(bmpt.getWidth(), bmpt.getHeight(),
                    maxDim);
            m.postScale((float) optsScale.outWidth / (float) bmpt.getWidth(),
                    (float) optsScale.outHeight / (float) bmpt.getHeight());
        }

        return Bitmap.createBitmap(bmpt, 0, 0, bmpt.getWidth(), bmpt.getHeight(), m, true);
    }

    private static BitmapFactory.Options getResampling(int cx, int cy, int max) {
        float scaleVal = 1.0f;
        BitmapFactory.Options bfo = new BitmapFactory.Options();
        if (cx > cy) {
            scaleVal = (float) max / (float) cx;
        } else if (cy > cx) {
            scaleVal = (float) max / (float) cy;
        } else {
            scaleVal = (float) max / (float) cx;
        }
        bfo.outWidth = (int) (cx * scaleVal + 0.5f);
        bfo.outHeight = (int) (cy * scaleVal + 0.5f);
        return bfo;
    }

    private static int getClosestResampleSize(int cx, int cy, int maxDim) {
        // int max = Math.max(cx, cy);
        int min = Math.min(cx, cy);
        int resample = 1;
        for (resample = 1; resample < Integer.MAX_VALUE; resample++) {
            if (resample * maxDim > min) {
                resample--;
                break;
            }
        }
        if (resample > 0) {
            return resample;
        }
        return 1;
    }

    public static BitmapFactory.Options getBitmapDims(String path) throws Exception {
        BitmapFactory.Options bfo = new BitmapFactory.Options();
        bfo.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, bfo);
        return bfo;
    }

    public static byte[] getBytes(InputStream is) {
        try {
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            byte[] buf = new byte[1024];
            for (int i = 0; (i = is.read(buf)) > 0; ) {
                os.write(buf, 0, i);
            }
            os.close();
            return os.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

}