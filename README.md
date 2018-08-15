# 重构概况

<img src="https://ws4.sinaimg.cn/large/0069RVTdgy1fuaoudaytwj30e80e8jtg.jpg" height="256">

这是WakeUp课程表的Kotlin重构版本，它重构的地方不仅仅是换了一种语言来编写这么简单。若只是单单地用Kotlin替换Java，那重构的意义其实也不太大🤔。在用户看来，这是一个界面更为友好、启动更为迅速、功能更为强大的版本。对我而言，此次重构的意义更在于设计模式的转变。

先放一些截图。

<div align="center">
    <img src="https://ws4.sinaimg.cn/large/0069RVTdgy1fuaptapzioj30u01hcaib.jpg" height="500">
    <img src="https://ws2.sinaimg.cn/large/0069RVTdgy1fuaptksyycj30u01hcgqa.jpg" height="500">
    <img src="https://ws1.sinaimg.cn/large/0069RVTdgy1fuaptsxu6lj30u01hcwi5.jpg" height="500">
</div>

旧版从动手到首次发布，仅仅用了一周的时间，编写过程极为匆忙，很多地方都来不及细想，代码写得不甚规范，经常是一个Activity把所有与它有关的代码容纳进去了。后期维护起来看得确实有点头晕，而且需要扩展新功能的时候……嗯无从下手，老是用一些曲线救国的方法。我认为旧版从实现上最令人诟病的一点，应该是数据的存储过分依赖SharePreferences，其实核心的课程数据也是用SP来存储的🤣。旧版第一次引入数据库还是为了管理AppWidget桌面小部件，使用的是GreenDao，这种做法好像是在我设计[咩咩](https://github.com/YZune/YoungCommemoration)时想出来的。咩咩的桌面小部件方面我自认为是做得很完美了。

代码糟糕的旧版在前，重构时我就重视起了框架。MVC、MVP、MVVM……其实我是一直会纠结哪种框架是最佳实现的，也关注Google推荐哪种框架。然后就发现了[Android Architecture Components](https://developer.android.com/topic/libraries/architecture/)，谷歌在Google I/O 2017发布一套帮助开发者解决Android架构设计的方案，名字有点长……这套框架我这萌新在使用初期真的是各种不爽，但到了后期，写着写着就忍不住感叹这套框架是真的牛逼。不过关于这套框架的大部分中文资料都只是官方文档的翻译，会让人摸不着头脑。我会尝试具体写一下我使用这套框架的情况。

# 开源相关

