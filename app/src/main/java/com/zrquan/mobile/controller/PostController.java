package com.zrquan.mobile.controller;

import com.android.volley.VolleyError;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import com.zrquan.mobile.event.Post.PullDownEvent;
import com.zrquan.mobile.event.Post.PullUpEvent;
import com.zrquan.mobile.event.Post.StartLoadEvent;
import com.zrquan.mobile.model.PostFeed;
import com.zrquan.mobile.support.util.LogUtils;
import com.zrquan.mobile.support.util.UrlUtils;
import com.zrquan.mobile.support.volley.VolleyJsonRequest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.greenrobot.event.EventBus;

public class PostController {

    public static void startLoad(int userId, String sortType) {
        // pass second argument as "null" for GET requests
        Map<String, String> params = new HashMap<>();
        params.put("user_id", Integer.toString(userId));
        final String url =
                "http://192.168.1.102:3000/home/posts?user_id=" + userId + "&sort=" + sortType;

        LogUtils.i("服务器URL:" + url);
        VolleyJsonRequest.get(url, new VolleyJsonRequest.ResponseHandler() {
            @Override
            public void onResponse(JsonObject response) {
                try {
                    LogUtils.i("收到请求的回复了");

                    JsonArray idArray = response.get("ids").getAsJsonArray();
                    if (idArray != null && idArray.size() != 0) {
                        // 转所有的post id 成数组
                        Integer[] postIds = new Gson().fromJson(idArray, Integer[].class);
                        List<PostFeed> initialList = getPostFeedList(response, "initial_result");
                        EventBus.getDefault().post(new StartLoadEvent(postIds, initialList));
                    } else {
                        EventBus.getDefault().post(new StartLoadEvent(null, null));
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onErrorResponse(VolleyError error) {}
        });
    }

    public static void pullDown(int userId, String sortType) {
        // pass second argument as "null" for GET requests
        Map<String, String> params = new HashMap<>();
        params.put("user_id", Integer.toString(userId));
        final String url =
                "http://192.168.1.102:3000/home/posts?user_id=" + userId + "&sort=" + sortType;

        LogUtils.i("服务器URL:" + url);
        VolleyJsonRequest.get(url, new VolleyJsonRequest.ResponseHandler() {
            @Override
            public void onResponse(JsonObject response) {
                try {
                    LogUtils.i("收到请求的回复了");

                    JsonArray idArray = response.get("ids").getAsJsonArray();
                    if (idArray != null && idArray.size() != 0) {
                        // 转所有的post id 成数组
                        Integer[] postIds = new Gson().fromJson(idArray, Integer[].class);
                        List<PostFeed> initialList = getPostFeedList(response, "initial_result");
                        EventBus.getDefault().post(new PullDownEvent(postIds, initialList));
                    } else {
                        EventBus.getDefault().post(new PullDownEvent(null, null));
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onErrorResponse(VolleyError error) {}
        });
    }

    public static void pullUp(Integer[] postIds, String sortType) {
        // pass second argument as "null" for GET requests
        Map<String, String> params = new HashMap<>();
        String queryString = "";
        for (int i=0; i<postIds.length; i++) {
            if (i == 0) {
                queryString = UrlUtils.joinParams("post_id[]", postIds[i].toString());
            } else {
                queryString = queryString + UrlUtils.PARAMETERS_SEPARATOR +
                        UrlUtils.joinParams("post_id[]", postIds[i].toString());
            }
        }
        queryString = queryString + UrlUtils.PARAMETERS_SEPARATOR + "sort=" + sortType;
        final String url = "http://192.168.1.102:3000/home/posts?" + queryString;

        LogUtils.i("服务器URL:" + url);
        VolleyJsonRequest.get(url, new VolleyJsonRequest.ResponseHandler() {
            @Override
            public void onResponse(JsonObject response) {
                try {
                    LogUtils.i("收到请求的回复了");
                    List<PostFeed> partialList = getPostFeedList(response, "partial_result");
                    LogUtils.i("讨论数:" + partialList.size());
                    EventBus.getDefault().post(new PullUpEvent(partialList));

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onErrorResponse(VolleyError error) {}
        });
    }

    private static List<PostFeed> getPostFeedList(JsonObject response, String resultKey) {
        JsonArray resultArray = response.get(resultKey).getAsJsonArray();
        List<PostFeed> pfList = new ArrayList<>();
        Gson gson = new GsonBuilder()
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .create();
        for (int i = 0; i < resultArray.size(); i ++ ) {
            PostFeed d = gson.fromJson(resultArray.get(i), PostFeed.class);
            pfList.add(d);
        }
        return pfList;
    }
}
