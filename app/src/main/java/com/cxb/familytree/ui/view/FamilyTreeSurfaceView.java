package com.cxb.familytree.ui.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.cxb.familytree.model.FamilyMember;
import com.cxb.familytree.utils.DisplayUtil;
import com.orhanobut.logger.Logger;

/**
 * 家谱树
 */

public class FamilyTreeSurfaceView extends SurfaceView implements SurfaceHolder.Callback {

    private final int ACTION_START_DRAW = 0;//开始画
    private final int ACTION_DRAW_FINISH = 1;//画完

    private final int ACTION_REFRESH_UI = 2;//更新UI

    private SurfaceHolder mSurfaceHolder;

    private HandlerThread mWorkThread;
    private Handler mWorkHandler;

    private Handler mUIHandler;

    private boolean isRunning = false;
    private Canvas mCanvas;

    private int screenWidth;
    private int screenHeight;

    private FamilyMember mFamilyMember;

    private int avatarWidth;//头像宽高
    private Paint mAvatarPaint;//头像画笔

    public FamilyTreeSurfaceView(Context context) {
        this(context, null, 0);

    }

    public FamilyTreeSurfaceView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FamilyTreeSurfaceView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        initThread();
        initData();

        mSurfaceHolder = getHolder();
        mSurfaceHolder.addCallback(this);
    }

    private void initData() {
        screenWidth = DisplayUtil.getScreenWidth();
        screenHeight = DisplayUtil.getScreenHeight();
        avatarWidth = DisplayUtil.dip2px(100);

        mAvatarPaint = new Paint();
        mAvatarPaint.setColor(Color.RED);
        mAvatarPaint.setStyle(Paint.Style.FILL);
    }

    private void initThread() {
        mUIHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (msg.what == ACTION_REFRESH_UI) {

                }
            }
        };

        mWorkThread = new HandlerThread("DrawFamilyTree");
        mWorkThread.start();
        mWorkHandler = new Handler(mWorkThread.getLooper()) {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (msg.what == ACTION_START_DRAW) {

                } else if (msg.what == ACTION_DRAW_FINISH) {
                    mUIHandler.sendEmptyMessage(ACTION_REFRESH_UI);
                }
            }
        };
    }

    public void setFamilyData(FamilyMember familyData) {
        this.mFamilyMember = familyData;
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        Logger.d("surfaceCreated");
        isRunning = true;
        mWorkHandler.post(new Runnable() {
            @Override
            public void run() {
                while (isRunning) {
                    try {
                        synchronized (mSurfaceHolder) {
                            mCanvas = mSurfaceHolder.lockCanvas(null);
                            drawTree();
                        }
                    } finally {
                        mSurfaceHolder.unlockCanvasAndPost(mCanvas);
                    }
                }
            }
        });
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        Logger.d("surfaceChanged");
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        Logger.d("surfaceDestroyed");
        isRunning = false;
    }

    private void drawTree() {
        if (mFamilyMember != null) {

        }

        mCanvas.drawColor(Color.WHITE);
        mCanvas.translate(screenWidth / 2, screenHeight / 2);
        mCanvas.drawCircle(0, 0, avatarWidth, mAvatarPaint);
    }
}
