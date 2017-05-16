package com.cxb.familytree.ui.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.cxb.familytree.R;
import com.cxb.familytree.model.FamilyMember;
import com.cxb.familytree.utils.DisplayUtil;
import com.cxb.familytree.utils.GlideCircleTransform;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lenovo on 17/5/8.
 */

public class FamilyTreeView extends ViewGroup {

    private final int mMaxHeightDP = 800;//最大高度为800dp
    private final int mSpaceDP = 50;//间距为50dp
    private final int mLineWidthDP = 2;//连线宽度2dp

    private OnFamilySelectListener mOnFamilySelectListener;

    private int mScreenWidth;//屏幕宽度PX
    private int mScreenHeight;//屏幕高度PX

    private int mMaxWidthPX;//最大宽度PX
    private int mMaxHeightPX;//最大高度PX
    private int mSpacePX;//元素间距PX
    private int mLineWidthPX;//连线宽度PX
    private int mLastInterceptX;
    private int mLastInterceptY;

    private FamilyMember mFamilyMember;//我的
    private FamilyMember mMySpouse;//配偶
    private FamilyMember mMyFather;//父亲
    private FamilyMember mMyMother;//母亲
    private List<FamilyMember> mMyBrothers;//兄弟姐妹
    private List<FamilyMember> mMyChildren;//儿女

    private View mMineView;//我的View
    private View mSpouseView;//配偶View
    private View mFatherView;//父亲View
    private View mMotherView;//母亲View
    private List<View> mBrothersView;//兄弟姐妹View
    private View mPaternalGrandFatherView;//爷爷View
    private View mPaternalGrandMotherView;//奶奶View
    private View mMaternalGrandFatherView;//外公View
    private View mMaternalGrandMotherView;//外婆View
    private List<View> mChildrenView;//儿女View
    private List<View> mGrandChildrenView;//孙儿女View

    private int mGrandChildrenMaxWidth;//孙儿女所占总长度

    private Paint mPaint;//连线样式
    private Path mPath;//路径

    private int mCurrentX;//当前X轴偏移量
    private int mCurrentY;//当前Y轴偏移量
    private int mTouchX;//触摸点的X坐标
    private int mTouchY;//触摸点的Y坐标

    public FamilyTreeView(Context context) {
        this(context, null, 0);
    }

    public FamilyTreeView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FamilyTreeView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private void recycleAllView() {
        removeAllViews();
        mMineView = null;
        mSpouseView = null;
        mFatherView = null;
        mMotherView = null;
        mBrothersView = new ArrayList<>();
        mPaternalGrandFatherView = null;
        mPaternalGrandMotherView = null;
        mMaternalGrandFatherView = null;
        mMaternalGrandMotherView = null;
        mChildrenView = new ArrayList<>();
        mGrandChildrenView = new ArrayList<>();
    }

    private void initData() {
        mScreenWidth = DisplayUtil.getScreenWidth();
        mScreenHeight = DisplayUtil.getScreenHeight();
        mSpacePX = DisplayUtil.dip2px(mSpaceDP);
        mLineWidthPX = DisplayUtil.dip2px(mLineWidthDP);

        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.reset();
        mPaint.setColor(0xFF000000);
        mPaint.setStrokeWidth(mLineWidthPX);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setPathEffect(new DashPathEffect(new float[]{mLineWidthDP * 2, mLineWidthDP * 2}, 0));

        mPath = new Path();
        mPath.reset();

        mMySpouse = mFamilyMember.getSpouse();
        mMyFather = mFamilyMember.getFather();
        mMyMother = mFamilyMember.getMother();
        mMyBrothers = mFamilyMember.getBrothers();
        mMyChildren = mFamilyMember.getChildren();
    }

    private void initWidthAndHeight() {
        int[] widthDP = {
                350,//第一代最大宽度
                250,//第二代最大宽度
                50,//第三代最大宽度
                50,//第四代最大宽度
                50//第五代最大宽度
        };

        if (mMySpouse != null) {
            widthDP[2] += 100;
        }
        if (mMyBrothers != null) {
            widthDP[2] += 100 * mMyBrothers.size();
        }

        if (mMyChildren != null) {
            widthDP[3] += 100 * mMyChildren.size();
            int mGrandChildrenCount = 0;
            for (int i = 0; i < mMyChildren.size(); i++) {
                List<FamilyMember> grandChildrenList = mMyChildren.get(i).getChildren();
                if (grandChildrenList != null && grandChildrenList.size() > 0) {
                    mGrandChildrenCount += grandChildrenList.size();
                } else {
                    mGrandChildrenCount += 1;
                }
            }
            widthDP[4] = mGrandChildrenCount * 60 + mSpaceDP * (mGrandChildrenCount - 1);
            mGrandChildrenMaxWidth = DisplayUtil.dip2px(widthDP[4]);
        }

        mMaxWidthPX = mScreenWidth;
        for (int width : widthDP) {
            int widthPX = DisplayUtil.dip2px(width);
            if (widthPX > mMaxWidthPX) {
                mMaxWidthPX = widthPX;
            }
        }

        mMaxHeightPX = Math.max(DisplayUtil.dip2px(mMaxHeightDP), mScreenHeight);
    }

