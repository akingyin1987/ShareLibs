package com.zlcdgroup.libs.tusdkcamera;
import android.support.v7.app.AppCompatActivity;
import java.io.File;
import org.lasque.tusdk.core.TuSdkResult;
import org.lasque.tusdk.core.utils.TLog;
import org.lasque.tusdk.impl.activity.TuFragment;
import org.lasque.tusdk.impl.components.sticker.TuEditStickerFragment;
import org.lasque.tusdk.impl.components.sticker.TuEditStickerOption;
import org.lasque.tusdk.modules.components.TuSdkHelperComponent;

/**
 * @ Description:
 * 帖纸
 * Company:重庆中陆承大科技有限公司
 * @ Author king
 * @ Date 2016/9/24 11:09
 * @ Version V1.0
 */

public class StickerEditorComponent   implements TuEditStickerFragment.TuEditStickerFragmentDelegate {

  TuSdkHelperComponent  componentHelper;

  /*
	 * 显示视图
	 */

  public void showSample(AppCompatActivity activity,File   outfile)
  {
    if (activity == null) return;
    // see-http://tusdk.com/docs/android/api/org/lasque/tusdk/impl/components/base/TuSdkHelperComponent.html
    this.componentHelper = new TuSdkHelperComponent(activity);

    // 组件选项配置
    // @see-http://tusdk.com/docs/android/api/org/lasque/tusdk/impl/components/sticker/TuEditStickerOption.html
    TuEditStickerOption option = new TuEditStickerOption();

    // 是否在控制器结束后自动删除临时文件
    option.setAutoRemoveTemp(true);
    // 设置贴纸单元格的高度
    option.setGridHeight(150);
    // 设置贴纸单元格的间距
    option.setGridPadding(8);

    // 是否显示处理结果预览图 (默认：关闭，调试时可以开启)
    option.setShowResultPreview(true);

    TuEditStickerFragment fragment = option.fragment();

    // 输入的图片对象 (处理优先级: Image > TempFilePath > ImageSqlInfo)
    fragment.setTempFilePath(outfile);

    fragment.setDelegate(this);

    // 开启贴纸编辑界面
    componentHelper.pushModalNavigationActivity(fragment, true);
  }


  @Override public void onTuEditStickerFragmentEdited(TuEditStickerFragment tuEditStickerFragment,
      TuSdkResult tuSdkResult) {
    tuEditStickerFragment.hubDismissRightNow();
    tuEditStickerFragment.dismissActivityWithAnim();
    TLog.d("onTuEditStickerFragmentEdited: %s", tuSdkResult);

    // 默认输出为 Bitmap  -> result.image

    // 如果保存到临时文件 (默认不保存, 当设置为true时, TuSdkResult.imageFile, 处理完成后将自动清理原始图片)
    // option.setSaveToTemp(true);  ->  result.imageFile

    // 保存到系统相册 (默认不保存, 当设置为true时, TuSdkResult.sqlInfo, 处理完成后将自动清理原始图片)
    // option.setSaveToAlbum(true);  -> result.image
  }

  @Override
  public boolean onTuEditStickerFragmentEditedAsync(TuEditStickerFragment tuEditStickerFragment,
      TuSdkResult tuSdkResult) {
    return false;
  }

  @Override
  public void onComponentError(TuFragment tuFragment, TuSdkResult tuSdkResult, Error error) {

  }
}
