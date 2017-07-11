package com.cxb.familytree.ui.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.cxb.familytree.R;
import com.cxb.familytree.db.FamilyDBHelper;
import com.cxb.familytree.interfaces.OnFamilyClickListener;
import com.cxb.familytree.model.FamilyBean;
import com.cxb.familytree.utils.DisplayUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * 家谱树自定义ViewGroup（没有有养父母）
 */

public class FamilyTreeView3 extends ViewGroup {

    private static final int SPACE_WIDTH_DP = 20;//间距为20dp
    private static final int ITEM_WIDTH_DP = 50;//家庭成员View宽度50dp
    private static final int ITEM_HEIGHT_DP = 80;//家庭成员View高度80dp
    private static final float CALL_TEXT_SIZE_SP = 9f;//称呼文字大小9sp
    private static final float NAME_TEXT_SIZE_SP = 11f;//名称文字大小11sp
    private static final int LINE_WIDTH_DP = 2;//连线宽度2dp
    private static final int SCROLL_WIDTH = 2;//移动超过2dp，响应滑动，否则属于点击

    private OnFamilyClickListener mOnFamilyClickListener;

    private int mScreenWidth;//屏幕宽度PX
    private int mScreenHeight;//屏幕高度PX
    private int mItemWidthPX;//家庭成员View宽度PX
    private int mItemHeightPX;//家庭成员View高度PX
    private int mMaxWidthPX;//最大宽度PX
    private int mMaxHeightPX;//最大高度PX
    private int mSpacePX;//元素间距PX
    private int mLineWidthPX;//连线宽度PX

    private int[] mGenerationTop;//每代顶部位置
    private int[] mGenerationLeft;//每代左边位置
    private int[] mGenerationRight;//每代右边位置

    private int mWidthMeasureSpec;
    private int mHeightMeasureSpec;

    private int mShowWidthPX;//在屏幕所占的宽度
    private int mShowHeightPX;//在屏幕所占的高度

    private int mScrollWidth;//移动范围
    private int mCurrentX;//当前X轴偏移量
    private int mCurrentY;//当前Y轴偏移量
    private int mLastTouchX;//最后一次触摸的X坐标
    private int mLastTouchY;//最后一次触摸的Y坐标
    private int mLastInterceptX;
    private int mLastInterceptY;

    private int mCurrentLeft = 0;//当前选中View的Left距离
    private int mCurrentTop = 0;//当前选中View的Top距离
    private int mCurrentScrollX = 0;//当前滚动位置
    private int mCurrentScrollY = 0;//当前滚动位置

    private Paint mPaint;//连线样式
    private Path mPath;//路径

    private FamilyBean mMyInfo;//我
    private List<FamilyBean> mMyParentInfo;//父母
    private List<FamilyBean> mMyPGrandParentInfo;//爷爷，奶奶
    private List<FamilyBean> mMyMGrandParentInfo;//公公，婆婆
    private List<FamilyBean> mMyChildrenInfo;//子女
    private List<FamilyBean> mMyLittleBrotherInfo;//弟弟，妹妹
    private List<FamilyBean> mMyBrotherInfo;//哥哥，姐姐
    private List<FamilyBean> mMyFaUncleInfo;//叔伯姑
    private List<FamilyBean> mMyMoUncleInfo;//舅姨

    private Pair<View, View> mMyView;//我和配偶View
    private Pair<View, View> mMyParentView;//我的父母View
    private Pair<View, View> mMyPGrandParentView;//我的爷爷奶奶View
    private Pair<View, View> mMyMGrandParentView;//我的公公婆婆View

    private List<Pair<View, View>> mMyChildrenView;//我的子女View
    private List<Pair<View, View>> mMyGrandChildrenView;//我的子孙View

    private List<Pair<View, View>> mMyLittleBrothersView;//我的亲弟弟妹妹View
    private List<Pair<View, View>> mMyLittleBroChildrenView;//我的亲弟弟妹妹的子女View

