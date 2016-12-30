# NetDetector
A sample of detecting the changement of network state on Android device by using BroadcastReceiver.

通过使用BroadcastReceiver来检测Android设备网络变化的示例
========

## 0x01 目标
在实际开发中，我们不可避免地需要对请求错误进行处理，通常情况下，我们会这样去处理错误请求：
1. 没有网络的情况下，提示用户网络连接不可用，引导用户打开网络或重新刷新等
2. 有网络的情况下，则是客户端或服务端的错误，给用户相应的提示

如果针对第一种情况,我们需要在网络恢复的时候重新刷新数据或进行其他操作,又应该如何实现呢？以下就是我们的目标：
* 监听Android设备网络状态
* 在网络状态发生改变时,做出相应操作

在示例中，我们在网络状态发生变化时，显示当前网络变化的类型。

## 0x02 思路
在Android系统在网络变化的情况下，会发出 action 为 `ConnectivityManager.CONNECTIVITY_ACTION` 的系统广播，我们只需要注册 `BroadcastReceiver` 去监听该广播即可监听设备的网络变化情况。

那么，注册  `BroadcastReceiver` 是静态注册呢，还是动态注册呢？
* 静态注册：通常来讲，退出应用后，该应用仍然能够接收到相应的广播
* 动态注册：随着所在Context或应用被销毁后，不会收到相应的广播

**注意**：针对静态注册，这里是用“通常来讲”来修饰的，也就是说，存在特殊情况，即：存在即使使用静态注册，也不会收到相应的广播的情况：
> Android3.1之后，系统为了加强了安全性控制，应用程序安装后或是(设置)应用管理中被强制关闭后处于stopped状态，在这种状态下接收不到任何广播，除非广播带有 `FLAG_INCLUDE_STOPPED_PACKAGES` 标志，而默认所有系统广播都是 `FLAG_EXCLUDE_STOPPED_PACKAGES` 的，所以就没法通过系统广播自启动了。 这其中就包括 `ConnectivityManager.CONNECTIVITY_ACTION` 。     
> 关于这一块的内容，不是本篇重点，欲了解详情，请移步[Android应用为何开机自启动、自启动失败原因](http://www.trinea.cn/android/android-boot_completed-not-work/)

另外，Android 7.0 移除了三个隐式广播([Android 7.0 行为变更](https://developer.android.google.cn/about/versions/nougat/android-7.0-changes.html#bg-opt))，其中就包括 `ConnectivityManager.CONNECTIVITY_ACTION` , 这意味着通过静态注册 `BroadcastReceiver` 来监听该广播的方式在 `targetSdkVersion >= 24` 版本上不再生效，如何解决这一问题请移步 [Android 7.0 网络变化监听](http://relex.me/android-7-connectivity_action/)。

所以，这里采取动态注册的方式。



