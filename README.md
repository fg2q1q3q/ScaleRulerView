##ScreenShot
<img src="https://github.com/fg2q1q3q/ScaleRulerView/blob/master/img/s.gif?raw=true"/>
## 支持属性
```
<!-- 设置起始值 -->
        <attr name="rv_rule_start" format="integer"/>
        <!-- 设置结束值-->
        <attr name="rv_rule_end" format="integer"/>
        <!-- 设置默认刻度 -->
        <attr name="rv_rule_default_value" format="integer"/>
        <!-- 设置默认刻度后的精确度数-->
        <attr name="rv_rule_default_cell" format="integer"/>
        <!-- 设置大刻度内小刻度数量 -->
        <attr name="rv_item_cell_count" format="integer"/>
        <!-- 设置大刻度间隔值 -->
        <attr name="rv_item_value" format="integer"/>
        <!-- 设置单位 -->
        <attr name="rv_danwei" format="string"/>
        <!-- 设置当前值字体颜色-->
        <attr name="rv_value_font_color" format="color"/>
        <!-- 设置刻度尺背景色 -->
        <attr name="rv_rule_color" format="color"/>
        <!-- 设置大刻度数字字体颜色-->
        <attr name="rv_item_font_color" format="color"/>
        <!-- 设置小刻度高度，备注：大刻度高度为小刻度2倍，半数分割线高度为小刻度1.5倍-->
        <attr name="rv_short_height" format="dimension"/>
        <!-- 设置大刻度间隔宽度-->
        <attr name="rv_item_width" format="dimension"/>
        <!-- 设置大刻度数字字体大小-->
        <attr name="rv_item_font_size" format="dimension"/>
        <!-- 设置当前值字体大小-->
        <attr name="rv_value_font_size" format="dimension"/>
```
## 用法
##Gradle
在project 中build.gradle下增加(已有跳过)
```
	allprojects {
		repositories {
			...
			maven { url "https://jitpack.io" }
		}
	}
```
在app module中增加
```
	dependencies {
	        compile 'com.github.fg2q1q3q:ScaleRulerView:1.0'
	}
```
XML文件

```
    <scalerulerview.zxl.com.mylibrary.RulerView
        android:id="@+id/birthRulerView"
        android:layout_width="match_parent"
        android:layout_height="134dp"
        android:layout_marginBottom="10dp"
        android:layout_marginTop="10dp"
        android:background="@android:color/white"/>

    <scalerulerview.zxl.com.mylibrary.RulerView
        android:id="@+id/heightRulerView"
        android:layout_width="match_parent"
        android:layout_height="134dp"
        android:layout_marginBottom="10dp"
        android:layout_marginTop="10dp"
        app:rv_danwei="公斤"
        app:rv_item_cell_count="5"
        app:rv_item_font_color="@color/colorAccent"
        app:rv_item_font_size="24sp"
        app:rv_rule_start="40"
        app:rv_rule_end="200"
        app:rv_rule_default_value="51"
        app:rv_rule_default_cell="1"
        app:rv_item_value="1"
        app:rv_rule_color="#c9c9c9"
        app:rv_value_font_color="@color/colorPrimary"
        android:background="@android:color/white"/>
```
Java文件
```
mScaleRulerView.notifyView();
mScaleRulerView.setValueChangeListener(new RulerView.OnValueChangeListener() {
            @Override
            public void onValueChange(float value) {
            // you can get the value here
            }
        });
```

##联系我
如有疑问请提[issue](https://github.com/fg2q1q3q/ScaleRulerView/issues)或[<img src="http://pub.idqqimg.com/wpa/images/group.png"/>](http://shang.qq.com/wpa/qunwpa?idkey=03fba535df405bc7b1ad645bb8d34a0b3dbb97d284a906341dcecc98cdb84360)
