package ir.fekrafarinan.yademman.Leitner.Utils;


import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.util.Log;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.File;
import java.io.FileOutputStream;

import ir.fekrafarinan.yademman.R;

public class ImageDownloader {
    private static String RAW_URL;
    private static final String ROOT_DIRECTORY = "Leitner/Card Images/";

    public static Bitmap downloadImage(String IMAGE_NAME, Context context) {
        Bitmap myBitmap;
        BitmapFactory.Options options;
        options = new BitmapFactory.Options();
        options.inSampleSize = 4;
        File imgFile = new File(Environment.getExternalStorageDirectory().getPath()
                + "/" + ROOT_DIRECTORY + IMAGE_NAME + ".jpg");
        if (imgFile.exists()) {
            myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath(), options);
        } else {
            RAW_URL = context.getString(R.string.base_url) + "database/cardImages/";
            Picasso.with(context).load(RAW_URL + IMAGE_NAME + ".jpg").into(getTarget(IMAGE_NAME));
            imgFile = new File(Environment.getExternalStorageDirectory().getPath()
                    + "/" + ROOT_DIRECTORY + IMAGE_NAME + ".jpg");
            myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath(), options);
            if (!hasToSaveImage(context)) {
                imgFile.delete();
            }
        }
        return myBitmap;
    }

    private static Target getTarget(final String fileName) {
        return new Target() {

            @Override
            public void onBitmapLoaded(final Bitmap bitmap, Picasso.LoadedFrom from) {
                new Thread(new Runnable() {

                    @Override
                    public void run() {
                        File direct = new File(
                                Environment.getExternalStorageDirectory().getPath()
                                        + "/" + ROOT_DIRECTORY);
                        if (!direct.exists()) {
                            File wallpaperDirectory = new File("/sdcard/" + ROOT_DIRECTORY);
                            wallpaperDirectory.mkdirs();
                        }
                        File file = new File(new File("/sdcard/" + ROOT_DIRECTORY), fileName + ".jpg");
                        try {
                            if (file.exists())
                                file.delete();
                            if (!file.createNewFile())
                                Log.d("File", "Image File Hasn't Been Created");
                            FileOutputStream oStream = new FileOutputStream(file);
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, oStream);
                            oStream.flush();
                            oStream.close();
                        } catch (Exception e) {
                            Log.d("SavingFileError", e.getMessage());
                        }
                    }
                }).start();
            }

            @Override
            public void onBitmapFailed(Drawable errorDrawable) {

            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {

            }
        };
    }

    private static boolean hasToSaveImage(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("shared", Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean("hasSavePhoto", true);
    }
}
