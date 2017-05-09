package com.cxb.familytree.ui.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
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

public class FamilyTreeView3 extends ViewGroup {

    private final int maxHeightDP = 800;//最大高度为800dp
    private final int spaceDP = 50;//间距为50dp
    private final int lineWidthDP = 2;//连线宽度2dp

    private OnFamilySelectListener mOnFamilySelectListener;

    private int screenWidth;//屏幕宽度PX
    private int screenHeight;//屏幕高度PX

    private int maxWidthPX;//最大宽度PX
    private int maxHeightPX;//最大高度PX
    private int spacePX;//元素间距PX
    private int lineWidthPX;//连线宽度PX

    private FamilyMember mFamilyMember;

    private View mineView;//我的View
    private View spouseView;//配偶View
    private View fatherView;//父亲View
    private View motherView;//母亲View
    private List<View> brothersView;//兄弟姐妹View
    private View paternalGrandFatherView;//爷爷View
    private View paternalGrandMotherView;//奶奶View
    private View maternalGrandFatherView;//外公
    private View maternalGrandMotherView;//外婆

    private Paint mPaint;//连线样式
    private Path mPath;//路径

    private int currentX;//当前X坐标
    private int currentY;//当前Y坐标

    private int touchX;//移动的距离
    private int touchY;//移动的距离

    public FamilyTreeView3(Context context) {
        this(context, null, 0);
    }

