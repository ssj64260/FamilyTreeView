package com.cxb.familytree.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * 家族人员
 */

public class FamilyMember implements Parcelable {

    private String id;//人员ID
    private String name;//姓名
    private String call;//称呼
    private String avatar;//头像

    private FamilyMember spouse;//配偶
    private FamilyMember father;//父亲
    private FamilyMember mother;//母亲
    private List<FamilyMember> brothers;//兄弟姐妹
    private List<FamilyMember> children;//儿女


    private boolean isSelect = false;//是否选中

    public FamilyMember() {

    }

    protected FamilyMember(Parcel in) {
        id = in.readString();
        name = in.readString();
        call = in.readString();
        avatar = in.readString();
        spouse = in.readParcelable(FamilyMember.class.getClassLoader());
        father = in.readParcelable(FamilyMember.class.getClassLoader());
        mother = in.readParcelable(FamilyMember.class.getClassLoader());
        brothers = in.createTypedArrayList(FamilyMember.CREATOR);
        children = in.createTypedArrayList(FamilyMember.CREATOR);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(name);
        dest.writeString(call);
        dest.writeString(avatar);
        dest.writeParcelable(spouse, flags);
        dest.writeParcelable(father, flags);
        dest.writeParcelable(mother, flags);
        dest.writeTypedList(brothers);
        dest.writeTypedList(children);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<FamilyMember> CREATOR = new Creator<FamilyMember>() {
        @Override
        public FamilyMember createFromParcel(Parcel in) {
            return new FamilyMember(in);
        }

        @Override
        public FamilyMember[] newArray(int size) {
            return new FamilyMember[size];
        }
    };

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCall() {
        return call;
    }

    public void setCall(String call) {
        this.call = call;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public FamilyMember getSpouse() {
        return spouse;
    }

    public void setSpouse(FamilyMember spouse) {
        this.spouse = spouse;
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
