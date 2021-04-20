package com.recipe.app.src.fridge;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class AndroidPushPeriodicNotifications {
//    public static String PeriodicNotificationJson(Integer userIdx,String ingredientName) throws JSONException {
    public static String PeriodicNotificationJson(String deviceToken,String ingredientName) throws JSONException {

//        String sampleData[] = {"device token value 1","device token value 2","device token value 3"};

//        List<String> tokenlist = new ArrayList<String>();

//        for(int i=0; i<sampleData.length; i++){
//            tokenlist.add(sampleData[i]);
//        }



//        List<Integer> tokenlist = new ArrayList<>();

//        for (Integer user :userMapList.keySet()){
//            tokenlist.add(user);
////            String ingredient = userMapList.get(user);
//        }
//

        JSONObject body = new JSONObject();
        JSONArray array = new JSONArray();
        array.put(deviceToken);

//        for(int i=0; i<tokenlist.size(); i++) {
//            array.put(tokenlist.get(i));
//        }


//        body.put("registration_ids", deviceToken);
        body.put("registration_ids", array);

        // 한메시지를 여러사람에게 보내는 것
        // 여러사람에게 여러메시지를 보낼 수 있도록
        JSONObject notification = new JSONObject();
//        LocalDate localDate = LocalDate.now();
//        notification.put("title","hello!");
//        notification.put("body","Today is "+localDate.getDayOfWeek().name()+"!");

        notification.put("title","유통기한 알림");
        notification.put("body",ingredientName+"의 유통기한이 3일 남았습니다.");
        body.put("notification", notification);

        System.out.println(body.toString());

        return body.toString();
    }
//    public static String PeriodicNotificationJson(Map<Integer, String> userMapList) throws JSONException {
//        LocalDate localDate = LocalDate.now();
//
//        // userList로 디바이스 토큰값구하기 클라랑 얘기
////        String sampleData[] = {"device token value 1","device token value 2","device token value 3"}; // 알랍 보내려는 디바이스 토큰값 입력
//
////        List<String> tokenlist = new ArrayList<String>();
//
////        for(int i=0; i<sampleData.length; i++){
////            tokenlist.add(sampleData[i]);
////        }
//
//
//
//        List<Integer> tokenlist = new ArrayList<>();
//
//        for (Integer user :userMapList.keySet()){
//            tokenlist.add(user);
////            String ingredient = userMapList.get(user);
//        }
//
//        System.out.print("tokenlist:");
//        System.out.println(tokenlist); // test
//
//        JSONObject body = new JSONObject();
//        JSONArray array = new JSONArray();
//
//        for(int i=0; i<tokenlist.size(); i++) {
//            array.put(tokenlist.get(i));
//        }
//
//
//        body.put("registration_ids", array);
//
//
//        // 한메시지를 여러사람에게 보내는 것
//        // 여러사람에게 여러메시지를 보낼 수 있도록
//        JSONObject notification = new JSONObject();
////        notification.put("title","hello!");
////        notification.put("body","Today is "+localDate.getDayOfWeek().name()+"!");
//        notification.put("title","유통기한 알림");
//        notification.put("body","유통기한이 3일 남은 재료가 있습니다.");
//        body.put("notification", notification);
//
//        System.out.println(body.toString());
//
//        return body.toString();
//    }
}