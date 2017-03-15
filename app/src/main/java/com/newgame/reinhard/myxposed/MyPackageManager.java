package com.newgame.reinhard.myxposed;

import android.annotation.TargetApi;
import android.content.ComponentName;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.FeatureInfo;
import android.content.pm.InstrumentationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageInstaller;
import android.content.pm.PackageManager;
import android.content.pm.PermissionGroupInfo;
import android.content.pm.PermissionInfo;
import android.content.pm.ProviderInfo;
import android.content.pm.ResolveInfo;
import android.content.pm.ServiceInfo;
import android.content.pm.Signature;
import android.content.res.Resources;
import android.content.res.XmlResourceParser;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.UserHandle;
import android.util.Log;

import java.util.List;

import timber.log.Timber;

/**
 * 通过 hook PackageManager 的 getPackageInfo 方法以实现java层的签名hook。
 *
 * @author 李剑波
 * @date 17/3/14
 */

public class MyPackageManager extends PackageManager {

    public static final int SIG_INDEX_PKG_NAME = 0;
    public static final int SIG_INDEX_SIGNATURE_0 = 1;
    public static final String[][] SIGNATURES = {{"com.ztgame.bob",
            "3082019b30820104a003020102020424cd94a7300d06092a864886f70d01010505003011310f300d060355040a0c067a7467616d653020170d3135303532353134323531355a180f32303635303531323134323531355a3011310f300d060355040a0c067a7467616d6530819f300d06092a864886f70d010101050003818d0030818902818100a56f4581ad3b642dcef975c069df5a0fcb704c013fef272bde263263d2e1d5fe4526cab667602043b4e1647d86f31f4daddcd2db78d6827fbc239176f2d184fc3cd278cdaf4c14018c859c12c82e0c90cd3b4d68847aa69f037e481775e73d505380f7a007b77bb0aac3c0ff5e246381a9da1730b3a0ffae5ee11a6b9bd960210203010001300d06092a864886f70d0101050500038181004f4512e6bbcf9753bff558be2d3f30d6a025af536085d3397447427aa8da7b27817c138e21ebc4633322a0283d4073985e81941cd152e15d8167f628052a9df4a55fc3db05279196039347714ff54d1ba175632fd584b59f0525548a4dec9e0cb553579e26ffc68fd1a38f4009dd774d443dddb718e1c3bdc99378d50a00aee4"},
            {"com.garena.game.kgtw", "3082019d30820106a003020102020463257a3f300d06092a864886f70d010105050030123110300e060355040a0c0754656e63656e743020170d3136303331353039303431335a180f32303636303330333039303431335a30123110300e060355040a0c0754656e63656e7430819f300d06092a864886f70d010101050003818d0030818902818100b53e640f86ee356dfa59ea1459c460250b883d95e539a8887dcfab30d836f10e5ccd90c73a2b931ec4848afd0c37393b87a33a8a66e9e5f56617d1fe86ce271023834395e89eac5ed7dd1f0b3964c9197d6d531c62fb33c9db8e244ea3beb5da0aa73701bebbaade9280aff8b8b2af294d65ce259a1c81842fdb9c5ca1fea1470203010001300d06092a864886f70d01010505000381810099a327d149220ac199fed36a7c6fc026237f9589b473b9f2454266259f7a92718b17b571ffea7cd4c0c7e44478142ac3273bd6e5af7eb2025054d23797505e4cd17d1f69be8ba5f814cd6b4ae9edd45dfe3513a221907fb2eff10a4f5eefe4818dfc093ab19628231068790cc3b615a5d103341e6ab88c6438d3f02f5d947d55"},};


    PackageManager oldPm;

    public MyPackageManager(PackageManager pm) {
        this.oldPm = pm;
    }

    public static Signature getAppSignature0(String pkgName) {
        Timber.tag("boy").e("getAppSignature0: pkgName = " + pkgName);
        Timber.tag("boy").e(Log.getStackTraceString(new Throwable()));
        for (String[] sig : SIGNATURES) {
            if (sig[SIG_INDEX_PKG_NAME].equals(pkgName)) {
                Timber.tag("boy").e("getAppSignature0: success! sig = " + sig[SIG_INDEX_SIGNATURE_0]);
                return new Signature(sig[SIG_INDEX_SIGNATURE_0]);
            }
        }

        return null;
    }