    private void initView() {
        mMineView = createFamilyView(mFamilyMember);
        if (mMySpouse != null) {
            mSpouseView = createFamilyView(mMySpouse);
        }

        if (mMyFather != null) {
            mFatherView = createFamilyView(mMyFather);
            FamilyMember myPaternalGrandFather = mMyFather.getFather();
            FamilyMember myPaternalGrandMother = mMyFather.getMother();
            if (myPaternalGrandFather != null) {
                mPaternalGrandFatherView = createFamilyView(myPaternalGrandFather);
            }
            if (myPaternalGrandMother != null) {
                mPaternalGrandMotherView = createFamilyView(myPaternalGrandMother);
            }

        }
        if (mMyMother != null) {
            mMotherView = createFamilyView(mMyMother);
            FamilyMember myMaternalGrandFather = mMyMother.getFather();
            FamilyMember myMaternalGrandMother = mMyMother.getMother();

            if (myMaternalGrandFather != null) {
                mMaternalGrandFatherView = createFamilyView(myMaternalGrandFather);
            }
            if (myMaternalGrandMother != null) {
                mMaternalGrandMotherView = createFamilyView(myMaternalGrandMother);
            }
        }

        mBrothersView.clear();
        if (mMyBrothers != null) {
            for (FamilyMember family : mMyBrothers) {
                mBrothersView.add(createFamilyView(family));
            }
        }

        mChildrenView.clear();
        if (mMyChildren != null) {
            for (FamilyMember family : mMyChildren) {
                mChildrenView.add(createFamilyView(family));
                List<FamilyMember> grandChildrens = family.getChildren();

                if (grandChildrens != null && grandChildrens.size() > 0) {
                    for (FamilyMember childFamily : grandChildrens) {
                        mGrandChildrenView.add(createFamilyView(childFamily));
                    }
                }
            }
        }
    }

