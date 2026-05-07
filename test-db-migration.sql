-- =====================================================================
-- 테스트 DB 한 번 실행 — 검색 관련 스키마 정비
-- 대상: jdbc:mysql://recipe-240706.../RecipeStorageTest
-- =====================================================================
-- 적용 후 깨지던 16개 테스트가 통과하게 됩니다.
-- 각 ALTER 는 idempotent 가 아니므로 이미 적용된 부분이 있으면 해당 줄만 주석 처리하고 재실행하세요.
-- 운영 DB 에는 SEARCH_NORI_MIGRATION.md / RECOMMENDED_SEARCH_MIGRATION.md 의 절차를 그대로 따르세요.
-- =====================================================================

-- 1. searchTokens 컬럼 (4개 테이블) — 이미 있으면 스킵
ALTER TABLE Recipe           ADD COLUMN searchTokens TEXT;
ALTER TABLE RecipeIngredient ADD COLUMN searchTokens VARCHAR(128);
ALTER TABLE BlogRecipe       ADD COLUMN searchTokens TEXT;
ALTER TABLE YoutubeRecipe    ADD COLUMN searchTokens TEXT;

-- 2. FULLTEXT 인덱스 (default parser, ngram 미사용 — Java 측에서 토큰화 후 공백 구분 저장)
ALTER TABLE Recipe           ADD FULLTEXT INDEX ft_recipe_search    (searchTokens);
ALTER TABLE RecipeIngredient ADD FULLTEXT INDEX ft_recipe_ing_search(searchTokens);
ALTER TABLE BlogRecipe       ADD FULLTEXT INDEX ft_blog_search      (searchTokens);
ALTER TABLE YoutubeRecipe    ADD FULLTEXT INDEX ft_youtube_search   (searchTokens);

-- 3. IngredientSynonym 테이블 (운영 INSERT 전 단계 — 테스트엔 데이터 필요 없음)
CREATE TABLE IngredientSynonym (
    synonymId BIGINT PRIMARY KEY AUTO_INCREMENT,
    groupId   BIGINT       NOT NULL,
    name      VARCHAR(64)  NOT NULL,
    UNIQUE KEY uk_synonym_name (name),
    KEY        idx_synonym_group (groupId)
);

-- =====================================================================
-- 검증
-- =====================================================================
-- SHOW CREATE TABLE RecipeIngredient;          -- ft_recipe_ing_search 보이는지
-- SELECT COUNT(*) FROM IngredientSynonym;      -- 0 정상
