package com.vtec.j1tth4.vtecpos;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;

import java.io.File;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class Utils {

    public static File getVtecAssetPath(){
        File sdcard = Environment.getExternalStorageDirectory();
        File assetPath = new File(sdcard + File.separator + VtecPosApplication.VTEC_ROOT_DIR);
        if(!assetPath.exists())
            assetPath.mkdirs();
        return assetPath;
    }
}
