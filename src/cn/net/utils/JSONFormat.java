package cn.net.utils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

public class JSONFormat {
    private static String SPACE = "    ";

    /**
     * 格式化输出JSONArray
     *
     * @param arr
     * @return
     */
    public static String format(JSONArray arr) {
        if (arr != null) {
            return format(arr.toString());
        }
        return "";
    }

    /**
     * 格式化输出JSONObject
     *
     * @param obj
     * @return
     */
    public static String format(JSONObject obj) {
        if (obj != null) {
            return format(obj.toString());
        }
        return "";
    }

    private static String format(String jsonStr) {
        if (jsonStr == null || jsonStr.length() < 1) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        char last = '\0';
        char llast = '\0';
        char current = '\0';
        int indent = 0;
        for (int i = 0; i < jsonStr.length(); i++) {
            llast = last;
            last = current;
            current = jsonStr.charAt(i);
            switch (current) {
            case '{':
            case '[':
                // if (condition) {
                //
                // }
                sb.append(current);
                sb.append('\n');
                indent++;
                addIndentBlank(sb, indent);
                break;
            case '"':
                sb.append(current);
                if (last == ',' && llast == '}') {
                    sb.append('\n');
                    addIndentBlank(sb, indent);
                }
                break;
            case '}':
            case ']':
                sb.append('\n');
                indent--;
                addIndentBlank(sb, indent);
                sb.append(current);
                break;
            case ',':
                sb.append(current);

                switch (last) {
                case '\\':

                    break;
                case ']':
                    // 支持JsonArray
                    sb.append('\n');
                    addIndentBlank(sb, indent);
                    break;
                case '"':
                    // 支持json Value里多个,的
                    if (llast != ':') {
                        sb.append('\n');
                        addIndentBlank(sb, indent);
                    }
                    break;

                default:
                    break;
                }
                break;
            default:
                sb.append(current);
            }
        }

        return sb.toString();
    }

    /**
     * 添加space
     *
     * @param sb
     * @param indent
     */
    private static void addIndentBlank(StringBuilder sb, int indent) {
        for (int i = 0; i < indent; i++) {
            sb.append(SPACE);
        }
    }
}