    private View createFamilyView(FamilyMember family) {
        View familyView = LayoutInflater.from(getContext()).inflate(R.layout.item_family, this, false);
        ImageView ivAvatar = (ImageView) familyView.findViewById(R.id.iv_avatar);
        TextView tvCall = (TextView) familyView.findViewById(R.id.tv_call);

        familyView.setTag(family);
        String url = family.getMemberImg();
        if (!TextUtils.isEmpty(url)) {
            Glide.with(getContext())
                    .load(url)
                    .placeholder(R.drawable.family_avatar)
                    .error(R.drawable.family_avatar)
                    .centerCrop()
                    .transform(new GlideCircleTransform(getContext()))
                    .dontAnimate()
                    .into(ivAvatar);
        }
        if (family.isSelect()) {
            ivAvatar.setBackgroundResource(R.drawable.ic_avatar_background);
        }
        tvCall.setText(family.getCall());
        familyView.setOnClickListener(click);

        this.addView(familyView);
        return familyView;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        if (mMineView != null) {
            measureChild(mMineView, widthMeasureSpec, heightMeasureSpec);
        }
        if (mSpouseView != null) {
            measureChild(mSpouseView, widthMeasureSpec, heightMeasureSpec);
        }

        if (mFatherView != null) {
            measureChild(mFatherView, widthMeasureSpec, heightMeasureSpec);

            if (mPaternalGrandFatherView != null) {
                measureChild(mPaternalGrandFatherView, widthMeasureSpec, heightMeasureSpec);
            }
            if (mPaternalGrandMotherView != null) {
                measureChild(mPaternalGrandMotherView, widthMeasureSpec, heightMeasureSpec);
            }
        }
        if (mMotherView != null) {
            measureChild(mMotherView, widthMeasureSpec, heightMeasureSpec);

            if (mMaternalGrandFatherView != null) {
                measureChild(mMaternalGrandFatherView, widthMeasureSpec, heightMeasureSpec);
            }
            if (mMaternalGrandMotherView != null) {
                measureChild(mMaternalGrandMotherView, widthMeasureSpec, heightMeasureSpec);
            }
        }

        if (mBrothersView != null && mBrothersView.size() > 0) {
            for (View view : mBrothersView) {
                measureChild(view, widthMeasureSpec, heightMeasureSpec);
            }
        }

        if (mChildrenView != null && mChildrenView.size() > 0) {
            for (View view : mChildrenView) {
                measureChild(view, widthMeasureSpec, heightMeasureSpec);
            }

            if (mGrandChildrenView != null && mGrandChildrenView.size() > 0) {
                for (View view : mGrandChildrenView) {
                    measureChild(view, widthMeasureSpec, heightMeasureSpec);
                }
            }
        }

        setMeasuredDimension(mMaxWidthPX, mMaxHeightPX);
        scrollTo((mMaxWidthPX - mScreenWidth) / 2, (mMaxHeightPX - mScreenHeight) / 2);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if (mMineView != null) {
            int mineWidth = mMineView.getMeasuredWidth();
            int mineHeight = mMineView.getMeasuredHeight();
            int mineLeft = mMaxWidthPX / 2 - mineWidth / 2;
            int mineTop = mMaxHeightPX / 2 - mineHeight / 2;

            setChildFrame(mMineView, mineLeft, mineTop, mineWidth, mineHeight);

            if (mSpouseView != null) {
                setChildFrame(mSpouseView,
                        mineLeft + mineWidth + mSpacePX,
                        mineTop,
                        mineWidth, mineHeight);
            }

            if (mFatherView != null) {
                int fatherLeft = mineLeft;
                int fatherTop = mineTop - mineHeight - mSpacePX;

                if (mMotherView != null) {
                    fatherLeft -= mineWidth + mSpacePX;
                }

                setChildFrame(mFatherView, fatherLeft, fatherTop, mineWidth, mineHeight);

                if (mPaternalGrandFatherView != null) {
                    int grandFatherLeft = fatherLeft;

                    if (mPaternalGrandMotherView != null) {
                        grandFatherLeft -= mineWidth;
                    }

                    setChildFrame(mPaternalGrandFatherView,
                            grandFatherLeft, fatherTop - mineHeight - mSpacePX,
                            mineWidth, mineHeight);
                }

                if (mPaternalGrandMotherView != null) {
                    int grandMotherLeft = fatherLeft;

                    if (mPaternalGrandFatherView != null) {
                        grandMotherLeft += mineWidth;
                    }

                    setChildFrame(mPaternalGrandMotherView,
                            grandMotherLeft, fatherTop - mineHeight - mSpacePX,
                            mineWidth, mineHeight);
                }
            }

            if (mMotherView != null) {
                int motherLeft = mineLeft;
                int motherTop = mineTop - mineHeight - mSpacePX;

                if (mFatherView != null) {
                    motherLeft += mineWidth + mSpacePX;
                }

                setChildFrame(mMotherView, motherLeft, motherTop, mineWidth, mineHeight);

                if (mMaternalGrandFatherView != null) {
                    int grandFatherLeft = motherLeft;

                    if (mMaternalGrandMotherView != null) {
                        grandFatherLeft -= mineWidth;
                    }

                    setChildFrame(mMaternalGrandFatherView,
                            grandFatherLeft, motherTop - mineHeight - mSpacePX,
                            mineWidth, mineHeight);
                }

                if (mMaternalGrandMotherView != null) {
                    int grandMotherLeft = motherLeft;

                    if (mMaternalGrandFatherView != null) {
                        grandMotherLeft += mineWidth;
                    }

                    setChildFrame(mMaternalGrandMotherView,
                            grandMotherLeft, motherTop - mineHeight - mSpacePX,
                            mineWidth, mineHeight);
                }
            }

            if (mBrothersView != null && mBrothersView.size() > 0) {
                int brotherCount = mBrothersView.size();
                for (int i = 0; i < brotherCount; i++) {
                    View brotherView = mBrothersView.get(i);
                    setChildFrame(brotherView,
                            mineLeft - (i + 1) * (mineWidth + mSpacePX),
                            mineTop,
                            mineWidth, mineHeight);
                }
            }

            if (mGrandChildrenView != null && mGrandChildrenView.size() > 0) {
                int grandChildrenTop = mineTop + (mineHeight + mSpacePX) * 2;
                int grandChildrenLeft = mineLeft + mineWidth / 2 - mGrandChildrenMaxWidth / 2;

                int grandChildrenCount = mGrandChildrenView.size();

                int index = 0;
                for (int i = 0; i < mMyChildren.size(); i++) {
                    View childView = mChildrenView.get(i);
                    int childLeft = grandChildrenLeft;
                    int childTop = mineTop + mineHeight + mSpacePX;

                    FamilyMember myChild = mMyChildren.get(i);
                    List<FamilyMember> myGrandChildren = myChild.getChildren();
                    if (myGrandChildren != null && myGrandChildren.size() > 0) {
                        int startGrandChildLeft = grandChildrenLeft;
                        int endGrandChildLeft = grandChildrenLeft;

                        int myGrandChildrenCount = myGrandChildren.size();
                        for (int j = 0; j < myGrandChildrenCount; j++) {
                            View grandChildView = mGrandChildrenView.get(index);
                            setChildFrame(grandChildView, grandChildrenLeft, grandChildrenTop, mineWidth, mineHeight);
                            endGrandChildLeft = grandChildrenLeft;
                            grandChildrenLeft += mineWidth + mSpacePX;
                            index++;
                        }

                        childLeft = (endGrandChildLeft - startGrandChildLeft) / 2 + startGrandChildLeft;
                    } else {
                        grandChildrenLeft += mineWidth + mSpacePX;
                    }

                    setChildFrame(childView, childLeft, childTop, mineWidth, mineHeight);
                }
            } else {
                if (mMyChildren != null && mMyChildren.size() > 0) {
                    int childrenCount = mMyChildren.size();

                    int childLeft = (int) mMineView.getX() + mineWidth / 2 - (childrenCount * (mineWidth + mSpacePX) - mSpacePX) / 2;
                    int childTop = mineTop + mineHeight + mSpacePX;

                    for (View childView : mChildrenView) {
                        setChildFrame(childView, childLeft, childTop, mineWidth, mineHeight);
                        childLeft += mineWidth + mSpacePX;
                    }
                }
            }
        }
    }

