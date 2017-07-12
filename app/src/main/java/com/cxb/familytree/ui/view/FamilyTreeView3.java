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
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.cxb.familytree.R;
import com.cxb.familytree.db.FamilyDBHelper;
import com.cxb.familytree.interfaces.OnFamilyClickListener;
import com.cxb.familytree.model.FamilyBean;
import com.cxb.familytree.utils.DisplayUtil;
import com.orhanobut.logger.Logger;

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

    private int mCurrentWidth;//当前选中View离左边距离
    private int mCurrentHeight;//当前选中View离顶部距离

    private Paint mPaint;//连线样式
    private Path mPath;//路径

    private FamilyBean mMyInfo;//我
    private FamilyBean mMyParentInfo;//父母
    private FamilyBean mMyPGrandParentInfo;//爷爷，奶奶
    private FamilyBean mMyMGrandParentInfo;//外公，外婆
    private List<FamilyBean> mMyChildrenInfo;//子女
    private List<FamilyBean> mMyLittleBrotherInfo;//弟弟，妹妹
    private List<FamilyBean> mMyBrotherInfo;//哥哥，姐姐
    private List<FamilyBean> mMyFaUncleInfo;//叔伯姑
    private List<FamilyBean> mMyMoUncleInfo;//舅姨

    private View mOnlyMyView;//我view
    private Pair<View, View> mMyView;//我和配偶View
    private Pair<View, View> mMyParentView;//我的父母View
    private Pair<View, View> mMyPGrandParentView;//我的爷爷奶奶View
    private Pair<View, View> mMyMGrandParentView;//我的外公外婆View

    private List<Pair<View, View>> mMyChildrenView;//我的子女View
    private List<Pair<View, View>> mMyGrandChildrenView;//我的子孙View

    private List<Pair<View, View>> mMyLittleBrotherView;//我的亲弟弟妹妹View
    private List<Pair<View, View>> mMyLittleBroChildrenView;//我的亲弟弟妹妹的子女View
    private List<Pair<View, View>> mMyBrotherView;//我的亲哥哥姐姐View
    private List<Pair<View, View>> mMyBroChildrenView;//我的亲哥哥姐姐的子女View

    private List<Pair<View, View>> mMyMoUncleView;//我的舅姨View
    private List<Pair<View, View>> mMyMoUncleChildrenView;//我的舅姨的子女View
    private List<Pair<View, View>> mMyFaUncleView;//我的叔伯姑View
    private List<Pair<View, View>> mMyFaUncleChildrenView;//我的叔伯姑的子女View

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

        mMyChildrenInfo = new ArrayList<>();
        mMyBrotherInfo = new ArrayList<>();
        mMyLittleBrotherInfo = new ArrayList<>();
        mMyFaUncleInfo = new ArrayList<>();
        mMyMoUncleInfo = new ArrayList<>();

        mMyChildrenView = new ArrayList<>();
        mMyGrandChildrenView = new ArrayList<>();
        mMyLittleBrotherView = new ArrayList<>();
        mMyLittleBroChildrenView = new ArrayList<>();
        mMyBrotherView = new ArrayList<>();
        mMyBroChildrenView = new ArrayList<>();
        mMyMoUncleView = new ArrayList<>();
        mMyMoUncleChildrenView = new ArrayList<>();
        mMyFaUncleView = new ArrayList<>();
        mMyFaUncleChildrenView = new ArrayList<>();
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
        mMyParentInfo = null;
        mMyPGrandParentInfo = null;
        mMyMGrandParentInfo = null;
        mMyChildrenInfo.clear();
        mMyBrotherInfo.clear();
        mMyLittleBrotherInfo.clear();
        mMyFaUncleInfo.clear();
        mMyMoUncleInfo.clear();

        mMyView = null;
        mOnlyMyView = null;
        mMyParentView = null;
        mMyPGrandParentView = null;
        mMyMGrandParentView = null;
        mMyChildrenView.clear();
        mMyGrandChildrenView.clear();
        mMyLittleBrotherView.clear();
        mMyLittleBroChildrenView.clear();
        mMyBrotherView.clear();
        mMyBroChildrenView.clear();
        mMyMoUncleView.clear();
        mMyMoUncleChildrenView.clear();
        mMyFaUncleView.clear();
        mMyFaUncleChildrenView.clear();
    }

    private void initData(String familyId) {
        mMyInfo = mDBHelper.findFamilyById(familyId);
        if (mMyInfo != null) {
            mMyInfo.setSelect(true);
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
                father.setSpouse(mother);
                mMyParentInfo = father;
            } else if (mother != null) {
                mMyParentInfo = mother;
            }
            if (father != null) {
                final String pGrandFatherId = father.getFatherId();
                final String pGrandMotherId = father.getMotherId();
                final FamilyBean grandFather = mDBHelper.findFamilyById(pGrandFatherId);
                final FamilyBean grandMother = mDBHelper.findFamilyById(pGrandMotherId);
                if (grandFather != null) {
                    grandFather.setSpouse(grandMother);
                    mMyPGrandParentInfo = grandFather;
                } else if (grandMother != null) {
                    mMyPGrandParentInfo = grandMother;
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
                final String mGrandFatherId = mother.getFatherId();
                final String mGrandMotherId = mother.getMotherId();
                final FamilyBean grandFather = mDBHelper.findFamilyById(mGrandFatherId);
                final FamilyBean grandMother = mDBHelper.findFamilyById(mGrandMotherId);
                if (grandFather != null) {
                    grandFather.setSpouse(grandMother);
                    mMyMGrandParentInfo = grandFather;
                } else if (grandMother != null) {
                    mMyMGrandParentInfo = grandMother;
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
        initEachRightPart(5, mMyChildrenInfo, mMyChildrenView, mMyGrandChildrenView);
        setRightByGeneration(4, mMyChildrenView);
        setLeftByGeneration(4, mMyChildrenView);

        initMyPart(3);

        initEachRightPart(4, mMyLittleBrotherInfo, mMyLittleBrotherView, mMyLittleBroChildrenView);
        setRightByGeneration(3, mMyLittleBrotherView);
        initEachLeftPart(4, mMyBrotherInfo, mMyBrotherView, mMyBroChildrenView);
        setLeftByGeneration(3, mMyBrotherView);

        initMyParentPart(2);

        initEachRightPart(3, mMyMoUncleInfo, mMyMoUncleView, mMyMoUncleChildrenView);
        setRightByGeneration(2, mMyMoUncleView);
        initEachLeftPart(3, mMyFaUncleInfo, mMyFaUncleView, mMyFaUncleChildrenView);
        setLeftByGeneration(2, mMyFaUncleView);
    }

    private void initEachRightPart(int generation,
                                   List<FamilyBean> parentinfoList,
                                   List<Pair<View, View>> parentViewList,
                                   List<Pair<View, View>> childrenViewList) {
        final int position = generation - 1;
        final int count = parentinfoList.size();
        if (count > 0) {
            for (int i = 0; i < count; i++) {
                final FamilyBean parentInfo = parentinfoList.get(i);
                setRightPart(parentInfo, position, parentViewList, childrenViewList);
            }
        } else {
            mGenerationRight[position] += mItemWidthPX + mSpacePX;
        }

    }

    private void setRightPart(FamilyBean familyInfo, int position,
                              List<Pair<View, View>> parentViewList,
                              List<Pair<View, View>> childrenViewList) {
        final FamilyBean familySpouseInfo = familyInfo.getSpouse();
        final String familySex = familyInfo.getSex();
        final List<FamilyBean> childList = familyInfo.getChildren();
        final int count = childList.size();
        final int startLeft = mGenerationRight[position];

        if (familySpouseInfo != null && count == 1) {
            final FamilyBean childInfo = childList.get(0);
            final String childSex = childInfo.getSex();
            final FamilyBean childSpouseInfo = childInfo.getSpouse();
            if (childSpouseInfo == null) {
                final int maleLeft = mGenerationRight[position];
                final int femaleLeft = maleLeft + mItemWidthPX + mSpacePX;
                final View maleView;
                final View femaleView;
                if ("1".equals(familySex)) {
                    maleView = createFamilyView(familyInfo, maleLeft, mGenerationTop[position - 1]);
                    femaleView = createFamilyView(familySpouseInfo, femaleLeft, mGenerationTop[position - 1]);
                } else {
                    maleView = createFamilyView(familySpouseInfo, maleLeft, mGenerationTop[position - 1]);
                    femaleView = createFamilyView(familyInfo, femaleLeft, mGenerationTop[position - 1]);
                }
                parentViewList.add(Pair.create(maleView, femaleView));

                final int childLeft = (maleLeft + femaleLeft) / 2;
                final View childView = createFamilyView(childInfo, childLeft, mGenerationTop[position]);
                final View childSpouseView = null;
                if ("1".equals(childSex)) {
                    childrenViewList.add(Pair.create(childView, childSpouseView));
                } else {
                    childrenViewList.add(Pair.create(childSpouseView, childView));
                }
                mGenerationRight[position] = femaleLeft + mItemWidthPX + mSpacePX;
                return;
            }
        }

        if (count > 0) {
            for (int i = 0; i < count; i++) {
                final FamilyBean childInfo = childList.get(i);
                final String childSex = childInfo.getSex();
                final FamilyBean childSpouseInfo = childInfo.getSpouse();

                final View maleView;
                final View femaleView;
                if ("1".equals(childSex)) {
                    maleView = createFamilyView(childInfo, mGenerationRight[position], mGenerationTop[position]);
                    mGenerationRight[position] += mItemWidthPX + mSpacePX;
                    if (childSpouseInfo != null) {
                        femaleView = createFamilyView(childSpouseInfo, mGenerationRight[position], mGenerationTop[position]);
                        mGenerationRight[position] += mItemWidthPX + mSpacePX;
                    } else {
                        femaleView = null;
                    }
                } else {
                    if (childSpouseInfo != null) {
                        maleView = createFamilyView(childSpouseInfo, mGenerationRight[position], mGenerationTop[position]);
                        mGenerationRight[position] += mItemWidthPX + mSpacePX;
                    } else {
                        maleView = null;
                    }
                    femaleView = createFamilyView(childInfo, mGenerationRight[position], mGenerationTop[position]);
                    mGenerationRight[position] += mItemWidthPX + mSpacePX;
                }
                childrenViewList.add(Pair.create(maleView, femaleView));
            }
        } else {
            mGenerationRight[position] += mItemWidthPX + mSpacePX;
            if (familySpouseInfo != null) {
                mGenerationRight[position] += mItemWidthPX + mSpacePX;
            }
        }

        final int centerLeft = (mGenerationRight[position] - mSpacePX - mItemWidthPX + startLeft) / 2;
        final int maleLeft = centerLeft - (mItemWidthPX + mSpacePX) / 2;
        final int femaleLeft = centerLeft + (mItemWidthPX + mSpacePX) / 2;
        parentViewList.add(setParentLocate(position - 1, centerLeft, maleLeft, femaleLeft, familyInfo));
    }

    private void initEachLeftPart(int generation,
                                  List<FamilyBean> parentinfoList,
                                  List<Pair<View, View>> parentViewList,
                                  List<Pair<View, View>> childrenViewList) {
        final int position = generation - 1;
        final int count = parentinfoList.size();
        if (count > 0) {
            for (int i = count - 1; i >= 0; i--) {
                final FamilyBean parentInfo = parentinfoList.get(i);
                setLeftPart(parentInfo, position, parentViewList, childrenViewList);
            }
        } else {
            mGenerationLeft[position] -= (mItemWidthPX + mSpacePX);
        }
    }

    private void setLeftPart(FamilyBean familyInfo, int position,
                             List<Pair<View, View>> parentViewList,
                             List<Pair<View, View>> childrenViewList) {
        final FamilyBean familySpouseInfo = familyInfo.getSpouse();
        final String familySex = familyInfo.getSex();
        final List<FamilyBean> childList = familyInfo.getChildren();
        final int count = childList.size();
        final int startLeft = mGenerationLeft[position];

        if (familySpouseInfo != null && count == 1) {
            final FamilyBean childInfo = childList.get(0);
            final String childSex = childInfo.getSex();
            final FamilyBean childSpouseInfo = childInfo.getSpouse();
            if (childSpouseInfo == null) {
                final int maleLeft = mGenerationLeft[position] - mSpacePX - mItemWidthPX;
                final int femaleLeft = mGenerationLeft[position];
                final View maleView;
                final View femaleView;
                if ("1".equals(familySex)) {
                    maleView = createFamilyView(familyInfo, maleLeft, mGenerationTop[position - 1]);
                    femaleView = createFamilyView(familySpouseInfo, femaleLeft, mGenerationTop[position - 1]);
                } else {
                    maleView = createFamilyView(familySpouseInfo, maleLeft, mGenerationTop[position - 1]);
                    femaleView = createFamilyView(familyInfo, femaleLeft, mGenerationTop[position - 1]);
                }
                parentViewList.add(0, Pair.create(maleView, femaleView));

                final int childLeft = (maleLeft + femaleLeft) / 2;
                final View childView = createFamilyView(childInfo, childLeft, mGenerationTop[position]);
                final View childSpouseView = null;
                if ("1".equals(childSex)) {
                    childrenViewList.add(0, Pair.create(childView, childSpouseView));
                } else {
                    childrenViewList.add(0, Pair.create(childSpouseView, childView));
                }
                mGenerationLeft[position] = maleLeft - mSpacePX - mItemWidthPX;
                return;
            }
        }

        if (count > 0) {
            for (int i = count - 1; i >= 0; i--) {
                final FamilyBean childInfo = childList.get(i);
                final String childSex = childInfo.getSex();
                final FamilyBean childSpouseInfo = childInfo.getSpouse();

                final View maleView;
                final View femaleView;
                if ("1".equals(childSex)) {
                    maleView = createFamilyView(childInfo, mGenerationLeft[position], mGenerationTop[position]);
                    mGenerationLeft[position] -= (mItemWidthPX + mSpacePX);
                    if (childSpouseInfo != null) {
                        femaleView = createFamilyView(childSpouseInfo, mGenerationLeft[position], mGenerationTop[position]);
                        mGenerationLeft[position] -= (mItemWidthPX + mSpacePX);
                    } else {
                        femaleView = null;
                    }
                } else {
                    if (childSpouseInfo != null) {
                        maleView = createFamilyView(childSpouseInfo, mGenerationLeft[position], mGenerationTop[position]);
                        mGenerationLeft[position] -= (mItemWidthPX + mSpacePX);
                    } else {
                        maleView = null;
                    }
                    femaleView = createFamilyView(childInfo, mGenerationLeft[position], mGenerationTop[position]);
                    mGenerationLeft[position] -= (mItemWidthPX + mSpacePX);
                }
                childrenViewList.add(0, Pair.create(maleView, femaleView));
            }
        } else {
            mGenerationLeft[position] -= (mItemWidthPX + mSpacePX);
            if (familySpouseInfo != null) {
                mGenerationLeft[position] -= (mItemWidthPX + mSpacePX);
            }
        }

        final int centerLeft = (startLeft + mGenerationLeft[position] + mItemWidthPX + mSpacePX) / 2;
        final int maleLeft = centerLeft - (mItemWidthPX + mSpacePX) / 2;
        final int femaleLeft = centerLeft + (mItemWidthPX + mSpacePX) / 2;
        parentViewList.add(0, setParentLocate(position - 1, centerLeft, maleLeft, femaleLeft, familyInfo));
    }

    private void initMyPart(int generation) {
        final int position = generation - 1;
        final int centerLeft = (mGenerationRight[position + 1] + mGenerationLeft[position + 1]) / 2;
        final int maleLeft = centerLeft - (mItemWidthPX + mSpacePX) / 2;
        final int femaleLeft = centerLeft + (mItemWidthPX + mSpacePX) / 2;
        mMyView = setParentLocate(position, centerLeft, maleLeft, femaleLeft, mMyInfo);
        if ("1".equals(mMyInfo.getSex())) {
            mOnlyMyView = mMyView.first;
        } else {
            mOnlyMyView = mMyView.second;
        }

        final List<Pair<View, View>> myPairList = new ArrayList<>(1);
        myPairList.add(mMyView);
        setRightByGeneration(generation, myPairList);
        setLeftByGeneration(generation, myPairList);
        if (mGenerationLeft[position + 1] > mGenerationLeft[position]) {
            mGenerationLeft[position + 1] = mGenerationLeft[position];
        }
        if (mGenerationRight[position + 1] < mGenerationRight[position]) {
            mGenerationRight[position + 1] = mGenerationRight[position];
        }
    }

    private void initMyParentPart(int generation) {
        if (mMyParentInfo != null) {
            final int position = generation - 1;
            final int parentCenterLeft;
            final String mySex = mMyInfo.getSex();
            if ("1".equals(mySex)) {
                parentCenterLeft = mMyView.first.getLeft();
            } else {
                parentCenterLeft = mMyView.second.getLeft();
            }
            final int parentMaleLeft = parentCenterLeft - (mItemWidthPX + mSpacePX) / 2;
            final int parentFemaleLeft = parentCenterLeft + (mItemWidthPX + mSpacePX) / 2;
            mMyParentView = setParentLocate(position, parentCenterLeft, parentMaleLeft, parentFemaleLeft, mMyParentInfo);

            final List<Pair<View, View>> myPairList = new ArrayList<>(1);
            myPairList.add(mMyView);
            setRightByGeneration(generation, myPairList);
            setLeftByGeneration(generation, myPairList);
            if (mGenerationLeft[position + 1] > mGenerationLeft[position]) {
                mGenerationLeft[position + 1] = mGenerationLeft[position];
            }
            if (mGenerationRight[position + 1] < mGenerationRight[position]) {
                mGenerationRight[position + 1] = mGenerationRight[position];
            }

            final View fatherView = mMyParentView.first;
            final View motherView = mMyParentView.second;
            if (fatherView != null) {
                if (mMyPGrandParentInfo != null) {
                    final int centerLeft = fatherView.getLeft();
                    final int maleLeft = centerLeft - mSpacePX - mItemWidthPX;
                    mMyPGrandParentView = setParentLocate(position - 1, centerLeft, maleLeft, centerLeft, mMyPGrandParentInfo);
                }
            }
            if (motherView != null) {
                if (mMyMGrandParentInfo != null) {
                    final int centerLeft = motherView.getLeft();
                    final int femaleLeft = centerLeft + mItemWidthPX + mSpacePX;
                    mMyMGrandParentView = setParentLocate(position - 1, centerLeft, centerLeft, femaleLeft, mMyMGrandParentInfo);
                }
            }
        }
    }

    private Pair<View, View> setParentLocate(int position, int centerLeft, int maleLeft, int femaleLeft, FamilyBean familyInfo) {
        final String familySex = familyInfo.getSex();
        final FamilyBean familySpouseInfo = familyInfo.getSpouse();
        final View maleView;
        final View femaleView;
        if (familySpouseInfo != null) {
            if ("1".equals(familySex)) {
                maleView = createFamilyView(familyInfo, maleLeft, mGenerationTop[position]);
                femaleView = createFamilyView(familySpouseInfo, femaleLeft, mGenerationTop[position]);
            } else {
                maleView = createFamilyView(familySpouseInfo, maleLeft, mGenerationTop[position]);
                femaleView = createFamilyView(familyInfo, femaleLeft, mGenerationTop[position]);
            }
            return Pair.create(maleView, femaleView);
        } else {
            if ("1".equals(familySex)) {
                maleView = createFamilyView(familyInfo, centerLeft, mGenerationTop[position]);
                femaleView = null;
            } else {
                maleView = null;
                femaleView = createFamilyView(familyInfo, centerLeft, mGenerationTop[position]);
            }
            return Pair.create(maleView, femaleView);
        }
    }

    private void setRightByGeneration(int generation, List<Pair<View, View>> pairList) {
        final int position = generation - 1;
        if (pairList.size() > 0) {
            final Pair<View, View> lastPair = pairList.get(pairList.size() - 1);
            final View lastView = lastPair.second != null ? lastPair.second : lastPair.first;
            mGenerationRight[position] = lastView.getLeft();
        }
        mGenerationRight[position] += mItemWidthPX + mSpacePX;
    }

    private void setLeftByGeneration(int generation, List<Pair<View, View>> pairList) {
        final int position = generation - 1;
        if (pairList.size() > 0) {
            final Pair<View, View> firstPair = pairList.get(0);
            final View firstView = firstPair.first != null ? firstPair.first : firstPair.second;
            mGenerationLeft[position] = firstView.getLeft();
        }
        mGenerationLeft[position] -= (mSpacePX + mItemWidthPX);
    }

    private View createFamilyView(FamilyBean family, int left, int top) {
        final View familyView = LayoutInflater.from(getContext()).inflate(R.layout.item_family3, this, false);
        familyView.getLayoutParams().width = mItemWidthPX;
        familyView.getLayoutParams().height = mItemHeightPX;
        familyView.setLeft(left);
        familyView.setTop(top);
        familyView.setTag(family);

        final ImageView ivAvatar = (ImageView) familyView.findViewById(R.id.iv_avatar);
        ivAvatar.getLayoutParams().height = mItemWidthPX;

        final LinearLayout llBackground = (LinearLayout) familyView.findViewById(R.id.ll_background);

        final TextView tvName = (TextView) familyView.findViewById(R.id.tv_name);
        tvName.setTextSize(NAME_TEXT_SIZE_SP);
        tvName.setText(family.getMemberName());

        final TextView tvCall = (TextView) familyView.findViewById(R.id.tv_call);
        tvCall.setTextSize(CALL_TEXT_SIZE_SP);
        tvCall.setText("(" + family.getCall() + ")");

        final String url = family.getMemberImg();
        final String sex = family.getSex();
        Glide.with(getContext())
                .load(url)
                .placeholder("2".equals(sex) ? R.drawable.ic_female_avatar : R.drawable.ic_male_avatar)
                .error("2".equals(sex) ? R.drawable.ic_female_avatar : R.drawable.ic_male_avatar)
                .centerCrop()
                .dontAnimate()
                .into(ivAvatar);
        if (family.isSelect()) {
            familyView.setBackgroundResource(R.drawable.shape_bg_select);
            llBackground.setBackgroundResource("2".equals(sex) ? R.color.red_middle : R.color.green_middle);
        } else {
            familyView.setBackgroundResource("2".equals(sex) ? R.drawable.shape_bg_female : R.drawable.shape_bg_male);
            llBackground.setBackgroundResource(R.color.transparent);
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

        if (mCurrentWidth == 0 && mCurrentHeight == 0) {
            mCurrentWidth = (mShowWidthPX - mItemWidthPX) / 2;
            mCurrentHeight = (mShowHeightPX - mItemHeightPX) / 2;
        }

        scrollTo(mOnlyMyView.getLeft() - mCurrentWidth, mOnlyMyView.getTop() - mCurrentHeight);
    }

    private void setChildViewFrame(View childView, int left, int top, int width, int height) {
        childView.layout(left, top, left + width, top + height);
    }

    @Override
    protected void onDraw(Canvas canvas) {
//        super.onDraw(canvas);
        drawPart(canvas, mMyChildrenInfo, mMyChildrenView, mMyGrandChildrenView);
        drawPart(canvas, mMyLittleBrotherInfo, mMyLittleBrotherView, mMyLittleBroChildrenView);
        drawPart(canvas, mMyBrotherInfo, mMyBrotherView, mMyBroChildrenView);
        drawPart(canvas, mMyMoUncleInfo, mMyMoUncleView, mMyMoUncleChildrenView);
        drawPart(canvas, mMyFaUncleInfo, mMyFaUncleView, mMyFaUncleChildrenView);
        drawIndependentViewLine(canvas, mMyInfo, mMyView, 3);
        drawIndependentViewLine(canvas, mMyParentInfo, mMyParentView, 2);
        drawIndependentViewLine(canvas, mMyPGrandParentInfo, mMyPGrandParentView, 1);
        drawIndependentViewLine(canvas, mMyMGrandParentInfo, mMyMGrandParentView, 1);

    }

    private void drawPart(Canvas canvas,
                          List<FamilyBean> parentInfoList,
                          List<Pair<View, View>> parentViewList,
                          List<Pair<View, View>> childrenViewList) {
        int childIndex = 0;
        final int parentCount = parentInfoList.size();
        for (int i = 0; i < parentCount; i++) {
            final FamilyBean parentInfo = parentInfoList.get(i);
            final Pair<View, View> parentPair = parentViewList.get(i);
            final List<FamilyBean> childrenInfoList = parentInfo.getChildren();
            final int childrenCount = childrenInfoList.size();
            final int centerX = drawViewLine(canvas, parentInfo, parentPair);

            for (int j = 0; j < childrenCount; j++, childIndex++) {
                final FamilyBean childInfo = childrenInfoList.get(j);
                final Pair<View, View> childPair = childrenViewList.get(childIndex);
                drawViewLine(canvas, childInfo, childPair);

                if (j == 0 || j == childrenCount - 1) {
                    final String childSex = childInfo.getSex();
                    final View childView;
                    if ("1".equals(childSex)) {
                        childView = childPair.first;
                    } else {
                        childView = childPair.second;
                    }
                    final int horizontalY = childView.getTop() - mSpacePX / 2;
                    final int horizontalStartX = childView.getLeft() + mItemWidthPX / 2;
                    drawLine(canvas, horizontalStartX, centerX, horizontalY, horizontalY);
                }
            }
        }

    }

    private int drawViewLine(Canvas canvas, FamilyBean familyInfo, Pair<View, View> familyPair) {
        int centerX = 0;
        final String familySex = familyInfo.getSex();
        final String fatherId = familyInfo.getFatherId();
        final String motherId = familyInfo.getMotherId();
        final List<FamilyBean> childrenList = familyInfo.getChildren();
        final View familyView;
        final View familySpouseView;
        if ("1".equals(familySex)) {
            familyView = familyPair.first;
            familySpouseView = familyPair.second;
        } else {
            familyView = familyPair.second;
            familySpouseView = familyPair.first;
        }
        if (familyView != null && familySpouseView != null) {
            final int horizontalStartX = familyView.getLeft() + mItemWidthPX / 2;
            final int horizontalEndX = familySpouseView.getLeft() + mItemWidthPX / 2;
            final int horizontalY = familyView.getTop() + mItemHeightPX / 2;
            drawLine(canvas, horizontalStartX, horizontalEndX, horizontalY, horizontalY);
            if (childrenList != null && childrenList.size() > 0) {
                final int verticalX = (horizontalEndX + horizontalStartX) / 2;
                final int vertiaclEndY = familyView.getTop() + mItemHeightPX + mSpacePX / 2;
                drawLine(canvas, verticalX, verticalX, horizontalY, vertiaclEndY);
                centerX = verticalX;
            }
        } else {
            if (familyView != null && childrenList != null && childrenList.size() > 0) {
                final int verticalX = familyView.getLeft() + mItemWidthPX / 2;
                final int verticalStartY = familyView.getTop() + mItemHeightPX / 2;
                final int vertiaclEndY = familyView.getTop() + mItemHeightPX + mSpacePX / 2;
                drawLine(canvas, verticalX, verticalX, verticalStartY, vertiaclEndY);
                centerX = verticalX;
            }
        }
        if (familyView != null && (!TextUtils.isEmpty(fatherId) || !TextUtils.isEmpty(motherId))) {
            final int verticalX = familyView.getLeft() + mItemWidthPX / 2;
            final int verticalStartY = familyView.getTop();
            final int verticalEndY = verticalStartY - mSpacePX / 2;
            drawLine(canvas, verticalX, verticalX, verticalStartY, verticalEndY);
        }
        return centerX;
    }

    private void drawIndependentViewLine(Canvas canvas, FamilyBean familyInfo, Pair<View, View> familyPair, int generation) {
        if (familyInfo != null) {
            final boolean haveTopLine;
            final boolean haveSpoueTopLine;
            if (generation == 1) {
                haveTopLine = false;
                haveSpoueTopLine = false;
            } else if (generation == 2) {
                haveTopLine = true;
                haveSpoueTopLine = true;
            } else if (generation == 3) {
                haveTopLine = true;
                haveSpoueTopLine = false;
            } else {
                return;
            }
            final String familySex = familyInfo.getSex();
            final String fatherId = familyInfo.getFatherId();
            final String motherId = familyInfo.getMotherId();
            final FamilyBean spouseInfo = familyInfo.getSpouse();
            final View familyView;
            final View familySpouseView;
            if ("1".equals(familySex)) {
                familyView = familyPair.first;
                familySpouseView = familyPair.second;
            } else {
                familyView = familyPair.second;
                familySpouseView = familyPair.first;
            }
            if (familyView != null && familySpouseView != null) {
                final int horizontalStartX = familyView.getLeft() + mItemWidthPX / 2;
                final int horizontalEndX = familySpouseView.getLeft() + mItemWidthPX / 2;
                final int horizontalY = familyView.getTop() + mItemHeightPX / 2;
                drawLine(canvas, horizontalStartX, horizontalEndX, horizontalY, horizontalY);
                final int verticalX = (horizontalEndX + horizontalStartX) / 2;
                final int vertiaclEndY = familyView.getTop() + mItemHeightPX + mSpacePX / 2;
                drawLine(canvas, verticalX, verticalX, horizontalY, vertiaclEndY);
            } else {
                if (familyView != null) {
                    final int verticalX = familyView.getLeft() + mItemWidthPX / 2;
                    final int verticalStartY = familyView.getTop() + mItemHeightPX / 2;
                    final int vertiaclEndY = familyView.getTop() + mItemHeightPX + mSpacePX / 2;
                    drawLine(canvas, verticalX, verticalX, verticalStartY, vertiaclEndY);
                }
            }
            if (haveTopLine) {
                if (familyView != null && (!TextUtils.isEmpty(fatherId) || !TextUtils.isEmpty(motherId))) {
                    final int verticalX = familyView.getLeft() + mItemWidthPX / 2;
                    final int verticalStartY = familyView.getTop();
                    final int verticalEndY = verticalStartY - mSpacePX / 2;
                    drawLine(canvas, verticalX, verticalX, verticalStartY, verticalEndY);
                    if (generation == 2) {
                        final int horizontalStartX;
                        if (!TextUtils.isEmpty(fatherId) && !TextUtils.isEmpty(motherId)) {
                            horizontalStartX = familyView.getLeft() - mSpacePX / 2;
                        } else {
                            horizontalStartX = familyView.getLeft() + mItemWidthPX / 2;
                        }
                        drawLine(canvas, horizontalStartX, verticalX, verticalEndY, verticalEndY);
                    }
                }
                if (haveSpoueTopLine && spouseInfo != null && familySpouseView != null) {
                    final String spouseFatherId = spouseInfo.getFatherId();
                    final String spouseMotherId = spouseInfo.getMotherId();
                    if (!TextUtils.isEmpty(spouseFatherId) || !TextUtils.isEmpty(spouseMotherId)) {
                        final int verticalX = familySpouseView.getLeft() + mItemWidthPX / 2;
                        final int verticalStartY = familySpouseView.getTop();
                        final int verticalEndY = verticalStartY - mSpacePX / 2;
                        drawLine(canvas, verticalX, verticalX, verticalStartY, verticalEndY);
                        final int horizontalStartX;
                        if (!TextUtils.isEmpty(spouseFatherId) && !TextUtils.isEmpty(spouseMotherId)) {
                            horizontalStartX = familySpouseView.getLeft() + mItemWidthPX + mSpacePX / 2;
                        } else {
                            horizontalStartX = familySpouseView.getLeft() + mItemWidthPX / 2;
                        }
                        drawLine(canvas, horizontalStartX, verticalX, verticalEndY, verticalEndY);
                    }
                }
            }
        }
    }

    private void drawLine(Canvas canvas, int startX, int endX, int startY, int endY) {
        mPath.reset();
        mPath.moveTo(startX, startY);
        mPath.lineTo(endX, endY);
        canvas.drawPath(mPath, mPaint);
    }

    public void setOnFamilyClickListener(OnFamilyClickListener onFamilyClickListener) {
        this.mOnFamilyClickListener = onFamilyClickListener;
    }

    private OnClickListener click = new OnClickListener() {
        @Override
        public void onClick(View v) {
            if (mOnFamilyClickListener != null) {
                mCurrentWidth = v.getLeft() - getScrollX();
                mCurrentHeight = v.getTop() - getScrollY();
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
                Logger.d("scrollX = " + getScrollX() + "\nscrollY = " + getScrollY());
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
