package com.recipe.app.common.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 내부 응답 코드 관리
 */
@Getter
@RequiredArgsConstructor
public enum BaseResponseStatus {
    /**
     * 1000 : 요청 성공
     */
    SUCCESS(1000, "요청에 성공하였습니다."),


    /**
     * 2000 : Request 오류
     */
    // Common
    REQUEST_ERROR(2000, "입력값을 확인해주세요."),
    EMPTY_JWT(2001, "JWT를 입력해주세요."),
    INVALID_JWT(2002, "유효하지 않은 JWT입니다."),
    EMPTY_TOKEN(2003, "ACCESS TOKEN 값을 입력해주세요."),
    // users
    USERS_EMPTY_USER_ID(2010, "유저 아이디 값을 확인해주세요."),

    // [POST] /users
    POST_USERS_EMPTY_EMAIL(2015, "이메일을 입력해주세요."),
    POST_USERS_INVALID_EMAIL(2016, "이메일 형식을 확인해주세요."),
    POST_USERS_EMPTY_PASSWORD(2017, "비밀번호를 입력해주세요."),
    POST_USERS_EMPTY_CONFIRM_PASSWORD(2018, "비밀번호 확인을 입력해주세요."),
    POST_USERS_WRONG_PASSWORD(2019, "비밀번호를 다시 입력해주세요."),
    POST_USERS_DO_NOT_MATCH_PASSWORD(2020, "비밀번호와 비밀번호확인 값이 일치하지 않습니다."),
    POST_USERS_EMPTY_NICKNAME(2021, "닉네임을 입력해주세요."),

    // [PATCH] /users/:userId
    PATCH_USERS_DO_NOT_MATCH_PASSWORD(2022, "비밀번호와 비밀번호확인 값이 일치하지 않습니다."),

    // [POST] /users/login
    POST_USERS_LOGIN_EMPTY_EMAIL(2023, "이메일을 입력해주세요."),
    POST_USERS_LOGIN_INVALID_EMAIL(2024, "이메일 형식을 확인해주세요."),
    POST_USERS_LOGIN_EMPTY_PASSWORD(2025, "비밀번호를 입력해주세요."),

    // [PATCH] /users/:userId/status
    PATCH_USERS_STATUS_INVALID_USER_STATUS(2026, "상태코드를 확인해주세요."),

    // boards
    BOARDS_EMPTY_BOARD_ID(2040, "게시판 아이디 값을 확인해주세요."),

    // [POST] /boards
    POST_BOARDS_EMPTY_TITLE(2041, "게시판 제목을 입력해주세요."),
    POST_BOARDS_EMPTY_CONTENTS(2042, "내용을 입력해주세요."),

    // [PATCH] /boards/:boardId
    PATCH_BOARDS_EMPTY_TITLE(2043, "게시판 제목을 입력해주세요."),
    PATCH_BOARDS_EMPTY_CONTENTS(2044, "내용을 입력해주세요."),

    // [PATCH] /boards/:boardId/status
    PATCH_BOARDS_STATUS_INVALID_BOARD_STATUS(2045, "상태코드를 확인해주세요."),

    // [GET] /my-recipes/:myRecipeIdx


    // [POST] /my-recipes
    EMPTY_TITLE(2050, "제목을 입력해주세요."),
    EMPTY_CONTENT(2051, "내용을 입력해주세요."),
    EMPTY_THUMBNAIL(2052, "썸네일을 입력해주세요."),
    EMPTY_INGREDIENT_NAME(2082, "재료 직접입력 - 재료명을 입력하세요."),
    EMPTY_INGREDIENT_ICON(2083, "재료 직접입력 - 재료 아이콘을 입력하세요."),

    // [PATCH] /my-recipes/:myRecipeIdx
    EMPTY_MY_RECIPEIDX(2054, "나만의 레시피 인덱스값을 입력해주세요."),

    // [POST] /scraps/youtube
    EMPTY_YOUTUBEURL(2055, "유튜브 url을 입력해주세요."),
    EMPTY_POST_DATE(2056, "포스팅 날짜를 입력해주세요."),
    EMPTY_CHANNEL_NAME(2057, "유튜브 채널명을 입력해주세요."),
    EMPTY_YOUTUBEIDX(2058, "유튜브 인덱스를 입력해주세요."),
    EMPTY_PLAY_TIME(2053, "영상 재생시간을 입력해주세요."),
    INVALID_THUMBNAIL(2054, "유효하지 않은 이미지타입-jpg,png,gif,pdf 형식의 이미지를 입력하세요."),
    INVALID_YOUTUBE_URL(2055, "유효하지 않은 url 타입입니다."),
    INVALID_DATE(2056, "유효하지 않은 날짜 타입-YYYY.MM.DD 형식으로 입력하세요."),

