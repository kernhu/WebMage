package com.xcion.webmage.share;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import java.util.ArrayList;

/**
 * system share utils
 */
public class SystemSharer {


    /**
     * share text
     *
     * @param context
     * @param text
     * @param title
     */
    public static void shareText(Context context, String text, String title) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, text);
        context.startActivity(Intent.createChooser(intent, title));
    }


    /**
     * share image
     *
     * @param context
     * @param uri
     * @param title
     */
    public static void shareImage(Context context, Uri uri, String title) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("image/png");
        intent.putExtra(Intent.EXTRA_STREAM, uri);
        context.startActivity(Intent.createChooser(intent, title));
    }


    /**
     * share images
     *
     * @param context
     * @param imageUris
     * @param title
     */
    public static void sendMoreImage(Context context, ArrayList<Uri> imageUris, String title) {
        Intent mulIntent = new Intent(Intent.ACTION_SEND_MULTIPLE);
        mulIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, imageUris);
        mulIntent.setType("image/jpeg");
        context.startActivity(Intent.createChooser(mulIntent, title));
    }
}
