# gt_SlideMenu
侧滑菜单（仿qq5.0版本的侧滑效果）

## 本项目整体通过自定义ViewGroup来实现
  继承FrameLayout，实现主界面和菜单界面的包裹

## 利用了V4包中的ViewDragHelper实现菜单与主界面的拖拽：
  1.ViewDragHelper主要用于处理ViewGroup中对子View的拖拽处理
  
  2.它是Google在2013年开发者大会提出的 
  
  3.它主要封装了对View的触摸位置，触摸速度，移动距离等的检测和Scroller,通过接口回调的方式告诉我们;只需要我们指定是否需要移动，移动多少等;  
  
  4.本质是对触摸事件的解析类（类似于GestureDetector手势识别类）

## 利用的nineoldandroids这个动画库，来实现了打开，关闭和拖拽中的动画处理
  nineoldandroids动画封装了属性动画，进行了深度的定制，使用起来较属性动画来说更加简单，方便，更利于开发！    