    public FamilyTreeView3(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FamilyTreeView3(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        screenWidth = DisplayUtil.getScreenWidth();
        screenHeight = DisplayUtil.getScreenHeight();
        spacePX = DisplayUtil.dip2px(spaceDP);
        lineWidthPX = DisplayUtil.dip2px(lineWidthDP);

        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setColor(0xFF000000);
        mPaint.setStrokeWidth(lineWidthPX);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setPathEffect(new DashPathEffect(new float[]{lineWidthDP * 2, lineWidthDP * 2}, 0));

        mPath = new Path();

        brothersView = new ArrayList<>();

//        initWidthAndHeight();
//        initView();
    }

    private void initWidthAndHeight() {
        int[] widthDP = {
                350,//第一代最大宽度
                250,//第二代最大宽度
                50,//第三代最大宽度
                50,//第四代最大宽度
                50//第五代最大宽度
        };

        if (mFamilyMember.getSpouse() != null) {
            widthDP[2] += 100;
        }
        if (mFamilyMember.getBrothers() != null) {
            widthDP[2] += 100 * mFamilyMember.getBrothers().size();
        }

        List<FamilyMember> myChildren = mFamilyMember.getChildren();
        if (myChildren != null) {
            widthDP[3] += 100 * myChildren.size();
            int grandChildrenCount = 0;
            for (int i = 0; i < myChildren.size(); i++) {
                List<FamilyMember> grandChildrenList = myChildren.get(i).getChildren();
                if (grandChildrenList != null) {
                    grandChildrenCount += grandChildrenList.size();
                }
            }
            widthDP[4] = (grandChildrenCount * 2 - 1) * 50;
        }

        maxWidthPX = screenWidth;
        for (int width : widthDP) {
            int widthPX = DisplayUtil.dip2px(width);
            if (widthPX > maxWidthPX) {
                maxWidthPX = widthPX;
            }
        }

        maxHeightPX = Math.max(DisplayUtil.dip2px(maxHeightDP), screenHeight);
    }

    private void initView() {
        FamilyMember mySpouse = mFamilyMember.getSpouse();
        FamilyMember myFather = mFamilyMember.getFather();
        FamilyMember myMother = mFamilyMember.getMother();
        List<FamilyMember> myBrothers = mFamilyMember.getBrothers();
        List<FamilyMember> myChildren = mFamilyMember.getChildren();

        mineView = createFamilyView(mFamilyMember);
        if (mySpouse != null) {
            spouseView = createFamilyView(mySpouse);
        }

        if (myFather != null) {
            fatherView = createFamilyView(myFather);
            FamilyMember myPaternalGrandFather = myFather.getFather();
            FamilyMember myPaternalGrandMother = myFather.getMother();
            if (myPaternalGrandFather != null) {
                paternalGrandFatherView = createFamilyView(myPaternalGrandFather);
            }
            if (myPaternalGrandMother != null) {
                paternalGrandMotherView = createFamilyView(myPaternalGrandMother);
            }

        }
        if (myMother != null) {
            motherView = createFamilyView(myMother);
            FamilyMember myMaternalGrandFather = myMother.getFather();
            FamilyMember myMaternalGrandMother = myMother.getMother();

            if (myMaternalGrandFather != null) {
                maternalGrandFatherView = createFamilyView(myMaternalGrandFather);
            }
            if (myMaternalGrandMother != null) {
                maternalGrandMotherView = createFamilyView(myMaternalGrandMother);
            }
        }

        brothersView.clear();
        for (FamilyMember family : myBrothers) {
            brothersView.add(createFamilyView(family));
        }
    }

    private View createFamilyView(FamilyMember family) {
        View familyView = LayoutInflater.from(getContext()).inflate(R.layout.item_family, this, false);
        ImageView ivAvatar = (ImageView) familyView.findViewById(R.id.iv_avatar);
        TextView tvCall = (TextView) familyView.findViewById(R.id.tv_call);

        familyView.setTag(family);
        Glide.with(getContext())
                .load(family.getAvatar())
                .centerCrop()
                .transform(new GlideCircleTransform(getContext()))
                .dontAnimate()
                .into(ivAvatar);
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

        if (mineView != null) {
            measureChild(mineView, widthMeasureSpec, heightMeasureSpec);
        }
        if (spouseView != null) {
            measureChild(spouseView, widthMeasureSpec, heightMeasureSpec);
        }

        if (fatherView != null) {
            measureChild(fatherView, widthMeasureSpec, heightMeasureSpec);

            if (paternalGrandFatherView != null) {
                measureChild(paternalGrandFatherView, widthMeasureSpec, heightMeasureSpec);
            }
            if (paternalGrandMotherView != null) {
                measureChild(paternalGrandMotherView, widthMeasureSpec, heightMeasureSpec);
            }
        }
        if (motherView != null) {
            measureChild(motherView, widthMeasureSpec, heightMeasureSpec);

            if (maternalGrandFatherView != null) {
                measureChild(maternalGrandFatherView, widthMeasureSpec, heightMeasureSpec);
            }
            if (maternalGrandMotherView != null) {
                measureChild(maternalGrandMotherView, widthMeasureSpec, heightMeasureSpec);
            }
        }

        if (brothersView.size() > 0) {
            for (View view : brothersView) {
                measureChild(view, widthMeasureSpec, heightMeasureSpec);
            }
        }

        setMeasuredDimension(maxWidthPX, maxHeightPX);
        scrollTo((maxWidthPX - screenWidth) / 2, (maxHeightPX - screenHeight) / 2);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        setLayout(l, t, r, b);
    }

    private void setLayout(int left, int top, int right, int bottom) {
        int mineWidth = mineView.getMeasuredWidth();
        int mineHeight = mineView.getMeasuredHeight();
        int mineLeft = maxWidthPX / 2 - mineWidth / 2;
        int mineTop = maxHeightPX / 2 - mineHeight / 2;

        setChildFrame(mineView, mineLeft, mineTop, mineWidth, mineHeight);
        if (spouseView != null) {
            int spouseWidth = spouseView.getMeasuredWidth();
            int spouseHeight = spouseView.getMeasuredHeight();
            setChildFrame(spouseView,
                    mineLeft + spouseWidth + spacePX,
                    mineTop,
                    spouseWidth, spouseHeight);
        }

        if (fatherView != null && motherView != null) {
            int fatherWidth = fatherView.getMeasuredWidth();
            int fatherHeight = fatherView.getMeasuredHeight();
            setChildFrame(fatherView,
                    mineLeft - fatherWidth - spacePX,
                    mineTop - fatherHeight - spacePX,
                    fatherWidth, fatherHeight);

            int motherWidth = motherView.getMeasuredWidth();
            int motherHeight = motherView.getMeasuredHeight();
            setChildFrame(motherView,
                    mineLeft + motherWidth + spacePX,
                    mineTop - motherHeight - spacePX,
                    motherWidth, motherHeight);
        } else if (fatherView != null) {
            int fatherWidth = fatherView.getMeasuredWidth();
            int fatherHeight = fatherView.getMeasuredHeight();
            setChildFrame(fatherView,
                    mineLeft,
                    mineTop - fatherHeight - spacePX,
                    fatherWidth, fatherHeight);
        } else if (motherView != null) {
            int motherWidth = motherView.getMeasuredWidth();
            int motherHeight = motherView.getMeasuredHeight();
            setChildFrame(motherView,
                    mineLeft,
                    mineTop - motherHeight - spacePX,
                    motherWidth, motherHeight);
        }

        if (fatherView != null) {
            int fatherLeft = (int) fatherView.getX();
            int fatherTop = (int) fatherView.getY();

            if (paternalGrandFatherView != null && paternalGrandMotherView != null) {
                int grandFatherWidth = paternalGrandFatherView.getMeasuredWidth();
                int grandFatherHeight = paternalGrandFatherView.getMeasuredHeight();
                setChildFrame(paternalGrandFatherView,
                        fatherLeft - grandFatherWidth,
                        fatherTop - grandFatherHeight - spacePX,
                        grandFatherWidth, grandFatherHeight);

                int grandMotherWidth = paternalGrandMotherView.getMeasuredWidth();
                int grandMotherHeight = paternalGrandMotherView.getMeasuredHeight();
                setChildFrame(paternalGrandMotherView,
                        fatherLeft + grandMotherWidth,
                        fatherTop - grandMotherHeight - spacePX,
                        grandMotherWidth, grandMotherHeight);
            } else if (paternalGrandFatherView != null) {
                int grandFatherWidth = paternalGrandFatherView.getMeasuredWidth();
                int grandFatherHeight = paternalGrandFatherView.getMeasuredHeight();
                setChildFrame(paternalGrandFatherView,
                        fatherLeft,
                        fatherTop - grandFatherHeight - spacePX,
                        grandFatherWidth, grandFatherHeight);
            } else if (paternalGrandMotherView != null) {
                int grandMotherWidth = paternalGrandMotherView.getMeasuredWidth();
                int grandMotherHeight = paternalGrandMotherView.getMeasuredHeight();
                setChildFrame(paternalGrandMotherView,
                        fatherLeft,
                        fatherTop - grandMotherHeight - spacePX,
                        grandMotherWidth, grandMotherHeight);
            }
        }

        if (motherView != null) {
            int motherLeft = (int) motherView.getX();
            int motherTop = (int) motherView.getY();

            if (maternalGrandFatherView != null && maternalGrandMotherView != null) {
                int grandFatherWidth = maternalGrandFatherView.getMeasuredWidth();
                int grandFatherHeight = maternalGrandFatherView.getMeasuredHeight();
                setChildFrame(maternalGrandFatherView,
                        motherLeft - grandFatherWidth,
                        motherTop - grandFatherHeight - spacePX,
                        grandFatherWidth, grandFatherHeight);

                int grandMotherWidth = maternalGrandMotherView.getMeasuredWidth();
                int grandMotherHeight = maternalGrandMotherView.getMeasuredHeight();
                setChildFrame(maternalGrandMotherView,
                        motherLeft + grandMotherWidth,
                        motherTop - grandMotherHeight - spacePX,
                        grandMotherWidth, grandMotherHeight);
            } else if (maternalGrandFatherView != null) {
                int grandFatherWidth = maternalGrandFatherView.getMeasuredWidth();
                int grandFatherHeight = maternalGrandFatherView.getMeasuredHeight();
                setChildFrame(maternalGrandFatherView,
                        motherLeft,
                        motherTop - grandFatherHeight - spacePX,
                        grandFatherWidth, grandFatherHeight);
            } else if (maternalGrandMotherView != null) {
                int grandMotherWidth = maternalGrandMotherView.getMeasuredWidth();
                int grandMotherHeight = maternalGrandMotherView.getMeasuredHeight();
                setChildFrame(maternalGrandMotherView,
                        motherLeft,
                        motherTop - grandMotherHeight - spacePX,
                        grandMotherWidth, grandMotherHeight);
            }
        }

        if (brothersView.size() > 0) {
            int brotherCount = brothersView.size();
            for (int i = 0; i < brotherCount; i++) {
                View brotherView = brothersView.get(i);
                int brotherWidth = brotherView.getMeasuredWidth();
                int brotherHeight = brotherView.getMeasuredHeight();
                setChildFrame(brotherView,
                        mineLeft - (i + 1) * (brotherWidth + spacePX),
                        mineTop,
                        brotherWidth, brotherHeight);
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
    }

    private void drawSpouseLine(Canvas canvas) {
        if (spouseView != null) {
            int horizontalLineStartX = (int) mineView.getX() + mineView.getWidth();
            int horizontalLineStopX = (int) spouseView.getX();
            int horizontalLineY = (int) spouseView.getY() + spacePX / 2;
            mPath.reset();
            mPath.moveTo(horizontalLineStartX, horizontalLineY);
            mPath.lineTo(horizontalLineStopX, horizontalLineY);
            canvas.drawPath(mPath, mPaint);
        }
    }

    private void drawParentLine(Canvas canvas) {
        if (fatherView != null && motherView != null) {
            int horizontalLineStartX = (int) fatherView.getX() + fatherView.getMeasuredWidth();
            int horizontalLineStopX = (int) motherView.getX();
            int horizontalLineY = (int) fatherView.getY() + spacePX / 2;
            mPath.reset();
            mPath.moveTo(horizontalLineStartX, horizontalLineY);
            mPath.lineTo(horizontalLineStopX, horizontalLineY);
            canvas.drawPath(mPath, mPaint);

            int verticalLineX = (int) mineView.getX() + mineView.getMeasuredWidth() / 2;
            int verticalLineStopY = (int) mineView.getY();
            mPath.reset();
            mPath.moveTo(verticalLineX, horizontalLineY);
            mPath.lineTo(verticalLineX, verticalLineStopY);
            canvas.drawPath(mPath, mPaint);
        } else if (fatherView != null) {
            int verticalLineX = (int) mineView.getX() + mineView.getMeasuredWidth() / 2;
            int verticalLineStartY = (int) fatherView.getY() + fatherView.getMeasuredHeight();
            int verticalLineStopY = (int) mineView.getY();
            mPath.reset();
            mPath.moveTo(verticalLineX, verticalLineStartY);
            mPath.lineTo(verticalLineX, verticalLineStopY);
            canvas.drawPath(mPath, mPaint);
        } else if (motherView != null) {
            int verticalLineX = (int) mineView.getX() + mineView.getMeasuredWidth() / 2;
            int verticalLineStartY = (int) motherView.getY() + motherView.getMeasuredHeight();
            int verticalLineStopY = (int) mineView.getY();
            mPath.reset();
            mPath.moveTo(verticalLineX, verticalLineStartY);
            mPath.lineTo(verticalLineX, verticalLineStopY);
            canvas.drawPath(mPath, mPaint);
        }

        if (fatherView != null) {
            if (paternalGrandFatherView != null && paternalGrandMotherView != null) {
                int horizontalLineStartX = (int) paternalGrandFatherView.getX() + paternalGrandFatherView.getMeasuredWidth();
                int horizontalLineStopX = (int) paternalGrandMotherView.getX();
                int horizontalLineY = (int) paternalGrandFatherView.getY() + spacePX / 2;
                mPath.reset();
                mPath.moveTo(horizontalLineStartX, horizontalLineY);
                mPath.lineTo(horizontalLineStopX, horizontalLineY);
                canvas.drawPath(mPath, mPaint);

                int verticalLineX = (int) fatherView.getX() + fatherView.getMeasuredWidth() / 2;
                int verticalLineStopY = (int) fatherView.getY();
                mPath.reset();
                mPath.moveTo(verticalLineX, horizontalLineY);
                mPath.lineTo(verticalLineX, verticalLineStopY);
                canvas.drawPath(mPath, mPaint);
            } else if (paternalGrandFatherView != null) {
                int verticalLineX = (int) fatherView.getX() + fatherView.getMeasuredWidth() / 2;
                int verticalLineStartY = (int) paternalGrandFatherView.getY() + paternalGrandFatherView.getMeasuredHeight();
                int verticalLineStopY = (int) fatherView.getY();
                mPath.reset();
                mPath.moveTo(verticalLineX, verticalLineStartY);
                mPath.lineTo(verticalLineX, verticalLineStopY);
                canvas.drawPath(mPath, mPaint);
            } else if (paternalGrandMotherView != null) {
                int verticalLineX = (int) fatherView.getX() + fatherView.getMeasuredWidth() / 2;
                int verticalLineStartY = (int) paternalGrandMotherView.getY() + paternalGrandMotherView.getMeasuredHeight();
                int verticalLineStopY = (int) fatherView.getY();
                mPath.reset();
                mPath.moveTo(verticalLineX, verticalLineStartY);
                mPath.lineTo(verticalLineX, verticalLineStopY);
                canvas.drawPath(mPath, mPaint);
            }
        }

        if (motherView != null) {
            if (maternalGrandFatherView != null && maternalGrandMotherView != null) {
                int horizontalLineStartX = (int) maternalGrandFatherView.getX() + maternalGrandFatherView.getMeasuredWidth();
                int horizontalLineStopX = (int) maternalGrandMotherView.getX();
                int horizontalLineY = (int) maternalGrandFatherView.getY() + spacePX / 2;
                mPath.reset();
                mPath.moveTo(horizontalLineStartX, horizontalLineY);
                mPath.lineTo(horizontalLineStopX, horizontalLineY);
                canvas.drawPath(mPath, mPaint);

                int verticalLineX = (int) motherView.getX() + motherView.getMeasuredWidth() / 2;
                int verticalLineStopY = (int) motherView.getY();
                mPath.reset();
                mPath.moveTo(verticalLineX, horizontalLineY);
                mPath.lineTo(verticalLineX, verticalLineStopY);
                canvas.drawPath(mPath, mPaint);
            } else if (maternalGrandFatherView != null) {
                int verticalLineX = (int) motherView.getX() + motherView.getMeasuredWidth() / 2;
                int verticalLineStartY = (int) maternalGrandFatherView.getY() + maternalGrandFatherView.getMeasuredHeight();
                int verticalLineStopY = (int) motherView.getY();
                mPath.reset();
                mPath.moveTo(verticalLineX, verticalLineStartY);
                mPath.lineTo(verticalLineX, verticalLineStopY);
                canvas.drawPath(mPath, mPaint);
            } else if (maternalGrandMotherView != null) {
                int verticalLineX = (int) motherView.getX() + motherView.getMeasuredWidth() / 2;
                int verticalLineStartY = (int) maternalGrandMotherView.getY() + maternalGrandMotherView.getMeasuredHeight();
                int verticalLineStopY = (int) motherView.getY();
                mPath.reset();
                mPath.moveTo(verticalLineX, verticalLineStartY);
                mPath.lineTo(verticalLineX, verticalLineStopY);
                canvas.drawPath(mPath, mPaint);
            }
        }
    }

    private void drawBrothersLine(Canvas canvas) {
        if (brothersView.size() > 0) {
            int brotherCount = brothersView.size();
            View brotherView = brothersView.get(brotherCount - 1);

            int horizontalLineStartX = (int) (brotherView.getX() + brotherView.getMeasuredWidth());
            int horizontalLineY = (int) (brotherView.getY() + spacePX / 2);
            mPath.reset();
            mPath.moveTo(horizontalLineStartX, horizontalLineY);
            mPath.lineTo(mineView.getX(), horizontalLineY);
            canvas.drawPath(mPath, mPaint);
        }
    }

    public void setmFamilyMember(FamilyMember mFamilyMember) {
        this.mFamilyMember = mFamilyMember;
        mFamilyMember.setSelect(true);
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
        int currentTouchX = (int) event.getX();
        int currentTouchY = (int) event.getY();
//        Logger.d(touchX + "  " + touchY);
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                currentX = getScrollX();
                currentY = getScrollY();
                touchX = (int) event.getX();
                touchY = (int) event.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                int dx = currentTouchX - touchX;
                int dy = currentTouchY - touchY;
                currentX -= dx;
                currentY -= dy;
                if (currentX < 0) {
                    currentX = 0;
                }
                if (currentY < 0) {
                    currentY = 0;
                }
                if (currentX > maxWidthPX - screenWidth) {
                    currentX = maxWidthPX - screenWidth;
                }
                if (currentY > maxHeightPX - screenHeight) {
                    currentY = maxHeightPX - screenHeight;
                }
                this.scrollTo(currentX, currentY);
                touchX = currentTouchX;
                touchY = currentTouchY;
                break;
            case MotionEvent.ACTION_UP:

                break;
        }
        return true;
    }
}
