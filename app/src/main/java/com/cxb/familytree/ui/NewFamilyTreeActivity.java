package com.cxb.familytree.ui;

import android.Manifest;
import android.os.Bundle;

import com.alibaba.fastjson.JSONObject;
import com.cxb.familytree.MyApplication;
import com.cxb.familytree.R;
import com.cxb.familytree.db.FamilyDBHelper;
import com.cxb.familytree.interfaces.OnFamilyClickListener;
import com.cxb.familytree.model.FamilyBean;
import com.cxb.familytree.ui.view.NewFamilyTreeView;
import com.cxb.familytree.utils.AssetsUtil;
import com.cxb.familytree.utils.ToastMaster;

import java.util.List;

/**
 * 仿亲友+
 */

public class NewFamilyTreeActivity extends BaseActivity {

    private static final String MY_ID = "601";

    private NewFamilyTreeView ftvTree;//没有养父母

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_family_tree);

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
        ftvTree.destroyView();
    }

    @Override
    public void onPermissionSuccess() {
        setData();
    }

    private void initView() {
        ftvTree = findViewById(R.id.ftv_tree);
    }

    private void setData() {
        String json = AssetsUtil.getAssetsTxtByName(this, "family_tree.txt");
        List<FamilyBean> mList = JSONObject.parseArray(json, FamilyBean.class);

        final FamilyDBHelper dbHelper = new FamilyDBHelper(MyApplication.getInstance());
        dbHelper.save(mList);
        final FamilyBean my = dbHelper.findFamilyById(MY_ID);
        dbHelper.closeDB();

        ftvTree.setShowBottomSpouse(false);
        ftvTree.drawFamilyTree(my);
        ftvTree.setOnFamilyClickListener(familyClick);
    }

    private OnFamilyClickListener familyClick = new OnFamilyClickListener() {
        @Override
        public void onFamilySelect(FamilyBean family) {
            if (family.isSelect()) {
                ToastMaster.toast(family.getMemberName());
            } else {
                ftvTree.drawFamilyTree(family);
            }
        }
    };

}
