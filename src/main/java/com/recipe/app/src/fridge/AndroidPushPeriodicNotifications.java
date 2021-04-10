package com.recipe.app.src.fridge;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class AndroidPushPeriodicNotifications {

    public static String PeriodicNotificationJson(ArrayList userList) throws JSONException {
        LocalDate localDate = LocalDate.now();

        // userList로 디바이스 토큰값구하기 클라랑 얘기
        String sampleData[] = {"device token value 1","device token value 2","device token value 3"}; // 알랍 보내려는 디바이스 토큰값 입력

        JSONObject body = new JSONObject();

        List<String> tokenlist = new ArrayList<String>();

        for(int i=0; i<sampleData.length; i++){
            tokenlist.add(sampleData[i]);
        }

        JSONArray array = new JSONArray();

        for(int i=0; i<tokenlist.size(); i++) {
            array.put(tokenlist.get(i));
        }

        body.put("registration_ids", array);

        JSONObject notification = new JSONObject();
//        notification.put("title","hello!");
//        notification.put("body","Today is "+localDate.getDayOfWeek().name()+"!");
        notification.put("title","유통기한 알림");
        notification.put("body","유통기한이 3일 남은 재료가 있습니다.");
        body.put("notification", notification);

        System.out.println(body.toString());

        return body.toString();
    }
}