package com.cxb.familytree.interfaces;

import com.cxb.familytree.model.FamilyBean;

/**
 * 家庭成员选中回调
 */

public interface OnFamilyClickListener {
    void onFamilySelect(FamilyBean family);
}
