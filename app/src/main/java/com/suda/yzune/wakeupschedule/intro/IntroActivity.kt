package com.suda.yzune.wakeupschedule.intro

import android.os.Build
import android.os.Bundle
import android.util.TypedValue
import androidx.core.content.ContextCompat
import com.github.paolorotolo.appintro.AppIntro
import com.suda.yzune.wakeupschedule.R
import com.suda.yzune.wakeupschedule.utils.PreferenceUtils
import org.jetbrains.anko.backgroundResource

class IntroActivity : AppIntro() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            addSlide(IntroFragment.newInstance("https://ws2.sinaimg.cn/large/006tNbRwgy1fw81qicpytj30dc0nqjxa.jpg", "上下滑动右侧下半部分\n可以快速切换周数哦~"))
        }
        addSlide(IntroFragment.newInstance("https://ws4.sinaimg.cn/large/006tNbRwgy1fw81qzeiwdj30dc0nqq7c.jpg", "从右侧上半部分往左划\n进行多课表的管理\n每个课表都可以有完全不同的设置哦\n如开学日期、背景图片……"))
        addSlide(IntroFragment.newInstance("https://ws3.sinaimg.cn/large/006tNbRwgy1fw81rjkb2vj30dc0nq3zr.jpg", "点击多课表管理界面的卡片\n可以看到一个课表里有哪些科目\n可以进行统一管理~"))
        addSlide(IntroFragment.newInstance("https://ws1.sinaimg.cn/large/006tNbRwgy1fw81s4ny0hj30dc0nq425.jpg", "全新设计的课程导出导入功能\n可以导入朋友分享的文件\n共享包括上课时间、课程表设置等内容"))
        addSlide(IntroFragment.newInstance("https://ws2.sinaimg.cn/large/006tNbRwgy1fw81sts3agj30dc0nqkef.jpg", "本App是有桌面小部件的哦\n长按桌面可以添加，可能位置比较靠后\n小部件的外观设置在课表设置的最下面哦"))
        setBarColor(ContextCompat.getColor(this, R.color.transparent))
        val outValue = TypedValue()
        theme.resolveAttribute(R.attr.selectableItemBackgroundBorderless, outValue, true)
        doneButton.backgroundResource = outValue.resourceId
        setColorDoneText(ContextCompat.getColor(this, R.color.colorAccent))
        setIndicatorColor(ContextCompat.getColor(this, R.color.colorAccent), ContextCompat.getColor(this, R.color.grey))
        setNextArrowColor(ContextCompat.getColor(this, R.color.colorAccent))
        setSeparatorColor(ContextCompat.getColor(this, R.color.white))
        showSkipButton(false)
    }

    override fun onDonePressed(currentFragment: androidx.fragment.app.Fragment?) {
        super.onDonePressed(currentFragment)
        PreferenceUtils.saveBooleanToSP(applicationContext, "v3.20", true)
        finish()
    }

}