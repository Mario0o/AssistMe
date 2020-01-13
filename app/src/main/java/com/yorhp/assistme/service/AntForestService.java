package com.yorhp.assistme.service;

import android.accessibilityservice.GestureDescription;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Path;
import android.os.SystemClock;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import com.yorhp.assistme.app.BaseAccessbilityService;
import com.yorhp.assistme.util.ScreenUtil;
import com.yorhp.assistme.util.color.LabColorLike;
import com.yorhp.recordlibrary.ScreenRecordUtil;

/**
 * @author yorhp
 * @date 2020-01-12
 */

public class AntForestService extends BaseAccessbilityService {

    public static final String TAG = "AntForestService";
    private static final String ANT_FOREST_PACKAGES_NAME = "com.eg.android.AlipayGphone";
    private static final String ANT_FOREST_HOME_RECYCLEVIEW_ID = "com.alipay.android.phone.openplatform:id/home_apps_grid";
    private static final String ANT_FOREST_TITLE = "蚂蚁森林";

    /**
     * 可获取能量的列表的颜色
     */
    private static final int COLLECTABLE_ITEM_COLOR = Color.parseColor("#1da06e");


    /**
     * 滑动到朋友周排行列表的比例
     */
    private static final float FIRST_SCROLL_MORE_SCALE = 0.08F;

    /**
     * 蚂蚁森林标题ID
     */
    private static final String ANT_FOREST_TITLE_ID = "com.alipay.mobile.nebula:id/h5_tv_title";

    /**
     * 蚂蚁森林返回ID
     */
    private static final String ANT_FOREST_BACK_ID = "com.alipay.mobile.nebula:id/h5_tv_nav_back";

    /**
     * 是否进入了蚂蚁森林界面
     */
    private static boolean openAntForestPage = false;


    /**
     * 滑动时间
     */
    private static final int SLIPPING_TIME = 500;

    /**
     * 滑动等待时间
     */
    private static final int WAIT_SLIPPING_TIME = 300;


    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {

        //当前包名
        String packageName = event.getPackageName().toString();
        Log.d(TAG, "packageName is：" + packageName);
        if (!ANT_FOREST_PACKAGES_NAME.equals(packageName)) {
            return;
        }

        //获取应用列表List控件
        AccessibilityNodeInfo nodeInfo = findViewByID(ANT_FOREST_HOME_RECYCLEVIEW_ID);
        if (nodeInfo != null) {
            for (int i = 0; i < nodeInfo.getChildCount(); i++) {
                //获取到子控件
                AccessibilityNodeInfo nodeInfoChild = nodeInfo.getChild(i);
                AccessibilityNodeInfo antForest = findViewByText(nodeInfoChild, ANT_FOREST_TITLE);
                if (antForest != null) {
                    //进入蚂蚁森林界面
                    performViewClick(antForest);
                    openAntForestPage = false;
                    return;
                }
            }
        }

        //当前类名
        String className = "";
        try {
            className = event.getClassName().toString();
            Log.d(TAG, "className is：" + className);
        } catch (Exception e) {
            e.printStackTrace();
        }

        //蚂蚁森林界面的标题控件
        nodeInfo = findViewByID(ANT_FOREST_TITLE_ID);

        String title = null;
        if (nodeInfo != null && nodeInfo.getText() != null) {
            title = nodeInfo.getText().toString();
        }
        //标题存在，且在朋友列表
        if (title != null && ANT_FOREST_TITLE.equals(title)) {
            nodeInfo = findViewByID(ANT_FOREST_BACK_ID);
            if (nodeInfo != null) {
                //在更多好友界面，判断是否有能量
                if (!uiScrolling) {
                    findEnergyItem();
                    return;
                }
            } else {
                //在蚂蚁森林界面
                if (!openAntForestPage) {
                    getEnergy();
                    isCollecting = false;
                    slipItem();
                }
                return;
            }
        }

        if (nodeInfo != null) {
            //在好友的森林里面
            if (title != null && !title.equals(ANT_FOREST_TITLE) && title.contains(ANT_FOREST_TITLE)) {
                //进行偷能量操作
                if (!isCollecting) {
                    getEnergy();
                    //返回上一页
                    performBackClick();
                    SystemClock.sleep(WAIT_SLIPPING_TIME);
                    isCollecting = false;
                }
            }
        }
    }

    /**
     * 正在偷能量中
     */
    private boolean isCollecting = false;

    /**
     * 森林界面能量最高的高度
     */
    private static final float START_HEIGHT_TOP_SCALE = 0.23F;

