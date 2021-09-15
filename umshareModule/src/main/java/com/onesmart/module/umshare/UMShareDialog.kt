package com.onesmart.module.umshare

import android.Manifest
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.graphics.Bitmap
import android.text.TextUtils
import android.view.Gravity
import android.view.WindowManager
import android.widget.Toast
import com.jakewharton.rxbinding2.view.RxView
import com.tbruyelle.rxpermissions2.RxPermissions
import com.umeng.socialize.ShareAction
import com.umeng.socialize.UMShareAPI
import com.umeng.socialize.UMShareListener
import com.umeng.socialize.bean.SHARE_MEDIA
import com.umeng.socialize.media.UMImage
import com.umeng.socialize.media.UMWeb
import kotlinx.android.synthetic.main.ums_dialog_share.*
import java.io.File
import java.util.concurrent.TimeUnit

fun Context.shareDialog(
    context: Context,
    title: String? = null,
    description: String? = null,
    drawable: Any? = null,
    h5Url: String? = null
) = Dialog(this, R.style.UMSNormalDialog).apply {
    setContentView(R.layout.ums_dialog_share)
    window?.setDimAmount(0.70f)
    val params = window?.attributes
    params?.windowAnimations = R.style.UMSDialogStyleUpDown
    params?.width = WindowManager.LayoutParams.MATCH_PARENT
    params?.height = WindowManager.LayoutParams.WRAP_CONTENT
    params?.gravity = Gravity.BOTTOM

    tvClose.setOnClickListener {
        dismiss()
    }

    var mRxPermissions: RxPermissions? = null
    //微信好友
    RxView.clicks(tvWeChat).throttleFirst(2, TimeUnit.SECONDS).subscribe {
        if (UMShareAPI.get(context).isInstall(context as Activity, SHARE_MEDIA.WEIXIN)) {
            share(
                context,
                this,
                SHARE_MEDIA.WEIXIN,
                getWeb(context, title, description, drawable, h5Url),
                getImage(context, drawable)
            )
        } else {
            Toast.makeText(context, "您未安装该应用，暂时无法分享", Toast.LENGTH_SHORT).show()
        }
    }

    //朋友圈
    RxView.clicks(tvWeFriends).throttleFirst(2, TimeUnit.SECONDS).subscribe {
        if (UMShareAPI.get(context).isInstall(context as Activity, SHARE_MEDIA.WEIXIN)) {
            share(
                context,
                this,
                SHARE_MEDIA.WEIXIN_CIRCLE,
                getWeb(context, title, description, drawable, h5Url),
                getImage(context, drawable)
            )
        } else {
            Toast.makeText(context, "您未安装该应用，暂时无法分享", Toast.LENGTH_SHORT).show()
        }
    }

    //QQ空间
    RxView.clicks(tvQQZone).throttleFirst(2, TimeUnit.SECONDS).subscribe {
        mRxPermissions =
            if (mRxPermissions == null) RxPermissions(context as Activity) else mRxPermissions
        mRxPermissions?.request(
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_LOGS,
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.SET_DEBUG_APP,
            Manifest.permission.SYSTEM_ALERT_WINDOW,
            Manifest.permission.GET_ACCOUNTS,
            Manifest.permission.WRITE_APN_SETTINGS
        )
            ?.subscribe { granted ->
                if (!granted) {
                    share(
                        context,
                        this,
                        SHARE_MEDIA.QZONE,
                        getWeb(context, title, description, drawable, h5Url),
                        getImage(context, drawable)
                    )
                } else {
                    Toast.makeText(context, "请设置权限", Toast.LENGTH_SHORT).show()
                }
            }
    }

    //QQ好友
    RxView.clicks(tvQQ).throttleFirst(2, TimeUnit.SECONDS).subscribe {
        if (UMShareAPI.get(context).isInstall(context as Activity, SHARE_MEDIA.QQ)) {
            mRxPermissions = if (mRxPermissions == null) RxPermissions(context) else mRxPermissions
            mRxPermissions?.request(
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_LOGS,
                Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.SET_DEBUG_APP,
                Manifest.permission.SYSTEM_ALERT_WINDOW,
                Manifest.permission.GET_ACCOUNTS,
                Manifest.permission.WRITE_APN_SETTINGS
            )
                ?.subscribe { granted ->
                    if (!granted) {
                        share(
                            context,
                            this,
                            SHARE_MEDIA.QQ,
                            getWeb(context, title, description, drawable, h5Url),
                            getImage(context, drawable)
                        )
                    } else {
                        Toast.makeText(context, "请设置权限", Toast.LENGTH_SHORT).show()
                    }
                }
        } else {
            Toast.makeText(context, "您未安装该应用，暂时无法分享", Toast.LENGTH_SHORT).show()
        }
    }
}

fun getImage(
    context: Context,
    drawable: Any? = null
): UMImage? {
    when (drawable) {
        is Int -> {
            val image = UMImage(context, drawable)
            image.setThumb(image)
            return image
        }
        is Bitmap -> {
            val image = UMImage(context, drawable)
            image.setThumb(image)
            return image
        }
        is String -> {
            return UMImage(context, drawable)
        }
        is File -> {
            return UMImage(context, drawable)
        }
        is ByteArray -> {
            return UMImage(context, drawable)
        }
        else -> return null
    }
}

fun getWeb(
    context: Context,
    title: String? = null,
    description: String? = null,
    drawable: Any? = null,
    url: String? = null
): UMWeb? {
    if (TextUtils.isEmpty(title) && TextUtils.isEmpty(description)) return null
    val web = UMWeb(url)
    web.title = title ?: ""
    web.description = description ?: ""

    if (drawable == null) return web
    if (drawable is Int) {
        web.setThumb(UMImage(context, drawable))
    } else if (drawable is String) {
        web.setThumb(UMImage(context, drawable))
    }
    return web
}

fun share(context: Context, dialog: Dialog?, media: SHARE_MEDIA, web: UMWeb?, image: UMImage?) {
    if (web != null) {
        ShareAction(context as Activity)
            .setPlatform(media)
            .withMedia(web)
            .setCallback(object : UMShareListener {
                override fun onStart(share_media: SHARE_MEDIA) {}

                override fun onResult(share_media: SHARE_MEDIA) {
                    dialog?.dismiss()
                }

                override fun onError(share_media: SHARE_MEDIA, throwable: Throwable) {
                    Toast.makeText(context, "分享失败:${throwable.message}", Toast.LENGTH_SHORT).show()
                }

                override fun onCancel(share_media: SHARE_MEDIA) {
                    dialog?.dismiss()
                }
            }).share()
        return
    }

    ShareAction(context as Activity?)
        .setPlatform(media)
        .withMedia(image)
        .setCallback(object : UMShareListener {
            override fun onStart(share_media: SHARE_MEDIA) {}

            override fun onResult(share_media: SHARE_MEDIA) {
                dialog?.dismiss()
            }

            override fun onError(share_media: SHARE_MEDIA, throwable: Throwable) {
                Toast.makeText(context, "分享失败:${throwable.message}", Toast.LENGTH_SHORT).show()
            }

            override fun onCancel(share_media: SHARE_MEDIA) {
                dialog?.dismiss()
            }
        }).share()
    dialog?.dismiss()
}