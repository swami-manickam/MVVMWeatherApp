package com.challange.weather.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.util.Log;
import android.webkit.MimeTypeMap;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.util.Comparator;

public class FileUtils {

  public static final String MIME_TYPE_AUDIO = "audio/*";
  public static final String MIME_TYPE_TEXT = "text/*";
  public static final String MIME_TYPE_IMAGE = "image/*";
  public static final String MIME_TYPE_VIDEO = "video/*";
  public static final String MIME_TYPE_APP = "application/*";
  public static final String HIDDEN_PREFIX = ".";
  /**
   * TAG for log messages.
   */
  static final String TAG = "FileUtils";
  private static final boolean DEBUG = false; // Set to true to enable logging
  /**
   * File and folder comparator. TODO Expose sorting option method
   *
   * @author paulburke
   */
  public static Comparator<File> sComparator = new Comparator<File>() {
    @Override public int compare(File f1, File f2) {
      // Sort alphabetically by lower case, which is much cleaner
      return f1.getName().toLowerCase().compareTo(f2.getName().toLowerCase());
    }
  };
  /**
   * File (not directories) filter.
   *
   * @author paulburke
   */
  public static FileFilter sFileFilter = new FileFilter() {
    @Override public boolean accept(File file) {
      final String fileName = file.getName();
      // Return files only (not directories) and skip hidden files
      return file.isFile() && !fileName.startsWith(HIDDEN_PREFIX);
    }
  };
  /**
   * Folder (directories) filter.
   *
   * @author paulburke
   */
  public static FileFilter sDirFilter = new FileFilter() {
    @Override public boolean accept(File file) {
      final String fileName = file.getName();
      // Return directories only and skip hidden directories
      return file.isDirectory() && !fileName.startsWith(HIDDEN_PREFIX);
    }
  };

  private FileUtils() {
  } //private constructor to enforce Singleton pattern

  /**
   * Gets the extension of a file name, like ".png" or ".jpg".
   *
   * @return Extension including the dot("."); "" if there is no extension;
   * null if uri was null.
   */
  public static String getExtension(String uri) {
    if (uri == null) {
      return null;
    }

    int dot = uri.lastIndexOf(".");
    if (dot >= 0) {
      return uri.substring(dot);
    } else {
      // No extension.
      return "";
    }
  }

  public static String getExtensionFromMimeType(String mimeType) {
    if (mimeType == null) {
      return null;
    }

    return MimeTypeMap.getSingleton().getExtensionFromMimeType(mimeType);
  }

  public static String getExtensionFromUrl(String url) {
    if (url == null) {
      return null;
    }

    return getExtensionFromMimeType(getMimeTypeFromUrl(url));
  }

  /**
   * @return Whether the URI is a local one.
   */
  public static boolean isLocal(String url) {
    return url != null && !url.startsWith("http://") && !url.startsWith("https://");
  }

  /**
   * @return True if Uri is a MediaStore Uri.
   * @author paulburke
   */
  public static boolean isMediaUri(Uri uri) {
    return "media".equalsIgnoreCase(uri.getAuthority());
  }

  /**
   * Convert File into Uri.
   *
   * @return uri
   */
  public static Uri getUri(File file) {
    if (file != null) {
      return Uri.fromFile(file);
    }
    return null;
  }

  /**
   * Returns the path only (without file name).
   */
  public static File getPathWithoutFilename(File file) {
    if (file != null) {
      if (file.isDirectory()) {
        // no file to be split off. Return everything
        return file;
      } else {
        String filename = file.getName();
        String filepath = file.getAbsolutePath();

        // Construct path without file name.
        String pathwithoutname = filepath.substring(0, filepath.length() - filename.length());
        if (pathwithoutname.endsWith("/")) {
          pathwithoutname = pathwithoutname.substring(0, pathwithoutname.length() - 1);
        }
        return new File(pathwithoutname);
      }
    }
    return null;
  }

  /**
   * @return The MIME type for the given file.
   */
  public static String getMimeType(File file) {

    String extension = getExtension(file.getName());

    if (extension.length() > 0) {
      return MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension.substring(1));
    }

