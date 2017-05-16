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
        mFamilyMember = new FamilyMember();
        mFamilyMember.setMemberId("301");
        mFamilyMember.setMemberImg("http://c1.haibao.cn/img/600_0_100_0/1463119532.3054/a3a0d6272250a8481a5940a9fe7d9979.jpg");
        mFamilyMember.setMemberName("提利昂·兰尼斯特");
        mFamilyMember.setCall("我");
        mFamilyMember.setSpouse(getSpouseData());
        mFamilyMember.setBrothers(getBrothersData());
        mFamilyMember.setChildren(getChildrenData());
        mFamilyMember.setFather(getFatherData());
        mFamilyMember.setMother(getMotherData());
        mFamilyMember.setFosterFather(getFosterFatherData());
        mFamilyMember.setFosterMother(getFosterMotherData());

        Gson gson = new Gson();
        Logger.json(gson.toJson(mFamilyMember));
    }

    private FamilyMember getSpouseData() {
        String SPOUSE = "配偶";
        String spouseId = "302";
        String spouseAvatar = "http://img4.imgtn.bdimg.com/it/u=1035343743,1638639787&fm=15&gp=0.jpg";
        String spouseName = "立花瑠莉";
        FamilyMember spouse = new FamilyMember();
        spouse.setMemberId(spouseId);
        spouse.setMemberImg(spouseAvatar);
        spouse.setMemberName(spouseName);
        spouse.setCall(SPOUSE);
        return spouse;
    }

    private List<FamilyMember> getBrothersData() {
        String BIGBROTHER = "哥哥";
        String brotherId = "303";
        String brotherAvatar = "http://upload.ct.youth.cn/2016/0518/1463564738559.png";
        String brotherName = "琼恩·雪诺";
        FamilyMember brother = new FamilyMember();
        brother.setMemberId(brotherId);
        brother.setMemberImg(brotherAvatar);
        brother.setMemberName(brotherName);
        brother.setCall(BIGBROTHER);

        String LITTLESISTER = "妹妹";
        String sisterId = "304";
        String sisterAvatar = "http://a1.att.hudong.com/02/99/20300543116259143460994521443_s.jpg";
        String sisterName = "艾莉亚·史塔克";
        FamilyMember sister = new FamilyMember();
        sister.setMemberId(sisterId);
        sister.setMemberImg(sisterAvatar);
        sister.setMemberName(sisterName);
        sister.setCall(LITTLESISTER);

        List<FamilyMember> myBrothers = new ArrayList<>();
        myBrothers.add(brother);
        myBrothers.add(sister);
        return myBrothers;
    }

    private List<FamilyMember> getChildrenData() {
        String GRANDSON = "孙子";
        String grandSonId1 = "501";
        String grandSonAvatar1 = "http://i0.hdslb.com/video/8b/8b5e061a83e448c13df77e65e49fd500.jpg";
        String grandSonName1 = "刘醒";
        FamilyMember grandSon1 = new FamilyMember();
        grandSon1.setMemberId(grandSonId1);
        grandSon1.setMemberImg(grandSonAvatar1);
        grandSon1.setMemberName(grandSonName1);
        grandSon1.setCall(GRANDSON);

        String grandSonId2 = "502";
        String grandSonAvatar2 = "http://i0.hdslb.com/video/aa/aaf47ea58cf8842a5d5ed75a96e97511.jpg";
        String grandSonName2 = "非凡哥";
        FamilyMember grandSon2 = new FamilyMember();
        grandSon2.setMemberId(grandSonId2);
        grandSon2.setMemberImg(grandSonAvatar2);
        grandSon2.setMemberName(grandSonName2);
        grandSon2.setCall(GRANDSON);

        String grandSonId3 = "503";
        String grandSonAvatar3 = "http://f.hiphotos.baidu.com/zhidao/wh%3D450%2C600/sign=6ebe4667f036afc30e5937618629c7f2/32fa828ba61ea8d390ee7fa3900a304e241f584c.jpg";
        String grandSonName3 = "教皇";
        FamilyMember grandSon3 = new FamilyMember();
        grandSon3.setMemberId(grandSonId3);
        grandSon3.setMemberImg(grandSonAvatar3);
        grandSon3.setMemberName(grandSonName3);
        grandSon3.setCall(GRANDSON);

        String grandSonId4 = "504";
        String grandSonAvatar4 = "http://p4.music.126.net/859Wz1Ea9n8MI1Iu6JnPuQ==/3309529999694445.jpg?param=180y180";
        String grandSonName4 = "乌蝇哥";
        FamilyMember grandSon4 = new FamilyMember();
        grandSon4.setMemberId(grandSonId4);
        grandSon4.setMemberImg(grandSonAvatar4);
        grandSon4.setMemberName(grandSonName4);
        grandSon4.setCall(GRANDSON);

        String grandSonId5 = "505";
        String grandSonAvatar5 = "http://img0.d17.cc/file/upload/201406/23/17-27-11-36-178338.jpg";
        String grandSonName5 = "金馆长";
        FamilyMember grandSon5 = new FamilyMember();
        grandSon5.setMemberId(grandSonId5);
        grandSon5.setMemberImg(grandSonAvatar5);
        grandSon5.setMemberName(grandSonName5);
        grandSon5.setCall(GRANDSON);

        String grandSonId6 = "506";
        String grandSonAvatar6 = "http://img.qq1234.org/uploads/allimg/141231/1443201111-17.png";
        String grandSonName6 = "梁逸峰";
        FamilyMember grandSon6 = new FamilyMember();
        grandSon6.setMemberId(grandSonId6);
        grandSon6.setMemberImg(grandSonAvatar6);
        grandSon6.setMemberName(grandSonName6);
        grandSon6.setCall(GRANDSON);

        String grandSonId7 = "507";
        String grandSonAvatar7 = "http://p3.music.126.net/rYSoep1ptPUOPiObrRFq3w==/528865155258775.jpg";
        String grandSonName7 = "van样";
        FamilyMember grandSon7 = new FamilyMember();
        grandSon7.setMemberId(grandSonId7);
        grandSon7.setMemberImg(grandSonAvatar7);
        grandSon7.setMemberName(grandSonName7);
        grandSon7.setCall(GRANDSON);

        String GRANDDAUGHTER = "孙女";
        String grandDaughterId1 = "508";
        String grandDaughterAvatar1 = "http://img.mp.itc.cn/upload/20161014/f2dd4e791a7b4d35b5bf087b3bcb2713_th.jpeg";
        String grandDaughterName1 = "劳拉·金尼";
        FamilyMember grandDaughter1 = new FamilyMember();
        grandDaughter1.setMemberId(grandDaughterId1);
        grandDaughter1.setMemberImg(grandDaughterAvatar1);
        grandDaughter1.setMemberName(grandDaughterName1);
        grandDaughter1.setCall(GRANDDAUGHTER);

        String grandDaughterId2 = "509";
        String grandDaughterAvatar2 = "http://image.ytcutv.com/material/news/img/2016/04/d9c302b38eea3ec7082235e15ad30aad.jpg";
        String grandDaughterName2 = "福原爱";
        FamilyMember grandDaughter2 = new FamilyMember();
        grandDaughter2.setMemberId(grandDaughterId2);
        grandDaughter2.setMemberImg(grandDaughterAvatar2);
        grandDaughter2.setMemberName(grandDaughterName2);
        grandDaughter2.setCall(GRANDDAUGHTER);

        List<FamilyMember> children1 = new ArrayList<>();
        children1.add(grandSon1);
        children1.add(grandSon2);
        String SON = "儿子";
        String sonId1 = "401";
        String sonAvatar1 = "http://img3.imgtn.bdimg.com/it/u=4053942608,2450134601&fm=23&gp=0.jpg";
        String sonName1 = "泰格";
        FamilyMember son1 = new FamilyMember();
        son1.setMemberId(sonId1);
        son1.setMemberImg(sonAvatar1);
        son1.setMemberName(sonName1);
        son1.setCall(SON);
        son1.setChildren(children1);

        String sonId2 = "402";
        String sonAvatar2 = "http://img03.muzhiwan.com/2014/09/25/com.legacygames.tarzanunleashed_104554/542420bddea91.png";
        String sonName2 = "泰山";
        FamilyMember son2 = new FamilyMember();
        son2.setMemberId(sonId2);
        son2.setMemberImg(sonAvatar2);
        son2.setMemberName(sonName2);
        son2.setCall(SON);

        List<FamilyMember> children3 = new ArrayList<>();
        children3.add(grandDaughter1);
        children3.add(grandDaughter2);
        children3.add(grandSon3);
        String DAUGHTER = "女儿";
        String daughterId1 = "403";
        String daughterAvatar1 = "http://img0.imgtn.bdimg.com/it/u=1746062772,4131395608&fm=23&gp=0.jpg";
        String daughterName1 = "莉莉丝";
        FamilyMember daughter1 = new FamilyMember();
        daughter1.setMemberId(daughterId1);
        daughter1.setMemberImg(daughterAvatar1);
        daughter1.setMemberName(daughterName1);
        daughter1.setCall(DAUGHTER);
        daughter1.setChildren(children3);

        List<FamilyMember> children4 = new ArrayList<>();
        children4.add(grandSon4);
        children4.add(grandSon5);
        children4.add(grandSon6);
        String daughterId2 = "404";
        String daughterAvatar2 = "http://v1.qzone.cc/avatar/201405/22/14/17/537d968a54f77328.jpg%21200x200.jpg";
        String daughterName2 = "罗里垭";
        FamilyMember daughter2 = new FamilyMember();
        daughter2.setMemberId(daughterId2);
        daughter2.setMemberImg(daughterAvatar2);
        daughter2.setMemberName(daughterName2);
        daughter2.setCall(DAUGHTER);
        daughter2.setChildren(children4);

        List<FamilyMember> children5 = new ArrayList<>();
        children5.add(grandSon7);
        String daughterId3 = "405";
        String daughterAvatar3 = "http://pic.baike.soso.com/p/20131221/bki-20131221095418-1474375397.jpg";
        String daughterName3 = "洛丽塔";
        FamilyMember daughter3 = new FamilyMember();
        daughter3.setMemberId(daughterId3);
        daughter3.setMemberImg(daughterAvatar3);
        daughter3.setMemberName(daughterName3);
        daughter3.setCall(DAUGHTER);
        daughter3.setChildren(children5);

        List<FamilyMember> myChildren = new ArrayList<>();
        myChildren.add(son1);
        myChildren.add(son2);
        myChildren.add(daughter1);
        myChildren.add(daughter2);
        myChildren.add(daughter3);

        return myChildren;
    }


    private FamilyMember getFatherData() {
        String PATERNALGRANDFATHER = "爷爷";
        String fGrandPaId = "101";
        String fGrandPaAvater = "http://img1.cache.netease.com/catchpic/C/CB/CBA11F1580A1BE8B174B36E7F810CE1F.jpg";
        String fGrandPaName = "强森";
        FamilyMember fGrandPa = new FamilyMember();
        fGrandPa.setMemberId(fGrandPaId);
        fGrandPa.setMemberImg(fGrandPaAvater);
        fGrandPa.setMemberName(fGrandPaName);
        fGrandPa.setCall(PATERNALGRANDFATHER);

        String PATERNALGRANDMOTHER = "奶奶";
        String fGrandMaId = "102";
        String fGrandMaAvater = "http://img3.duitang.com/uploads/item/201606/19/20160619163933_EfKMX.png";
        String fGrandMaName = "丹尼莉丝·坦格利安";
        FamilyMember fGrandMa = new FamilyMember();
        fGrandMa.setMemberId(fGrandMaId);
        fGrandMa.setMemberImg(fGrandMaAvater);
        fGrandMa.setMemberName(fGrandMaName);
        fGrandMa.setCall(PATERNALGRANDMOTHER);

        String FATHER = "父亲";
        String fatherId = "201";
        String fatherAvatar = "http://g4.hexunimg.cn/2014-09-02/168113626.jpg";
        String fatherName = "史泰龙";
        FamilyMember father = new FamilyMember();
        father.setMemberId(fatherId);
        father.setMemberImg(fatherAvatar);
        father.setMemberName(fatherName);
        father.setCall(FATHER);
        father.setFather(fGrandPa);
        father.setMother(fGrandMa);
        return father;
    }

    private FamilyMember getMotherData() {
        String MATERNALGRANDFATHER = "外公";
        String mGrandPaId = "103";
        String mGrandPaAvater = "http://a0.att.hudong.com/22/20/01300542735711144075202548878_s.jpg";
        String mGrandPaName = "詹姆·兰尼斯特";
        FamilyMember mGrandPa = new FamilyMember();
        mGrandPa.setMemberId(mGrandPaId);
        mGrandPa.setMemberImg(mGrandPaAvater);
        mGrandPa.setMemberName(mGrandPaName);
        mGrandPa.setCall(MATERNALGRANDFATHER);

        String MATERNALGRANDMOTHER = "外婆";
        String mGradnMaId = "104";
        String mGrandMaAvater = "http://img003.21cnimg.com/photos/album/20141211/m600/7C0FDC909057A2795A2781CE0AEB2DAC.jpeg";
        String mGrandMaName = "珊莎·史塔克";
        FamilyMember mGrandMa = new FamilyMember();
        mGrandMa.setMemberId(mGradnMaId);
        mGrandMa.setMemberImg(mGrandMaAvater);
        mGrandMa.setMemberName(mGrandMaName);
        mGrandMa.setCall(MATERNALGRANDMOTHER);

        String MOTHER = "母亲";
        String motherId = "202";
        String motherAvatar = "http://pic.baike.soso.com/p/20130717/20130717143103-575469240.jpg";
        String motherName = "艾玛";
        FamilyMember mother = new FamilyMember();
        mother.setMemberId(motherId);
        mother.setMemberImg(motherAvatar);
        mother.setMemberName(motherName);
        mother.setCall(MOTHER);
        mother.setFather(mGrandPa);
        mother.setMother(mGrandMa);
        return mother;
    }

    private FamilyMember getFosterFatherData() {
        String FOSTERFATHER = "养父";
        String fosterFatherId = "203";
        String fosterFatherAvater = "http://i2.sinaimg.cn/ent/2011/0617/U4099P28DT20110617142147.jpg";
        String fosterFatherName = "艾德·史塔克";
        FamilyMember fosterFather = new FamilyMember();
        fosterFather.setMemberId(fosterFatherId);
        fosterFather.setMemberImg(fosterFatherAvater);
        fosterFather.setMemberName(fosterFatherName);
        fosterFather.setCall(FOSTERFATHER);
        return fosterFather;
    }

    private FamilyMember getFosterMotherData() {
        String FOSTERMOTHER = "养母";
        String fosterMotherId = "204";
        String fosterMotherAvater = "http://a4.att.hudong.com/71/34/01300542899749141759349066580.jpg";
        String fosterMotherName = "凯瑟琳·徒利·史塔克";
        FamilyMember fosterMother = new FamilyMember();
        fosterMother.setMemberId(fosterMotherId);
        fosterMother.setMemberImg(fosterMotherAvater);
        fosterMother.setMemberName(fosterMotherName);
        fosterMother.setCall(FOSTERMOTHER);
        return fosterMother;
    }

    private void initView() {
        ftvTree = (FamilyTreeView) findViewById(R.id.ftv_tree);
    }

    private void setData() {
        ftvTree.setFamilyMember(mFamilyMember);
        ftvTree.setmOnFamilySelectListener(new FamilyTreeView.OnFamilySelectListener() {
            @Override
            public void onFamilySelect(FamilyMember family) {
                ToastMaster.toast(family.getCall());
            }
        });
    }

}