    private void setChildFrame(View childView, int left, int top, int width, int height) {
        childView.layout(left, top, left + width, top + height);
    }

    @Override
    protected void onDraw(Canvas canvas) {
//        super.onDraw(canvas);
        drawSpouseLine(canvas);
        drawParentLine(canvas);
        drawBrothersLine(canvas);
        drawChildrenLine(canvas);
    }

    private void drawSpouseLine(Canvas canvas) {
        if (mSpouseView != null) {
            int horizontalLineStartX = (int) mMineView.getX() + mMineView.getWidth();
            int horizontalLineStopX = (int) mSpouseView.getX();
            int horizontalLineY = (int) mSpouseView.getY() + mSpouseView.getMeasuredWidth() / 2;
            mPath.reset();
            mPath.moveTo(horizontalLineStartX, horizontalLineY);
            mPath.lineTo(horizontalLineStopX, horizontalLineY);
            canvas.drawPath(mPath, mPaint);
        }
    }

    private void drawParentLine(Canvas canvas) {
        if (mFatherView != null || mMotherView != null) {
            int mineWidth = mMineView.getMeasuredWidth();
            int mineHeight = mMineView.getMeasuredHeight();
            int mineX = (int) mMineView.getX();
            int mineY = (int) mMineView.getY();

            int verticalLineX = mineX + mineWidth / 2;
            int verticalLineStartY = mineY - mSpacePX - mineHeight + mineWidth / 2;
            mPath.reset();
            mPath.moveTo(verticalLineX, verticalLineStartY);
            mPath.lineTo(verticalLineX, mineY);
            canvas.drawPath(mPath, mPaint);

            if (mFatherView != null && mMotherView != null) {
                int horizontalLineStartX = (int) mFatherView.getX() + mFatherView.getMeasuredWidth();
                int horizontalLineStopX = (int) mMotherView.getX();
                int horizontalLineY = (int) mFatherView.getY() + mFatherView.getMeasuredWidth() / 2;
                mPath.reset();
                mPath.moveTo(horizontalLineStartX, horizontalLineY);
                mPath.lineTo(horizontalLineStopX, horizontalLineY);
                canvas.drawPath(mPath, mPaint);
            }
        }

        if (mFatherView != null) {
            if (mPaternalGrandFatherView != null || mPaternalGrandMotherView != null) {
                int fatherWidth = mFatherView.getMeasuredWidth();
                int fatherHeight = mFatherView.getMeasuredHeight();
                int fatherX = (int) mFatherView.getX();
                int fatherY = (int) mFatherView.getY();

                int verticalLineX = fatherX + fatherWidth / 2;
                int verticalLineStartY = fatherY - mSpacePX - fatherHeight + fatherWidth / 2;
                mPath.reset();
                mPath.moveTo(verticalLineX, verticalLineStartY);
                mPath.lineTo(verticalLineX, fatherY);
                canvas.drawPath(mPath, mPaint);

                if (mPaternalGrandFatherView != null && mPaternalGrandMotherView != null) {
                    int horizontalLineStartX = (int) mPaternalGrandFatherView.getX() + mPaternalGrandFatherView.getMeasuredWidth();
                    int horizontalLineStopX = (int) mPaternalGrandMotherView.getX();
                    int horizontalLineY = (int) mPaternalGrandFatherView.getY() + mPaternalGrandFatherView.getMeasuredWidth() / 2;
                    mPath.reset();
                    mPath.moveTo(horizontalLineStartX, horizontalLineY);
                    mPath.lineTo(horizontalLineStopX, horizontalLineY);
                    canvas.drawPath(mPath, mPaint);
                }
            }
        }

        if (mMotherView != null) {
            if (mMaternalGrandFatherView != null || mMaternalGrandMotherView != null) {
                int motherWidth = mMotherView.getMeasuredWidth();
                int motherHeight = mMotherView.getMeasuredHeight();
                int motherX = (int) mMotherView.getX();
                int motherY = (int) mMotherView.getY();

                int verticalLineX = motherX + motherWidth / 2;
                int verticalLineStartY = motherY - mSpacePX - motherHeight + motherWidth / 2;
                mPath.reset();
                mPath.moveTo(verticalLineX, verticalLineStartY);
                mPath.lineTo(verticalLineX, motherY);
                canvas.drawPath(mPath, mPaint);

                if (mMaternalGrandFatherView != null && mMaternalGrandMotherView != null) {
                    int horizontalLineStartX = (int) mMaternalGrandFatherView.getX() + mMaternalGrandFatherView.getMeasuredWidth();
                    int horizontalLineStopX = (int) mMaternalGrandMotherView.getX();
                    int horizontalLineY = (int) mMaternalGrandFatherView.getY() + mMaternalGrandFatherView.getMeasuredWidth() / 2;

                    mPath.reset();
                    mPath.moveTo(horizontalLineStartX, horizontalLineY);
                    mPath.lineTo(horizontalLineStopX, horizontalLineY);
                    canvas.drawPath(mPath, mPaint);
                }
            }
        }
    }

