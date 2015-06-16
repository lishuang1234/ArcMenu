# ArcMenu

##半圆形弹出式菜单

 实现思路:
 1.自定义ViewGroup,构造函数获取配置值
 2.onLayout()确定子View包括一个功能按钮的位置,同时为功能按钮绑定监听
 3.功能按钮按下,处理本身动画(旋转),处理各个子Menu显示动画(移动和旋转),为各个MenuItem设置监听与回调,更新状态
 4.按下Menu时,设置各个MenuItem消失状态.

大神教程[传送门](http://blog.csdn.net/lmj623565791/article/details/37567907#java)

演示效果图：

![image](https://github.com/lishuang1234/ArcMenu/blob/master/ArcMenu/screenshort/optimized.gif)
