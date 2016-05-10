package com.rayerrt.attributionquery;

import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by 10940414 on 2016/5/6.
 */
public class AttributionQuery {

    private static final String TAG = "AttributionQuery";

    private static final String[] PREFIX = {
            "17951", "12593", "17911", "10193", "12520070", "12520026", "12520"
    };
//    private Integer[] numberPrefixList = new Integer[]{
//            130, 131, 132, 133, 134, 135, 136, 137, 138, 139,
//            145, 147, 150, 151, 152, 153, 155, 156, 157, 158, 159,
//            170, 175, 176, 177, 178, 180, 181, 182, 183, 184, 185, 186, 187, 188, 189
//    };

    private ArrayList<Integer> numberPrefixList = new ArrayList<>();
    private static final String[] PREFIX_CHINA = {
            "0086", "86", "+86"
    };

    public void getNumberPrefixList(DatabaseHelper helper) {
        SQLiteDatabase db = helper.getReadableDatabase();
        ArrayList<String> tables = new ArrayList<>();
        try {
            tables = helper.queryTableNames(db);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                db.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        int prefix = 0;
        if (null != tables && tables.size() > 0) {
            for (String table : tables) {
                try {
                    prefix = Integer.valueOf(table.replace("Attributions_", ""));
                    Log.d(TAG, "here prefix is " + prefix);
                    if (!numberPrefixList.contains(prefix)) {
                        numberPrefixList.add(prefix);
                    }
                } catch (NullPointerException e) {
                    e.printStackTrace();
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private String getQueryNumber(DatabaseHelper helper, String number) {
        String result = null;
        SQLiteDatabase db = null;
        int i;
        number = stripSeparators(number);

        if (number == null || number.length() < 3) {
            Log.d(TAG, "return null, number = " + number);
            return "";
        }

        // filter prefix such as "12580"
        boolean chinaPrefix = false;
        for (i = 0; i < PREFIX.length; i++) {
            if (number.startsWith(PREFIX[i])) {
                number = number.substring(PREFIX[i].length());
                break;
            }
        }

        // if is international-roaming, return null
        //if ("true".equals(SystemProperties.get(TelephonyProperties.PROPERTY_OPERATOR_ISROAMING))) {
        if (false) {
            Log.d(TAG, "sim1 roaming, return null...");
            return "";
        }
        //if ("true".equals(SystemProperties.get("gsm.operator.isroaming.2"))) {
        if (false) {
            Log.d(TAG, "sim2 roaming, return null...");
            return "";
        }

        // filter international prefix
        if (number.startsWith("00") && !number.startsWith("0086")) {
            return null;
        }

        Log.d(TAG, "begin checking number --> " + number);
        // filter "+86"/"86"/"0086"
        for (i = 0; i < PREFIX_CHINA.length; i++) {
            if (number.startsWith(PREFIX_CHINA[i])) {
                //chinaPrefix = true;
                if (number.startsWith("86")) {
                    if (number.length() <= 8) {
                        return null;
                    }
                }
                number = number.substring(PREFIX_CHINA[i].length());
                break;
            }
        }

        try {
            db = helper.getReadableDatabase();

            if (number.length() > 2 && number.length() < 6 && TextUtils.isDigitsOnly(number)) {
                result = querySpecialAttribution(helper, db, "ExportView_Special"
                        , number);
                return result;
            } else if (number.startsWith("0") && TextUtils.isDigitsOnly(number.substring(1, 3))) {
                result = queryFixedAttribution(helper, db, "ExportView_Fixed"
                        , number);
//                if (null != result) {
//                    return result;
//                }
            } else if (number.startsWith("1") && number.length() > 1
                    && !number.substring(1, 2).equals("0")) {
                result = queryMobileAttribution(helper, db, number);
//                if (null != result) {
//                    return result;
//                }
            }
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            try {
                db.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public String queryAttributionByNumber(DatabaseHelper databaseHelper, String numberString) {
        String result = null;
        if (isInternationalVersion()) {
            return result;
        }
        try {
            result = getQueryNumber(databaseHelper, numberString);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    private HashMap<String, String> getQueryNumberHashMap(DatabaseHelper helper, String number) {
        HashMap<String, String> attributionDetail = new HashMap<>();
        String result = null;
        SQLiteDatabase db = null;
        int i;
        number = stripSeparators(number);

        if (number == null || number.length() < 3) {
            Log.d(TAG, "return null, number = " + number);
            return attributionDetail;
        }

        // filter prefix such as "12580"
        boolean chinaPrefix = false;
        for (i = 0; i < PREFIX.length; i++) {
            if (number.startsWith(PREFIX[i])) {
                number = number.substring(PREFIX[i].length());
                break;
            }
        }

        // if is international-roaming, return null
        //if ("true".equals(SystemProperties.get(TelephonyProperties.PROPERTY_OPERATOR_ISROAMING))) {
        if (false) {
            Log.d(TAG, "sim1 roaming, return null...");
            return attributionDetail;
        }
        //if ("true".equals(SystemProperties.get("gsm.operator.isroaming.2"))) {
        if (false) {
            Log.d(TAG, "sim2 roaming, return null...");
            return attributionDetail;
        }

        // filter international prefix
        if (number.startsWith("00") && !number.startsWith("0086")) {
            return null;
        }
        Log.d(TAG, "begin checking number --> " + number);
        // filter "+86"/"86"/"0086"
        for (i = 0; i < PREFIX_CHINA.length; i++) {
            if (number.startsWith(PREFIX_CHINA[i])) {
                //chinaPrefix = true;
                if (number.startsWith("86")) {
                    if (number.length() <= 8) {
                        return null;
                    }
                }
                number = number.substring(PREFIX_CHINA[i].length());
                break;
            }
        }

        try {
            db = helper.getReadableDatabase();
            if (number.length() > 2 && number.length() < 6 && TextUtils.isDigitsOnly(number)) {
                result = querySpecialAttribution(helper, db, "ExportView_Special"
                        , number);
                attributionDetail.put("attribution", result);
                attributionDetail.put("type", "special");
                return attributionDetail;
            } else if (number.startsWith("0") && TextUtils.isDigitsOnly(number.substring(1, 3))) {
                result = queryFixedAttribution(helper, db, "ExportView_Fixed"
                        , number);
//                if (null != result) {
//                    return result;
//                }
                attributionDetail.put("attribution", result);
                attributionDetail.put("type", "fixed");
            } else if (number.startsWith("1") && number.length() > 1
                    && !number.substring(1, 2).equals("0")) {
                result = queryMobileAttribution(helper, db, number);
//                if (null != result) {
//                    return result;
//                }
                attributionDetail.put("attribution", result);
                attributionDetail.put("type", "mobile");
            }
            return attributionDetail;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            try {
                db.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    private String querySpecialAttribution(DatabaseHelper helper, SQLiteDatabase db, String tableName, String numberString) {
        String result = null;
        try {
            result = helper.queryAttributionIfNumberEquals(db, tableName, numberString);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    private String queryFixedAttribution(DatabaseHelper helper, SQLiteDatabase db, String tableName, String numberString) {
        String result = null;
//        try {
            result = helper.queryAttributionIfNumberEquals(db, tableName, numberString);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
        return result;
    }

    private String queryMobileAttribution(DatabaseHelper helper, SQLiteDatabase db, String numberString) {
        int length = numberString.length();
        if (length < 7) {
            return "";
        }
        if (length > 11) {
            return null;
        }
        boolean valid = false;
        String inputPrefix = numberString.substring(0, 3);
        for (int prefix : numberPrefixList) {
            if (inputPrefix.equals(String.valueOf(prefix))) {
                valid = true;
                break;
            }
        }
        if (!valid) {
            return null;
        }
        String tableName = String.format("ExportView_%s",
                inputPrefix);
        int phoneNumber = Integer.parseInt(numberString);
        int offset = -1;
        int flag;
        if (phoneNumber > 1000000 && phoneNumber < 1900000) {
            offset = phoneNumber - Integer.parseInt(inputPrefix) * 10000;
        } else if (phoneNumber > 10000000 && phoneNumber < 19000000) {
            offset = phoneNumber - Integer.parseInt(inputPrefix) * 100000;
        } else if (phoneNumber > 100000000 && phoneNumber < 190000000) {
            offset = phoneNumber - Integer.parseInt(inputPrefix) * 1000000;
        }
        flag = length - 7;
        String result = helper.getStringValueFromMobile(db,
                tableName, offset, flag);
        Log.d(TAG, "here tableName is " + tableName + ", offset is " + offset + "result is " + result);
        return result;
    }


    private String stripSeparators(String phoneNumber) {
        if (phoneNumber == null) {
            return null;
        }
        int len = phoneNumber.length();
        StringBuilder ret = new StringBuilder(len);

        for (int i = 0; i < len; i++) {
            char c = phoneNumber.charAt(i);
            if (isNonSeparator(c)) {
                ret.append(c);
            }
        }

        return ret.toString();
    }

    private boolean isNonSeparator(char c) {
        return (c >= '0' && c <= '9') || c == '*' || c == '#' || c == '+'
                || c == 'N' || c == 'w' || c == 'p';
    }

    public boolean isInternationalVersion() {
        return false;
        //return "yes".equalsIgnoreCase(SystemProperties.get("ro.vivo.product.overseas", "no"));
    }
}
