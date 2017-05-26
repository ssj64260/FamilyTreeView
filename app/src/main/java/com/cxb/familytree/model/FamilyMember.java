package com.cxb.familytree.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.litesuits.orm.db.annotation.Ignore;
import com.litesuits.orm.db.annotation.PrimaryKey;
import com.litesuits.orm.db.annotation.Table;
import com.litesuits.orm.db.enums.AssignType;

import java.util.List;

/**
 * 家族人员
 */

@Table("FamilyMember")
public class FamilyMember implements Parcelable {

    @PrimaryKey(AssignType.BY_MYSELF)
    private String memberId;//人员ID
    private String memberName;//姓名
    private String call;//称呼
    private String memberImg;//头像

    private String fatherId;//父亲ID
    private String motherId;//母亲ID
    private String spouseId;//配偶ID

    private String mothersId;//养母ID
    private String fathersId;//养父ID

    @Ignore
    private FamilyMember spouse;//配偶
    @Ignore
    private FamilyMember fosterFather;//养父
    @Ignore
    private FamilyMember fosterMother;//养母
    @Ignore
    private FamilyMember father;//父亲
    @Ignore
    private FamilyMember mother;//母亲
    @Ignore
    private List<FamilyMember> brothers;//兄弟姐妹
    @Ignore
    private List<FamilyMember> children;//儿女
    @Ignore
    private boolean isSelect = false;//是否选中


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.memberId);
        dest.writeString(this.memberName);
        dest.writeString(this.call);
        dest.writeString(this.memberImg);
        dest.writeString(this.fatherId);
        dest.writeString(this.motherId);
        dest.writeString(this.spouseId);
        dest.writeString(this.mothersId);
        dest.writeString(this.fathersId);
        dest.writeParcelable(this.spouse, flags);
        dest.writeParcelable(this.fosterFather, flags);
        dest.writeParcelable(this.fosterMother, flags);
        dest.writeParcelable(this.father, flags);
        dest.writeParcelable(this.mother, flags);
        dest.writeTypedList(this.brothers);
        dest.writeTypedList(this.children);
        dest.writeByte(this.isSelect ? (byte) 1 : (byte) 0);
    }

    public FamilyMember() {
    }

    protected FamilyMember(Parcel in) {
        this.memberId = in.readString();
        this.memberName = in.readString();
        this.call = in.readString();
        this.memberImg = in.readString();
        this.fatherId = in.readString();
        this.motherId = in.readString();
        this.spouseId = in.readString();
        this.mothersId = in.readString();
        this.fathersId = in.readString();
        this.spouse = in.readParcelable(FamilyMember.class.getClassLoader());
        this.fosterFather = in.readParcelable(FamilyMember.class.getClassLoader());
        this.fosterMother = in.readParcelable(FamilyMember.class.getClassLoader());
        this.father = in.readParcelable(FamilyMember.class.getClassLoader());
        this.mother = in.readParcelable(FamilyMember.class.getClassLoader());
        this.brothers = in.createTypedArrayList(FamilyMember.CREATOR);
        this.children = in.createTypedArrayList(FamilyMember.CREATOR);
        this.isSelect = in.readByte() != 0;
    }

    public static final Creator<FamilyMember> CREATOR = new Creator<FamilyMember>() {
        @Override
        public FamilyMember createFromParcel(Parcel source) {
            return new FamilyMember(source);
        }

        @Override
        public FamilyMember[] newArray(int size) {
            return new FamilyMember[size];
        }
    };

    public String getMemberId() {
        return memberId;
    }

    public void setMemberId(String memberId) {
        this.memberId = memberId;
    }

    public String getMemberName() {
        return memberName;
    }

    public void setMemberName(String memberName) {
        this.memberName = memberName;
    }

    public String getCall() {
        return call;
    }

    public void setCall(String call) {
        this.call = call;
    }

    public String getMemberImg() {
        return memberImg;
    }

    public void setMemberImg(String memberImg) {
        this.memberImg = memberImg;
    }

    public String getFatherId() {
        return fatherId;
    }

    public void setFatherId(String fatherId) {
        this.fatherId = fatherId;
    }

    public String getMotherId() {
        return motherId;
    }

    public void setMotherId(String motherId) {
        this.motherId = motherId;
    }

    public String getSpouseId() {
        return spouseId;
    }

    public void setSpouseId(String spouseId) {
        this.spouseId = spouseId;
    }

    public String getMothersId() {
        return mothersId;
    }

    public void setMothersId(String mothersId) {
        this.mothersId = mothersId;
    }

    public String getFathersId() {
        return fathersId;
    }

    public void setFathersId(String fathersId) {
        this.fathersId = fathersId;
    }

    public FamilyMember getSpouse() {
        return spouse;
    }

    public void setSpouse(FamilyMember spouse) {
        this.spouse = spouse;
    }

    public FamilyMember getFosterFather() {
        return fosterFather;
    }

    public void setFosterFather(FamilyMember fosterFather) {
        this.fosterFather = fosterFather;
    }

    public FamilyMember getFosterMother() {
        return fosterMother;
    }

    public void setFosterMother(FamilyMember fosterMother) {
        this.fosterMother = fosterMother;
    }

    public FamilyMember getFather() {
        return father;
    }

    public void setFather(FamilyMember father) {
        this.father = father;
    }

    public FamilyMember getMother() {
        return mother;
    }

    public void setMother(FamilyMember mother) {
        this.mother = mother;
    }

    public List<FamilyMember> getBrothers() {
        return brothers;
    }

    public void setBrothers(List<FamilyMember> brothers) {
        this.brothers = brothers;
    }

    public List<FamilyMember> getChildren() {
        return children;
    }

    public void setChildren(List<FamilyMember> children) {
        this.children = children;
    }

    public boolean isSelect() {
        return isSelect;
    }

    public void setSelect(boolean select) {
        isSelect = select;
    }
}
