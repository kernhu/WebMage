package com.xcion.webmage.picture;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

/**
 * @Author: Kern Hu
 * @E-mail: sky580@126.com
 * @CreateDate: 2020/10/9 17:37
 * @UpdateUser: Kern Hu
 * @UpdateDate: 2020/10/9 17:37
 * @Version: 1.0
 * @Description: java类作用描述
 * @UpdateRemark: 更新说明
 */
public class ImageLoader {

    private int connectTimeout = 1000 * 12;
    private int readTimeout = 1000 * 12;
    private String url;
    private Callback callback;

    public interface Callback {

        void onSuccess(Bitmap bitmap);

        void onFailure(String error);

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

        new LoaderAsyncTask(getConnectTimeout(), getReadTimeout(), getUrl(), getCallback()).execute();

    }


    class LoaderAsyncTask extends AsyncTask<String, Integer, Bitmap> {

        int connectTimeout;
        int readTimeout;
        String imageUrl;
        Callback callback;

        public LoaderAsyncTask(int connectTimeout, int readTimeout, String imageUrl, Callback callback) {
            this.connectTimeout = connectTimeout;
            this.readTimeout = readTimeout;
            this.imageUrl = imageUrl;
            this.callback = callback;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            if (callback != null) {
                callback.onProgressChanged(values[0]);
            }
        }

        @Override
        protected Bitmap doInBackground(String... strings) {

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
                    publishProgress(value);
                    outputStream.write(data, 0, len);
                }
                byte[] result = outputStream.toByteArray();
                bitmap = BitmapFactory.decodeByteArray(result, 0, result.length);
                inputStream.close();
                outputStream.close();
                connection.disconnect();

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
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            if (bitmap != null) {
                if (callback != null) {
                    callback.onSuccess(bitmap);
                }
            }
        }
    }
}
