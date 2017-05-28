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
 * 家谱树自定义ViewGroup
 */

public class FamilyTreeView extends ViewGroup {

    private static final int MAX_HEIGHT_DP = 850;//最大高度为850dp
    private static final int MINE_TOP_DP = 350;//选中成员Y坐标350DP
    private static final int SPACE_WIDTH_DP = 50;//间距为50dp
    private static final int ITEM_WIDTH_DP = 60;//家庭成员View宽度60dp
    private static final int ITEM_HEIGHT_DP = 100;//家庭成员View高度100dp
    private static final int LINE_WIDTH_DP = 2;//连线宽度2dp

    private OnFamilySelectListener mOnFamilySelectListener;

    private int mScreenWidth;//屏幕宽度PX
    private int mScreenHeight;//屏幕高度PX

    private int mItemWidthPX;//家庭成员View宽度PX
    private int mItemHeightPX;//家庭成员View高度PX
    private int mMaxWidthPX;//最大宽度PX
    private int mMaxHeightPX;//最大高度PX
    private int mSpacePX;//元素间距PX
    private int mLineWidthPX;//连线宽度PX
    private int mLastInterceptX;
    private int mLastInterceptY;

    private int mWidthMeasureSpec;
    private int mHeightMeasureSpec;

    private int mShowWidthPX;//在屏幕所占的宽度
    private int mShowHeightPX;//在屏幕所占的高度

    private FamilyMember mFamilyMember;//我的
    private FamilyMember mMySpouse;//配偶
    private FamilyMember mMyFather;//父亲
    private FamilyMember mMyMother;//母亲
    private List<FamilyMember> mMyBrothers;//兄弟姐妹
    private List<FamilyMember> mMyChildren;//子女
    private FamilyMember mMyFosterFather;//养父
    private FamilyMember mMyFosterMother;//养母

    private View mMineView;//我的View
    private View mSpouseView;//配偶View
    private View mFatherView;//父亲View
    private View mMotherView;//母亲View
    private List<View> mBrothersView;//兄弟姐妹View
    private View mPaternalGrandFatherView;//爷爷View
    private View mPaternalGrandMotherView;//奶奶View
    private View mMaternalGrandFatherView;//外公View
    private View mMaternalGrandMotherView;//外婆View
    private List<View> mChildrenView;//子女View
    private List<View> mGrandChildrenView;//孙子女View
    private View mFosterFatherView;//养父View
    private View mFosterMotherView;//养母View
    private View mFPGrandFatherView;//养祖父View
    private View mFPGrandMotherView;//养祖母View
    private View mFMGrandFatherView;//养外公View
    private View mFMGrandMotherView;//养外婆View

    private int mGrandChildrenMaxWidth;//孙子女所占总长度

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
        mPaternalGrandFatherView = null;
        mPaternalGrandMotherView = null;
        mMaternalGrandFatherView = null;
        mMaternalGrandMotherView = null;
        mFosterFatherView = null;
        mFosterMotherView = null;
        mFPGrandFatherView = null;
        mFPGrandMotherView = null;
        mFMGrandFatherView = null;
        mFMGrandMotherView = null;
        if (mBrothersView != null) {
            mBrothersView.clear();
            mBrothersView = null;
        }
        if (mChildrenView != null) {
            mChildrenView.clear();
            mChildrenView = null;
        }
        if (mGrandChildrenView != null) {
            mGrandChildrenView.clear();
            mGrandChildrenView = null;
        }
        mBrothersView = new ArrayList<>();
        mChildrenView = new ArrayList<>();
        mGrandChildrenView = new ArrayList<>();

