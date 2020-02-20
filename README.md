# WakeUp课程表 3.612

[Google Play 下载](https://play.google.com/store/apps/details?id=com.suda.yzune.wakeupschedule.pro) | [酷安下载](https://www.coolapk.com/apk/159120)

## 声明

开源旨在可以降低后来者的门槛，借鉴可以，但是希望在相关 App 中能有所声明。

教务网页解析的部分单独抽出了一个库，见 [CourseAdapter](https://github.com/YZune/CourseAdapter)

近期要忙于毕设，欢迎大佬们 PR

## 上架情况

截至2020.02.10

- 酷安[√] 19万
- 应用宝[√] 12674
- 魅族应用商店[√] 21590
- 小米应用商店[√] 61799
- OPPO应用商店[√] 19.3万
- VIVO应用商店[√] 23万
- 华为应用商店[√] 51.9万

## 开源相关

### 集成的开源库

- AndroidX 项目
- [Kotlin](https://github.com/JetBrains/kotlin)
- [Material Design](https://github.com/material-components/material-components-android)
- [Retrofit2](https://github.com/square/retrofit)
- [Toasty](https://github.com/GrenderG/Toasty)
- [jsoup](https://github.com/jhy/jsoup)
- [NumberPickerView](https://github.com/Carbs0126/NumberPickerView)
- [BaseRecyclerViewAdapterHelper](https://github.com/CymChad/BaseRecyclerViewAdapterHelper)
- [ColorPicker](https://github.com/jaredrummler/ColorPicker)
- [Glide](https://github.com/bumptech/glide)
- [Gson](https://github.com/google/gson)
- [kotlin-csv](https://github.com/doyaaaaaken/kotlin-csv)
- [TextDrawable](https://github.com/jahirfiquitiva/TextDrawable)
- [Android-QuickSideBar](https://github.com/saiwu-bigkoo/Android-QuickSideBar/)
- [sticky-headers-recyclerview](https://github.com/timehop/sticky-headers-recyclerview)
- [biweekly](https://github.com/mangstadt/biweekly)
- [appcenter-sdk-android](https://github.com/microsoft/appcenter-sdk-android)

### 参考项目

苏大的正方教务模拟登录和课程解析部分参考了[另一个课程表项目](https://github.com/mnnyang/ClassSchedule)，不过我对课程解析部分改动非常大，导入更为准确。

## TODO

- 集成“咩咩”
- 支持课程笔记
- 直接写入系统日历
- ~~完善对方正教务课程的解析~~
- 适配已经提交数据的学校
- ~~数据备份和恢复（用课程文件导出导入实现了，还支持分享）~~
- ~~课程分享~~
- ~~增加对夏冬令时的支持（可以设置任意数量的时间表）~~
- 注册登录，小范围的社交，主要是为社团的活动服务
- 完全迁移至AndroidX
- 国际化

## License

```
Copyright 2019 YZune. https://github.com/YZune

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