    // [POST] /scraps/blog
    POST_SCRAP_BLOG_EMPTY_TITLE(2059, "제목을 입력해주세요."),
    POST_SCRAP_BLOG_EMPTY_THUMBNAIL(2060, "썸네일을 입력해주세요.."),
    POST_SCRAP_BLOG_EMPTY_BLOGURL(2061, "블로그 url을 입력해주세요."),
    POST_SCRAP_BLOG_EMPTY_DESCRIPTION(2062, "내용을 입력해주세요."),
    POST_SCRAP_BLOG_EMPTY_BLOGGER_NAME(2063, "블로거명을 입력해주세요."),
    POST_SCRAP_BLOG_EMPTY_POST_DATE(2064, "포스팅 날짜를 입력해주세요."),

    // [POST] /fridges/direct-basket
    POST_FRIDGES_DIRECT_BASKET_EMPTY_INGREDIENT_NAME(2065, "재료명을 입력하세요."),
    POST_FRIDGES_DIRECT_BASKET_EMPTY_INGREDIENT_ICON(2066, "재료 아이콘을 입력하세요."),
    POST_FRIDGES_DIRECT_BASKET_EMPTY_INGREDIENT_CATEGORY_IDX(2067, "재료 카테고리 인덱스를 입력하세요."),
    POST_FRIDGES_DIRECT_BASKET_DUPLICATED_INGREDIENT_NAME_IN_INGREDIENTS(2069, ""),

    // [POST] /fridges/basket
    POST_FRIDGES_BASKET_EMPTY_INGREDIENT_LIST(2070, "재료리스트를 입력해주세요."),
    FAILED_TO_GET_INGREDIENT(2071, "재료조회에 실패했습니다."),
    NOT_FOUND_INGREDIENT(2072, "재료를 찾을 수 없습니다."),
    POST_FRIDGES_BASKET_EXIST_INGREDIENT_NAME(2068, ""),

    // [POST] /fridges
    POST_FRIDGES_EMPTY_FRIDGE_BASKET_LIST(2073, "냉장고 바구니 리스트를 입력해주세요."),
    FAILED_TO_POST_FRIDGES(2074, "냉장고에 재료넣기를 실패했습니다."),
    POST_FRIDGES_EXIST_INGREDIENT_NAME(2075, ""),
    POST_FRIDGES_EMPTY_INGREDIENT_NAME(2084, "재료명을 입력하세요."),
    POST_FRIDGES_EMPTY_INGREDIENT_ICON(2085, "재료 아이콘을 입력하세요."),
    EMPTY_STORAGE_METHOD(2086, "재료 보관방법을 입력하세요."),
    EMPTY_INGREDIENT_COUNT(2087, "재료 개수를 입력하세요."),

    // [POST] /views/blog
    POST_VIEW_BLOG_EMPTY_BLOGURL(2076, "블로그 url을 입력해주세요."),

    // receipts
    RECEIPTS_EMPTY_RECEIPT_IDX(2077, "영수증 인덱스 값을 확인해주세요."),


    // [DELETE] /fridges/ingredient
    EMPTY_INGREDIENT(2078, "삭제할 재료를 입력하세요."),

    // [PATCH] /fridge/ingredient
    EMPTY_PATCH_FRIDGE_LIST(2079, "수정할 냉장고 재료 리스트를 입력하세요."),
    INVALID_STORAGE_METHOD(2080, "냉장,냉동,실온만 입력하세요."),

    // [PATCH] /fcm/token
    EMPTY_FCM_TOKEN(2081, "FCM 토큰을 입력하세요."),

    // recipes
    RECIPES_EMPTY_RECIPE_IDX(2082, "공공 레시피 인덱스 값을 확인해주세요."),

    // [PATCH] /fridge/basket
    PATCH_FRIDGES_BASKET_EMPTY_FRIDGES_BASKET_LIST(2083, "수정할 냉장고 바구니 리스트를 입력하세요."),

    // 2088부터 가능
    /**
     * 3000 : Response 오류
     */
    // Common
    RESPONSE_ERROR(3000, "값을 불러오는데 실패하였습니다."),

    // users
    NOT_FOUND_USER(3010, "존재하지 않는 회원입니다."),
    FORBIDDEN_USER(3011, "해당 회원에 접근할 수 없습니다."),

    // [POST] /users
    DUPLICATED_USER(3013, "이미 존재하는 회원입니다."),

    // [POST] /users/naver-login
    FAILED_TO_NAVER_LOGIN(3015, "네이버 로그인에 실패하였습니다."),

    // [POST] /users/kakao-login
    FAILED_TO_KAKAO_LOGIN(3016, "카카오 로그인에 실패하였습니다."),

    FAILED_TO_GET_USER(3020, "유저 상세 정보 조회에 실패하였습니다."),
    FAILED_TO_PATCH_USER(3021, "유저 정보 수정에 실패하였습니다."),


