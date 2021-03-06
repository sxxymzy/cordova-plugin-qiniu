package org.cordova.plugin.qiniu;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaWebView;
import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.UpCompletionHandler;
import com.qiniu.android.storage.UpProgressHandler;
import com.qiniu.android.storage.UploadManager;
import com.qiniu.android.storage.UploadOptions;

import java.net.URLDecoder;
import java.util.Map;

import android.util.Log;

/**
 * @author yumemor
 * <p>
 * 基于七牛云储存编写的Corodva上传插件
 */
public class QiniuPlugin extends CordovaPlugin implements UpCompletionHandler {
    /**
     * 七牛上传管理器
     */
    private UploadManager uploadManager;
    /**
     * cordova回调
     */
    private CallbackContext callbackContext;

    @Override
    public void initialize(CordovaInterface cordova, CordovaWebView webView) {
        uploadManager = new UploadManager();
    }

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        this.callbackContext = callbackContext;
        try {
            Method method = this.getClass().getDeclaredMethod(action, JSONArray.class);
            if (method != null) {
                method.invoke(this, args);
                return true;
            } else {
                callbackContext.error("Action错误!");
            }
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return false;
    }

    private void uploadFile(JSONArray args) throws JSONException, UnsupportedEncodingException {
        // String prefix = args.optJSONObject(0).getString("prefix");
        // String filePath = args.optJSONObject(0).getString("filePath");
        // String name = StrUtils.appendPrefix(prefix, StrUtils.getFileName(filePath));	//获取文件名称 添加前缀
        // args: [filePath, remoteKey, token, callbacks.progressCbk, options]);
        String filePath = args.optString(0);
        String remoteKey = args.optString(1);
        String token = args.optString(2);
        JSONObject options = args.optJSONObject(3);
        final double[] lastPercent = {0};
        UploadOptions uploadOptions = new UploadOptions(null,
                options.optString("mimeType"), options.optBoolean("checkCrc"),
                (key, percent) -> {
                    if (percent - lastPercent[0] > 0.1) {
                        Log.i("qiniu", key + ": " + percent);
                    }
                    lastPercent[0] = percent;
                    JSONObject rspJson = new JSONObject();
                    try {
                        rspJson.put("type", "progress");
                        rspJson.put("data", percent);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    PluginResult prs = new PluginResult(PluginResult.Status.OK, rspJson);
                    prs.setKeepCallback(true);
                    callbackContext.sendPluginResult(prs);
                }, null); // todo can't cancel
        filePath = URLDecoder.decode(filePath, "UTF-8");    //文件路径解码
        filePath = filePath.replace("file://", "");    //去掉 file:// 路径。
        uploadManager.put(filePath, remoteKey, token, this, uploadOptions);
    }

    @Override
    public void complete(String name, ResponseInfo info, JSONObject json) {
        int statusCode = info.statusCode;
        JSONObject jsonObject = new JSONObject();
        try {
            JSONObject httpObj = new JSONObject();
            httpObj.put("statusCode", statusCode);
            httpObj.put("message", info.message);
            httpObj.put("error", info.error);

            jsonObject.put("type", "success");
            jsonObject.put("data", json);
            jsonObject.put("response", httpObj);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (statusCode == 200) {
            callbackContext.success(jsonObject);
        } else {
            callbackContext.error(jsonObject);
        }
    }

}
