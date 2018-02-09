# NetDetector
A simple library that can detect network state changes on Android device by using BroadcastReceiver.

通过使用BroadcastReceiver来检测Android设备网络变化的库

## 功能
- 监听网络状态，当网络状态改变时进行回调

## Dependency 导入
#### Android Studio / Gradle
1. 要使用这个库，需要把 `jitpack.io` 添加到仓库列表中，在 **项目根目录** 的 `build.gradle` 中添加：
    ```Groovy
    allprojects {
        repositories {
            jcenter()
            maven { url 'https://jitpack.io' }
        }
    }
    ```
2. 在相应 **模块** 的 `build.gradle` 中加入库的依赖：
    ```Groovy
        dependencies {
                compile 'com.github.tianma8023:NetDetector:v0.2.0'
        }
    ```

## 使用
1. 添加权限
    ```xml
    <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
    ```

2. Application 注册 BroadcastReceiver：
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

3. BaseActivity 抽取，在 `BaseActivity` 中默认实现接口 `NetStateChangeObserver` 并提供是否需要注册 `Observer` 的方法：
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
4. 需要实现网络监听的 Activity 只需要复写方法 `needRegisterNetworkChangeObserver` 并返回 `true` ，并复写相关回调函数即可。

## 示例 & 源码介绍
- 本项目中的 `app` 模块就是示例，也可以参考示例程序 [demo.apk](/apk/demo.apk)
- 源码介绍请移步 [原理分析&源码介绍](./introduction.md)