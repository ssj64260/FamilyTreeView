package com.cxb.familytree.ui;

import android.Manifest;
import android.os.Bundle;

import com.cxb.familytree.R;
import com.cxb.familytree.db.FamilyLiteOrm;
import com.cxb.familytree.model.FamilyMember;
import com.cxb.familytree.ui.view.FamilyTreeView;
import com.cxb.familytree.utils.AssetsUtil;
import com.cxb.familytree.utils.ToastMaster;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.List;

public class MainActivity extends BaseActivity {

    private static final String MY_FAMILY_ID = "601";

    private FamilyTreeView ftvTree;

    private FamilyMember mFamilyMember;

    private FamilyLiteOrm mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
        initData();
        setData();
    }

    private void initData() {
        mDatabase = new FamilyLiteOrm(this);

        String json = AssetsUtil.getAssetsTxtByName(this, "family_tree.txt");

        Gson gson = new Gson();
        List<FamilyMember> mList = gson.fromJson(json, new TypeToken<List<FamilyMember>>() {
        }.getType());
        mDatabase.deleteTable();
        mDatabase.save(mList);
    }

    private void initView() {
        ftvTree = (FamilyTreeView) findViewById(R.id.ftv_tree);
    }

    private void setData() {
        mFamilyMember = mDatabase.getFamilyTreeById(MY_FAMILY_ID);
        if (mFamilyMember != null) {
            ftvTree.setFamilyMember(mFamilyMember);
        }

        ftvTree.setmOnFamilySelectListener(new FamilyTreeView.OnFamilySelectListener() {
            @Override
            public void onFamilySelect(FamilyMember family) {
                if (family.isSelect()) {
                    ToastMaster.toast(family.getMemberName());
                } else {
                    String currentFamilyId = family.getMemberId();
                    FamilyMember currentFamily = mDatabase.getFamilyTreeById(currentFamilyId);
                    if (currentFamily != null) {
                        ftvTree.setFamilyMember(currentFamily);
                    }
                }
            }
        });
    }

}
