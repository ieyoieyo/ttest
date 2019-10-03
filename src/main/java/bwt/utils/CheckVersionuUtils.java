package bwt.utils;

import android.content.Context;
import android.util.Log;

import bwt.yfbhj.R;


public class CheckVersionuUtils {
    static public Boolean isNeedUpdate(Context context, String version){
        String[] newVersions = version.split("\\.");
        String[] currVersions = context.getResources().getString(R.string.version).split("\\.");
//        Log.d("CheckVersionuUtils", newVersions[0]+ "-" +currVersions[0]);
//        Log.d("CheckVersionuUtils", newVersions[1]+ "-" +currVersions[1]);
//        Log.d("CheckVersionuUtils", newVersions[2]+ "-" +currVersions[2]);
        if(newVersions.length == 3 && currVersions.length == 3){
            if(Integer.parseInt(newVersions[0]) > Integer.parseInt(currVersions[0])){
                return true;
            }else if(Integer.parseInt(newVersions[0]) == Integer.parseInt(currVersions[0])){
                if(Integer.parseInt(newVersions[1]) > Integer.parseInt(currVersions[1])){
                    return true;
                }else if(Integer.parseInt(newVersions[1]) == Integer.parseInt(currVersions[1])){
                    if(Integer.parseInt(newVersions[2]) > Integer.parseInt(currVersions[2])){
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
