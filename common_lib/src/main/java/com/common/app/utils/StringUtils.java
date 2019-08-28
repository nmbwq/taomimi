package com.common.app.utils;

import android.text.TextUtils;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 字符串操作工具包
 *
 * @author liux (http://my.oschina.net/liux)
 * @version 1.0
 * @created 2012-3-21
 */
public class StringUtils {
    /**
     * 数字转字符串去掉小数点后面的0
     *
     * @param value
     * @return
     */
    public static String intToStringDeleteZero(int value) {
        String val = String.valueOf(value);
        return stringToStringDeleteZero(val);
    }

    /**
     * 数字转字符串去掉小数点后面的0
     *
     * @param value
     * @return
     */
    public static String doubleToStringDeleteZero(double value) {
        String val = String.valueOf(value);
        return stringToStringDeleteZero(val);
    }

    /**
     * 字符串转字符串去掉小数点后面的0
     *
     * @param value
     * @return
     */
    public static String stringToStringDeleteZero(String value) {
        String val = String.valueOf(value).trim();
        if (TextUtils.isEmpty(val)) {
            return "";
        }
        if (val.indexOf(".") > 0) {
            val = val.replaceAll("0+?$", "");//去掉多余的0
            val = val.replaceAll("[.]$", "");//如最后一位是.则去掉
        }
        if (TextUtils.isEmpty(val) || TextUtils.equals(val, "null")) {
            return "";
        }
        return val;
    }

    /**
     * string 转换万  个   单位表示
     *
     * @param value
     * @return
     */
    public static String stringToStringUnit(String value) {
        return intToStringUnit(Integer.valueOf(value));
    }

    /**
     * int 转换万  个   单位表示
     *
     * @param value
     * @return
     */
    public static String intToStringUnit(int value) {
        String s = String.valueOf(value);
        String sVal = s.contains(".") ? s.split("\\.")[0] : s;

        value = Integer.valueOf(sVal);
        if (value >= 10000 && value < 100000000) {
            String val = String.valueOf(value);

            String s1 = val.substring(0, val.length() - 4) + "." + val.substring(val.length() - 4, val.length() - 2);
            String s2 = new BigDecimal(s1).setScale(1, BigDecimal.ROUND_HALF_UP).toString();
            return stringToStringDeleteZero(s2) + "万";
        } else if (value >= 100000000) {
            String val = String.valueOf(value);
            String s1 = val.substring(0, val.length() - 8) + val.substring(val.length() - 8, val.length() - 7);
            String s2 = new BigDecimal(s1).setScale(1, BigDecimal.ROUND_HALF_UP).toString();
            return stringToStringDeleteZero(s2) + "亿";
        }
        return value + "";
    }

    public static boolean isTbaoOrTianMaoRex(String url) {
        String rex = "http[s]?://.*?(?:\\.((?:taobao)|(?:tmall)|(?:liangxinyao)|(?:95095)))\\.";
        boolean b = url.matches(rex);
        return b;
    }

    public static boolean isProductInfoRex(String url) {
        String rex = "http[s]?://.*?(?:\\.(?:(?:taobao)|(?:tmall)|(?:liangxinyao)|(?:95095)))\\..*?(?:(?:[?|&]id=(\\d*)(?!.*?[\\?|&]item_id=))|(?:[?|&]item_id=(\\d*))).*";
        boolean b = url.matches(rex);
        return b;
    }

    public static String getProductId(String url) {
        String rex = "(?:&|\\?)(?:(?:itemId)|(?:id))=(\\d*)";
        Pattern pattern = Pattern.compile(rex);
        Matcher m = pattern.matcher(url);
        String id = "";
        while (m.find()) {
            id = m.group(1);
        }
        return id;
    }

    /**
     * 判断是否是淘口令还是连接，1是淘口令，2是连接，0是其他
     *
     * @param content
     * @return
     */
    public static String zhiNengSouType(String content) {
        String type = "0";
        if (TextUtils.isEmpty(content)) {
            return type;
        }
        if (!TextUtils.isEmpty(getTKL(content))) {
            return type = "1";
        } else if (!TextUtils.isEmpty(getURL(content))) {
            return type = "2";
        }
        return type;
    }

    /**
     * 获取内容的淘口令
     *
     * @param content
     * @return
     */
    public static String getTKL(String content) {
        String rex = "(?:[￥\\(《\\\\ud83d\\\\udd11\\\\ud83d\\\\udddd\\\\ud83d\\\\ude3a\\\\ud83d\\\\ude38\\\\ud83c\\\\udf81\\\\ud83d\\\\udcf2\\\\ud83d\\\\udcb0\\\\ud83d\\\\udcb2\\\\u2714\\\\ud83c\\\\udfb5\\\\ud83d\\\\udd10]([A-Za-z0-9]{7,15})[￥\\)《\\\\ud83d\\\\udd11\\\\ud83d\\\\udddd\\\\ud83d\\\\ude3a\\\\ud83d\\\\ude38\\\\ud83c\\\\udf81\\\\ud83d\\\\udcf2\\\\ud83d\\\\udcb0\\\\ud83d\\\\udcb2\\\\u2714\\\\ud83c\\\\udfb5\\\\ud83d\\\\udd10])";
        Pattern pattern = Pattern.compile(rex);
        Matcher m = pattern.matcher(content);
        String tkl = "";
        while (m.find()) {
            tkl = m.group(1);
        }

        return tkl;
    }

    /**
     * 获取内容的url地址
     *
     * @param content
     * @return
     */
    public static String getURL(String content) {
        String rex = "http[s]?://.*?(?:\\.((?:taobao)|(?:tmall)|(?:liangxinyao)|(?:95095)))\\.";
        Pattern pattern = Pattern.compile(rex);
        Matcher m = pattern.matcher(content);
        String url = "";
        while (m.find()) {
            url = m.group(1);
        }
        return url;
    }

    /*private final static Pattern taobao = Pattern.compile("http[s]?://.*?(?:\\\\.((taobao)|(?:tmall)|(?:liangxinyao)|(?:95095)))\\\\.");
    private final static Pattern productDetails = Pattern.compile("http[s]?://.*?(?:\\\\.(?:(?:taobao)|(?:tmall)|(?:liangxinyao)|(?:95095)))\\\\..*?(?:(?:[\\?|&]id=(\\\\d*)(?!.*?[\\?|&]item_id=))|(?:[\\?|&]item_id=(\\\\d*)))");
    private final static Pattern emailer = Pattern
            .compile("\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*");

    private final static Pattern IMG_URL = Pattern
            .compile(".*?(gif|jpeg|png|jpg|bmp)");

    private final static Pattern URL = Pattern
            .compile("^(https|http)://.*?$(net|com|.com.cn|org|me|)");

    private final static Pattern money = Pattern.compile("^(([1-9]{1}\\d*)|([0]{1}))(\\.(\\d){0,2})?$");

    private final static Pattern PHONENUM = Pattern.compile("1\\d{10}");
    private final static Pattern CARNO = Pattern.compile("[a-zA-Z0-9]{4}");

    private final static Pattern PASSWORD = Pattern.compile("((?=.*\\d)(?=.*[a-zA-Z]).{6,16})");

    private final static ThreadLocal<SimpleDateFormat> dateFormater = new ThreadLocal<SimpleDateFormat>() {
        @Override
        protected SimpleDateFormat initialValue() {
            return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        }
    };

    private final static ThreadLocal<SimpleDateFormat> dateFormater2 = new ThreadLocal<SimpleDateFormat>() {
        @Override
        protected SimpleDateFormat initialValue() {
            return new SimpleDateFormat("yyyy-MM-dd");
        }
    };

    private final static ThreadLocal<SimpleDateFormat> dateFormat3 = new ThreadLocal<SimpleDateFormat>() {
        @Override
        protected SimpleDateFormat initialValue() {
            return new SimpleDateFormat("yyyy-MM-dd HH:mm");
        }
    };

    *//**
     * 将字符串转位日期类型
     *
     * @param sdate
     * @return
     *//*
    public static Date toDate(String sdate) {
        return toDate(sdate, dateFormater.get());
    }

    public static Date toDate(String sdate, SimpleDateFormat dateFormater) {
        try {
            return dateFormater.parse(sdate);
        } catch (Exception e) {
            return null;
        }
    }

    public static String getDateString(Date date) {
        return dateFormater.get().format(date);
    }

    public static String getDateString(String sdate) {
        return dateFormat3.get().format(toDate(sdate));
    }

    public static String getDateStrForyyyy_MM_dd(long time) {
        return dateFormater2.get().format(time);
    }

    *//**
     * 以友好的方式显示时间
     *
     * @param sdate
     * @return
     *//*
    *//*public static String friendly_time(String sdate) {
        Date time = null;

        if (TimeZoneUtil.isInEasternEightZones())
            time = toDate(sdate);
        else
            time = TimeZoneUtil.transformTime(toDate(sdate),
                    TimeZone.getTimeZone("GMT+08"), TimeZone.getDefault());

        if (time == null) {
            return "Unknown";
        }
        String ftime = "";
        Calendar cal = Calendar.getInstance();

        // 判断是否是同一天
        String curDate = dateFormater2.get().format(cal.getTime());
        String paramDate = dateFormater2.get().format(time);
        if (curDate.equals(paramDate)) {
            int hour = (int) ((cal.getTimeInMillis() - time.getTime()) / 3600000);
            if (hour == 0)
                ftime = Math.max(
                        (cal.getTimeInMillis() - time.getTime()) / 60000, 1)
                        + "分钟前";
            else
                ftime = hour + "小时前";
            return ftime;
        }

        long lt = time.getTime() / 86400000;
        long ct = cal.getTimeInMillis() / 86400000;
        int days = (int) (ct - lt);
        if (days == 0) {
            int hour = (int) ((cal.getTimeInMillis() - time.getTime()) / 3600000);
            if (hour == 0)
                ftime = Math.max(
                        (cal.getTimeInMillis() - time.getTime()) / 60000, 1)
                        + "分钟前";
            else
                ftime = hour + "小时前";
        } else if (days == 1) {
            ftime = "昨天";
        } else if (days == 2) {
            ftime = "前天 ";
        } else if (days > 2 && days < 31) {
            ftime = days + "天前";
        } else if (days >= 31 && days <= 2 * 31) {
            ftime = "一个月前";
        } else if (days > 2 * 31 && days <= 3 * 31) {
            ftime = "2个月前";
        } else if (days > 3 * 31 && days <= 4 * 31) {
            ftime = "3个月前";
        } else {
            ftime = dateFormater2.get().format(time);
        }
        return ftime;
    }*//*

    *//**
     * 检测是否有emoji表情
     *
     * @param source
     * @return
     *//*
    public static boolean containsEmoji(String source) {
        int len = source.length();
        for (int i = 0; i < len; i++) {
            char codePoint = source.charAt(i);
            if (!isEmojiCharacter(codePoint)) { // 如果不能匹配,则该字符是Emoji表情
                return true;
            }
        }
        return false;
    }

    *//**
     * 判断是否是Emoji
     *
     * @param codePoint 比较的单个字符
     * @return
     *//*
    private static boolean isEmojiCharacter(char codePoint) {
        return (codePoint == 0x0) || (codePoint == 0x9) || (codePoint == 0xA)
                || (codePoint == 0xD)
                || ((codePoint >= 0x20) && (codePoint <= 0xD7FF))
                || ((codePoint >= 0xE000) && (codePoint <= 0xFFFD))
                || ((codePoint >= 0x10000) && (codePoint <= 0x10FFFF));
    }

    *//**
     * @param str       字符串只能为两位小数或者整数
     * @param isDecimal 是否是小数
     * @Description 格式化字符串，每三位用逗号隔开
     *//*
    public static String addComma(String str, boolean isDecimal) {
        //先将字符串颠倒顺序
        str = new StringBuilder(str).reverse().toString();
        if (str.equals("0")) {
            return str;
        }
        String str2 = "";
        for (int i = 0; i < str.length(); i++) {
            if (i * 3 + 3 > str.length()) {
                str2 += str.substring(i * 3, str.length());
                break;
            }
            str2 += str.substring(i * 3, i * 3 + 3) + ",";
        }
        if (str2.endsWith(",")) {
            str2 = str2.substring(0, str2.length() - 1);
        }
        //最后再将顺序反转过来
        String temp = new StringBuilder(str2).reverse().toString();
        if (isDecimal) {
            //去掉最后的","
            return temp.substring(0, temp.lastIndexOf(",")) + temp.substring(temp.lastIndexOf(",") + 1, temp.length());
        } else {
            return temp;
        }
    }

    public static String friendly_time2(String sdate) {
        String res = "";
        if (isEmpty(sdate))
            return "";

        String[] weekDays = {"星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六"};
        String currentData = StringUtils.getDataTime("MM-dd");
        int currentDay = toInt(currentData.substring(3));
        int currentMoth = toInt(currentData.substring(0, 2));

        int sMoth = toInt(sdate.substring(5, 7));
        int sDay = toInt(sdate.substring(8, 10));
        int sYear = toInt(sdate.substring(0, 4));
        Date dt = new Date(sYear, sMoth - 1, sDay - 1);

        if (sDay == currentDay && sMoth == currentMoth) {
            res = "今天 / " + weekDays[getWeekOfDate(new Date())];
        } else if (sDay == currentDay + 1 && sMoth == currentMoth) {
            res = "昨天 / " + weekDays[(getWeekOfDate(new Date()) + 6) % 7];
        } else {
            if (sMoth < 10) {
                res = "0";
            }
            res += sMoth + "/";
            if (sDay < 10) {
                res += "0";
            }
            res += sDay + " / " + weekDays[getWeekOfDate(dt)];
        }

        return res;
    }


    *//**
     * 智能格式化
     *//*
    public static String friendly_time3(String sdate) {
        String res = "";
        if (isEmpty(sdate))
            return "";

        Date date = StringUtils.toDate(sdate);
        if (date == null)
            return sdate;

        SimpleDateFormat format = dateFormater2.get();

        if (isToday(date.getTime())) {
            format.applyPattern(isMorning(date.getTime()) ? "上午 hh:mm" : "下午 hh:mm");
            res = format.format(date);
        } else if (isYesterday(date.getTime())) {
            format.applyPattern(isMorning(date.getTime()) ? "昨天 上午 hh:mm" : "昨天 下午 hh:mm");
            res = format.format(date);
        } else if (isCurrentYear(date.getTime())) {
            format.applyPattern(isMorning(date.getTime()) ? "MM-dd 上午 hh:mm" : "MM-dd 下午 hh:mm");
            res = format.format(date);
        } else {
            format.applyPattern(isMorning(date.getTime()) ? "yyyy-MM-dd 上午 hh:mm" : "yyyy-MM-dd 下午 hh:mm");
            res = format.format(date);
        }
        return res;
    }

    *//**
     * @return 判断一个时间是不是上午
     *//*
    public static boolean isMorning(long when) {
        android.text.format.Time time = new android.text.format.Time();
        time.set(when);

        int hour = time.hour;
        return (hour >= 0) && (hour < 12);
    }

    *//**
     * @return 判断一个时间是不是今天
     *//*
    public static boolean isToday(long when) {
        android.text.format.Time time = new android.text.format.Time();
        time.set(when);

        int thenYear = time.year;
        int thenMonth = time.month;
        int thenMonthDay = time.monthDay;

        time.set(System.currentTimeMillis());
        return (thenYear == time.year)
                && (thenMonth == time.month)
                && (thenMonthDay == time.monthDay);
    }

    *//**
     * @return 判断一个时间是不是昨天
     *//*
    public static boolean isYesterday(long when) {
        android.text.format.Time time = new android.text.format.Time();
        time.set(when);

        int thenYear = time.year;
        int thenMonth = time.month;
        int thenMonthDay = time.monthDay;

        time.set(System.currentTimeMillis());
        return (thenYear == time.year)
                && (thenMonth == time.month)
                && (time.monthDay - thenMonthDay == 1);
    }

    *//**
     * @return 判断一个时间是不是明天
     *//*
    public static boolean isTomorrow(long when) {
        android.text.format.Time time = new android.text.format.Time();
        time.set(when);

        int thenYear = time.year;
        int thenMonth = time.month;
        int thenMonthDay = time.monthDay;

        time.set(System.currentTimeMillis());
        return (thenYear == time.year)
                && (thenMonth == time.month)
                && (time.monthDay - thenMonthDay == -1);
    }


    *//**
     * @return 判断一个时间是不是今年
     *//*
    public static boolean isCurrentYear(long when) {
        android.text.format.Time time = new android.text.format.Time();
        time.set(when);

        int thenYear = time.year;

        time.set(System.currentTimeMillis());
        return (thenYear == time.year);
    }

    *//**
     * 获取当前日期是星期几<br>
     *
     * @param dt
     * @return 当前日期是星期几
     *//*
    public static int getWeekOfDate(Date dt) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(dt);
        int w = cal.get(Calendar.DAY_OF_WEEK) - 1;
        if (w < 0)
            w = 0;
        return w;
    }

    *//**
     * 判断给定字符串时间是否为今日
     *
     * @param sdate
     * @return boolean
     *//*
    public static boolean isToday(String sdate) {
        boolean b = false;
        Date time = toDate(sdate);
        Date today = new Date();
        if (time != null) {
            String nowDate = dateFormater2.get().format(today);
            String timeDate = dateFormater2.get().format(time);
            if (nowDate.equals(timeDate)) {
                b = true;
            }
        }
        return b;
    }

    *//**
     * 是否是相同的一天
     *
     * @param sDate1 sDate1
     * @param sDate2 sDate2
     * @return
     *//*
    public static boolean isSameDay(String sDate1, String sDate2) {
        if (TextUtils.isEmpty(sDate1) || TextUtils.isEmpty(sDate2)) {
            return false;
        }
        boolean b = false;
        Date date1 = toDate(sDate1);
        Date date2 = toDate(sDate2);
        if (date1 != null && date2 != null) {
            String d1 = dateFormater2.get().format(date1);
            String d2 = dateFormater2.get().format(date2);
            if (d1.equals(d2)) {
                b = true;
            }
        }
        return b;
    }

    *//**
     * 返回long类型的今天的日期
     *
     * @return
     *//*
    public static long getToday() {
        Calendar cal = Calendar.getInstance();
        String curDate = dateFormater2.get().format(cal.getTime());
        curDate = curDate.replace("-", "");
        return Long.parseLong(curDate);
    }

    public static String getCurTimeStr() {
        Calendar cal = Calendar.getInstance();
        String curDate = dateFormater.get().format(cal.getTime());
        return curDate;
    }

    public static String getTimeStr(long time) {
        return dateFormater.get().format(new Date(time));
    }

    *//***
     * 计算两个时间差，返回的是的秒s
     *
     * @param dete1
     * @param date2
     * @return
     * @author 火蚁 2015-2-9 下午4:50:06
     *//*
    public static long calDateDifferent(String dete1, String date2) {

        long diff = 0;

        Date d1 = null;
        Date d2 = null;

        try {
            d1 = dateFormater.get().parse(dete1);
            d2 = dateFormater.get().parse(date2);

            // 毫秒ms
            diff = d2.getTime() - d1.getTime();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return diff / 1000;
    }

    *//**
     * 判断给定字符串是否空白串。 空白串是指由空格、制表符、回车符、换行符组成的字符串 若输入字符串为null或空字符串，返回true
     *
     * @param input
     * @return boolean
     *//*
    public static boolean isEmpty(String input) {
        if (input == null || "".equals(input))
            return true;

        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);
            if (c != ' ' && c != '\t' && c != '\r' && c != '\n') {
                return false;
            }
        }
        return true;
    }

    *//**
     * 判断是不是一个合法的电子邮件地址
     *
     * @param email
     * @return
     *//*
    public static boolean isEmail(String email) {
        if (email == null || email.trim().length() == 0)
            return false;
        return emailer.matcher(email).matches();
    }

    *//**
     * 判断手机号是不是一个合法的手机号
     *
     * @param phoneNum
     * @return
     *//*
    public static boolean isPhoneNum(String phoneNum) {
        if (phoneNum == null || phoneNum.trim().length() == 0) {
            return false;
        }
        return PHONENUM.matcher(phoneNum).matches();
    }

    public static boolean isMoneyNum(String moneyNum) {
        if (TextUtils.isEmpty(moneyNum)) {
            return false;
        }
        return money.matcher(moneyNum).matches();
    }

    *//**
     * 车牌后四位正则验证
     *//*
    public static boolean isCarNo(String carNo) {
        if (carNo == null || carNo.trim().length() != 4) {
            return false;
        }
        return CARNO.matcher(carNo).matches();
    }

    *//**
     * 判断密码是不是一个合法的密码
     *
     * @param password
     * @return
     *//*
    public static boolean isPassword(String password) {
        if (password == null || password.trim().length() == 0)
            return false;
        return PASSWORD.matcher(password).matches();
    }

    public static boolean isTaobaoOrTmall(String url) {
        return url.contains("taobao") || url.contains("tmall") || url.contains("liangxinyao") || url.contains("95095");
    }

    public static boolean isDetails(String url) {
        return (url.contains("taobao") || url.contains("tmall") || url.contains("liangxinyao") || url.contains("95095")) && (Pattern.matches(".*[?|&]item_id=.*", url) || Pattern.matches(".*[?|&]id=.*", url));
    }

    public static boolean isTaobaoOrTmallRex(String url) {
        String rex = "http[s]?://.*?(?:\\.((?:taobao)|(?:tmall)|(?:liangxinyao)|(?:95095)))\\.";
        Pattern r = Pattern.compile(rex);
        Matcher m = r.matcher(url);
        return m.find();
    }

    public static boolean isDetailsRex(String url) {
        String rex = "http[s]?://.*?(?:\\.(?:(?:taobao)|(?:tmall)|(?:liangxinyao)|(?:95095)))\\..*?(?:(?:[?|&]id=(\\d*)(?!.*?[?|&]item_id=))|(?:[?|&]item_id=(\\d*)))";
        Pattern r = Pattern.compile(rex);
        Matcher m = r.matcher(url);
        return m.find();
    }

    public static String getUrlPosition(String url, String position) {
        String parm = URLRequest(url).get(position);
        return parm == null ? "" : parm;
    }

    *//**
     * 解析出url参数中的键值对
     * 如 "index.jsp?Action=del&id=123"，解析出Action:del,id:123存入map中
     *
     * @param URL url地址
     * @return url请求参数部分
     *//*
    public static Map<String, String> URLRequest(String URL) {
        Map<String, String> mapRequest = new HashMap<String, String>();

        String[] arrSplit = null;

        String strUrlParam = TruncateUrlPage(URL);
        if (strUrlParam == null) {
            return mapRequest;
        }
        //每个键值为一组 www.2cto.com
        arrSplit = strUrlParam.split("[&]");
        for (String strSplit : arrSplit) {
            String[] arrSplitEqual = null;
            arrSplitEqual = strSplit.split("[=]");

            //解析出键值
            if (arrSplitEqual.length > 1) {
                //正确解析
                mapRequest.put(arrSplitEqual[0], arrSplitEqual[1]);

            } else {
                if (arrSplitEqual[0] != "") {
                    //只有参数没有值，不加入
                    mapRequest.put(arrSplitEqual[0], "");
                }
            }
        }
        return mapRequest;
    }

    *//**
     * 去掉url中的路径，留下请求参数部分
     *
     * @param strURL url地址
     * @return url请求参数部分
     *//*
    private static String TruncateUrlPage(String strURL) {
        String strAllParam = null;
        String[] arrSplit = null;

        strURL = strURL.trim();

        arrSplit = strURL.split("[?]");
        if (strURL.length() > 1) {
            if (arrSplit.length > 1) {
                if (arrSplit[1] != null) {
                    strAllParam = arrSplit[1];
                }
            }
        }

        return strAllParam;
    }


    *//**
     * 判断一个url是否为图片url
     *
     * @param url
     * @return
     *//*
    public static boolean isImgUrl(String url) {
        if (url == null || url.trim().length() == 0)
            return false;
        return IMG_URL.matcher(url).matches();
    }

    *//**
     * 判断是否为一个合法的url地址
     *
     * @param str
     * @return
     *//*
    public static boolean isUrl(String str) {
        if (str == null || str.trim().length() == 0)
            return false;
        return URL.matcher(str).matches();
    }

    *//**
     * 字符串转整数
     *
     * @param str
     * @param defValue
     * @return
     *//*
    public static int toInt(String str, int defValue) {
        try {
            return Integer.parseInt(str);
        } catch (Exception e) {
        }
        return defValue;
    }

    *//**
     * 对象转整数
     *
     * @param obj
     * @return 转换异常返回 0
     *//*
    public static int toInt(Object obj) {
        if (obj == null)
            return 0;
        return toInt(obj.toString(), 0);
    }

    *//**
     * 对象转整数
     *
     * @param obj
     * @return 转换异常返回 0
     *//*
    public static long toLong(String obj) {
        try {
            return Long.parseLong(obj);
        } catch (Exception e) {
        }
        return 0;
    }

    *//**
     * 字符串转布尔值
     *
     * @param b
     * @return 转换异常返回 false
     *//*
    public static boolean toBool(String b) {
        try {
            return Boolean.parseBoolean(b);
        } catch (Exception e) {
        }
        return false;
    }

    public static String getString(String s) {
        return s == null ? "" : s;
    }

    *//**
     * 将一个InputStream流转换成字符串
     *
     * @param is
     * @return
     *//*
    public static String toConvertString(InputStream is) {
        StringBuffer res = new StringBuffer();
        InputStreamReader isr = new InputStreamReader(is);
        BufferedReader read = new BufferedReader(isr);
        try {
            String line;
            line = read.readLine();
            while (line != null) {
                res.append(line + "<br>");
                line = read.readLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (null != isr) {
                    isr.close();
                    isr.close();
                }
                if (null != read) {
                    read.close();
                    read = null;
                }
                if (null != is) {
                    is.close();
                    is = null;
                }
            } catch (IOException e) {
            }
        }
        return res.toString();
    }

    *//***
     * 截取字符串
     *
     * @param start 从那里开始，0算起
     * @param num   截取多少个
     * @param str   截取的字符串
     * @return
     *//*
    public static String getSubString(int start, int num, String str) {
        if (str == null) {
            return "";
        }
        int leng = str.length();
        if (start < 0) {
            start = 0;
        }
        if (start > leng) {
            start = leng;
        }
        if (num < 0) {
            num = 1;
        }
        int end = start + num;
        if (end > leng) {
            end = leng;
        }
        return str.substring(start, end);
    }

    *//**
     * 获取当前时间为每年第几周
     *
     * @return
     *//*
    public static int getWeekOfYear() {
        return getWeekOfYear(new Date());
    }

    *//**
     * 获取当前时间为每年第几周
     *
     * @param date
     * @return
     *//*
    public static int getWeekOfYear(Date date) {
        Calendar c = Calendar.getInstance();
        c.setFirstDayOfWeek(Calendar.MONDAY);
        c.setTime(date);
        int week = c.get(Calendar.WEEK_OF_YEAR) - 1;
        week = week == 0 ? 52 : week;
        return week > 0 ? week : 1;
    }

    public static int[] getCurrentDate() {
        int[] dateBundle = new int[3];
        String[] temp = getDataTime("yyyy-MM-dd").split("-");

        for (int i = 0; i < 3; i++) {
            try {
                dateBundle[i] = Integer.parseInt(temp[i]);
            } catch (Exception e) {
                dateBundle[i] = 0;
            }
        }
        return dateBundle;
    }

    *//**
     * 返回当前系统时间
     *//*
    public static String getDataTime(String format) {
        SimpleDateFormat df = new SimpleDateFormat(format);
        return df.format(new Date());
    }

    *//**
     * 把数据转换成类似于1千+，2.1万+,1百万+
     *
     * @param num
     * @return
     *//*
    public static String longToStringAlert(long num) {
        String result = "";
        if (num < 10000) {
            result = String.valueOf(num);
        } else if (num < 1000000) {
            long a1 = num / 10000;
            if (num % 10000 == 0) {
                result = a1 + "万+";
            } else {
                long a2 = num % 10000;
                result = a1 + "." + String.valueOf(a2).substring(0, 1) + "万+";
            }
        } else {
            result = (num / 1000000) + "百万+";
        }

        return result;
    }

    *//**
     * 从url中获得文件名
     *
     * @param url 文件路径
     * @return 文件名
     *//*
    public static String getFileName(String url) {
        if (url != null && url.length() > 0) {
            int index = url.lastIndexOf('/');
            if (index > 0) {
                return url.substring(index + 1);
            }
        }
        return "";
    }

    */

    /**
     * 使用md5的算法进行加密
     *//*
    public static String md5(String plainText) {
        String s = null;
        char hexDigits[] = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};// 用来将字节转换成16进制表示的字符
        try {
            java.security.MessageDigest md = java.security.MessageDigest.getInstance("MD5");
            md.update(plainText.getBytes());
            byte tmp[] = md.digest();
            char str[] = new char[16 * 2];
            int k = 0;
            for (int i = 0; i < 16; i++) {
                byte byte0 = tmp[i];
                str[k++] = hexDigits[byte0 >>> 4 & 0xf];
                str[k++] = hexDigits[byte0 & 0xf];

            }
            s = new String(str);
        } catch (NoSuchAlgorithmException e) {
//            CrashReport.postCatchedException(e);
        }
        return s;

    }

    public static String isZhengshu(String str) {
        return str.contains(".00") ? str.replace(".00", "") : str;
    }


    */
    public static boolean isSpace(String s) {
        return TextUtils.isEmpty(s) ? false : true;
    }


    //-------------------------------------------------------------------分割线
    public static boolean vertifyPhone(String phone) {
        if (TextUtils.isEmpty(phone)) {
            Utils.toast("手机号不能为空!");
            return false;
        }
        if (phone.length() == 11 && phone.startsWith("1")) {
            return true;
        }
        Utils.toast("请确认手机号格式!");
        return false;
    }

    public static boolean verifyPsd(String psd) {
        boolean b;
        if (TextUtils.isEmpty(psd)) {
            Utils.toast("密码不能为空！");
            return true;
        }
        if (psd.length() > 16 || psd.length() < 6) {
            Utils.toast("密码长度应为6-16位");
            return true;
        }

        String rex = "^[a-zA-Z0-9\\~\\!\\@#\\$\\%\\^\\&\\*\\(\\)_\\+\\[\\]\\{\\}\\|\\\\\\;\\:\\'\\\"\\,\\.\\/\\<\\>\\?]{6,16}$";
        b = psd.matches(rex);
        if (!b) {
            Utils.toast("密码包含非法字符");
        }
        return !b;
    }


    /**
     * Get MD5 Code
     */
    public static String getMD5(String text) {
        try {
            byte[] byteArray = text.getBytes("utf8");
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(byteArray, 0, byteArray.length);
            return convertToHex(md.digest());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * Convert byte array to Hex string
     */
    private static String convertToHex(byte[] data) {
        StringBuilder buf = new StringBuilder();
        for (int i = 0; i < data.length; i++) {
            int halfbyte = (data[i] >>> 4) & 0x0F;
            int two_halfs = 0;
            do {
                if ((0 <= halfbyte) && (halfbyte <= 9))
                    buf.append((char) ('0' + halfbyte));
                else
                    buf.append((char) ('a' + (halfbyte - 10)));
                halfbyte = data[i] & 0x0F;
            } while (two_halfs++ < 1);
        }
        return buf.toString();
    }


    public static String getUrlString(String url, String key) {
        String rex = "[?&]" + key + "=([^&]*)";
        Pattern pattern = Pattern.compile(rex);// 匹配的模式
        Matcher m = pattern.matcher("&" + url);
        while (m.find()) {
            return m.group(1);
        }
        return "";
    }

}
