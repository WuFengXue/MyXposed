package com.newgame.reinhard.myxposed;


import android.app.Activity;
import android.app.Application;
import android.app.Service;
import android.content.pm.PackageManager;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.IXposedHookZygoteInit;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;
import timber.log.Timber;

/**
 * hook 工具集，封装了逆向分析常用的一些hook点。
 *
 * @author 李剑波
 * @date 17/3/13
 */

public class HookUtils implements IXposedHookLoadPackage, IXposedHookZygoteInit {

    /**
     * 是否打印钩子的目标类和方法，该功能主要用于分析第三方模块使用了哪些钩子
     */
    private static final boolean ENABLE_LOG_HOOKER = false;
    private static final String PKG = "com.garena.game.kgtw";

    @Override
    public void initZygote(StartupParam startupParam) throws Throwable {
        if (ENABLE_LOG_HOOKER) {
            hookXpApi(startupParam);
        }
    }

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam loadPackageParam) throws Throwable {
        Timber.plant(new Timber.DebugTree());
        hookGetPackageManager(loadPackageParam);
    }

    void hookGetPackageManager(XC_LoadPackage.LoadPackageParam lparam) {
        if (!PKG.equals(lparam.packageName)) {
            return;
        }

        XposedHelpers.findAndHookMethod("android.content.ContextWrapper", lparam.classLoader, "getPackageManager", new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                if (param.thisObject instanceof Application
                        || param.thisObject instanceof Activity
                        || param.thisObject instanceof Service) {
                    param.setResult(new MyPackageManager((PackageManager) param.getResult()));
                } else {
                    Timber.e(new Throwable("getPackageManager: unknown type of ContextWrapper"));
                }
            }
        });
    }

    void hookShowToast(XC_LoadPackage.LoadPackageParam lparam) {
        if (!PKG.equals(lparam.packageName)) {
            return;
        }

        XposedHelpers.findAndHookMethod("android.widget.Toast", lparam.classLoader, "show", new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                Timber.tag("girl").e(new Throwable());
            }
        });
    }

    /**
     * 用于分析第三方 XP 模块采用了哪些钩子，主要用于模块包体比较大，
     * 做了加固或者混淆做得比较彻底的场景
     * <p>
     * 钩子的目标类和方法名，可在 Xposed Installer 的日志中查看
     */
    private void hookXpApi(StartupParam param) {
        hookXpFindAndHookMethod();
        hookXpFindAndHookConstructor();
        hookXpHookAllMethods();
        hookXpHookAllConstructors();
    }

    private void hookXpFindAndHookMethod() {
        try {
            Class cls = XposedHelpers.findClass(
                    "de.robv.android.xposed.XposedHelpers",
                    HookUtils.class.getClassLoader());
            XposedHelpers.findAndHookMethod(cls, "findAndHookMethod",
                    Class.class, String.class, Object[].class, new XC_MethodHook() {
                        @Override
                        protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                            super.beforeHookedMethod(param);
                            XposedBridge.log("findAndHookMethod: cls = " + param.args[0]
                                    + ", method = " + param.args[1]);
                        }
                    });
        } catch (Exception e) {
            XposedBridge.log(e);
        }
    }

    private void hookXpFindAndHookConstructor() {
        try {
            Class cls = XposedHelpers.findClass(
                    "de.robv.android.xposed.XposedHelpers",
                    HookUtils.class.getClassLoader());
            XposedHelpers.findAndHookMethod(cls, "findAndHookConstructor",
                    Class.class, Object[].class, new XC_MethodHook() {
                        @Override
                        protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                            super.beforeHookedMethod(param);
                            XposedBridge.log("findAndHookConstructor: cls = " + param.args[0]);
                        }
                    });
        } catch (Exception e) {
            XposedBridge.log(e);
        }
    }

    private void hookXpHookAllMethods() {
        try {
            Class cls = XposedHelpers.findClass(
                    "de.robv.android.xposed.XposedBridge",
                    HookUtils.class.getClassLoader());
            Class clsXC_MethodHook = XposedHelpers.findClass(
                    "de.robv.android.xposed.XC_MethodHook", HookUtils.class.getClassLoader()
            );
            XposedHelpers.findAndHookMethod(cls, "hookAllMethods",
                    Class.class, String.class, clsXC_MethodHook, new XC_MethodHook() {
                        @Override
                        protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                            super.beforeHookedMethod(param);
                            XposedBridge.log("hookAllMethods: cls = " + param.args[0]
                                    + ", method = " + param.args[1]);
                        }
                    });
        } catch (Exception e) {
            XposedBridge.log(e);
        }
    }

    private void hookXpHookAllConstructors() {
        try {
            Class cls = XposedHelpers.findClass(
                    "de.robv.android.xposed.XposedBridge",
                    HookUtils.class.getClassLoader());
            Class clsXC_MethodHook = XposedHelpers.findClass(
                    "de.robv.android.xposed.XC_MethodHook", HookUtils.class.getClassLoader()
            );
            XposedHelpers.findAndHookMethod(cls, "hookAllConstructors",
                    Class.class, clsXC_MethodHook, new XC_MethodHook() {
                        @Override
                        protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                            super.beforeHookedMethod(param);
                            XposedBridge.log("hookAllConstructors: cls = " + param.args[0]);
                        }
                    });
        } catch (Exception e) {
            XposedBridge.log(e);
        }
    }
}
