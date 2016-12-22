package com.zlcdgroup.libs.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.util.Log;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * * *                #                                                   #
 * #                       _oo0oo_                     #
 * #                      o8888888o                    #
 * #                      88" . "88                    #
 * #                      (| -_- |)                    #
 * #                      0\  =  /0                    #
 * #                    ___/`---'\___                  #
 * #                  .' \\|     |# '.                 #
 * #                 / \\|||  :  |||# \                #
 * #                / _||||| -:- |||||- \              #
 * #               |   | \\\  -  #/ |   |              #
 * #               | \_|  ''\---/''  |_/ |             #
 * #               \  .-\__  '-'  ___/-. /             #
 * #             ___'. .'  /--.--\  `. .'___           #
 * #          ."" '<  `.___\_<|>_/___.' >' "".         #
 * #         | | :  `- \`.;`\ _ /`;.`/ - ` : | |       #
 * #         \  \ `_.   \_ __\ /__ _/   .-` /  /       #
 * #     =====`-.____`.___ \_____/___.-`___.-'=====    #
 * #                       `=---='                     #
 * #     ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~   #
 * #                                                   #
 * #               佛祖保佑         永无BUG              #
 * #
 *
 * @ Description:                                          #
 * Company:重庆中陆承大科技有限公司
 * @ Author king
 * @ Date 2016/12/22 11:03
 * @ Version V1.0
 */

public class ImageDecoder {

  public Bitmap decode(File file, ImageSize targetSize) throws IOException {
    Bitmap decodedBitmap;
    ImageFileInfo imageInfo;

    InputStream imageStream = getImageStream(file);
    try {
      imageInfo = defineImageSizeAndRotation(imageStream, file);
      imageStream = resetStream(imageStream, file);
      BitmapFactory.Options decodingOptions = prepareDecodingOptions(imageInfo.imageSize, targetSize);
      decodedBitmap = BitmapFactory.decodeStream(imageStream, null, decodingOptions);
    } finally {
      closeSilently(imageStream);
    }

    if (decodedBitmap != null) {
      decodedBitmap = considerExactScaleAndOrientatiton(decodedBitmap, targetSize, imageInfo.exif.rotation,
          imageInfo.exif.flipHorizontal);
    }
    return decodedBitmap;
  }

  private InputStream getImageStream(File res) throws IOException{
    return new FileInputStream(res);
  }

  /**
   * 定义image的大小和旋转的度
   */
  protected ImageFileInfo defineImageSizeAndRotation(InputStream imageStream, File file)
      throws IOException {
    BitmapFactory.Options options = new BitmapFactory.Options();
    options.inJustDecodeBounds = true;
    BitmapFactory.decodeStream(imageStream, null, options);

    ExifInfo exif;
    if (canDefineExifParams(options.outMimeType)) {
      exif = defineExifOrientation(file);
    } else {
      exif = new ExifInfo();
    }
    return new ImageFileInfo(new ImageSize(options.outWidth, options.outHeight, exif.rotation), exif);
  }

  private boolean canDefineExifParams(String mimeType) {
    return "image/jpeg".equalsIgnoreCase(mimeType);
  }

  protected ExifInfo defineExifOrientation(File imageUri) {
    int rotation = 0;
    boolean flip = false;
    try {
      ExifInterface exif = new ExifInterface(imageUri.getAbsolutePath());
      int exifOrientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
      switch (exifOrientation) {
        case ExifInterface.ORIENTATION_FLIP_HORIZONTAL:
          flip = true;
        case ExifInterface.ORIENTATION_NORMAL:
          rotation = 0;
          break;
        case ExifInterface.ORIENTATION_TRANSVERSE:
          flip = true;
        case ExifInterface.ORIENTATION_ROTATE_90:
          rotation = 90;
          break;
        case ExifInterface.ORIENTATION_FLIP_VERTICAL:
          flip = true;
        case ExifInterface.ORIENTATION_ROTATE_180:
          rotation = 180;
          break;
        case ExifInterface.ORIENTATION_TRANSPOSE:
          flip = true;
        case ExifInterface.ORIENTATION_ROTATE_270:
          rotation = 270;
          break;
      }
    } catch (IOException e) {
      Log.w("decoder", "Can't read EXIF tags from file [%s]" + imageUri.getAbsolutePath());
    }
    return new ExifInfo(rotation, flip);
  }

  /**
   * 如果imageStream支持reset，则返回reset后的imageStream，否则返回 {@link #getImageStream(File)}
   */
  protected InputStream resetStream(InputStream imageStream, File res) throws IOException {
    //http://zhangbo-peipei-163-com.iteye.com/blog/2022460
    if (imageStream.markSupported()) {
      try {
        imageStream.reset();
        return imageStream;
      } catch (IOException ignored) {
      }
    }
    closeSilently(imageStream);
    return getImageStream(res);
  }

  /**
   * 返回计算好 simple size 的Option
   */
  protected BitmapFactory.Options prepareDecodingOptions(ImageSize imageSize, ImageSize targetSize) {
    int scale = ImageUtils.computeImageSampleSize(imageSize, targetSize);
    BitmapFactory.Options decodingOptions = new BitmapFactory.Options();
    decodingOptions.inSampleSize = scale;
    return decodingOptions;
  }

  protected Bitmap considerExactScaleAndOrientatiton(Bitmap subsampledBitmap, ImageSize targetSize,
      int rotation, boolean flipHorizontal) {
    Matrix m = new Matrix();
    // 缩小到精确的大小,如果需要
    ImageSize srcSize = new ImageSize(subsampledBitmap.getWidth(), subsampledBitmap.getHeight(), rotation);
    float scale = ImageUtils.computeImageScale(srcSize, targetSize, false);
    if (Float.compare(scale, 1f) != 0) {
      m.setScale(scale, scale);
    }
    // 翻转 bitmap 如果需要
    if (flipHorizontal) {
      m.postScale(-1, 1);

    }
    // 选择 bitmap 如果需要
    if (rotation != 0) {
      m.postRotate(rotation);
    }

    Bitmap finalBitmap = Bitmap.createBitmap(subsampledBitmap, 0, 0, subsampledBitmap.getWidth(), subsampledBitmap
        .getHeight(), m, true);
    if (finalBitmap != subsampledBitmap) {
      subsampledBitmap.recycle();
    }
    return finalBitmap;
  }

  protected static class ExifInfo {

    public final int rotation;
    public final boolean flipHorizontal;

    protected ExifInfo() {
      this.rotation = 0;
      this.flipHorizontal = false;
    }

    protected ExifInfo(int rotation, boolean flipHorizontal) {
      this.rotation = rotation;
      this.flipHorizontal = flipHorizontal;
    }
  }

  protected static class ImageFileInfo {

    public final ImageSize imageSize;
    public final ExifInfo exif;

    protected ImageFileInfo(ImageSize imageSize, ExifInfo exif) {
      this.imageSize = imageSize;
      this.exif = exif;
    }
  }

  public static void closeSilently(Closeable closeable) {
    if (closeable != null) {
      try {
        closeable.close();
      } catch (Exception ignored) {
      }
    }
  }
}