    return "application/octet-stream";
  }

  /**
   * @return The MIME type for the give Uri.
   */
  public static String getMimeType(Context context, Uri uri) {
    String mimeType;
    if (uri.getScheme().equals(ContentResolver.SCHEME_CONTENT)) {
      ContentResolver cr = context.getContentResolver();
      mimeType = cr.getType(uri);
    } else {
      String fileExtension = MimeTypeMap.getFileExtensionFromUrl(uri.toString());
      mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(fileExtension.toLowerCase());
    }
    return mimeType;
  }

  /**
   * @param uri The Uri to check.
   * @return Whether the Uri authority is ExternalStorageProvider.
   * @author paulburke
   */
  public static boolean isExternalStorageDocument(Uri uri) {
    return "com.android.externalstorage.documents".equals(uri.getAuthority());
  }

  /**
   * @param uri The Uri to check.
   * @return Whether the Uri authority is DownloadsProvider.
   * @author paulburke
   */
  public static boolean isDownloadsDocument(Uri uri) {
    return "com.android.providers.downloads.documents".equals(uri.getAuthority());
  }

  /**
   * @param uri The Uri to check.
   * @return Whether the Uri authority is MediaProvider.
   * @author paulburke
   */
  public static boolean isMediaDocument(Uri uri) {
    return "com.android.providers.media.documents".equals(uri.getAuthority());
  }

  /**
   * @param uri The Uri to check.
   * @return Whether the Uri authority is Google Photos.
   */
  public static boolean isGooglePhotosUri(Uri uri) {
    return "com.google.android.apps.photos.content".equals(uri.getAuthority());
  }

  /**
   * Get the value of the data column for this Uri. This is useful for
   * MediaStore Uris, and other file-based ContentProviders.
   *
   * @param context The context.
   * @param uri The Uri to query.
   * @param selection (Optional) Filter used in the query.
   * @param selectionArgs (Optional) Selection arguments used in the query.
   * @return The value of the _data column, which is typically a file path.
   * @author paulburke
   */
  public static String getDataColumn(Context context, Uri uri, String selection,
      String[] selectionArgs) {

    Cursor cursor = null;
    final String column = "_data";
    final String[] projection = {
        column
    };

    try {
      cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
      if (cursor != null && cursor.moveToFirst()) {
        if (DEBUG) DatabaseUtils.dumpCursor(cursor);

        final int column_index = cursor.getColumnIndexOrThrow(column);
        return cursor.getString(column_index);
      }
    } finally {
      if (cursor != null) cursor.close();
    }
    return null;
  }

  public static String getFileSizeWithCursor(Context context, Uri uri) {

    if (uri == null) return "0 KB";

    Cursor returnCursor = context.getContentResolver().query(uri, null, null, null, null);

    if (returnCursor != null) {
      int sizeIndex = returnCursor.getColumnIndex(OpenableColumns.SIZE);
      returnCursor.moveToFirst();
      if (sizeIndex != 0) {
        long size = returnCursor.getLong(sizeIndex);
        returnCursor.close();
        return getStringSizeLengthFile(size);
      }
    }

    return "0 KB";
  }

  public static String getStringSizeLengthFile(long size) {

    DecimalFormat df = new DecimalFormat("0.00");

    float sizeKb = 1024.0f;
    float sizeMo = sizeKb * sizeKb;
    float sizeGo = sizeMo * sizeKb;
    float sizeTerra = sizeGo * sizeKb;
    if (size < sizeMo) {
      return df.format(size / sizeKb) + " KB";
    } else if (size < sizeGo) {
      return df.format(size / sizeMo) + " MB";
    } else if (size < sizeTerra) return df.format(size / sizeGo) + " GB";

    return "";
  }

  /**
   * Get the file size in a human-readable string.
   *
   * @author paulburke
   */
  public static String getReadableFileSize(int size) {

    final int BYTES_IN_KILOBYTES = 1024;
    final DecimalFormat dec = new DecimalFormat("###.#");
    final String KILOBYTES = " KB";
    final String MEGABYTES = " MB";
    final String GIGABYTES = " GB";
    float fileSize = 0;
    String suffix = KILOBYTES;

    if (size > BYTES_IN_KILOBYTES) {
      fileSize = size / BYTES_IN_KILOBYTES;
      if (fileSize > BYTES_IN_KILOBYTES) {
        fileSize = fileSize / BYTES_IN_KILOBYTES;
        if (fileSize > BYTES_IN_KILOBYTES) {
          fileSize = fileSize / BYTES_IN_KILOBYTES;
          suffix = GIGABYTES;
        } else {
          suffix = MEGABYTES;
        }
      }
    }
    return String.valueOf(dec.format(fileSize) + suffix);
  }

  /**
   * Attempt to retrieve the thumbnail of given File from the MediaStore. This
   * should not be called on the UI thread.
   *
   * @author paulburke
   */
  public static Bitmap getThumbnail(Context context, File file) {
    return getThumbnail(context, getUri(file), getMimeType(file));
  }

  /**
   * Attempt to retrieve the thumbnail of given Uri from the MediaStore. This
   * should not be called on the UI thread.
   *
   * @author paulburke
   */
  public static Bitmap getThumbnail(Context context, Uri uri) {
    return getThumbnail(context, uri, getMimeType(context, uri));
  }

  /**
   * Attempt to retrieve the thumbnail of given Uri from the MediaStore. This
   * should not be called on the UI thread.
   *
   * @author paulburke
   */
  public static Bitmap getThumbnail(Context context, Uri uri, String mimeType) {
    if (!isMediaUri(uri)) {
      return null;
    }

    Bitmap bm = null;
    if (uri != null) {
      final ContentResolver resolver = context.getContentResolver();
      Cursor cursor = null;
      try {
        cursor = resolver.query(uri, null, null, null, null);
        if (cursor.moveToFirst()) {
          final int id = cursor.getInt(0);
          if (DEBUG) Log.d(TAG, "Got thumb ID: " + id);

          if (mimeType.contains("video")) {
            bm = MediaStore.Video.Thumbnails.getThumbnail(resolver, id,
                MediaStore.Video.Thumbnails.MINI_KIND, null);
          } else if (mimeType.contains(FileUtils.MIME_TYPE_IMAGE)) {
            bm = MediaStore.Images.Thumbnails.getThumbnail(resolver, id,
                MediaStore.Images.Thumbnails.MINI_KIND, null);
          }
        }
      } catch (Exception e) {
      } finally {
        if (cursor != null) cursor.close();
      }
    }
    return bm;
  }

  /**
   * Get the Intent for selecting content to be used in an Intent Chooser.
   *
   * @return The intent for opening a file with Intent.createChooser()
   * @author paulburke
   */
  public static Intent createGetContentIntent() {
    // Implicitly allow the user to select a particular kind of data
    final Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
    // The MIME data type filter
    intent.setType("*/*");
    // Only return URIs that can be opened with ContentResolver
    intent.addCategory(Intent.CATEGORY_OPENABLE);
    intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
    return intent;
  }

  public static File getDownloadFileDirectory(Context context) {
    return context.getCacheDir();
  }

  /**
   * @return The MIME type for the give Url.
   */
  public static String getMimeTypeFromUrl(String url) {
    return MimeTypeMap.getSingleton()
        .getMimeTypeFromExtension(MimeTypeMap.getFileExtensionFromUrl(url));
  }

  public static boolean copyFile(Context context, Uri src, File dst) {
    InputStream inputStream = null;
    FileOutputStream fileOutputStream = null;
    try {
      inputStream = context.getContentResolver().openInputStream(src);
      fileOutputStream = new FileOutputStream(dst);

      if (!dst.exists()) {
        dst.createNewFile();
      }

      byte[] buffer = new byte[1000];
      while (inputStream.read(buffer, 0, buffer.length) >= 0) {
        fileOutputStream.write(buffer, 0, buffer.length);
      }
      return true;
    } catch (IOException e) {
      return false;
    } finally {
      if (inputStream != null) {
        try {
          inputStream.close();
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
      if (fileOutputStream != null) {
        try {
          fileOutputStream.close();
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    }
  }

  public static String getFileName(Context context, Uri uri) {
    String fileName = null;
    if (uri.getScheme().equals(ContentResolver.SCHEME_CONTENT)) {
      Cursor cursor = context.getContentResolver().query(uri, null, null, null, null, null);

      if (cursor != null) {
        if (cursor.moveToFirst()) {
          int index = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
          fileName = cursor.getString(index);
        }
        cursor.close();
      }
    } else {
      fileName = uri.getLastPathSegment();
    }
    return fileName;
  }

  public static byte[] getBytesFromUri(Context context, Uri uri) throws IOException {
    if (uri == null) return null;

    InputStream inputStream = context.getContentResolver().openInputStream(uri);

    ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
    int bufferSize = 1024;
    byte[] buffer = new byte[bufferSize];

    int len;
    while ((len = inputStream.read(buffer)) != -1) {
      byteBuffer.write(buffer, 0, len);
    }
    return byteBuffer.toByteArray();
  }

  public static Bitmap getThumbnailFromUri(Context context, Uri uri) {
    Bitmap bitmap = null;
    try {
      InputStream input = context.getContentResolver().openInputStream(uri);
      BitmapFactory.Options onlyBoundsOptions = new BitmapFactory.Options();
      onlyBoundsOptions.inJustDecodeBounds = true;
      onlyBoundsOptions.inDither = true;//optional
      onlyBoundsOptions.inPreferredConfig = Bitmap.Config.ARGB_8888;//optional
      BitmapFactory.decodeStream(input, null, onlyBoundsOptions);
      input.close();
      if ((onlyBoundsOptions.outWidth == -1) || (onlyBoundsOptions.outHeight == -1)) return null;
      BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
      bitmapOptions.inDither = true;//optional
      bitmapOptions.inPreferredConfig = Bitmap.Config.ARGB_8888;//optional
      input = context.getContentResolver().openInputStream(uri);
      bitmap = BitmapFactory.decodeStream(input, null, bitmapOptions);
      input.close();
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }

    return bitmap;
  }

  private static int getPowerOfTwoForSampleRatio(double ratio) {
    int k = Integer.highestOneBit((int) Math.floor(ratio));
    if (k == 0) {
      return 1;
    } else {
      return k;
    }
  }
}