    @Override
    public PackageInfo getPackageInfo(String packageName, int flags) throws NameNotFoundException {
        PackageInfo info = oldPm.getPackageInfo(packageName, flags);
        if ((flags & 0x40) == GET_SIGNATURES) {
            Signature signature = getAppSignature0(packageName);
            Log.e("girl", "getSignature: pkg = " + packageName);
            if (signature != null) {
                info.signatures[0] = signature;
                Log.e("girl", "hook signature: new sig = " + signature.toCharsString());
                Log.e("girl", "hook signature: stack trace = " + Log.getStackTraceString(new Throwable()));
            }
        }
        return info;
    }

    @Override
    public String[] currentToCanonicalPackageNames(String[] names) {
        return oldPm.currentToCanonicalPackageNames(names);
    }

    @Override
    public String[] canonicalToCurrentPackageNames(String[] names) {
        return oldPm.canonicalToCurrentPackageNames(names);
    }

    @Override
    public Intent getLaunchIntentForPackage(String packageName) {
        return oldPm.getLaunchIntentForPackage(packageName);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public Intent getLeanbackLaunchIntentForPackage(String packageName) {
        return oldPm.getLeanbackLaunchIntentForPackage(packageName);
    }

    @Override
    public int[] getPackageGids(String packageName) throws NameNotFoundException {
        return oldPm.getPackageGids(packageName);
    }

    @TargetApi(Build.VERSION_CODES.N)
    @Override
    public int[] getPackageGids(String packageName, int flags) throws NameNotFoundException {
        return oldPm.getPackageGids(packageName, flags);
    }

    @TargetApi(Build.VERSION_CODES.N)
    @Override
    public int getPackageUid(String packageName, int flags) throws NameNotFoundException {
        return oldPm.getPackageUid(packageName, flags);
    }

    @Override
    public PermissionInfo getPermissionInfo(String name, int flags) throws NameNotFoundException {
        return oldPm.getPermissionInfo(name, flags);
    }

    @Override
    public List<PermissionInfo> queryPermissionsByGroup(String group, int flags) throws NameNotFoundException {
        return oldPm.queryPermissionsByGroup(group, flags);
    }

    @Override
    public PermissionGroupInfo getPermissionGroupInfo(String name, int flags) throws NameNotFoundException {
        return oldPm.getPermissionGroupInfo(name, flags);
    }

    @Override
    public List<PermissionGroupInfo> getAllPermissionGroups(int flags) {
        return oldPm.getAllPermissionGroups(flags);
    }

    @Override
    public ApplicationInfo getApplicationInfo(String packageName, int flags) throws NameNotFoundException {
        return oldPm.getApplicationInfo(packageName, flags);
    }

    @Override
    public ActivityInfo getActivityInfo(ComponentName component, int flags) throws NameNotFoundException {
        return oldPm.getActivityInfo(component, flags);
    }

    @Override
    public ActivityInfo getReceiverInfo(ComponentName component, int flags) throws NameNotFoundException {
        return oldPm.getReceiverInfo(component, flags);
    }

    @Override
    public ServiceInfo getServiceInfo(ComponentName component, int flags) throws NameNotFoundException {
        return oldPm.getServiceInfo(component, flags);
    }

    @Override
    public ProviderInfo getProviderInfo(ComponentName component, int flags) throws NameNotFoundException {
        return oldPm.getProviderInfo(component, flags);
    }

    @Override
    public List<PackageInfo> getInstalledPackages(int flags) {
        return oldPm.getInstalledPackages(flags);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    @Override
    public List<PackageInfo> getPackagesHoldingPermissions(String[] permissions, int flags) {
        return oldPm.getPackagesHoldingPermissions(permissions, flags);
    }

    @Override
    public int checkPermission(String permName, String pkgName) {
        return oldPm.checkPermission(permName, pkgName);
    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public boolean isPermissionRevokedByPolicy(String permName, String pkgName) {
        return oldPm.isPermissionRevokedByPolicy(permName, pkgName);
    }

    @Override
    public boolean addPermission(PermissionInfo info) {
        return oldPm.addPermission(info);
    }

    @Override
    public boolean addPermissionAsync(PermissionInfo info) {
        return oldPm.addPermissionAsync(info);
    }

    @Override
    public void removePermission(String name) {
        oldPm.removePermission(name);
    }

    @Override
    public int checkSignatures(String pkg1, String pkg2) {
        return oldPm.checkSignatures(pkg1, pkg2);
    }

    @Override
    public int checkSignatures(int uid1, int uid2) {
        return oldPm.checkSignatures(uid1, uid2);
    }

    @Override
    public String[] getPackagesForUid(int uid) {
        return oldPm.getPackagesForUid(uid);
    }

    @Override
    public String getNameForUid(int uid) {
        return oldPm.getNameForUid(uid);
    }

    @Override
    public List<ApplicationInfo> getInstalledApplications(int flags) {
        return oldPm.getInstalledApplications(flags);
    }

    @Override
    public String[] getSystemSharedLibraryNames() {
        return oldPm.getSystemSharedLibraryNames();
    }

    @Override
    public FeatureInfo[] getSystemAvailableFeatures() {
        return oldPm.getSystemAvailableFeatures();
    }

    @Override
    public boolean hasSystemFeature(String name) {
        return oldPm.hasSystemFeature(name);
    }

    @Override
    public boolean hasSystemFeature(String name, int version) {
        return false;
    }

    @Override
    public ResolveInfo resolveActivity(Intent intent, int flags) {
        return oldPm.resolveActivity(intent, flags);
    }

    @Override
    public List<ResolveInfo> queryIntentActivities(Intent intent, int flags) {
        return oldPm.queryIntentActivities(intent, flags);
    }

    @Override
    public List<ResolveInfo> queryIntentActivityOptions(ComponentName caller, Intent[] specifics, Intent intent, int flags) {
        return oldPm.queryIntentActivityOptions(caller, specifics, intent, flags);
    }

    @Override
    public List<ResolveInfo> queryBroadcastReceivers(Intent intent, int flags) {
        return oldPm.queryBroadcastReceivers(intent, flags);
    }

    @Override
    public ResolveInfo resolveService(Intent intent, int flags) {
        return oldPm.resolveService(intent, flags);
    }

    @Override
    public List<ResolveInfo> queryIntentServices(Intent intent, int flags) {
        return oldPm.queryIntentServices(intent, flags);
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    @Override
    public List<ResolveInfo> queryIntentContentProviders(Intent intent, int flags) {
        return oldPm.queryIntentContentProviders(intent, flags);
    }

    @Override
    public ProviderInfo resolveContentProvider(String name, int flags) {
        return oldPm.resolveContentProvider(name, flags);
    }

    @Override
    public List<ProviderInfo> queryContentProviders(String processName, int uid, int flags) {
        return oldPm.queryContentProviders(processName, uid, flags);
    }

    @Override
    public InstrumentationInfo getInstrumentationInfo(ComponentName className, int flags) throws NameNotFoundException {
        return oldPm.getInstrumentationInfo(className, flags);
    }

    @Override
    public List<InstrumentationInfo> queryInstrumentation(String targetPackage, int flags) {
        return oldPm.queryInstrumentation(targetPackage, flags);
    }

    @Override
    public Drawable getDrawable(String packageName, int resid, ApplicationInfo appInfo) {
        return oldPm.getDrawable(packageName, resid, appInfo);
    }

    @Override
    public Drawable getActivityIcon(ComponentName activityName) throws NameNotFoundException {
        return oldPm.getActivityIcon(activityName);
    }

    @Override
    public Drawable getActivityIcon(Intent intent) throws NameNotFoundException {
        return oldPm.getActivityIcon(intent);
    }

    @TargetApi(Build.VERSION_CODES.KITKAT_WATCH)
    @Override
    public Drawable getActivityBanner(ComponentName activityName) throws NameNotFoundException {
        return oldPm.getActivityBanner(activityName);
    }

    @TargetApi(Build.VERSION_CODES.KITKAT_WATCH)
    @Override
    public Drawable getActivityBanner(Intent intent) throws NameNotFoundException {
        return oldPm.getActivityBanner(intent);
    }

    @Override
    public Drawable getDefaultActivityIcon() {
        return oldPm.getDefaultActivityIcon();
    }

    @Override
    public Drawable getApplicationIcon(ApplicationInfo info) {
        return oldPm.getApplicationIcon(info);
    }

    @Override
    public Drawable getApplicationIcon(String packageName) throws NameNotFoundException {
        return oldPm.getApplicationIcon(packageName);
    }

    @TargetApi(Build.VERSION_CODES.KITKAT_WATCH)
    @Override
    public Drawable getApplicationBanner(ApplicationInfo info) {
        return oldPm.getApplicationBanner(info);
    }

    @TargetApi(Build.VERSION_CODES.KITKAT_WATCH)
    @Override
    public Drawable getApplicationBanner(String packageName) throws NameNotFoundException {
        return oldPm.getApplicationBanner(packageName);
    }

    @Override
    public Drawable getActivityLogo(ComponentName activityName) throws NameNotFoundException {
        return oldPm.getActivityLogo(activityName);
    }

    @Override
    public Drawable getActivityLogo(Intent intent) throws NameNotFoundException {
        return oldPm.getActivityLogo(intent);
    }

    @Override
    public Drawable getApplicationLogo(ApplicationInfo info) {
        return oldPm.getApplicationLogo(info);
    }

    @Override
    public Drawable getApplicationLogo(String packageName) throws NameNotFoundException {
        return oldPm.getApplicationLogo(packageName);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public Drawable getUserBadgedIcon(Drawable icon, UserHandle user) {
        return oldPm.getUserBadgedIcon(icon, user);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public Drawable getUserBadgedDrawableForDensity(Drawable drawable, UserHandle user, Rect badgeLocation, int badgeDensity) {
        return oldPm.getUserBadgedDrawableForDensity(drawable, user, badgeLocation, badgeDensity);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public CharSequence getUserBadgedLabel(CharSequence label, UserHandle user) {
        return oldPm.getUserBadgedLabel(label, user);
    }

    @Override
    public CharSequence getText(String packageName, int resid, ApplicationInfo appInfo) {
        return oldPm.getText(packageName, resid, appInfo);
    }

    @Override
    public XmlResourceParser getXml(String packageName, int resid, ApplicationInfo appInfo) {
        return oldPm.getXml(packageName, resid, appInfo);
    }

    @Override
    public CharSequence getApplicationLabel(ApplicationInfo info) {
        return oldPm.getApplicationLabel(info);
    }

    @Override
    public Resources getResourcesForActivity(ComponentName activityName) throws NameNotFoundException {
        return oldPm.getResourcesForActivity(activityName);
    }

    @Override
    public Resources getResourcesForApplication(ApplicationInfo app) throws NameNotFoundException {
        return oldPm.getResourcesForApplication(app);
    }

    @Override
    public Resources getResourcesForApplication(String appPackageName) throws NameNotFoundException {
        return oldPm.getResourcesForApplication(appPackageName);
    }

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    @Override
    public void verifyPendingInstall(int id, int verificationCode) {
        oldPm.verifyPendingInstall(id, verificationCode);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    public void extendVerificationTimeout(int id, int verificationCodeAtTimeout, long millisecondsToDelay) {
        oldPm.extendVerificationTimeout(id, verificationCodeAtTimeout, millisecondsToDelay);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    public void setInstallerPackageName(String targetPackage, String installerPackageName) {
        oldPm.setInstallerPackageName(targetPackage, installerPackageName);
    }

    @Override
    public String getInstallerPackageName(String packageName) {
        return oldPm.getInstallerPackageName(packageName);
    }

    @Override
    public void addPackageToPreferred(String packageName) {
        oldPm.addPackageToPreferred(packageName);
    }

    @Override
    public void removePackageFromPreferred(String packageName) {
        oldPm.removePackageFromPreferred(packageName);
    }

    @Override
    public List<PackageInfo> getPreferredPackages(int flags) {
        return oldPm.getPreferredPackages(flags);
    }

    @Override
    public void addPreferredActivity(IntentFilter filter, int match, ComponentName[] set, ComponentName activity) {
        oldPm.addPreferredActivity(filter, match, set, activity);
    }

    @Override
    public void clearPackagePreferredActivities(String packageName) {
        oldPm.clearPackagePreferredActivities(packageName);
    }

    @Override
    public int getPreferredActivities(List<IntentFilter> outFilters, List<ComponentName> outActivities, String packageName) {
        return oldPm.getPreferredActivities(outFilters, outActivities, packageName);
    }

    @Override
    public void setComponentEnabledSetting(ComponentName componentName, int newState, int flags) {
        oldPm.setComponentEnabledSetting(componentName, newState, flags);
    }

    @Override
    public int getComponentEnabledSetting(ComponentName componentName) {
        return oldPm.getComponentEnabledSetting(componentName);
    }

    @Override
    public void setApplicationEnabledSetting(String packageName, int newState, int flags) {
        oldPm.setApplicationEnabledSetting(packageName, newState, flags);
    }

    @Override
    public int getApplicationEnabledSetting(String packageName) {
        return oldPm.getApplicationEnabledSetting(packageName);
    }

    @Override
    public boolean isSafeMode() {
        return oldPm.isSafeMode();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public PackageInstaller getPackageInstaller() {
        return oldPm.getPackageInstaller();
    }
}
