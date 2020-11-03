package com.onesmart.umshare

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.onesmart.module.umshare.UMShareManager
import com.onesmart.module.umshare.bean.UMConfig
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initUM()

        btnShare.setOnClickListener {
            shareImage(this, R.mipmap.ums_ic_share_friend)
//            share(this,
//                    "精锐在线少儿“明日之星”卡拉OK大赛等你来挑战",
//                    "LET’S SING",
//                    R.mipmap.ums_ic_share_friend,
//                    "https://baidu.com")
        }
    }

    /**
     * 友盟分享SDK初始化
     */
    private fun initUM() {
        val umConfig = UMConfig(
                "",
                "",
                "",
                "",
                "")
        UMShareManager.instance.initUM(this, umConfig)
    }

    /**
     *  通用分享
     *
     *  context      Context
     *  title        分享标题
     *  description  分享描述
     *  drawable     小图标  1.	int        resId, 如 R.drawable.ic_
     *                      2.	String    图片Url或图片本地路径
     *                      3.	File      图片file
     *                      4.	Byte[]    图片byte[]
     *  h5Url        点击H5跳转链接地址
     */
    private fun share(context: Context,
                      title: String? = null,
                      description: String? = null,
                      drawable: Any? = null,
                      h5Url: String? = null) {
        UMShareManager.instance.share(context,
                title,
                description,
                drawable,
                h5Url)
    }

    /**
     * 分享单张图片
     *
     * context   Context
     * drawable  1.	int        resId, 如 R.drawable.ic_
     *           2.	String    图片Url或图片本地路径
     *           3.	File      图片file
     *           4.	Byte[]    图片byte[]
     */
    private fun shareImage(context: Context, drawable: Any) {
        UMShareManager.instance.shareImage(context, drawable)
    }

}