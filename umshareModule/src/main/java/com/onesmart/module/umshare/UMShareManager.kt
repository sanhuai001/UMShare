package com.onesmart.module.umshare

import android.content.Context
import com.onesmart.module.umshare.bean.UMConfig
import com.umeng.commonsdk.UMConfigure
import com.umeng.socialize.PlatformConfig

class UMShareManager {

    companion object {
        private var mManager: UMShareManager? = null
        val instance: UMShareManager
            get() {
                if (mManager == null) {
                    synchronized(UMShareManager::class.java) {
                        if (mManager == null) {
                            mManager = UMShareManager()
                        }
                    }
                }

                return mManager as UMShareManager
            }
    }

    fun initUM(context: Context, umConfig: UMConfig, appId: String) {
        UMConfigure.init(context, UMConfigure.DEVICE_TYPE_PHONE, "")

        PlatformConfig.setWeixin(umConfig.wxAppKey, umConfig.wxAppSecret)
        PlatformConfig.setWXFileProvider("$appId.FileProvider")
        PlatformConfig.setQQZone(umConfig.qqAppKey, umConfig.qqAppSecret)
        PlatformConfig.setQQFileProvider("$appId.FileProvider")
    }

    fun share(
        context: Context,
        title: String? = null,
        description: String? = null,
        drawable: Any? = null,
        h5Url: String? = null
    ) {
        context.shareDialog(context, title, description, drawable, h5Url).show()
    }

    fun shareImage(
        context: Context,
        drawable: Any? = null
    ) {
        context.shareDialog(context, drawable = drawable).show()
    }
}