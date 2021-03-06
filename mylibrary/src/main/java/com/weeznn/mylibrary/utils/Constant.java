package com.weeznn.mylibrary.utils;

/**
 * Created by weeznn on 2018/4/9.
 */

public interface Constant {
    //打开百度语音服务时的请求码
    int REQUEST_CODE_MET = 1;
    int REQUEST_CODE_MET_EDIT = 4;
    int REQUEST_CODE_DIA = 2;
    int REQUEST_CODE_NOT = 3;

    //百度语音服务的返回码 -->
    int RESOULT_CODE_DOWN = 1;
    int RESOULT_CODE_CANCEL = 0;

    //CODE-->
    int CODE_MRT = 1;
    int CODE_DAI = 2;
    int CODE_NOT = 3;
    int CODE_PEO = 4;

    //百度NLP词性 人名    地名  机构名 时间
    String PRE="PRE";
    String LOC="LOC";
    String ORG="ORG";
    String TIME="TIME";


}
