package com.cxb.familytree.ui;

import android.app.Activity;
import android.os.Bundle;

import com.cxb.familytree.R;
import com.cxb.familytree.model.FamilyMember;
import com.cxb.familytree.ui.view.FamilyTreeView;
import com.cxb.familytree.utils.ToastMaster;
import com.google.gson.Gson;
import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity {

    private FamilyTreeView ftvTree;

    private FamilyMember mFamilyMember;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initData();
        initView();
        setData();

    }

    private void initData() {
        String BIGBROTHER = "哥哥";
        String brotherId = "3";
        String brotherAvatar = "http://img4.a0bi.com/upload/ttq/20170506/1494045006559.png";
        String brotherName = "扎特";
        FamilyMember brother = new FamilyMember();
        brother.setId(brotherId);
        brother.setAvatar(brotherAvatar);
        brother.setName(brotherName);
        brother.setCall(BIGBROTHER);

        String LITTLESISTER = "妹妹";
        String sisterId = "4";
        String sisterAvatar = "http://e.hiphotos.baidu.com/zhidao/wh%3D450%2C600/sign=0197b59000087bf47db95fedc7e37b1a/38dbb6fd5266d016152614f3952bd40735fa3529.jpg";
        String sisterName = "艾薇";
        FamilyMember sister = new FamilyMember();
        sister.setId(sisterId);
        sister.setAvatar(sisterAvatar);
        sister.setName(sisterName);
        sister.setCall(LITTLESISTER);

        String PATERNALGRANDFATHER = "爷爷";
        String fGrandPaId = "12";
        String fGrandPaAvater = "http://img4.imgtn.bdimg.com/it/u=2184849766,490259022&fm=23&gp=0.jpg";
        String fGrandPaName = "强森";
        FamilyMember fGrandPa = new FamilyMember();
        fGrandPa.setId(fGrandPaId);
        fGrandPa.setAvatar(fGrandPaAvater);
        fGrandPa.setName(fGrandPaName);
        fGrandPa.setCall(PATERNALGRANDFATHER);

        String PATERNALGRANDMOTHER = "奶奶";
        String fGrandMaId = "13";
        String fGrandMaAvater = "http://www.qiujie.org/uploads/allimg/121112/16212VV1-6.jpg";
        String fGrandMaName = "玲娜";
        FamilyMember fGrandMa = new FamilyMember();
        fGrandMa.setId(fGrandMaId);
        fGrandMa.setAvatar(fGrandMaAvater);
        fGrandMa.setName(fGrandMaName);
        fGrandMa.setCall(PATERNALGRANDMOTHER);

        String MATERNALGRANDFATHER = "外公";
        String mGrandPaId = "14";
        String mGrandPaAvater = "http://b-ssl.duitang.com/uploads/item/201405/31/20140531180244_5GAst.jpeg";
        String mGrandPaName = "博得";
        FamilyMember mGrandPa = new FamilyMember();
        mGrandPa.setId(mGrandPaId);
        mGrandPa.setAvatar(mGrandPaAvater);
        mGrandPa.setName(mGrandPaName);
        mGrandPa.setCall(MATERNALGRANDFATHER);

        String MATERNALGRANDMOTHER = "外婆";
        String mGradnMaId = "15";
        String mGrandMaAvater = "http://imgtu.5011.net/uploads/content/20170323/6784541490257694.jpg";
        String mGrandMaName = "维纳";
        FamilyMember mGrandMa = new FamilyMember();
        mGrandMa.setId(mGradnMaId);
        mGrandMa.setAvatar(mGrandMaAvater);
        mGrandMa.setName(mGrandMaName);
        mGrandMa.setCall(MATERNALGRANDMOTHER);

        String FATHER = "父亲";
        String fatherId = "5";
        String fatherAvatar = "http://img2.touxiang.cn/file/20170220/11f3df02d77983ac3df8706c5583c6c2.jpg";
        String fatherName = "史泰龙";
        FamilyMember father = new FamilyMember();
        father.setId(fatherId);
        father.setAvatar(fatherAvatar);
        father.setName(fatherName);
        father.setCall(FATHER);
        father.setFather(fGrandPa);
        father.setMother(fGrandMa);

        String MOTHER = "母亲";
        String motherId = "6";
        String motherAvatar = "http://img4.duitang.com/uploads/item/201409/19/20140919222955_YULYe.png";
        String motherName = "艾玛";
        FamilyMember mother = new FamilyMember();
        mother.setId(motherId);
        mother.setAvatar(motherAvatar);
        mother.setName(motherName);
        mother.setCall(MOTHER);
        mother.setFather(mGrandPa);
        mother.setMother(mGrandMa);

        String SPOUSE = "配偶";
        String spouseId = "2";
        String spouseAvatar = "http://www.fuhaodq.com/d/file/201704/14/nnx0jj4qx43jjt.jpg";
        String spouseName = "雯雅婷";
        FamilyMember spouse = new FamilyMember();
        spouse.setId(spouseId);
        spouse.setAvatar(spouseAvatar);
        spouse.setName(spouseName);
        spouse.setCall(SPOUSE);

        String SON = "儿子";
        String sonId1 = "7";
        String sonAvatar1 = "http://img1.imgtn.bdimg.com/it/u=2793651579,2512119374&fm=23&gp=0.jpg";
        String sonName1 = "泰格";
        FamilyMember son1 = new FamilyMember();
        son1.setId(sonId1);
        son1.setAvatar(sonAvatar1);
        son1.setName(sonName1);
        son1.setCall(SON);

        String sonId2 = "8";
        String sonAvatar2 = "http://img2.imgtn.bdimg.com/it/u=633501977,2103670830&fm=214&gp=0.jpg";
        String sonName2 = "泰山";
        FamilyMember son2 = new FamilyMember();
        son2.setId(sonId2);
        son2.setAvatar(sonAvatar2);
        son2.setName(sonName2);
        son2.setCall(SON);

        String DAUGHTER = "女儿";
        String daughterId1 = "9";
        String daughterAvatar1 = "http://img.duoziwang.com/2017/03/13/B2139.jpg";
        String daughterName1 = "莉莉丝";
        FamilyMember daughter1 = new FamilyMember();
        daughter1.setId(daughterId1);
        daughter1.setAvatar(daughterAvatar1);
        daughter1.setName(daughterName1);
        daughter1.setCall(DAUGHTER);

        String daughterId2 = "10";
        String daughterAvatar2 = "http://v1.qzone.cc/avatar/201405/22/14/17/537d968a54f77328.jpg%21200x200.jpg";
        String daughterName2 = "罗里垭";
        FamilyMember daughter2 = new FamilyMember();
        daughter2.setId(daughterId2);
        daughter2.setAvatar(daughterAvatar2);
        daughter2.setName(daughterName2);
        daughter2.setCall(DAUGHTER);

        String daughterId3 = "11";
        String daughterAvatar3 = "http://img3.imgtn.bdimg.com/it/u=2584920597,2650792998&fm=214&gp=0.jpg";
        String daughterName3 = "洛丽塔";
        FamilyMember daughter3 = new FamilyMember();
        daughter3.setId(daughterId3);
        daughter3.setAvatar(daughterAvatar3);
        daughter3.setName(daughterName3);
        daughter3.setCall(DAUGHTER);

        List<FamilyMember> myBrothers = new ArrayList<>();
        myBrothers.add(brother);
        myBrothers.add(sister);

        List<FamilyMember> myChildren = new ArrayList<>();
        myChildren.add(son1);
        myChildren.add(son2);
        myChildren.add(daughter1);
        myChildren.add(daughter2);
        myChildren.add(daughter3);

        mFamilyMember = new FamilyMember();
        mFamilyMember.setId("1");
        mFamilyMember.setAvatar("http://i-4.yxdown.com/2017/3/22/c2053050-9315-49d1-a598-de26c9b62738.png");
        mFamilyMember.setName("康娜");
        mFamilyMember.setCall("我");
        mFamilyMember.setSpouse(spouse);
        mFamilyMember.setBrothers(myBrothers);
        mFamilyMember.setChildren(myChildren);
        mFamilyMember.setFather(father);
        mFamilyMember.setMother(mother);

        Gson gson = new Gson();
        Logger.json(gson.toJson(mFamilyMember));
    }

    private void initView() {
        ftvTree = (FamilyTreeView) findViewById(R.id.ftv_tree);
    }

    private void setData() {
        ftvTree.setmFamilyMember(mFamilyMember);
        ftvTree.setmOnFamilySelectListener(new FamilyTreeView.OnFamilySelectListener() {
            @Override
            public void onFamilySelect(FamilyMember family) {
                ToastMaster.toast(family.getCall());
            }
        });
    }

}
