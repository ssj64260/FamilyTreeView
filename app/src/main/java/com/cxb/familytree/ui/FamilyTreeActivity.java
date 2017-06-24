package com.cxb.familytree.ui;

import android.Manifest;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.cxb.familytree.R;
import com.cxb.familytree.db.FamilyLiteOrm;
import com.cxb.familytree.interfaces.OnFamilySelectListener;
import com.cxb.familytree.model.FamilyMember;
import com.cxb.familytree.ui.view.FamilyTreeView;
import com.cxb.familytree.ui.view.FamilyTreeView2;
import com.cxb.familytree.utils.AssetsUtil;
import com.cxb.familytree.utils.ToastMaster;

import java.util.List;

/**
 * 家谱树
 */

public class FamilyTreeActivity extends BaseActivity {

    public static final String HAVE_FOSTER_PARENT = "have_foster_parent";//是否有养父母

    private static final String MY_ID = "601";

    private TextView tvChangeType;
    private Button btnEnlarge;
    private Button btnShrinkDown;
    private FamilyTreeView ftvTree;//没有养父母
    private FamilyTreeView2 ftvTree2;//有养父母

    private FamilyLiteOrm mDatabase;

    private boolean haveFosterParent = false;//是否有养父母

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_family_tree);

        initView();

        String appName = getString(R.string.app_name);
        permissions = new String[]{
                Manifest.permission.READ_EXTERNAL_STORAGE,
        };
        refuseTips = new String[]{
                String.format("在设置-应用-%1$s-权限中开启存储权限，以正常使用该功能", appName),
        };
        setPermissions();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mDatabase != null) {
            mDatabase.closeDB();
        }
    }

    @Override
    public void onPermissionSuccess() {
        setData();
    }

    private void initView() {
        tvChangeType = (TextView) findViewById(R.id.tv_change_type);
        btnEnlarge = (Button) findViewById(R.id.btn_enlarge);
        btnShrinkDown = (Button) findViewById(R.id.btn_shrink_down);
        ftvTree = (FamilyTreeView) findViewById(R.id.ftv_tree);
        ftvTree2 = (FamilyTreeView2) findViewById(R.id.ftv_tree2);
    }

    private void setData() {
        haveFosterParent = getIntent().getBooleanExtra(HAVE_FOSTER_PARENT, false);
        if (haveFosterParent) {
            tvChangeType.setText("有养父母");
            ftvTree.setVisibility(View.GONE);
            ftvTree2.setVisibility(View.VISIBLE);
            btnEnlarge.setVisibility(View.GONE);
            btnShrinkDown.setVisibility(View.GONE);
        } else {
            tvChangeType.setText("没有养父母");
            ftvTree.setVisibility(View.VISIBLE);
            ftvTree2.setVisibility(View.GONE);
        }

        mDatabase = new FamilyLiteOrm(this);
        String json = AssetsUtil.getAssetsTxtByName(this, "family_tree.txt");
        List<FamilyMember> mList = JSONObject.parseArray(json, FamilyMember.class);
        mDatabase.deleteTable();
        mDatabase.save(mList);

        btnEnlarge.setOnClickListener(click);
        btnShrinkDown.setOnClickListener(click);

        FamilyMember mFamilyMember = mDatabase.getFamilyTreeById(MY_ID);
        if (mFamilyMember != null) {
            if (haveFosterParent) {
                ftvTree2.setFamilyMember(mFamilyMember);
            } else {
                ftvTree.setFamilyMember(mFamilyMember);
            }
        }

        ftvTree.setOnFamilySelectListener(familySelect);
        ftvTree2.setOnFamilySelectListener(familySelect);
    }

    private OnFamilySelectListener familySelect = new OnFamilySelectListener() {
        @Override
        public void onFamilySelect(FamilyMember family) {
            if (family.isSelect()) {
                ToastMaster.toast(family.getMemberName());
            } else {
                String currentFamilyId = family.getMemberId();
                FamilyMember currentFamily = mDatabase.getFamilyTreeById(currentFamilyId);
                if (currentFamily != null) {
                    if (haveFosterParent) {
                        ftvTree2.setFamilyMember(currentFamily);
                    } else {
                        ftvTree.setFamilyMember(currentFamily);
                    }
                }
            }
        }
    };

    private View.OnClickListener click = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (!haveFosterParent) {
                switch (v.getId()) {
                    case R.id.btn_enlarge:
                        ftvTree.doEnlarge();
                        break;
                    case R.id.btn_shrink_down:
                        ftvTree.doShrinkDown();
                        break;
                }
            }
        }
    };
}
