##描述
自定义圆形圆角图片，继承自ImageView，使用Picasso等框架时直接into(this)就okay了。<br>
主要用了两种方式去实现圆角图片，一种是通过BitmapShader,另一种则是Xfermode的dstin模式。后者采用了WeakRefrence作为图片的缓存方式。
##用法(usage):
type:round or circle<br>
borderRadius:determined numbers by dp
