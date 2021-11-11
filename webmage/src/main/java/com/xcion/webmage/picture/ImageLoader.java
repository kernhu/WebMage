package com.xcion.webmage.picture;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @Author: Kern Hu
 * @E-mail: sky580@126.com
 * @CreateDate: 2020/10/9 17:37
 * @UpdateUser: Kern Hu
 * @UpdateDate: 2020/10/9 17:37
 * @Version: 1.0
 * @Description: http download image util
 * @UpdateRemark:
 */
public class ImageLoader {

    private int connectTimeout = 1000 * 12;
    private int readTimeout = 1000 * 12;
    private String url;
    private Callback callback;
    private ExecutorService executor = Executors.newCachedThreadPool();

    public interface Callback {

        /**
         * @param bitmap
         */
        void onSuccess(Bitmap bitmap);

        /**
         * @param error
         */
        void onFailure(String error);

        /**
         * @param progress
         */
        void onProgressChanged(int progress);

    }


    public static ImageLoader getInstance() {
        return new ImageLoader();
    }


    public int getConnectTimeout() {
        return connectTimeout;
    }

    public ImageLoader setConnectTimeout(int connectTimeout) {
        this.connectTimeout = connectTimeout;
        return this;
    }

    public int getReadTimeout() {
        return readTimeout;
    }

    public ImageLoader setReadTimeout(int readTimeout) {
        this.readTimeout = readTimeout;
        return this;
    }

    public String getUrl() {
        return url;
    }

    public ImageLoader setUrl(String url) {
        this.url = url;
        return this;
    }

    public Callback getCallback() {
        return callback;
    }

    public ImageLoader setCallback(Callback callback) {
        this.callback = callback;
        return this;
    }


    public void load() {
        executor.execute(new LoaderRunnable(getConnectTimeout(), getReadTimeout(), getUrl(), getCallback()));
    }

    class LoaderRunnable implements Runnable {

        int connectTimeout;
        int readTimeout;
        String imageUrl;
        Callback callback;

        public LoaderRunnable(int connectTimeout, int readTimeout, String imageUrl, Callback callback) {
            this.connectTimeout = connectTimeout;
            this.readTimeout = readTimeout;
            this.imageUrl = imageUrl;
            this.callback = callback;
        }

        @Override
        public void run() {
            HttpURLConnection connection = null;
            Bitmap bitmap = null;
            InputStream inputStream = null;
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            try {
                URL url = new URL(imageUrl);
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setConnectTimeout(connectTimeout);
                connection.setReadTimeout(readTimeout);
                inputStream = connection.getInputStream();

                int fileLength = connection.getContentLength();
                int len = 0;
                byte[] data = new byte[1024];
                int total_length = 0;
                int value = 0;
                while ((len = inputStream.read(data)) != -1) {
                    total_length += len;
                    value = ((total_length / fileLength) * 100);
                    outputStream.write(data, 0, len);
                    if (callback != null) {
                        callback.onProgressChanged(value);
                    }
                }
                byte[] result = outputStream.toByteArray();
                bitmap = BitmapFactory.decodeByteArray(result, 0, result.length);
                inputStream.close();
                outputStream.close();
                connection.disconnect();
                if (callback != null) {
                    callback.onSuccess(bitmap);
                }
            } catch (ProtocolException e) {
                e.printStackTrace();
                if (callback != null) {
                    callback.onFailure(e.getMessage());
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
                if (callback != null) {
                    callback.onFailure(e.getMessage());
                }
            } catch (IOException e) {
                e.printStackTrace();
                if (callback != null) {
                    callback.onFailure(e.getMessage());
                }
            } finally {
                if (inputStream != null) {
                    try {
                        inputStream.close();
                        outputStream.close();
                        connection.disconnect();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}