    private void drawBrothersLine(Canvas canvas) {
        if (mBrothersView != null && mBrothersView.size() > 0) {
            int brotherCount = mBrothersView.size();
            View brotherView = mBrothersView.get(brotherCount - 1);

            int horizontalLineStartX = (int) (brotherView.getX() + brotherView.getMeasuredWidth());
            int horizontalLineY = (int) (brotherView.getY() + brotherView.getMeasuredWidth() / 2);
            mPath.reset();
            mPath.moveTo(horizontalLineStartX, horizontalLineY);
            mPath.lineTo(mMineView.getX(), horizontalLineY);
            canvas.drawPath(mPath, mPaint);
        }
    }

    private void drawChildrenLine(Canvas canvas) {
        if (mMyChildren != null && mMyChildren.size() > 0) {
            int verticalLineX = (int) mMineView.getX() + mMineView.getMeasuredWidth() / 2;
            int verticalLineStartY = (int) mMineView.getY() + mMineView.getMeasuredHeight();
            int verticalLinesStopY = verticalLineStartY + mSpacePX + mMineView.getMeasuredWidth() / 2;
            mPath.reset();
            mPath.moveTo(verticalLineX, verticalLineStartY);
            mPath.lineTo(verticalLineX, verticalLinesStopY);
            canvas.drawPath(mPath, mPaint);

            int index = 0;
            int childrenViewCount = mChildrenView.size();
            for (int i = 0; i < childrenViewCount; i++) {
                View startChildView = mChildrenView.get(i);
                if (i < childrenViewCount - 1) {
                    View endChildView = mChildrenView.get(i + 1);
                    int horizontalLineStartX = (int) startChildView.getX() + startChildView.getMeasuredWidth();
                    int horizontalLineStopX = (int) endChildView.getX();
                    int horizontalLineY = (int) startChildView.getY() + startChildView.getMeasuredWidth() / 2;
                    mPath.reset();
                    mPath.moveTo(horizontalLineStartX, horizontalLineY);
                    mPath.lineTo(horizontalLineStopX, horizontalLineY);
                    canvas.drawPath(mPath, mPaint);
                }

                List<FamilyMember> grandChildren = mMyChildren.get(i).getChildren();
                if (grandChildren != null) {
                    int grandChildrenCount = grandChildren.size();
                    for (int j = 0; j < grandChildrenCount - 1; j++) {
                        View startView = mGrandChildrenView.get(j + index);
                        View endView = mGrandChildrenView.get(j + 1 + index);
                        int hLineStartX = (int) startView.getX() + startView.getMeasuredWidth();
                        int hLineY = (int) startView.getY() + startView.getMeasuredWidth() / 2;
                        int hLineStopX = (int) endView.getX();
                        mPath.reset();
                        mPath.moveTo(hLineStartX, hLineY);
                        mPath.lineTo(hLineStopX, hLineY);
                        canvas.drawPath(mPath, mPaint);
                    }

                    if (grandChildrenCount > 0) {
                        View grandChildView = mGrandChildrenView.get(index);
                        int vLineX = (int) startChildView.getX() + startChildView.getMeasuredWidth() / 2;
                        int vLineStopY = (int) startChildView.getY() + startChildView.getMeasuredHeight();
                        int hLineY = (int) grandChildView.getY() + grandChildView.getMeasuredWidth() / 2;
                        mPath.reset();
                        mPath.moveTo(vLineX, hLineY);
                        mPath.lineTo(vLineX, vLineStopY);
                        canvas.drawPath(mPath, mPaint);

                        index += grandChildrenCount;
                    }
                }
            }
        }
    }

