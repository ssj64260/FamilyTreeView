package com.cxb.familytree.db;

import android.content.Context;
import android.text.TextUtils;

import com.cxb.familytree.model.FamilyBean;
import com.litesuits.orm.LiteOrm;
import com.litesuits.orm.db.assit.QueryBuilder;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lenovo on 17/7/10.
 */

public class FamilyDBHelper {

    private final String DB_NAME = "FamilyTree.db";

    private final boolean DEBUGGABLE = true; // 是否输出log

    private LiteOrm liteOrm;

    public FamilyDBHelper(Context context) {
        liteOrm = LiteOrm.newSingleInstance(context, DB_NAME);
        liteOrm.setDebugged(DEBUGGABLE);
    }

    public FamilyBean findFamilyById(String familyId) {
        if (!TextUtils.isEmpty(familyId)) {
            List<FamilyBean> families = liteOrm.query(new QueryBuilder<>(FamilyBean.class).where("memberId = ?", familyId));
            if (families.size() > 0) {
                return families.get(0);
            }
        }

        return null;
    }

    public List<FamilyBean> findFamiliesByFatherId(String fatherId, String ignoreId) {
        if (!TextUtils.isEmpty(fatherId)) {
            final String sql = "fatherId = ? and memberId != ?";
            return liteOrm.query(new QueryBuilder<>(FamilyBean.class)
                    .appendOrderAscBy("birthday")
                    .where(sql, fatherId, ignoreId));
        } else {
            return new ArrayList<>();
        }
    }

    public List<FamilyBean> findFamiliesByMotherId(String motherId, String ignoreId) {
        if (!TextUtils.isEmpty(motherId)) {
            final String sql = "motherId = ? and memberId != ?";
            return liteOrm.query(new QueryBuilder<>(FamilyBean.class)
                    .appendOrderAscBy("birthday")
                    .where(sql, motherId, ignoreId));
        } else {
            return new ArrayList<>();
        }
    }

    public List<FamilyBean> findMyBrothersByParentId(String fatherId, String motherId, String ignoreId, String birthday, boolean isLittle) {
        final String parentId;
        String sql;
        if (!TextUtils.isEmpty(fatherId)) {
            sql = "fatherId = ? and memberId != ?";
            parentId = fatherId;
        } else if (!TextUtils.isEmpty(motherId)) {
            sql = "motherId = ? and memberId != ?";
            parentId = motherId;
        } else {
            return new ArrayList<>();
        }

        if (isLittle) {
            sql += " and birthday > ?";
        } else {
            sql += " and birthday <= ?";
        }

        return liteOrm.query(new QueryBuilder<>(FamilyBean.class)
                .appendOrderAscBy("birthday")
                .where(sql, parentId, ignoreId, birthday));
    }

    public long save(List<FamilyBean> families) {
        return liteOrm.save(families);
    }

    public int deleteTable() {
        return liteOrm.delete(FamilyBean.class);
    }

    public void closeDB() {
        if (liteOrm != null) {
            liteOrm.close();
        }
    }

}
