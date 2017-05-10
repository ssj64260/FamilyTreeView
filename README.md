# FamilyTreeView
### 家谱树绘制Demo，暂时还在开发中，主要使用自定义ViewGroup，和使用canvas进行划线，现实现了自己、配偶、兄弟姐妹、父母、祖父母、外祖父母、子女以及孙子的绘制，加入了touch事件可以移动。代码可能写得相对比较死，基本只能使用于家谱展示

## 预览图
UI相对比较丑，用着先，以后再改

![静态预览图](https://raw.githubusercontent.com/ssj64260/FamilyTreeView/master/image/MainActivityUI.png)

![动态预览图](https://raw.githubusercontent.com/ssj64260/FamilyTreeView/master/image/TGqRvy.gif)

## Logcat展示
Logcat里展示的是家庭成员关系对象对应的Json格式
![](https://raw.githubusercontent.com/ssj64260/FamilyTreeView/master/image/logcat%E5%B1%95%E7%A4%BA.png)

## model对象
![](https://raw.githubusercontent.com/ssj64260/FamilyTreeView/master/image/model%E5%AF%B9%E8%B1%A1.png)

## 思路说明
#### 1、代码执行流程
	1.1、首先当然时初始化数据，包括家庭成员对象、画笔等到。
	1.2、然后就是根据家庭成员对象，测量布局后ViewGroup的宽高，这里我是用了比较粗暴的测量方法，具体可以去看initWidthAndHeight()方法。
	1.3、初始化View，把家庭成员的View都初始化。然后调用invalidate()更新界面。
	1.4、在onMeasure测量家庭成员View的宽高。
	1.5、在onLayout里编排好每个成员的具体位置。
	1.6、在onDraw方法里，利用canvas画出家庭成员关系的连线。
	1.7、最后在onTouchEvent方法写下触摸事件移动ViewGroup，在onInterceptTouchEvent写下移动时是否拦截成员View的点击事件。
  
#### 2、
