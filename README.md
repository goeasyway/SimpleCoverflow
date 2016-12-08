# 老外的自定义View面试题实现篇

欠大家一篇文章，这段时间事情较多常在外面跑来跑去的，其本上没什么时间静下来写代码。然后看到了不少的网友给我反馈，想看一下是如何实现这个效果的：

![](http://upload-images.jianshu.io/upload_images/1685558-169d1fe875ddc041.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

我看到有人通过RecyclerView来实现这个效果，其实也可以，只是背离了考查自定义UI开发的目的。这里我做了一个简单的实现，当然是不完整的，我希望大家自己动手来完善它，那样这个实例中涉及的知识才能真正转化成你自己的技能。

这里简单说一下实现步骤：
>1. 实现自定义的圆角带阴影的View
2. 实现GroupView+Adapter完成布局
3. 实现滑动
