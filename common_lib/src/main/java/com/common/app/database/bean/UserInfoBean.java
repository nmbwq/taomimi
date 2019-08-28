package com.common.app.database.bean;

import com.common.app.database.converter.StringConverter;

import org.greenrobot.greendao.annotation.Convert;
import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Property;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Unique;

import java.util.ArrayList;
import java.util.List;

/**
 * @author: zhengjr
 * @since: 2018/6/21
 * @describe:
 */
@Entity
public class UserInfoBean {

    @Id
    @Unique
    @Property
    public String phone;//手机号作为唯一的字段

    @Property
    private String photo;

    @Property
    private long createTime;

    @Property
    private String createTimeStr;

    @Property
    private String customerServiceId;

    @Property
    private String id;

    @Property
    private int isActiveStatus;

    @Property
    private String lastProductId;

    @Property
    private String openImPassword;

    @Property
    private String password;

    @Property
    private String safeToken;

    @Property
    private int signedStatus;

    @Property
    private int status;

    @Property
    private int superiorId;

    @Property
    private int verifiCode;

    @Property
    public boolean isLogin;

    @Property
    public boolean firstLoginFlag;

    @Property
    public String invitationCode;

    @Property
    private String zhifubaoNum;

    @Property
    private String wxName;

    @Property
    private String wxPhoto;

    @Property
    private String wxOpenId;

    @Property
    private boolean msgState;

    @Property
    private String msgSize;

    @Property
    private int agencyLevel;//当前用户的代理等级

    @Convert(columnType = String.class, converter = StringConverter.class)
    private List<String> imageShare;


    @Generated(hash = 2125815257)
    public UserInfoBean(String phone, String photo, long createTime,
            String createTimeStr, String customerServiceId, String id,
            int isActiveStatus, String lastProductId, String openImPassword,
            String password, String safeToken, int signedStatus, int status,
            int superiorId, int verifiCode, boolean isLogin, boolean firstLoginFlag,
            String invitationCode, String zhifubaoNum, String wxName,
            String wxPhoto, String wxOpenId, boolean msgState, String msgSize,
            int agencyLevel, List<String> imageShare) {
        this.phone = phone;
        this.photo = photo;
        this.createTime = createTime;
        this.createTimeStr = createTimeStr;
        this.customerServiceId = customerServiceId;
        this.id = id;
        this.isActiveStatus = isActiveStatus;
        this.lastProductId = lastProductId;
        this.openImPassword = openImPassword;
        this.password = password;
        this.safeToken = safeToken;
        this.signedStatus = signedStatus;
        this.status = status;
        this.superiorId = superiorId;
        this.verifiCode = verifiCode;
        this.isLogin = isLogin;
        this.firstLoginFlag = firstLoginFlag;
        this.invitationCode = invitationCode;
        this.zhifubaoNum = zhifubaoNum;
        this.wxName = wxName;
        this.wxPhoto = wxPhoto;
        this.wxOpenId = wxOpenId;
        this.msgState = msgState;
        this.msgSize = msgSize;
        this.agencyLevel = agencyLevel;
        this.imageShare = imageShare;
    }

    @Generated(hash = 1818808915)
    public UserInfoBean() {
    }


    public String getPhone() {
        return this.phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public long getCreateTime() {
        return this.createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public String getCreateTimeStr() {
        return this.createTimeStr;
    }

    public void setCreateTimeStr(String createTimeStr) {
        this.createTimeStr = createTimeStr;
    }

    public String getCustomerServiceId() {
        return this.customerServiceId;
    }

    public void setCustomerServiceId(String customerServiceId) {
        this.customerServiceId = customerServiceId;
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getIsActiveStatus() {
        return this.isActiveStatus;
    }

    public void setIsActiveStatus(int isActiveStatus) {
        this.isActiveStatus = isActiveStatus;
    }

    public String getLastProductId() {
        return this.lastProductId;
    }

    public void setLastProductId(String lastProductId) {
        this.lastProductId = lastProductId;
    }

    public String getOpenImPassword() {
        return this.openImPassword;
    }

    public void setOpenImPassword(String openImPassword) {
        this.openImPassword = openImPassword;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getSafeToken() {
        return this.safeToken;
    }

    public void setSafeToken(String safeToken) {
        this.safeToken = safeToken;
    }

    public int getSignedStatus() {
        return this.signedStatus;
    }

    public void setSignedStatus(int signedStatus) {
        this.signedStatus = signedStatus;
    }

    public int getStatus() {
        return this.status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getSuperiorId() {
        return this.superiorId;
    }

    public void setSuperiorId(int superiorId) {
        this.superiorId = superiorId;
    }

    public int getVerifiCode() {
        return this.verifiCode;
    }

    public void setVerifiCode(int verifiCode) {
        this.verifiCode = verifiCode;
    }

    public boolean getIsLogin() {
        return this.isLogin;
    }

    public void setIsLogin(boolean isLogin) {
        this.isLogin = isLogin;
    }

    public String getPhoto() {
        return this.photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public boolean getFirstLoginFlag() {
        return this.firstLoginFlag;
    }

    public void setFirstLoginFlag(boolean firstLoginFlag) {
        this.firstLoginFlag = firstLoginFlag;
    }

    public String getInvitationCode() {
        return this.invitationCode;
    }

    public void setInvitationCode(String invitationCode) {
        this.invitationCode = invitationCode;
    }

    public String getZhifubaoNum() {
        return this.zhifubaoNum;
    }

    public void setZhifubaoNum(String zhifubaoNum) {
        this.zhifubaoNum = zhifubaoNum;
    }

    public String getWxName() {
        return this.wxName;
    }

    public void setWxName(String wxName) {
        this.wxName = wxName;
    }

    public String getWxPhoto() {
        return this.wxPhoto;
    }

    public void setWxPhoto(String wxPhoto) {
        this.wxPhoto = wxPhoto;
    }

    public String getWxOpenId() {
        return this.wxOpenId;
    }

    public void setWxOpenId(String wxOpenId) {
        this.wxOpenId = wxOpenId;
    }

    public boolean getMsgState() {
        return this.msgState;
    }

    public void setMsgState(boolean msgState) {
        this.msgState = msgState;
    }

    public String getMsgSize() {
        return this.msgSize;
    }

    public void setMsgSize(String msgSize) {
        this.msgSize = msgSize;
    }

    public int getAgencyLevel() {
        return this.agencyLevel;
    }

    public void setAgencyLevel(int agencyLevel) {
        this.agencyLevel = agencyLevel;
    }

    public List<String> getImageShare() {
        return this.imageShare;
    }

    public void setImageShare(List<String> imageShare) {
        this.imageShare = imageShare;
    }

}