    /**
     * 森林界面能量最低的高度
     */
    private static final float START_HEIGHT_BOTTOM_SCALE = 0.384F;
    /**
     * 可获取的字的颜色
     */
    private static final int ENERGY_COLOR = Color.parseColor("#c4f947");


    /**
     * 收取能量
     */
    private void getEnergy() {
        isCollecting = true;
        Bitmap bitmap = ScreenRecordUtil.getInstance().getScreenShot();
        int width = bitmap.getWidth();
        int startPointY = (int) (bitmap.getHeight() * START_HEIGHT_TOP_SCALE);
        int endPointY = (int) (bitmap.getHeight() * START_HEIGHT_BOTTOM_SCALE);
        for (int y = startPointY; y < endPointY; y = y + 25) {
            for (int x = 0; x < width; x=x+10) {
                int currentColor = bitmap.getPixel(x, y);
                //可收取能量
                if (labColorLike.isLike(ENERGY_COLOR, currentColor, 10)) {
                    clickOnScreen(x, y - 40, 10, null);
                    SystemClock.sleep(50);
                }
            }
        }
    }

    /**
     * 界面正在执行滑动
     */
    private boolean uiScrolling = false;


    //初始化颜色识别类
    private LabColorLike labColorLike = new LabColorLike();


    /**
     * 找有能量的item
     */
    private void findEnergyItem() {
        //进行截屏
        Bitmap bitmap = ScreenRecordUtil.getInstance().getScreenShot();
        int height = bitmap.getHeight();
        int pointX = bitmap.getWidth() - 7;
        for (int y = 200; y < height - 100; y = y + 5) {
            int currentColor = bitmap.getPixel(pointX, y);
            //颜色相同
            if (labColorLike.isLike(COLLECTABLE_ITEM_COLOR, currentColor, 10)) {
                clickOnScreen(pointX, y + 10, 10, null);
                SystemClock.sleep(1000);
                return;
            }
        }
        //未找到按钮
        uiScrolling = true;
        performScrollDownward(ScreenUtil.SCREEN_HEIGHT - 100, ScreenUtil.SCREEN_HEIGHT / 2, new GestureResultCallback() {
            @Override
            public void onCompleted(GestureDescription gestureDescription) {
                super.onCompleted(gestureDescription);
                uiScrolling = false;
            }
        });
    }

    /**
     * 模拟界面向下滑操作
     */
    public void performScrollDownward(int start, int distance, GestureResultCallback callback) {
        Path path = new Path();
        path.moveTo(ScreenUtil.SCREEN_WIDTH / 2, start);
        path.lineTo(ScreenUtil.SCREEN_WIDTH / 2, start - distance);
        gestureOnScreen(path, 0, SLIPPING_TIME, callback);
    }

    /**
     * 模拟界面向上滑操作
     */
    public void performScrollUpward(int start, int distance, GestureResultCallback callback) {
        Path path = new Path();
        path.moveTo(ScreenUtil.SCREEN_WIDTH / 2, start);
        path.lineTo(ScreenUtil.SCREEN_WIDTH / 2, start + distance);
        gestureOnScreen(path, 0, SLIPPING_TIME, callback);
    }


    @Override
    public void onInterrupt() {

    }


    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        ScreenUtil.getScreenSize(this);
        //打开支付宝APP
        Intent intent = getPackageManager().getLaunchIntentForPackage(ANT_FOREST_PACKAGES_NAME);
        if (intent != null) {
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
    }


    /**
     * 滑动到查看更多好友按钮出现
     */
    private void slipItem() {
        openAntForestPage = true;
        Log.d(TAG, "find the AntForest page");
        SystemClock.sleep(1000);
        //滑动到查看更多好友按钮出现
        performScrollDownward(ScreenUtil.SCREEN_HEIGHT - 100, ScreenUtil.SCREEN_HEIGHT / 2, new GestureResultCallback() {
            @Override
            public void onCompleted(GestureDescription gestureDescription) {
                super.onCompleted(gestureDescription);
                SystemClock.sleep(WAIT_SLIPPING_TIME);
                performScrollDownward(ScreenUtil.SCREEN_HEIGHT - 100, (int) (ScreenUtil.SCREEN_HEIGHT * (0.5 + FIRST_SCROLL_MORE_SCALE)), new GestureResultCallback() {
                    @Override
                    public void onCompleted(GestureDescription gestureDescription) {
                        super.onCompleted(gestureDescription);
                        clickOnScreen(ScreenUtil.SCREEN_WIDTH / 2, ScreenUtil.SCREEN_HEIGHT * (1 - FIRST_SCROLL_MORE_SCALE / 2), 10, null);
                    }
                });
            }
        });
    }
}