    // [GET] /my-recipes
    FAILED_TO_GET_MY_RECIPES(3022, "나만의 레시피 전체 조회에 실패하였습니다."),


    // [GET] /my-recipes/:myRecipeIdx
    FAILED_TO_GET_MY_RECIPE_INGREDIENTS(3024, "나만의 레시피 재료 조회에 실패하였습니다."),
    FAILED_TO_GET_MY_RECIPE(3025, "나만의 레시피 상세 조회에 실패하였습니다."),
    NO_FOUND_MY_RECIPE(3026, "존재하지 않는 나만의 레시피입니다."),

    // [POST] /my-recipes
    FAILED_TO_SAVE_MY_RECIPE(3027, "나만의 레시피 생성 저장에 실패하였습니다."),
    FAILED_TO_SAVE_MY_RECIPE_INGREDIENT(3072, "나만의 레시피 재료 저장에 실패하였습니다."),

    // [PATCH] /my-recipes/:myRecipeIdx
    FAILED_TO_PATCH_MY_RECIPE(3028, "나만의 레시피 수정에 실패하였습니다."),

    // [DELETE] /my-recipes/:myRecipeIdx
    FAILED_TO_DELETE_MY_RECIPE(3029, "나만의 레시피 삭제에 실패하였습니다."),
    FAILED_TO_DELETE_MY_RECIPE_INGREDIENT(3068, "나만의 레시피 재료 삭제에 실패하였습니다."),

    // [POST] /scraps/youtube
    FAILED_TO_POST_SCRAP_YOUTUBE(3030, "유튜브 스크랩하기에 실패하였습니다."),
    FAILED_TO_POST_DELETE_SCRAP_YOUTUBE(3032, "유튜브 스크랩 취소를 실패하였습니다."),
    // [GET] /scraps/youtube
    FAILED_TO_GET_SCRAP_YOUTUBE(3031, "유튜브 스크랩 조회에 실패하였습니다."),


    // [POST] /scraps/recipe
    FAILED_TO_POST_CREATE_SCRAP_PUBLIC(3033, "공공 레시피 스크랩 추가에 실패하였습니다."),
    FAILED_TO_POST_DELETE_SCRAP_PUBLIC(3034, "공공 레시피 스크랩 삭제에 실패하였습니다."),
    FAILE_TO_GET_RECIPE_INFO(3035, "공공레시피 정보 조회에 실패하였습니다."),
    NOT_FOUND_RECIPE_INFO(3036, "공공레시피 정보를 찾지 못하였습니다."),
    // [GET] /scraps/recipe
    FAILED_TO_GET_SCRAP_PUBLIC(3037, "공공 레시피 스크랩 조회에 실패하였습니다."),

    // [GET] /ingredients
    FAILED_TO_GET_INGREDIENT_CATEGORY(3038, "재료 카테고리 조회에 실패하였습니다."),
    NOT_FOUND_INGREDIENT_CATEGORY(3039, "재료 카테고리인덱스를 찾을 수 없습니다."),
    FAILED_TO_GET_INGREDIENT_LIST(3040, "재료 리스트 조회에 실패하였습니다."),
    FAILED_TO_GET_FRIDGE_BASKET_COUNT(3070, "냉장고 바구니 개수 조회에 실패하였습니다."),

    // [POST] /fridges/direct-basket
    FAILED_TO_POST_FRIDGES_DIRECT_BASKET(3041, "재료 직접 입력으로 냉장고 바구니 담기에 실패하였습니다."),
    FAILED_TO_RETREIVE_FRIDGE_BASKET_BY_NAME(3042, "재료명으로 냉장고 바구니 조회에 실패하였습니다."),
    FAILED_TO_RETREIVE_INGREDIENT_BY_NAME(3043, "재료명으로 재료 조회에 실패하였습니다."),

    // [GET] /fridges/basket
    FAILED_TO_RETREIVE_INGREDIENT_LIST_BY_USER(3044, "재료명으로 재료 조회에 실패하였습니다."),
    FAILED_TO_COUNT_FRIDGE_BASKET_BY_USER(3045, "유저로 바구니 속 개수 세기에 실패하였습니다."),

    // [POST] /fridges/basket
    FAILED_TO_POST_FRIDGES_BASKET(3046, "재료 선택으로 냉장고 바구니 담기에 실패했습니다."),


    // [GET] /fridges
    FAILED_TO_RETREIVE_FRIDGE_BY_USER(3047, "유저로 냉장고 조회에 실패했습니다."),

    // [POST] /fridges



    // receipts
    NOT_FOUND_RECEIPT(3048, "존재하지 않는 영수증입니다."),
    NOT_FOUND_BUY(3049, "존재하지 않는 구매 품목입니다."),

