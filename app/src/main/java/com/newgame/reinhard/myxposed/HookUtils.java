package com.newgame.reinhard.myxposed;


import android.app.Activity;
import android.app.Application;
import android.app.Service;
import android.content.pm.PackageManager;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

import timber.log.Timber;

/**
 * hook 工具集，封装了逆向分析常用的一些hook点。
 *
 * @author 李剑波
 * @date 17/3/13
 */

public class HookUtils implements IXposedHookLoadPackage {

    final String PKG = "com.garena.game.kgtw";

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam loadPackageParam) throws Throwable {
        Timber.plant(new Timber.DebugTree());
        hookGetPackageManager(loadPackageParam);
    }

    void hookGetPackageManager(XC_LoadPackage.LoadPackageParam lparam) {
        if (!PKG.equals(lparam.packageName))
            return;

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
        if (!PKG.equals(lparam.packageName))
            return;

        XposedHelpers.findAndHookMethod("android.widget.Toast", lparam.classLoader, "show", new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                Timber.tag("girl").e(new Throwable());
            }
        });
    }

}
