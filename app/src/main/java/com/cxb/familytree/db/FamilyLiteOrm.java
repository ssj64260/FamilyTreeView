package com.cxb.familytree.db;

import android.content.Context;
import android.text.TextUtils;

import com.cxb.familytree.model.FamilyMember;
import com.litesuits.orm.LiteOrm;
import com.litesuits.orm.db.assit.QueryBuilder;

import java.util.List;

/**
 * 家庭成员数据库管理
 */

public class FamilyLiteOrm {

    private final String DB_NAME = "FamilyTree.db";

    private final boolean DEBUGGABLE = true; // 是否输出log

    private LiteOrm liteOrm;

    public FamilyLiteOrm(Context context) {
        liteOrm = LiteOrm.newSingleInstance(context, DB_NAME);
        liteOrm.setDebugged(DEBUGGABLE);
    }

    public FamilyMember getFamilyTreeById(String familyId) {
        FamilyMember family = getFamilyById(familyId);
        if (family != null) {
            String fosterFather = family.getFathersId();
            String fosterMother = family.getMothersId();
            String fatherId = family.getFatherId();
            String motherId = family.getMotherId();
            String spouseId = family.getSpouseId();

            family.setFosterFather(getFamilyAndParentById(fosterFather));
            family.setFosterMother(getFamilyAndParentById(fosterMother));
            family.setFather(getFamilyAndParentById(fatherId));
            family.setMother(getFamilyAndParentById(motherId));
            family.setSpouse(getFamilyById(spouseId));
            family.setBrothers(getFamiliesByParentId(familyId, fatherId, motherId));
            family.setChildren(getChildrenByParentId(familyId));

            family.setSelect(true);
        }

        return family;
    }

    private FamilyMember getFamilyAndParentById(String familyId) {
        FamilyMember family = null;
        if (!TextUtils.isEmpty(familyId)) {
            family = getFamilyById(familyId);
            if (family != null) {
                String fatherId = family.getFatherId();
                String motherId = family.getMotherId();
                FamilyMember father = getFamilyById(fatherId);
                if (father != null) {
                    family.setFather(father);
                }
                FamilyMember mother = getFamilyById(motherId);
                if (mother != null) {
                    family.setMother(mother);
                }
            }
        }

        return family;
    }

    private List<FamilyMember> getChildrenByParentId(String parentId) {
        List<FamilyMember> children = getFamiliesByParentId(parentId, parentId, parentId);
        for (FamilyMember child : children) {
            String childId = child.getMemberId();

            child.setChildren(getFamiliesByParentId(childId, childId, childId));
        }

        return children;
    }

    public long save(List<FamilyMember> families) {
        return liteOrm.save(families);
    }

    public int deleteTable() {
        return liteOrm.delete(FamilyMember.class);
    }

    public FamilyMember getFamilyById(String id) {
        if (!TextUtils.isEmpty(id)) {
            List<FamilyMember> families = liteOrm.query(new QueryBuilder<>(FamilyMember.class).where("memberId = ?", id));
            if (families.size() > 0) {
                return families.get(0);
            }
        }

        return null;
    }

    public List<FamilyMember> getFamiliesByParentId(String myId, String fatherId, String motherId) {
        String parentId = null;
        if (!TextUtils.isEmpty(fatherId)) {
            parentId = fatherId;
        } else if (!TextUtils.isEmpty(motherId)) {
            parentId = motherId;
        }

        if (!TextUtils.isEmpty(parentId)) {
            return liteOrm.query(new QueryBuilder<>(FamilyMember.class)
                    .where("memberId != ? and (fatherId = ? or motherId = ?)", myId, parentId, parentId));
        }

        return null;
    }


    public void closeDB() {
        if (liteOrm != null) {
            liteOrm.close();
        }
    }
}
