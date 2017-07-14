package cn.parser.test;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Pattern;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import cn.net.utils.JSONFormat;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

/**
 * @Copyright © 2017 sanbo Inc. All rights reserved.
 * @Description: parser from net
 * @Version: 1.0
 * @Create: 2017年7月14日 下午7:10:17
 * @author: safei
 */
public class NetGetAllJson {
    private static String mBaseURL = "https://raw.githubusercontent.com/googlesupportter/googleapps/master/";
    private static String mAllJson = "all.json";
    private static String mAllFormatJson = "format_all.json";

    public static void main(String[] args) {

        /**
         * 检查所有的信息
         */
        JSONObject all = parserAll();
        if (all.size() > 0) {
            saveAll("list/" + mAllJson, all.toString());

            saveAll("list/" + mAllFormatJson, JSONFormat.format(all));
        }
        try {
            Runtime.getRuntime().exec("sh autopush.sh");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void saveAll(String fileName, String all) {

        FileWriter fw = null;
        File f = new File(fileName);
        try {
            if (f.exists()) {
                f.delete();
            }
            f.createNewFile();
            fw = new FileWriter(f, false);
            fw.write(all);
            fw.write("\n");
            fw.flush();

        } catch (Throwable e) {
            System.gc();
        } finally {
            if (fw != null) {
                try {
                    fw.close();
                } catch (Throwable e) {
                    System.gc();
                }
            }
        }
    }

    private static JSONObject parserAll() {
        JSONObject all = new JSONObject();

        JSONArray services = getGooglePlayService("GooglePlayService");
        all.put("GooglePlayService", services);

        JSONArray stores = getGooglePlayService("GooglePlayStore");
        all.put("GooglePlayStore", stores);

        String date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                .format(new Date(System.currentTimeMillis()));
        all.put("UpdateData", date);
        return all;
    }

    private static JSONArray getGooglePlayService(String name) {
        JSONArray arr = new JSONArray();
        JSONArray versions = getGooglePlayServiceVersion(name);
        if (versions.size() > 0) {
            String version = "";
            for (int i = 0; i < versions.size(); i++) {
                JSONObject vv = new JSONObject();
                version = versions.getString(i);
                if (version.length() > 0) {
                    JSONArray apps = getRowAddr(name, version);
                    if (apps.size() > 0) {
                        vv.put(version, apps);
                        if (vv.size() > 0) {
                            arr.add(vv);
                        }
                    }
                }
            }
        }
        // getRowAddr();
        return arr;
    }

    /**
     * 获取版本号. 支持 xx.xx.xx
     * 
     * @param name
     * @return
     */
    private static JSONArray getGooglePlayServiceVersion(String name) {
        JSONArray arr = new JSONArray();
        try {
            Document doc = Jsoup.connect("https://github.com/googlesupportter/googleapps/tree/master/" + name + "/")
                    .get();
            String[] ss = doc.text().split(" ");
            String pattern = "^\\d+.\\d+.\\d+$";
            // ^([0-9].){2,}\\.[a-zA-Z_]{0,} 适用于 7.9.90.Q

            for (int i = 0; i < ss.length - 1; i++) {
                boolean isMatch = Pattern.matches(pattern, ss[i]);
                if (isMatch) {
                    arr.add(ss[i]);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return arr;
    }

    /**
     * 获取具体apk名字
     * 
     * @param nam
     * @param version
     * @return
     */
    private static JSONArray getRowAddr(String name, String version) {
        JSONArray arr = new JSONArray();
        try {
            Document doc = Jsoup
                    .connect("https://github.com/googlesupportter/googleapps/tree/master/" + name + "/" + version)
                    .get();
            String[] ss = doc.text().split(" ");
            for (int i = 0; i < ss.length - 1; i++) {
                if (ss[i].endsWith(".apk")) {
                    arr.add(mBaseURL + name + "/" + version + "/" + ss[i]);
                }
            }
        } catch (IOException e) {
        }
        return arr;
    }
}
