package com.cxb.familytree.ui;

import android.Manifest;
import android.os.Bundle;

import com.alibaba.fastjson.JSONObject;
import com.cxb.familytree.R;
import com.cxb.familytree.interfaces.OnFamilyClickListener;
import com.cxb.familytree.model.FamilyBean;
import com.cxb.familytree.ui.view.FamilyTreeView3;
import com.cxb.familytree.utils.AssetsUtil;
import com.cxb.familytree.utils.ToastMaster;

import java.util.List;

/**
 * 仿亲友+
 */

public class FamilyTreeActivity3 extends BaseActivity {

    private static final String MY_ID = "601";

    private FamilyTreeView3 ftvTree;//没有养父母

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_family_tree3);

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
        ftvTree = (FamilyTreeView3) findViewById(R.id.ftv_tree);
    }

    private void setData() {
        String json = AssetsUtil.getAssetsTxtByName(this, "family_tree.txt");
        List<FamilyBean> mList = JSONObject.parseArray(json, FamilyBean.class);

        ftvTree.saveData(mList);
        ftvTree.drawFamilyTree(MY_ID);
        ftvTree.setOnFamilyClickListener(familyClick);
    }

    private OnFamilyClickListener familyClick = new OnFamilyClickListener() {
        @Override
        public void onFamilySelect(FamilyBean family) {
            if (family.isSelect()) {
                ToastMaster.toast(family.getMemberName());
            } else {
                String currentFamilyId = family.getMemberId();
                ftvTree.drawFamilyTree(currentFamilyId);
            }
        }
    };

}