    public void setFamilyMember(FamilyMember mFamilyMember) {
        this.mFamilyMember = mFamilyMember;
        mFamilyMember.setSelect(true);
        recycleAllView();
        initData();
        initWidthAndHeight();
        initView();
        invalidate();
    }

    public void setmOnFamilySelectListener(OnFamilySelectListener mOnFamilySelectListener) {
        this.mOnFamilySelectListener = mOnFamilySelectListener;
    }

    private OnClickListener click = new OnClickListener() {
        @Override
        public void onClick(View v) {
            if (mOnFamilySelectListener != null) {
                mOnFamilySelectListener.onFamilySelect((FamilyMember) v.getTag());
            }
        }
    };

    public interface OnFamilySelectListener {
        void onFamilySelect(FamilyMember family);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
//        Logger.d(mTouchX + "  " + mTouchY);
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
//                mCurrentX = getScrollX();
//                mCurrentY = getScrollY();
//                mTouchX = (int) event.getX();
//                mTouchY = (int) event.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                int currentTouchX = (int) event.getX();
                int currentTouchY = (int) event.getY();

                int dx = currentTouchX - mTouchX;
                int dy = currentTouchY - mTouchY;

                mCurrentX -= dx;
                mCurrentY -= dy;

                int minMoveX = -mScreenWidth;
                int minMoveY = -mScreenHeight;

                if (mCurrentX < minMoveX) {
                    mCurrentX = minMoveX;
                } else if (mCurrentX > mMaxWidthPX) {
                    mCurrentX = mMaxWidthPX;
                }

                if (mCurrentY < minMoveY) {
                    mCurrentY = minMoveY;
                } else if (mCurrentY > mMaxHeightPX) {
                    mCurrentY = mMaxHeightPX;
                }

                this.scrollTo(mCurrentX, mCurrentY);
                mTouchX = currentTouchX;
                mTouchY = currentTouchY;
                break;
            case MotionEvent.ACTION_UP:

                break;
        }
        return true;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        boolean intercerpt = false;
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mLastInterceptX = (int) ev.getX();
                mLastInterceptY = (int) ev.getY();
                mCurrentX = getScrollX();
                mCurrentY = getScrollY();
                mTouchX = (int) ev.getX();
                mTouchY = (int) ev.getY();
                intercerpt = false;
                break;
            case MotionEvent.ACTION_MOVE:
                int dx = Math.abs((int) ev.getX() - mLastInterceptX);
                int dy = Math.abs((int) ev.getY() - mLastInterceptY);
                if (dx < 1 && dy < 1) {
                    intercerpt = false;
                } else {
                    intercerpt = true;
                }
                break;
            case MotionEvent.ACTION_UP:
                intercerpt = false;
                break;
        }
        return intercerpt;
    }
}