    private List<Pair<View, View>> mMyBrothersView;//我的亲哥哥姐姐View
    private List<Pair<View, View>> mMyBroChildrenView;//我的亲哥哥姐姐的子女View

    private List<Pair<View, View>> mMyFaBrothersView;//我的叔伯姑View
    private List<Pair<View, View>> mMyFaBroChildrenView;//我的叔伯姑的子女View

    private List<Pair<View, View>> mMyMoBrothersView;//我的舅姨View
    private List<Pair<View, View>> mMyMoBroChildrenView;//我的舅姨的子女View

    private FamilyDBHelper mDBHelper;

    public FamilyTreeView3(Context context) {
        this(context, null, 0);
    }

    public FamilyTreeView3(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FamilyTreeView3(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        mScreenWidth = DisplayUtil.getScreenWidth();
        mScreenHeight = DisplayUtil.getScreenHeight();
        mScrollWidth = DisplayUtil.dip2px(SCROLL_WIDTH);
        mSpacePX = DisplayUtil.dip2px(SPACE_WIDTH_DP);
        mLineWidthPX = DisplayUtil.dip2px(LINE_WIDTH_DP);
        mItemWidthPX = DisplayUtil.dip2px(ITEM_WIDTH_DP);
        mItemHeightPX = DisplayUtil.dip2px(ITEM_HEIGHT_DP);
        mWidthMeasureSpec = MeasureSpec.makeMeasureSpec(mItemWidthPX, MeasureSpec.EXACTLY);
        mHeightMeasureSpec = MeasureSpec.makeMeasureSpec(mItemHeightPX, MeasureSpec.EXACTLY);

        mGenerationTop = new int[5];
        mGenerationTop[0] = 0;
        mGenerationTop[1] = mGenerationTop[0] + mItemHeightPX + mSpacePX;
        mGenerationTop[2] = mGenerationTop[1] + mItemHeightPX + mSpacePX;
        mGenerationTop[3] = mGenerationTop[2] + mItemHeightPX + mSpacePX;
        mGenerationTop[4] = mGenerationTop[3] + mItemHeightPX + mSpacePX;
        mGenerationLeft = new int[5];
        mGenerationRight = new int[5];

        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.reset();
        mPaint.setColor(0xFF888888);
        mPaint.setStrokeWidth(mLineWidthPX);
        mPaint.setStyle(Paint.Style.STROKE);

        mPath = new Path();
        mPath.reset();

        mDBHelper = new FamilyDBHelper(context);

        mMyParentInfo = new ArrayList<>();
        mMyPGrandParentInfo = new ArrayList<>();
        mMyMGrandParentInfo = new ArrayList<>();
        mMyChildrenInfo = new ArrayList<>();
        mMyBrotherInfo = new ArrayList<>();
        mMyLittleBrotherInfo = new ArrayList<>();
        mMyFaUncleInfo = new ArrayList<>();
        mMyMoUncleInfo = new ArrayList<>();

        mMyChildrenView = new ArrayList<>();
        mMyGrandChildrenView = new ArrayList<>();
        mMyLittleBrothersView = new ArrayList<>();
        mMyLittleBroChildrenView = new ArrayList<>();
        mMyMoBrothersView = new ArrayList<>();
        mMyMoBroChildrenView = new ArrayList<>();
    }

    public void drawFamilyTree(String familyId) {
        recycleAllView();
        initData(familyId);
        initView();
        invalidate();
    }

    public void destroyView() {
        recycleAllView();
        if (mDBHelper != null) {
            mDBHelper.closeDB();
        }
    }

    private void recycleAllView() {
        removeAllViews();

        for (int i = 0; i < mGenerationLeft.length; i++) {
            mGenerationLeft[i] = 0;
        }
        for (int i = 0; i < mGenerationRight.length; i++) {
            mGenerationRight[i] = 0;
        }

        mMyInfo = null;
        mMyParentInfo.clear();
        mMyPGrandParentInfo.clear();
        mMyMGrandParentInfo.clear();
        mMyChildrenInfo.clear();
        mMyBrotherInfo.clear();
        mMyLittleBrotherInfo.clear();
        mMyFaUncleInfo.clear();
        mMyMoUncleInfo.clear();

        mMyChildrenView.clear();
        mMyGrandChildrenView.clear();
        mMyLittleBrothersView.clear();
        mMyLittleBroChildrenView.clear();
        mMyMoBrothersView.clear();
        mMyMoBroChildrenView.clear();
    }

    private void initData(String familyId) {
        mMyInfo = mDBHelper.findFamilyById(familyId);
        if (mMyInfo != null) {
            final String sex = mMyInfo.getSex();
            final String birthday = mMyInfo.getBirthday();
            final String fatherId = mMyInfo.getFatherId();
            final String motherId = mMyInfo.getMotherId();
            final String spouseId = mMyInfo.getSpouseId();

            mMyInfo.setSpouse(mDBHelper.findFamilyById(spouseId));
            final List<FamilyBean> childrenList;
            if ("1".equals(sex)) {
                childrenList = mDBHelper.findFamiliesByFatherId(familyId, "");
            } else {
                childrenList = mDBHelper.findFamiliesByMotherId(familyId, "");
            }
            setSpouseAndChildren(childrenList, true);
            mMyChildrenInfo.addAll(childrenList);

            final FamilyBean father = mDBHelper.findFamilyById(fatherId);
            final FamilyBean mother = mDBHelper.findFamilyById(motherId);
            if (father != null) {
                mMyParentInfo.add(father);
                final String pGrandFatherId = father.getFatherId();
                final String pGrandMotherId = father.getMotherId();
                if (!TextUtils.isEmpty(pGrandFatherId)) {
                    mMyPGrandParentInfo.add(mDBHelper.findFamilyById(pGrandFatherId));
                }
                if (!TextUtils.isEmpty(pGrandMotherId)) {
                    mMyPGrandParentInfo.add(mDBHelper.findFamilyById(pGrandMotherId));
                }

                final List<FamilyBean> faUncleList;
                if (!TextUtils.isEmpty(pGrandFatherId)) {
                    faUncleList = mDBHelper.findFamiliesByFatherId(pGrandFatherId, fatherId);
                } else if (!TextUtils.isEmpty(pGrandMotherId)) {
                    faUncleList = mDBHelper.findFamiliesByMotherId(pGrandMotherId, fatherId);
                } else {
                    faUncleList = new ArrayList<>();
                }
                setSpouseAndChildren(faUncleList, true);
                mMyFaUncleInfo.addAll(faUncleList);
            }
            if (mother != null) {
                mMyParentInfo.add(mother);
                final String mGrandFatherId = mother.getFatherId();
                final String mGrandMotherId = mother.getMotherId();
                if (!TextUtils.isEmpty(mGrandFatherId)) {
                    mMyMGrandParentInfo.add(mDBHelper.findFamilyById(mGrandFatherId));
                }
                if (!TextUtils.isEmpty(mGrandMotherId)) {
                    mMyMGrandParentInfo.add(mDBHelper.findFamilyById(mGrandMotherId));
                }
                final List<FamilyBean> moUncleList;
                if (!TextUtils.isEmpty(mGrandFatherId)) {
                    moUncleList = mDBHelper.findFamiliesByFatherId(mGrandFatherId, motherId);
                } else if (!TextUtils.isEmpty(mGrandMotherId)) {
                    moUncleList = mDBHelper.findFamiliesByMotherId(mGrandMotherId, motherId);
                } else {
                    moUncleList = new ArrayList<>();
                }
                setSpouseAndChildren(moUncleList, true);
                mMyMoUncleInfo.addAll(moUncleList);
            }

            final List<FamilyBean> brotherList = mDBHelper.findMyBrothersByParentId(fatherId, motherId, familyId, birthday, false);
            setSpouseAndChildren(brotherList, true);
            mMyBrotherInfo.addAll(brotherList);

            final List<FamilyBean> littleBrotherList = mDBHelper.findMyBrothersByParentId(fatherId, motherId, familyId, birthday, true);
            setSpouseAndChildren(littleBrotherList, true);
            mMyLittleBrotherInfo.addAll(littleBrotherList);
        }
    }

    private void setSpouseAndChildren(List<FamilyBean> famliyList, boolean childNeedSpouse) {
        if (famliyList != null) {
            for (FamilyBean family : famliyList) {
                final String familyId = family.getMemberId();
                final String familySex = family.getSex();
                final String familySpouseId = family.getSpouseId();

                family.setSpouse(mDBHelper.findFamilyById(familySpouseId));
                final List<FamilyBean> childrenList;
                if ("1".equals(familySex)) {
                    childrenList = mDBHelper.findFamiliesByFatherId(familyId, "");
                } else {
                    childrenList = mDBHelper.findFamiliesByMotherId(familyId, "");
                }
                if (childrenList != null && childNeedSpouse) {
                    for (FamilyBean child : childrenList) {
                        final String childSpouseId = child.getSpouseId();
                        child.setSpouse(mDBHelper.findFamilyById(childSpouseId));
                    }
                }
                family.setChildren(childrenList);
            }
        }
    }

    public void saveData(List<FamilyBean> familyList) {
        mDBHelper.deleteTable();
        mDBHelper.save(familyList);
    }

    private void initView() {
        initEachPart(4, mMyChildrenInfo, mMyChildrenView, mMyGrandChildrenView);
        setRightByGeneration(4, mMyChildrenView);
        initEachPart(3, mMyLittleBrotherInfo, mMyLittleBrothersView, mMyLittleBroChildrenView);
        setRightByGeneration(3, mMyLittleBrothersView);
        initEachPart(2, mMyMoUncleInfo, mMyMoBrothersView, mMyMoBroChildrenView);

//        initMyChildrenView();
    }

    private void initEachPart(int bottomGeneration,
                              List<FamilyBean> infoList,
                              List<Pair<View, View>> fatherViewList,
                              List<Pair<View, View>> childrenViewList) {
        final int count = infoList.size();
        for (int i = 0; i < count; i++) {
            final FamilyBean childInfo = infoList.get(i);
            setChildren(childInfo, bottomGeneration, fatherViewList, childrenViewList);
            if (i < count - 1) {
                mGenerationRight[bottomGeneration] += mSpacePX;
            }
        }
    }

    private void setLeftByGeneration(int bottomGeneration, List<Pair<View, View>> pairList) {
        //TODO
        if (pairList.size() > 0) {
            final Pair<View, View> firstPair = pairList.get(0);
            final View firstView = firstPair.first != null ? firstPair.first : firstPair.second;
            mGenerationRight[bottomGeneration - 1] = firstView.getLeft();
        }
    }

    private void setRightByGeneration(int bottomGeneration, List<Pair<View, View>> pairList) {
        if (pairList.size() > 0) {
            final Pair<View, View> lastPair = pairList.get(pairList.size() - 1);
            final View lastView = lastPair.second != null ? lastPair.second : lastPair.first;
            mGenerationRight[bottomGeneration - 1] = lastView.getLeft() + mItemWidthPX;
        }
        mGenerationRight[bottomGeneration - 1] += mSpacePX;
    }

    private void setChildren(FamilyBean familyInfo, int generation,
                             List<Pair<View, View>> fatherViewList,
                             List<Pair<View, View>> childrenViewList) {
        final FamilyBean familySpouseInfo = familyInfo.getSpouse();
        final String familySex = familyInfo.getSex();
        final List<FamilyBean> childList = familyInfo.getChildren();
        final int count = childList.size();
        final int startLeft = mGenerationRight[generation];

        if (familySpouseInfo != null && count == 1) {
            final FamilyBean childInfo = childList.get(0);
            final String childSex = childInfo.getSex();
            final FamilyBean childSpouseInfo = childInfo.getSpouse();
            if (childSpouseInfo == null) {
                final int maleLeft = mGenerationRight[generation];
                final int femaleLeft = maleLeft + mItemWidthPX + mSpacePX;
                final View fatherView;
                final View motherView;
                if ("1".equals(familySex)) {
                    fatherView = createFamilyView(familyInfo, maleLeft, mGenerationTop[generation - 1]);
                    motherView = createFamilyView(familySpouseInfo, femaleLeft, mGenerationTop[generation - 1]);
                } else {
                    fatherView = createFamilyView(familySpouseInfo, maleLeft, mGenerationTop[generation - 1]);
                    motherView = createFamilyView(familyInfo, femaleLeft, mGenerationTop[generation - 1]);
                }
                fatherViewList.add(Pair.create(fatherView, motherView));

                final int childLeft = (maleLeft + femaleLeft) / 2;
                final View childView = createFamilyView(childInfo, childLeft, mGenerationTop[generation]);
                final View childSpouseView = null;
                if ("1".equals(childSex)) {
                    childrenViewList.add(Pair.create(childView, childSpouseView));
                } else {
                    childrenViewList.add(Pair.create(childSpouseView, childView));
                }
                mGenerationRight[generation] = femaleLeft + mItemWidthPX;
                return;
            }
        }

        if (count > 0) {
            for (int i = 0; i < count; i++) {
                final FamilyBean childInfo = childList.get(i);
                final String childSex = childInfo.getSex();
                final FamilyBean childSpouseInfo = childInfo.getSpouse();

                final View childView;
                final View childSpouseView;
                if ("1".equals(childSex)) {
                    childView = createFamilyView(childInfo, mGenerationRight[generation], mGenerationTop[generation]);
                    mGenerationRight[generation] += mItemWidthPX;
                    if (childSpouseInfo != null) {
                        mGenerationRight[generation] += mSpacePX;
                        childSpouseView = createFamilyView(childSpouseInfo, mGenerationRight[generation], mGenerationTop[generation]);
                        mGenerationRight[generation] += mItemWidthPX;
                    } else {
                        childSpouseView = null;
                    }
                    childrenViewList.add(Pair.create(childView, childSpouseView));
                } else {
                    if (childSpouseInfo != null) {
                        childSpouseView = createFamilyView(childSpouseInfo, mGenerationRight[generation], mGenerationTop[generation]);
                        mGenerationRight[generation] += mItemWidthPX + mSpacePX;
                    } else {
                        childSpouseView = null;
                    }
                    childView = createFamilyView(childInfo, mGenerationRight[generation], mGenerationTop[generation]);
                    mGenerationRight[generation] += mItemWidthPX;
                    childrenViewList.add(Pair.create(childSpouseView, childView));
                }

                if (i < count - 1) {
                    mGenerationRight[generation] += mSpacePX;
                }
            }
        } else {
            mGenerationRight[generation] += mItemWidthPX;
            if (familySpouseInfo != null) {
                mGenerationRight[generation] += mSpacePX + mItemWidthPX;
            }
        }

        final View familyView;
        final View familySpouseView;
        final int centerLeft = (mGenerationRight[generation] - mItemWidthPX + startLeft) / 2;
        final int motherLeft = centerLeft + (mItemWidthPX + mSpacePX) / 2;
        final int fatherLeft = centerLeft - (mItemWidthPX + mSpacePX) / 2;
        if (familySpouseInfo != null) {
            if ("1".equals(familySex)) {
                familyView = createFamilyView(familyInfo, fatherLeft, mGenerationTop[generation - 1]);
                familySpouseView = createFamilyView(familySpouseInfo, motherLeft, mGenerationTop[generation - 1]);
                fatherViewList.add(Pair.create(familyView, familySpouseView));
            } else {
                familySpouseView = createFamilyView(familySpouseInfo, fatherLeft, mGenerationTop[generation - 1]);
                familyView = createFamilyView(familyInfo, motherLeft, mGenerationTop[generation - 1]);
                fatherViewList.add(Pair.create(familySpouseView, familyView));
            }
        } else {
            familyView = createFamilyView(familyInfo, centerLeft, mGenerationTop[generation - 1]);
            familySpouseView = null;
            if ("1".equals(familySex)) {
                fatherViewList.add(Pair.create(familyView, familySpouseView));
            } else {
                fatherViewList.add(Pair.create(familySpouseView, familyView));
            }
        }
    }

    private View createFamilyView(FamilyBean family, int left, int top) {
        final View familyView = LayoutInflater.from(getContext()).inflate(R.layout.item_family, this, false);
        familyView.getLayoutParams().width = mItemWidthPX;
        familyView.getLayoutParams().height = mItemHeightPX;
        familyView.setLeft(left);
        familyView.setTop(top);
        familyView.setTag(family);

        final ImageView ivAvatar = (ImageView) familyView.findViewById(R.id.iv_avatar);
        ivAvatar.getLayoutParams().height = mItemWidthPX;

        final TextView tvCall = (TextView) familyView.findViewById(R.id.tv_call);
        tvCall.getLayoutParams().height = (mItemHeightPX - mItemWidthPX) / 2;
        tvCall.setTextSize(CALL_TEXT_SIZE_SP);
        tvCall.setText("(" + family.getCall() + ")");

        final TextView tvName = (TextView) familyView.findViewById(R.id.tv_name);
        tvName.getLayoutParams().height = (mItemHeightPX - mItemWidthPX) / 2;
        tvName.setTextSize(NAME_TEXT_SIZE_SP);
        tvName.setText(family.getMemberName());

        final String url = family.getMemberImg();
        if (!TextUtils.isEmpty(url)) {
            Glide.with(getContext())
                    .load(url)
                    .placeholder(R.drawable.family_avatar)
                    .error(R.drawable.family_avatar)
                    .centerCrop()
                    .dontAnimate()
                    .into(ivAvatar);
        }
        if (family.isSelect()) {
            ivAvatar.setBackgroundResource(R.drawable.shape_red_circle);
        }

        familyView.setOnClickListener(click);

        this.addView(familyView);
        return familyView;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        mShowWidthPX = MeasureSpec.getSize(widthMeasureSpec);
        mShowHeightPX = MeasureSpec.getSize(heightMeasureSpec);

        final int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            final View childView = getChildAt(i);
            childView.measure(mWidthMeasureSpec, mHeightMeasureSpec);
        }

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        final int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            final View childView = getChildAt(i);
            setChildViewFrame(childView, childView.getLeft(), childView.getTop(), mItemWidthPX, mItemHeightPX);
        }
    }

    private void setChildViewFrame(View childView, int left, int top, int width, int height) {
        childView.layout(left, top, left + width, top + height);
    }

    @Override
    protected void onDraw(Canvas canvas) {
//        super.onDraw(canvas);
//        testDrawChildrenLine4(canvas, mMyChildrenView);
//        testDrawChildrenLine5(canvas, mMyGrandChildrenView);
    }

    private void testDrawChildrenLine4(Canvas canvas) {

    }

    private void testDrawChildrenLine5(Canvas canvas, List<Pair<View, View>> pairList) {
        final int count = mMyChildrenInfo.size();
        for (int i = 0; i < count; i++) {
            final Pair<View, View> pair = pairList.get(i);
            final FamilyBean familyInfo = mMyChildrenInfo.get(i);
            final String familySex = familyInfo.getSex();
            final View view;
            if ("1".equals(familySex)) {
                view = pair.first;
            } else {
                view = pair.second;
            }

            final int verticalLineStartY = view.getTop() + mItemHeightPX / 2;
            final int verticalLineEndY = view.getTop() - mSpacePX / 2;
            final int verticalLineX = view.getLeft() + mItemWidthPX / 2;
            mPath.reset();
            mPath.moveTo(verticalLineX, verticalLineStartY);
            mPath.lineTo(verticalLineX, verticalLineEndY);
            canvas.drawPath(mPath, mPaint);
        }
        for (Pair<View, View> pair : pairList) {
            final View firstView = pair.first;
            final View scondView = pair.second;
            if (firstView != null && scondView != null) {
                final int horizontalLineY = firstView.getTop() + mItemHeightPX / 2;
                final int horizontalLineStartX = firstView.getLeft() + mItemWidthPX / 2;
                final int horizontalLineEndX = scondView.getLeft() + mItemWidthPX / 2;

                mPath.reset();
                mPath.moveTo(horizontalLineStartX, horizontalLineY);
                mPath.lineTo(horizontalLineEndX, horizontalLineY);
                canvas.drawPath(mPath, mPaint);
            }
        }
    }

    private void testDrawChildrenLine4(Canvas canvas, List<Pair<View, View>> pairList) {
        for (Pair<View, View> pair : pairList) {
            final View firstView = pair.first;
            final View scondView = pair.second;
            if (firstView != null && scondView != null) {
                final int horizontalLineY = firstView.getTop() + mItemHeightPX / 2;
                final int horizontalLineStartX = firstView.getLeft() + mItemWidthPX / 2;
                final int horizontalLineEndX = scondView.getLeft() + mItemWidthPX / 2;

                mPath.reset();
                mPath.moveTo(horizontalLineStartX, horizontalLineY);
                mPath.lineTo(horizontalLineEndX, horizontalLineY);
                canvas.drawPath(mPath, mPaint);
            }
        }
    }

    public void setOnFamilyClickListener(OnFamilyClickListener onFamilyClickListener) {
        this.mOnFamilyClickListener = onFamilyClickListener;
    }

    private OnClickListener click = new OnClickListener() {
        @Override
        public void onClick(View v) {
            if (mOnFamilyClickListener != null) {
                mCurrentLeft = v.getLeft();
                mCurrentTop = v.getTop();
                mCurrentScrollX = getScrollX();
                mCurrentScrollY = getScrollY();
                mOnFamilyClickListener.onFamilySelect((FamilyBean) v.getTag());
            }
        }
    };

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
//                mCurrentX = getScrollX();
//                mCurrentY = getScrollY();
//                mLastTouchX = (int) event.getX();
//                mLastTouchY = (int) event.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                final int currentTouchX = (int) event.getX();
                final int currentTouchY = (int) event.getY();

                final int distanceX = currentTouchX - mLastTouchX;
                final int distanceY = currentTouchY - mLastTouchY;

                mCurrentX -= distanceX;
                mCurrentY -= distanceY;

                this.scrollTo(mCurrentX, mCurrentY);
                mLastTouchX = currentTouchX;
                mLastTouchY = currentTouchY;
                break;
            case MotionEvent.ACTION_UP:

                break;
        }
        return true;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        boolean intercerpt = false;
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mLastInterceptX = (int) event.getX();
                mLastInterceptY = (int) event.getY();
                mCurrentX = getScrollX();
                mCurrentY = getScrollY();
                mLastTouchX = (int) event.getX();
                mLastTouchY = (int) event.getY();
                intercerpt = false;
                break;
            case MotionEvent.ACTION_MOVE:
                final int distanceX = Math.abs((int) event.getX() - mLastInterceptX);
                final int distanceY = Math.abs((int) event.getY() - mLastInterceptY);
                if (distanceX < mScrollWidth && distanceY < mScrollWidth) {
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