    //[POST] /views/youtube
    FAILED_TO_POST_VIEWS_YOUTUBE(3050, "유튜브 조회로그를 저장에 실패습니다."),

    // [DELETE] /fridges/ingredient
    FAILED_TO_GET_INGREDIENT_NAME(3051, "재료명 조회에 실패했습니다."),
    FAILED_TO_GET_FRIDGE(3052, "냉장고 조회에 실패했습니다."),
    FAILED_TO_DELETE_FRIDGE(3053, "냉장고 재료 삭제에 실패했습니다."),


    // [PATCH] /fridge/ingredient
    FAILED_TO_SAVE_FRIDGE(3054, "냉장고 재료 수정 저장에 실패했습니다."),
    FAILED_TO_PATCH_FRIDGES_INGREDIENT(3055, "냉장고 재료 수정에 실패했습니다."),

    // [POST] /recipes?keyword=
    FAILED_TO_POST_RECIPE_KEYWORD(3056, "레시피 검색어 저장에 실패했습니다."),

    // notification
    FAILED_TO_GET_SHELF_LIFE_USER_LIST(3057, "유통기한 지난 재료를 가진 유저리스트 조회에 실패했습니다."),

    // [GET] /fridges/recipe
    NO_INGREDIENT_THAT_MATCH_THE_RECIPE(3058, "레시피와 일치하는 재료가 없습니다."),
    FAILED_TO_GET_FRIDGE_LIST(3059, "냉장고 재료 조회에 실패했습니다."),
    FAILED_TO_GET_RECIPE_INFO_LIST(3060, "레시피 조회에 실패했습니다."),
    FAILED_TO_GET_RECIPE_INGREDIENTS_LIST(3061, "레시피 재료 조회에 실패했습니다."),

    // [POST] /fridges/notification
    EMPTY_USER_LIST(3062, "유저리스트가 비었습니다."),
    PUSH_NOTIFICATION_ERROR(3063, "푸시알림에 실패했습니다."),

    // [GET] /recipes/best-keyword
    FAILE_TO_GET_BEST_KEYWORD(3064, "인기검색어 조회에 실패했습니다."),


    // [DELETE] /fridges/basket?ingredient=
    FAILED_TO_GET_FRIDGE_BASKET(3065, "냉장고 바구니 조회에 실패했습니다."),
    FAILED_TO_DELETE_FRIDGE_BASKET(3066, "냉장고 바구니 삭제에 실패했습니다."),

    // [PATCH] /fridge/basket
    FAILED_TO_PATCH_FRIDGE_BASKET_INGREDIENTS(3067, "냉장고 바구니 재료 수정에 실패했습니다."),

    FAILED_TO_RETREIVE_FRIDGE_BY_NAME(3068, "재료명으로 냉장고 조회에 실패했습니다."),

    // [PATCH] /fcm/token
    FAILED_TO_PATCH_FCM_TOKEN(3072, "fcm 토큰 수정에 실패했습니다."),
    // 코드번호 3074부터 사용

    // [GET] /app/version
    FAILED_TO_GET_APP_VERSION(3073, "앱 버전 조회에 실패했습니다."),

    // [GET] /notice
    FAILED_TO_GET_NOTICE(3074, "공지 조회에 실패했습니다."),
    NOT_FOUND_NOTICE(3075, "공지가 존재하지 않습니다."),

    /**
     * 4000 : Database, Server 오류
     */
    DATABASE_ERROR(4000, "데이터베이스 연결에 실패하였습니다."),
    SERVER_ERROR(4001, "서버와의 연결에 실패하였습니다."),
    DATE_PARSE_ERROR(4002, "Date형 변환에 실패하였습니다."),

    PASSWORD_ENCRYPTION_ERROR(4011, "비밀번호 암호화에 실패하였습니다."),
    PASSWORD_DECRYPTION_ERROR(4012, "비밀번호 복호화에 실패하였습니다."),

    //소셜 사용자 정보 접근
    WRONG_URL(4021, "API URL이 잘못되었습니다."),
    FAILED_TO_CONNECT(4022, "연결이 실패했습니다."),
    FAILED_TO_READ_RESPONSE(4023, "API 응답을 읽는데 실패했습니다."),
    FAILED_TO_PARSE(4024, "JSON 파싱에 실패했습니다."),
    FORBIDDEN_ACCESS(4025, "접근 권한이 없습니다."),
    FAILED_TO_URL_ENCODER(4026, "검색어 인코딩에 실패했습니다."),
    FAILED_TO_CRAWLING(4027, "크롤링에 실패했습니다."),

    UNKNOWN_EXCEPTION(9999,"알수 없는 에러가 발생하였습니다.");

    private final int code;
    private final String message;

    public String getMessage(Object... arg) {
        return String.format(message, arg);
    }
}