        mMySpouse = null;
        mMyFather = null;
        mMyMother = null;
        if (mMyBrothers != null) {
            mMyBrothers.clear();
            mMyBrothers = null;
        }
        if (mMyChildren != null) {
            mMyChildren.clear();
            mMyChildren = null;
        }
    }

    private void initData() {
        mScreenWidth = DisplayUtil.getScreenWidth();
        mScreenHeight = DisplayUtil.getScreenHeight();
        mSpacePX = DisplayUtil.dip2px(SPACE_WIDTH_DP);
        mLineWidthPX = DisplayUtil.dip2px(LINE_WIDTH_DP);
        mItemWidthPX = DisplayUtil.dip2px(ITEM_WIDTH_DP);
        mItemHeightPX = DisplayUtil.dip2px(ITEM_HEIGHT_DP);
        mWidthMeasureSpec = MeasureSpec.makeMeasureSpec(mItemWidthPX, MeasureSpec.EXACTLY);
        mHeightMeasureSpec = MeasureSpec.makeMeasureSpec(mItemHeightPX, MeasureSpec.EXACTLY);

        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.reset();
        mPaint.setColor(0xFF888888);
        mPaint.setStrokeWidth(mLineWidthPX);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setPathEffect(new DashPathEffect(new float[]{mLineWidthPX, mLineWidthPX * 4}, 0));

        mPath = new Path();
        mPath.reset();

        mMySpouse = mFamilyMember.getSpouse();
        mMyFather = mFamilyMember.getFather();
        mMyMother = mFamilyMember.getMother();
        mMyBrothers = mFamilyMember.getBrothers();
        mMyChildren = mFamilyMember.getChildren();
        mMyFosterFather = mFamilyMember.getFosterFather();
        mMyFosterMother = mFamilyMember.getFosterMother();
    }

    private void initWidthAndHeight() {
        int[] widthDP = {
                830,//第一代最大宽度
                720,//第二代最大宽度
                ITEM_WIDTH_DP,//第三代最大宽度
                ITEM_WIDTH_DP,//第四代最大宽度
                ITEM_WIDTH_DP//第五代最大宽度
        };

        if (mMySpouse != null) {
            widthDP[2] = ITEM_WIDTH_DP + SPACE_WIDTH_DP + ITEM_WIDTH_DP;
        }
        if (mMyBrothers != null && mMyBrothers.size() > 0) {
            widthDP[2] = ITEM_WIDTH_DP + (SPACE_WIDTH_DP + ITEM_WIDTH_DP) * mMyBrothers.size() * 2;
        }

        if (mMyChildren != null) {
            widthDP[3] += (SPACE_WIDTH_DP + ITEM_WIDTH_DP) * mMyChildren.size();
            int mGrandChildrenCount = 0;
            for (int i = 0; i < mMyChildren.size(); i++) {
                List<FamilyMember> grandChildrenList = mMyChildren.get(i).getChildren();
                if (grandChildrenList != null && grandChildrenList.size() > 0) {
                    mGrandChildrenCount += grandChildrenList.size();
                } else {
                    mGrandChildrenCount += 1;
                }
            }
            widthDP[4] = mGrandChildrenCount * ITEM_WIDTH_DP + SPACE_WIDTH_DP * (mGrandChildrenCount - 1);
            mGrandChildrenMaxWidth = DisplayUtil.dip2px(widthDP[4]);
        }

        mMaxWidthPX = mScreenWidth;
        for (int width : widthDP) {
            int widthPX = DisplayUtil.dip2px(width);
            if (widthPX > mMaxWidthPX) {
                mMaxWidthPX = widthPX;
            }
        }

        mMaxHeightPX = Math.max(DisplayUtil.dip2px(MAX_HEIGHT_DP), mScreenHeight);
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

        if (mMyFosterFather != null) {
            mFosterFatherView = createFamilyView(mMyFosterFather);
            FamilyMember myFPGrandFather = mMyFosterFather.getFather();
            FamilyMember myFPGrandMother = mMyFosterFather.getMother();
            if (myFPGrandFather != null) {
                mFPGrandFatherView = createFamilyView(myFPGrandFather);
            }
            if (myFPGrandMother != null) {
                mFPGrandMotherView = createFamilyView(myFPGrandMother);
            }

        }
        if (mMyFosterMother != null) {
            mFosterMotherView = createFamilyView(mMyFosterMother);
            FamilyMember myFMGrandFather = mMyFosterMother.getFather();
            FamilyMember myFMGrandMother = mMyFosterMother.getMother();

            if (myFMGrandFather != null) {
                mFMGrandFatherView = createFamilyView(myFMGrandFather);
            }
            if (myFMGrandMother != null) {
                mFMGrandMotherView = createFamilyView(myFMGrandMother);
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
        familyView.getLayoutParams().width = mItemWidthPX;
        familyView.getLayoutParams().height = mItemHeightPX;

        ImageView ivAvatar = (ImageView) familyView.findViewById(R.id.iv_avatar);
        ivAvatar.getLayoutParams().height = mItemWidthPX;

        TextView tvCall = (TextView) familyView.findViewById(R.id.tv_call);
        tvCall.getLayoutParams().height = mItemHeightPX - mItemWidthPX;

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
        mShowWidthPX = MeasureSpec.getSize(widthMeasureSpec);
        mShowHeightPX = MeasureSpec.getSize(heightMeasureSpec);

        if (mMineView != null) {
            measureChild(mMineView, mWidthMeasureSpec, mHeightMeasureSpec);
        }
        if (mSpouseView != null) {
            measureChild(mSpouseView, mWidthMeasureSpec, mHeightMeasureSpec);
        }

        if (mFatherView != null) {
            measureChild(mFatherView, mWidthMeasureSpec, mHeightMeasureSpec);

            if (mPaternalGrandFatherView != null) {
                measureChild(mPaternalGrandFatherView, mWidthMeasureSpec, mHeightMeasureSpec);
            }
            if (mPaternalGrandMotherView != null) {
                measureChild(mPaternalGrandMotherView, mWidthMeasureSpec, mHeightMeasureSpec);
            }
        }
        if (mMotherView != null) {
            measureChild(mMotherView, mWidthMeasureSpec, mHeightMeasureSpec);

            if (mMaternalGrandFatherView != null) {
                measureChild(mMaternalGrandFatherView, mWidthMeasureSpec, mHeightMeasureSpec);
            }
            if (mMaternalGrandMotherView != null) {
                measureChild(mMaternalGrandMotherView, mWidthMeasureSpec, mHeightMeasureSpec);
            }
        }
        if (mFosterFatherView != null) {
            measureChild(mFosterFatherView, mWidthMeasureSpec, mHeightMeasureSpec);
            if (mFPGrandFatherView != null) {
                measureChild(mFPGrandFatherView, mWidthMeasureSpec, mHeightMeasureSpec);
            }
            if (mFPGrandMotherView != null) {
                measureChild(mFPGrandMotherView, mWidthMeasureSpec, mHeightMeasureSpec);
            }
        }
        if (mFosterMotherView != null) {
            measureChild(mFosterMotherView, mWidthMeasureSpec, mHeightMeasureSpec);
            if (mFMGrandFatherView != null) {
                measureChild(mFMGrandFatherView, mWidthMeasureSpec, mHeightMeasureSpec);
            }
            if (mFMGrandMotherView != null) {
                measureChild(mFMGrandMotherView, mWidthMeasureSpec, mHeightMeasureSpec);
            }
        }

        if (mBrothersView != null && mBrothersView.size() > 0) {
            for (View view : mBrothersView) {
                measureChild(view, mWidthMeasureSpec, mHeightMeasureSpec);
            }
        }

        if (mChildrenView != null && mChildrenView.size() > 0) {
            for (View view : mChildrenView) {
                measureChild(view, mWidthMeasureSpec, mHeightMeasureSpec);
            }

            if (mGrandChildrenView != null && mGrandChildrenView.size() > 0) {
                for (View view : mGrandChildrenView) {
                    measureChild(view, mWidthMeasureSpec, mHeightMeasureSpec);
                }
            }
        }

        setMeasuredDimension(mMaxWidthPX, mMaxHeightPX);
        scrollTo((mMaxWidthPX - mShowWidthPX) / 2, (mMaxHeightPX - mShowHeightPX) / 2);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if (mMineView != null) {
            int mineLeft = mMaxWidthPX / 2 - mItemWidthPX / 2;
            int mineTop = DisplayUtil.dip2px(MINE_TOP_DP);

            setChildViewFrame(mMineView, mineLeft, mineTop, mItemWidthPX, mItemHeightPX);

            if (mSpouseView != null) {
                setChildViewFrame(mSpouseView,
                        mineLeft + mItemWidthPX + mSpacePX, mineTop,
                        mItemWidthPX, mItemHeightPX);
            }

            int parentTop = mineTop - mSpacePX * 2 - mItemHeightPX;
            int grandParentTop = parentTop - mSpacePX - mItemHeightPX;

            int fosterFatherLeft = mineLeft;
            int fosterMotherLeft = mineLeft;
            int fatherLeft = mineLeft;
            int motherLeft = mineLeft;

            if (haveEitherFosterParent() && haveEitherParent()) {
                fosterFatherLeft -= (mItemWidthPX + mSpacePX) * 2;
                fosterMotherLeft -= (mItemWidthPX + mSpacePX) * 2;
                fatherLeft += (mItemWidthPX + mSpacePX) * 2;
                motherLeft += (mItemWidthPX + mSpacePX) * 2;
            }
            if (haveBothFosterParent()) {
                fosterFatherLeft -= mItemWidthPX + mSpacePX;
                fosterMotherLeft += mItemWidthPX + mSpacePX;
            }
            if (haveBothParent()) {
                fatherLeft -= mItemWidthPX + mSpacePX;
                motherLeft += mItemWidthPX + mSpacePX;
            }

            if (mFatherView != null) {
                setChildViewFrame(mFatherView, fatherLeft, parentTop, mItemWidthPX, mItemHeightPX);

                setGrandParentFrame(mPaternalGrandFatherView, mPaternalGrandMotherView, fatherLeft, grandParentTop);
            }
            if (mMotherView != null) {
                setChildViewFrame(mMotherView, motherLeft, parentTop, mItemWidthPX, mItemHeightPX);

                setGrandParentFrame(mMaternalGrandFatherView, mMaternalGrandMotherView, motherLeft, grandParentTop);
            }
            if (mFosterFatherView != null) {
                setChildViewFrame(mFosterFatherView, fosterFatherLeft, parentTop, mItemWidthPX, mItemHeightPX);

                setGrandParentFrame(mFPGrandFatherView, mFPGrandMotherView, fosterFatherLeft, grandParentTop);
            }
            if (mFosterMotherView != null) {
                setChildViewFrame(mFosterMotherView, fosterMotherLeft, parentTop, mItemWidthPX, mItemHeightPX);

                setGrandParentFrame(mFMGrandFatherView, mFMGrandMotherView, fosterMotherLeft, grandParentTop);
            }

            if (mBrothersView != null && mBrothersView.size() > 0) {
                int brotherCount = mBrothersView.size();
                for (int i = 0; i < brotherCount; i++) {
                    View brotherView = mBrothersView.get(i);
                    setChildViewFrame(brotherView,
                            mineLeft - (i + 1) * (mItemWidthPX + mSpacePX),
                            mineTop,
                            mItemWidthPX, mItemHeightPX);
                }
            }

            if (mGrandChildrenView != null && mGrandChildrenView.size() > 0) {
                int grandChildrenTop = mineTop + (mItemHeightPX + mSpacePX * 2) * 2;
                int grandChildrenLeft = mineLeft + mItemWidthPX / 2 - mGrandChildrenMaxWidth / 2;

//                int grandChildrenCount = mGrandChildrenView.size();

                int index = 0;
                for (int i = 0; i < mMyChildren.size(); i++) {
                    View childView = mChildrenView.get(i);
                    int childLeft = grandChildrenLeft;
                    int childTop = mineTop + mItemHeightPX + mSpacePX * 2;

                    FamilyMember myChild = mMyChildren.get(i);
                    List<FamilyMember> myGrandChildren = myChild.getChildren();
                    if (myGrandChildren != null && myGrandChildren.size() > 0) {
                        int startGrandChildLeft = grandChildrenLeft;
                        int endGrandChildLeft = grandChildrenLeft;

                        int myGrandChildrenCount = myGrandChildren.size();
                        for (int j = 0; j < myGrandChildrenCount; j++) {
                            View grandChildView = mGrandChildrenView.get(index);
                            setChildViewFrame(grandChildView, grandChildrenLeft, grandChildrenTop, mItemWidthPX, mItemHeightPX);
                            endGrandChildLeft = grandChildrenLeft;
                            grandChildrenLeft += mItemWidthPX + mSpacePX;
                            index++;
                        }

                        childLeft = (endGrandChildLeft - startGrandChildLeft) / 2 + startGrandChildLeft;
                    } else {
                        grandChildrenLeft += mItemWidthPX + mSpacePX;
                    }

                    setChildViewFrame(childView, childLeft, childTop, mItemWidthPX, mItemHeightPX);
                }
            } else {
                if (mMyChildren != null && mMyChildren.size() > 0) {
                    int childrenCount = mMyChildren.size();

                    int childLeft = (int) mMineView.getX() + mItemWidthPX / 2 - (childrenCount * (mItemWidthPX + mSpacePX) - mSpacePX) / 2;
                    int childTop = mineTop + mItemHeightPX + mSpacePX;

                    for (View childView : mChildrenView) {
                        setChildViewFrame(childView, childLeft, childTop, mItemWidthPX, mItemHeightPX);
                        childLeft += mItemWidthPX + mSpacePX;
                    }
                }
            }
        }
    }

    private void setGrandParentFrame(View grandFatherView, View grandMotherView, int defaultLeft, int defaultTop) {
        int grandFatherLeft = defaultLeft;
        int grandMotherLeft = defaultLeft;
        if (grandFatherView != null && grandMotherView != null) {
            grandFatherLeft -= mSpacePX;
            grandMotherLeft += mSpacePX;
        }

        if (grandFatherView != null) {
            setChildViewFrame(grandFatherView,
                    grandFatherLeft, defaultTop,
                    mItemWidthPX, mItemHeightPX);
        }

        if (grandMotherView != null) {
            setChildViewFrame(grandMotherView,
                    grandMotherLeft, defaultTop,
                    mItemWidthPX, mItemHeightPX);
        }
    }

    private void setChildViewFrame(View childView, int left, int top, int width, int height) {
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
            int horizontalLineY = (int) mSpouseView.getY() + mItemWidthPX / 2;
            mPath.reset();
            mPath.moveTo(horizontalLineStartX, horizontalLineY);
            mPath.lineTo(horizontalLineStopX, horizontalLineY);
            canvas.drawPath(mPath, mPaint);
        }
    }

    private void drawParentLine(Canvas canvas) {
        int mineX = (int) mMineView.getX();
        int mineY = (int) mMineView.getY();

        int fosterParentCenterX = mineX + mItemWidthPX / 2;
        int parentCenterX = mineX + mItemWidthPX / 2;

        int horizontalLineY = mineY - mSpacePX;

        if (haveEitherFosterParent() || haveEitherParent()) {
            int verticalLineX = mineX + mItemWidthPX / 2;
            mPath.reset();
            mPath.moveTo(verticalLineX, horizontalLineY);
            mPath.lineTo(verticalLineX, mineY);
            canvas.drawPath(mPath, mPaint);
        }
        if (haveEitherFosterParent() && haveEitherParent()) {
            fosterParentCenterX = mineX - (mItemWidthPX + mSpacePX) * 2 + mItemWidthPX / 2;
            parentCenterX = mineX + (mItemWidthPX + mSpacePX) * 2 + mItemWidthPX / 2;

            mPath.reset();
            mPath.moveTo(fosterParentCenterX, horizontalLineY);
            mPath.lineTo(parentCenterX, horizontalLineY);
            canvas.drawPath(mPath, mPaint);
        }
        if (haveEitherFosterParent()) {
            int verticalLineEndY = horizontalLineY;
            if (mFosterFatherView != null) {
                verticalLineEndY = (int) mFosterFatherView.getY() + mItemWidthPX / 2;
            } else if (mFosterMotherView != null) {
                verticalLineEndY = (int) mFosterMotherView.getY() + mItemWidthPX / 2;
            }
            mPath.reset();
            mPath.moveTo(fosterParentCenterX, horizontalLineY);
            mPath.lineTo(fosterParentCenterX, verticalLineEndY);
            canvas.drawPath(mPath, mPaint);
        }
        if (haveBothFosterParent()) {
            int lineStartX = (int) mFosterFatherView.getX() + mItemWidthPX / 2;
            int lineEndX = (int) mFosterMotherView.getX() + mItemWidthPX / 2;
            int lineY = (int) mFosterFatherView.getY() + mItemWidthPX / 2;
            mPath.reset();
            mPath.moveTo(lineStartX, lineY);
            mPath.lineTo(lineEndX, lineY);
            canvas.drawPath(mPath, mPaint);
        }
        if (haveEitherParent()) {
            int verticalLineEndY = horizontalLineY;
            if (mFatherView != null) {
                verticalLineEndY = (int) mFatherView.getY() + mItemWidthPX / 2;
            } else if (mMotherView != null) {
                verticalLineEndY = (int) mMotherView.getY() + mItemWidthPX / 2;
            }
            mPath.reset();
            mPath.moveTo(parentCenterX, horizontalLineY);
            mPath.lineTo(parentCenterX, verticalLineEndY);
            canvas.drawPath(mPath, mPaint);
        }
        if (haveBothParent()) {
            int lineStartX = (int) mFatherView.getX() + mItemWidthPX / 2;
            int lineEndX = (int) mMotherView.getX() + mItemWidthPX / 2;
            int lineY = (int) mFatherView.getY() + mItemWidthPX / 2;
            mPath.reset();
            mPath.moveTo(lineStartX, lineY);
            mPath.lineTo(lineEndX, lineY);
            canvas.drawPath(mPath, mPaint);
        }
        if (mFPGrandFatherView != null || mFPGrandMotherView != null) {
            drawGrandParentLine(canvas, mFosterFatherView, mFPGrandFatherView, mFPGrandMotherView);
        }
        if (mFMGrandFatherView != null || mFMGrandMotherView != null) {
            drawGrandParentLine(canvas, mFosterMotherView, mFMGrandFatherView, mFMGrandMotherView);
        }
        if (mPaternalGrandFatherView != null || mPaternalGrandMotherView != null) {
            drawGrandParentLine(canvas, mFatherView, mPaternalGrandFatherView, mPaternalGrandMotherView);
        }
        if (mMaternalGrandFatherView != null || mMaternalGrandMotherView != null) {
            drawGrandParentLine(canvas, mMotherView, mMaternalGrandFatherView, mMaternalGrandMotherView);
        }
    }

    private void drawGrandParentLine(Canvas canvas, View parentView, View grandFatherView, View grandMotherView) {
        int verticalLineX = (int) parentView.getX() + mItemWidthPX / 2;
        int verticalLineStartY = (int) parentView.getY() + mItemWidthPX / 2;
        int verticalLineEndY = (int) parentView.getY() - mSpacePX - mItemHeightPX + mItemWidthPX / 2;
        mPath.reset();
        mPath.moveTo(verticalLineX, verticalLineStartY);
        mPath.lineTo(verticalLineX, verticalLineEndY);
        canvas.drawPath(mPath, mPaint);

        if (grandFatherView != null && grandMotherView != null) {
            int horizontalLineStartX = (int) grandFatherView.getX() + mItemWidthPX / 2;
            int horizontalLineEndX = (int) grandMotherView.getX() + mItemWidthPX / 2;
            mPath.reset();
            mPath.moveTo(horizontalLineStartX, verticalLineEndY);
            mPath.lineTo(horizontalLineEndX, verticalLineEndY);
            canvas.drawPath(mPath, mPaint);
        }
    }

    private void drawBrothersLine(Canvas canvas) {
        if (mBrothersView != null && mBrothersView.size() > 0) {
            int brotherCount = mBrothersView.size();
            View brotherView = mBrothersView.get(brotherCount - 1);

            int horizontalLineStartX = (int) (brotherView.getX() + mItemWidthPX);
            int horizontalLineY = (int) (brotherView.getY() + mItemWidthPX / 2);
            mPath.reset();
            mPath.moveTo(horizontalLineStartX, horizontalLineY);
            mPath.lineTo(mMineView.getX(), horizontalLineY);
            canvas.drawPath(mPath, mPaint);
        }
    }

    private void drawChildrenLine(Canvas canvas) {
        if (mMyChildren != null && mMyChildren.size() > 0) {
            int myVerticalLineX = (int) mMineView.getX() + mItemWidthPX / 2;
            int myVerticalLineStartY = (int) mMineView.getY() + mItemHeightPX;
            int myVerticalLinesStopY = myVerticalLineStartY + mSpacePX;
            mPath.reset();
            mPath.moveTo(myVerticalLineX, myVerticalLineStartY);
            mPath.lineTo(myVerticalLineX, myVerticalLinesStopY);
            canvas.drawPath(mPath, mPaint);

            int index = 0;
            int childrenViewCount = mChildrenView.size();
            for (int i = 0; i < childrenViewCount; i++) {
                View startChildView = mChildrenView.get(i);
                int childLineY = (int) startChildView.getY() - mSpacePX;
                int childVerticalLineEndY = (int) startChildView.getY();
                int childLineStartX = (int) startChildView.getX() + mItemWidthPX / 2;
                mPath.reset();
                mPath.moveTo(childLineStartX, childLineY);
                mPath.lineTo(childLineStartX, childVerticalLineEndY);
                canvas.drawPath(mPath, mPaint);

                if (i < childrenViewCount - 1) {
                    View endChildView = mChildrenView.get(i + 1);
                    int horizontalLineStopX = (int) endChildView.getX() + mItemWidthPX / 2;
                    mPath.reset();
                    mPath.moveTo(childLineStartX, childLineY);
                    mPath.lineTo(horizontalLineStopX, childLineY);
                    canvas.drawPath(mPath, mPaint);
                }

                List<FamilyMember> grandChildren = mMyChildren.get(i).getChildren();
                if (grandChildren != null) {
                    int grandChildrenCount = grandChildren.size();
                    for (int j = 0; j < grandChildrenCount; j++) {
                        View startView = mGrandChildrenView.get(j + index);
                        int grandchildLineX = (int) startView.getX() + mItemWidthPX / 2;
                        int grandchildLineStartY = (int) startView.getY() - mSpacePX;
                        int garndchildLineEndY = (int) startView.getY();
                        mPath.reset();
                        mPath.moveTo(grandchildLineX, grandchildLineStartY);
                        mPath.lineTo(grandchildLineX, garndchildLineEndY);
                        canvas.drawPath(mPath, mPaint);


                        if (j < grandChildrenCount - 1) {
                            View endView = mGrandChildrenView.get(j + 1 + index);
                            int hLineStopX = (int) endView.getX() + mItemWidthPX / 2;
                            mPath.reset();
                            mPath.moveTo(grandchildLineX, grandchildLineStartY);
                            mPath.lineTo(hLineStopX, grandchildLineStartY);
                            canvas.drawPath(mPath, mPaint);
                        }
                    }

                    if (grandChildrenCount > 0) {
                        View grandChildView = mGrandChildrenView.get(index);
                        int vLineX = (int) startChildView.getX() + mItemWidthPX / 2;
                        int vLineStopY = (int) startChildView.getY() + mItemHeightPX;
                        int hLineY = (int) grandChildView.getY() - mSpacePX;
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

    //是否有父母其中一个
    private boolean haveEitherParent() {
        return mFatherView != null || mMotherView != null;
    }

    //是否父母都存在
    private boolean haveBothParent() {
        return mFatherView != null && mMotherView != null;
    }

    //是否有养父母其中一个
    private boolean haveEitherFosterParent() {
        return mFosterFatherView != null || mFosterMotherView != null;
    }

    //是否养父母都存在
    private boolean haveBothFosterParent() {
        return mFosterFatherView != null && mFosterMotherView != null;
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

                if (mCurrentX < 0) {
                    mCurrentX = 0;
                } else if (mCurrentX > mMaxWidthPX - mShowWidthPX) {
                    mCurrentX = mMaxWidthPX - mShowWidthPX;
                }

                if (mCurrentY < 0) {
                    mCurrentY = 0;
                } else if (mCurrentY > mMaxHeightPX - mShowHeightPX) {
                    mCurrentY = mMaxHeightPX - mShowHeightPX;
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
