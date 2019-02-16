# WakeUp课程表 3.521

<img src="https://ws4.sinaimg.cn/large/0069RVTdgy1fuaoudaytwj30e80e8jtg.jpg" height="256">

| 平台 | 下载地址 |
| :----: | :----: |
| Google Play | <a href='https://play.google.com/store/apps/details?id=com.suda.yzune.wakeupschedule.pro'><img alt='Get it on Google Play' src='https://i.loli.net/2018/06/27/5b32eac49f930.png' height="60"/> |
| 酷安 | <a href='https://www.coolapk.com/apk/159120'><img alt='去酷安下载' src='https://ws4.sinaimg.cn/large/006tNc79ly1fsphx16ybdj30go06st8q.jpg' height="60"/>|


## 上架情况

截至2019.02.16

- 酷安[√] 7.6万
- 应用宝[√] 5489
- 魅族应用商店[√] 2982 
- 小米应用商店[√] 11,529
- OPPO应用商店[√] 5.4万
- VIVO应用商店[√] 12万
- 华为应用商店[√] 10万

更新一下在各应用商店的情况。在移除主界面账户登录的提示字样后，在华为应用商店终于过审上架啦。个人感觉好像华为用户比较多，今天登录华为开发者平台才发现这个下载量在短短不到一个月暴涨2w+，这真是出乎我的意料，毕竟自己一直在把下载量往酷安上引。关于评分的话，目测评分最低的是在OPPO应用商店，看到上面一些带评分的评论也是醉了，不过算啦。随着用户数量的增多，我渐渐意识到，用户的使用习惯是存在差异的，哪怕使用主体是在校大学生。更多人似乎都希望一种开箱即用的方式，而且是随自己喜好的开箱即用，而不喜欢自己去设置里面折腾 _(:з」∠)_ 已经有点厌烦反反复复被问到的一些使用问题，大部分都是我已经在设置算是精心布置好了的。我甚至已经想为App做一个问答系统了ﾍ(;´Д｀ﾍ)

下面是一些截图。

<div align="center">
    <img src="https://ws2.sinaimg.cn/large/006tNbRwgy1fw81qicpytj30dc0nqjxa.jpg" height="500">
    <img src="https://ws4.sinaimg.cn/large/006tNbRwgy1fw81qzeiwdj30dc0nqq7c.jpg" height="500">
    <img src="https://ws2.sinaimg.cn/large/006tNbRwgy1fw81sts3agj30dc0nqkef.jpg" height="500">
</div>


## 重构概况

这是WakeUp课程表的Kotlin重构版本，它重构的地方不仅仅是换了一种语言来编写这么简单。若只是单单地用Kotlin替换Java，那重构的意义其实也不太大🤔。在用户看来，这是一个界面更为友好、启动更为迅速、功能更为强大的版本。对我而言，此次重构的意义更在于设计模式的转变。

旧版从动手到首次发布，仅仅用了一周的时间，编写过程极为匆忙，很多地方都来不及细想，代码写得不甚规范，经常是一个Activity把所有与它有关的代码容纳进去了。后期维护起来看得确实有点头晕，而且需要扩展新功能的时候……嗯无从下手，老是用一些曲线救国的方法。我认为旧版从实现上最令人诟病的一点，应该是数据的存储过分依赖SharePreferences，其实核心的课程数据也是用SP来存储的🤣。旧版第一次引入数据库还是为了管理AppWidget桌面小部件，使用的是GreenDao，这种做法好像是在我设计[咩咩](https://github.com/YZune/YoungCommemoration)时想出来的。咩咩的桌面小部件方面我自认为是做得很完美了。

代码糟糕的旧版在前，重构时我就重视起了框架。MVC、MVP、MVVM……其实我是一直会纠结哪种框架是最佳实现的，也关注Google推荐哪种框架。然后就发现了[Android Architecture Components](https://developer.android.com/topic/libraries/architecture/)，谷歌在Google I/O 2017发布一套帮助开发者解决Android架构设计的方案，名字有点长……这套框架我这萌新在使用初期真的是各种不爽，但到了后期，写着写着就忍不住感叹这套框架是真的牛逼。不过关于这套框架的大部分中文资料都只是官方文档的翻译，会让人摸不着头脑。我会尝试具体写一下我使用这套框架的情况。

## 开源相关

### 使用的开源库

- 网络请求[Retrofit2](https://github.com/square/retrofit)
- 设计框架[Android Architecture Components](https://developer.android.com/topic/libraries/architecture/)
- HTML解析[jsoup](https://github.com/jhy/jsoup)
- 环形进度条[SmoothProgressBar](https://github.com/castorflex/SmoothProgressBar)
- 数据选择器[WheelPicker](https://github.com/AigeStudio/WheelPicker)
- 令RecyclerView的使用更简单[BaseRecyclerViewAdapterHelper](https://github.com/CymChad/BaseRecyclerViewAdapterHelper)
- 颜色选择器[ColorPicker](https://github.com/QuadFlask/colorpicker)
- 图片变换[glide-transformations](https://github.com/wasabeef/glide-transformations)
- 图片加载[Glide](https://github.com/bumptech/glide)
- 知乎的图片选择库[Matisse](https://github.com/zhihu/Matisse)
- Json解析[Gson](https://github.com/google/gson)
- 用户引导[TapTargetView](https://github.com/KeepSafe/TapTargetView)

### 其他

模拟登录和课程解析部分参考了[另一个课程表项目](https://github.com/mnnyang/ClassSchedule)，不过我对课程解析部分改动非常大，导入更为准确。

## TODO

- 集成“咩咩”（这意味着咩咩也要用Kotlin重构啦）
- 完善对方正教务课程的解析
- 适配已经提交数据的学校
- ~~数据备份和恢复（用课程文件导出导入实现了，还支持分享）~~
- ~~课程分享~~
- ~~增加对夏冬令时的支持（可以设置任意数量的时间表）~~
- 注册登录，小范围的社交，主要是为社团的活动服务
- 完全迁移至AndroidX
- 国际化

## License

```
Copyright 2018 YZune. https://github.com/YZune

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
 limitations under the License.
 ```