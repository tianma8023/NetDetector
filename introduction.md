# 原理分析与源码介绍

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

所以，这里采取动态注册 `BroadcastReceiver` 的方式。那么，应该在哪里动态注册呢？这里有两种思路：

1.  
    - 定义 `BroadcastReceiver` 监听网络状态，并提供回调接口 `NetStateChangeObserver` 用以回调网络状态的变化 
    - 抽象出 `BaseActivity` ，提供注册/取消注册 `BroadcastReceiver` 的方法，并实现 `NetStateChangeObserver`
    - 需要监听网络状态的 `Activity` 调用 `BaseActivity` 提供的方法即可

2.  
    - 定义 `BroadcastReceiver` 监听网络状态，并提供回调接口 `NetStateChangeObserver` 用以回调网络状态的变化，并在 `BroadcastReceiver` 中维护 `NetStateChangeObserver` 列表，当网络发生变化则通知这些 Observer ，实现回调。
    - 在 `Application` 中注册/取消注册 `BroadcastReceiver`
    - 抽象 `BaseActivity` ，提供注册/取消注册 `NetStateChangeObserver` 观察者的方法， 并实现 `NetStateChangeObserver`
    - 需要监听网络状态的 `Activity` 调用 `BaseActivity` 提供的方法即可

上面的两种思路，比较重要的区别在于，第1种是在 `Activity` 中注册 `BroadcastReceiver` , 第2种是在 `Application` 中注册 `BroadcastReceiver`。前者需要多次注册 `BroadcastReceiver` 而后者只注册一次，所以在这里选择第2种思路。

## 0x03 实现
#### 添加权限
```java
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
```
#### 定义网络类型
```java
public enum NetworkType {

    NETWORK_WIFI("WiFi"),
    NETWORK_4G("4G"),
    NETWORK_3G("3G"),
    NETWORK_2G("2G"),
    NETWORK_UNKNOWN("Unknown"),
    NETWORK_NO("No network");

    private String desc;
    NetworkType(String desc) {
        this.desc = desc;
    }

    @Override
    public String toString() {
        return desc;
    }
}
```
#### 定义观察者
```java
/**
 * 网络状态变化观察者
 */
public interface NetStateChangeObserver {

    void onNetDisconnected();

    void onNetConnected(NetworkType networkType);
}
```

#### 实现 BroadcastReceiver
```java
/**
 * 监听网络状态变化的BroadcastReceiver
 */
public class NetStateChangeReceiver extends BroadcastReceiver {

    private static class InstanceHolder {
        private static final NetStateChangeReceiver INSTANCE = new NetStateChangeReceiver();
    }

    private List<NetStateChangeObserver> mObservers = new ArrayList<>();

    public NetStateChangeReceiver() {

    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (ConnectivityManager.CONNECTIVITY_ACTION.equals(intent.getAction())) {
            NetworkType networkType = NetworkUtils.getNetworkType(context);
            notifyObservers(networkType);
        }
    }

    /**
     * 注册网络监听
     */
    public static void registerReceiver(@NonNull Context context) {
        IntentFilter intentFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        context.registerReceiver(InstanceHolder.INSTANCE, intentFilter);
    }

    /**
     * 取消网络监听
     */
    public static void unregisterReceiver(@NonNull Context context) {
        context.unregisterReceiver(InstanceHolder.INSTANCE);
    }

    /**
     * 注册网络变化Observer
     */
    public static void registerObserver(NetStateChangeObserver observer) {
        if (observer == null)
            return;
        if (!InstanceHolder.INSTANCE.mObservers.contains(observer)) {
            InstanceHolder.INSTANCE.mObservers.add(observer);
        }
    }

    /**
     * 取消网络变化Observer的注册
     */
    public static void unregisterObserver(NetStateChangeObserver observer) {
        if (observer == null)
            return;
        if (InstanceHolder.INSTANCE.mObservers == null)
            return;
        InstanceHolder.INSTANCE.mObservers.remove(observer);
    }

    /**
     * 通知所有的Observer网络状态变化
     */
    private void notifyObservers(NetworkType networkType) {
        if (networkType == NetworkType.NETWORK_NO) {
            for(NetStateChangeObserver observer : mObservers) {
                observer.onNetDisconnected();
            }
        } else {
            for(NetStateChangeObserver observer : mObservers) {
                observer.onNetConnected(networkType);
            }
        }
    }
}
```

#### Application 注册 BroadcastReceiver
```java
public class AppContext extends Application{

    @Override
    public void onCreate() {
        super.onCreate();
        // 注册BroadcastReceiver
        NetStateChangeReceiver.registerReceiver(this);
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        // 取消BroadcastReceiver注册
        NetStateChangeReceiver.unregisterReceiver(this);
    }
}
```

#### BaseActivity 抽取
```java
public class BaseActivity extends AppCompatActivity implements NetStateChangeObserver {

    @Override
    protected void onResume() {
        super.onResume();
        if (needRegisterNetworkChangeObserver()) {
            NetStateChangeReceiver.registerObserver(this);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (needRegisterNetworkChangeObserver()) {
            NetStateChangeReceiver.unregisterObserver(this);
        }
    }

    /**
     * 是否需要注册网络变化的Observer,如果不需要监听网络变化,则返回false;否则返回true.默认返回false
     */
    protected boolean needRegisterNetworkChangeObserver() {
        return false;
    }

    @Override
    public void onNetDisconnected() {
    }

    @Override
    public void onNetConnected(NetworkType networkType) {
    }
}
```
需要实现网络监听的 Activity 只需要复写 `needRegisterNetworkChangeObserver` 并返回 true ，并复写相关回调函数即可。


