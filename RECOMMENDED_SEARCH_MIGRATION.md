# 추천 레시피 검색 + 재료 동의어 DB 이전 마이그레이션 가이드

## 배경

기존 추천 검색 (`findRecommendedRecipesByUserFridge`) 의 두 가지 한계:

1. **재료 매칭이 `ingredientName` exact equality** — 케이스/공백 차이 흡수 못함. 한 RecipeIngredient 가 "양파 1/4개" 같이 단어 + 단위 형태일 때 "양파" fridge 와 매치 안 됨.
2. **동의어 매핑이 Java `if/else` 체인** (`Ingredient.getIngredientNameWithSimilar()`) — 운영 중 동의어 추가 시 코드 변경 + 배포 필요. 14개 그룹 33개 단어가 엔티티 메서드 안에 박혀있던 상태.

이번 변경:
- 동의어를 **`IngredientSynonym` DB 테이블** + 부팅 시 캐시 로드로 분리
- 매칭을 `RecipeIngredient.searchTokens` (lowercase+trim 정규화) **FULLTEXT MATCH** 기반으로 전환
- 3-way 동의어 그룹 (새싹채소-어린잎채소-무순, 조개-조갯살-바지락) 의 transitive 매칭으로 통일

---

## 작업 완료 내역 (코드 측)

### 추가된 커밋

```
ed4e08f refactor: 추천 레시피 매칭을 RecipeIngredient.searchTokens FULLTEXT 기반으로 변경
1e19022 refactor: 재료 동의어를 Java if/else 에서 IngredientSynonym DB 테이블로 이전
5b931f3 test: 검색 Repository 통합 테스트의 키워드 인자를 SearchQuery 로 갱신 + 1글자 ExactToken 케이스 추가
```

### 핵심 설계 결정

| 결정 | 선택 | 이유 |
|---|---|---|
| 동의어 저장 | DB 테이블 (`IngredientSynonym`) | 운영 중 코드 배포 없이 추가 가능 |
| 동의어 모델 | `(groupId, name)` 페어 — 같은 groupId = 동의어 그룹 | 단순. 3-way 이상 자연스럽게 표현. 양방향/단방향 고민 불필요 |
| 매칭 transitive | 그룹 안 모든 단어가 서로 동의어 | 기존 if/else 의 비대칭 (어린잎채소→새싹채소만) 보다 자연스러움 |
| 캐시 전략 | `@PostConstruct` 부팅 시 1회 로드 (HashMap 기반) | 동의어 추가 빈도 낮음. TTL/무효화 매커니즘 불필요. DB 추가 시 인스턴스 재시작 필요 |
| 매칭 컬럼 | `RecipeIngredient.searchTokens` (lowercase + trim) | 케이스/공백 흡수. nori 미적용 (도메인 단어 보존) |
| 매칭 식 | `MATCH(searchTokens) AGAINST('... ... ...' IN BOOLEAN MODE)` (OR) | FULLTEXT 인덱스 사용. fridge 모든 동의어를 한 쿼리로 OR 매칭 |
| `hasInFridge` 시그니처 | `List<String>` → `Set<String> normalizedFridgeNames` | 정규화 1회 + 도메인 메서드 의도 명확화 |
| `calculateIngredientMatchRate` 시그니처 | 동일하게 `Set<String>` | 일관성 |
| 정규화 위치 | DTO (`RecipeDetailResponse`, `RecommendedRecipesResponse`) 진입부 | fridge 리스트 흐름 중 한 번만 정규화 |

### 추가된 / 변경된 파일

**신규**
- `src/main/java/com/recipe/app/src/ingredient/domain/IngredientSynonym.java` — 동의어 엔티티 (groupId + name)
- `src/main/java/com/recipe/app/src/ingredient/infra/IngredientSynonymRepository.java` — JpaRepository
- `src/main/java/com/recipe/app/src/ingredient/application/IngredientSynonymCache.java` — 부팅 시 로드 + `expand(Collection)` API
- `src/test/groovy/com/recipe/app/src/ingredient/application/IngredientSynonymCacheTest.groovy` — 동의어 그룹 / transitive / 미등록 / 빈 입력 케이스

**변경**
- `src/main/java/com/recipe/app/src/ingredient/domain/Ingredient.java` — `getIngredientNameWithSimilar()` 통째 제거 (54줄)
- `src/main/java/com/recipe/app/src/fridge/application/FridgeService.java` — `findIngredientNamesInFridge` 가 cache.expand 호출
- `src/main/java/com/recipe/app/src/recipe/domain/Recipe.java` — `calculateIngredientMatchRate(Set<String>)`. 빈 ingredients 처리
- `src/main/java/com/recipe/app/src/recipe/domain/RecipeIngredient.java` — `hasInFridge(Set<String>)` searchTokens 토큰 교집합 매칭. 생성자에서 searchTokens 즉시 채움 (단위 테스트 호환)
- `src/main/java/com/recipe/app/src/recipe/application/dto/RecipeDetailResponse.java` — 진입 시 fridge 이름 정규화 Set 변환
- `src/main/java/com/recipe/app/src/recipe/application/dto/RecommendedRecipesResponse.java` — 동일
- `src/main/java/com/recipe/app/src/recipe/infra/RecipeRepositoryImpl.java` — `findRecipesInFridge` 가 FULLTEXT MATCH OR 매칭
- `src/test/groovy/com/recipe/app/src/recipe/domain/RecipeIngredientTest.groovy` — Set 시그니처
- `src/test/groovy/com/recipe/app/src/recipe/domain/RecipeTest.groovy` — Set 시그니처
- `src/test/groovy/com/recipe/app/src/fridge/application/FridgeServiceTest.groovy` — IngredientSynonymCache mock 추가
- `src/test/groovy/com/recipe/app/src/recipe/infra/RecipeCustomRepositoryTest.groovy` — SearchQuery 시그니처 + ExactToken 케이스 추가
- `src/test/groovy/com/recipe/app/src/recipe/infra/blog/BlogRecipeCustomRepositoryTest.groovy` — 동일
- `src/test/groovy/com/recipe/app/src/recipe/infra/youtube/YoutubeRecipeCustomRepositoryTest.groovy` — 동일

### 자동 검증된 것
- `./gradlew compileJava compileTestGroovy` — 통과
- 단위 테스트
  - `IngredientSynonymCacheTest` — 동의어 그룹 매칭 / 3-way transitive / 미등록 / 빈 입력
  - `RecipeIngredientTest` — `hasInFridge(Set)` searchTokens 매칭
  - `RecipeTest` — `calculateIngredientMatchRate(Set)` 일치율 계산
- mock 기반 서비스 테스트
  - `FridgeServiceTest` — cache 주입 후 `findIngredientNamesInFridge` 동작
  - `RecipeSearchServiceTest` — 추천/상세 케이스 (matchRate 100/33 검증 포함)

### 변경 후 동의어 동작 비교

| 입력 | 기존 (if/else) | 새 (DB groupId) |
|---|---|---|
| 새싹채소 | [어린잎채소, 무순, 새싹채소] | [어린잎채소, 무순, 새싹채소] |
| 어린잎채소 | [새싹채소, 어린잎채소] (무순 X) | [새싹채소, 어린잎채소, **무순**] |
| 무순 | [새싹채소, 무순] (어린잎채소 X) | [새싹채소, **어린잎채소**, 무순] |
| 조갯살 | [조개, 조갯살] (바지락 X) | [조개, **바지락**, 조갯살] |
| 바지락 | [조개, 바지락] (조갯살 X) | [조개, **조갯살**, 바지락] |
| 새우 / 대하 | 양방향 OK | 양방향 OK (변동 없음) |
| 그 외 14 그룹 | 양방향 OK | 양방향 OK (변동 없음) |

→ 3-way 그룹 2개 (새싹채소·조개) 의 비대칭이 transitive 로 통일됨.

---

## 본인이 직접 해야 하는 것

### Step 1 — DB 스키마 + 초기 데이터

```sql
CREATE TABLE IngredientSynonym (
    synonymId BIGINT PRIMARY KEY AUTO_INCREMENT,
    groupId BIGINT NOT NULL,
    name VARCHAR(64) NOT NULL,
    UNIQUE KEY uk_synonym_name (name),
    KEY idx_synonym_group (groupId)
);

-- 기존 if/else 그대로 이전 (14 그룹 / 29 단어)
INSERT INTO IngredientSynonym (groupId, name) VALUES
  (1,  '새우'),     (1,  '대하'),
  (2,  '계란'),     (2,  '달걀'),
  (3,  '소고기'),   (3,  '쇠고기'),
  (4,  '후추'),     (4,  '후춧가루'),
  (5,  '간마늘'),   (5,  '다진마늘'),
  (6,  '새싹채소'), (6,  '어린잎채소'), (6, '무순'),
  (7,  '조개'),     (7,  '조갯살'),     (7, '바지락'),
  (8,  '케찹'),     (8,  '케첩'),
  (9,  '소면'),     (9,  '국수'),
  (10, '김치'),     (10, '김칫잎'),
  (11, '고춧가루'), (11, '고추가루'),
  (12, '올리브유'), (12, '올리브오일'),
  (13, '파스타'),   (13, '스파게티'),
  (14, '포도씨유'), (14, '식용유');
```

### Step 2 — 검증

#### 2-A. 부팅 로그
```
IngredientSynonymCache loaded - groups=14, words=29
```
이 라인이 `INFO` 레벨로 찍히는지 확인.

#### 2-B. 동의어 확장 동작
```sql
SELECT name, groupId FROM IngredientSynonym ORDER BY groupId, name;
```
14 그룹 29 row 가 정확히 들어왔는지.

#### 2-C. 추천 검색 동작
- 냉장고에 "어린잎채소" 만 등록 → 추천 호출 → "무순" 사용한 레시피도 매치되는지 (transitive 동작)
- 냉장고에 "양파" 등록 → 추천 호출 → 결과에 "양파" 들어간 레시피 + matchRate 가 정상 계산되는지
- 냉장고에 "Onion" 등록 (대문자) → 추천 호출 → "onion" / "Onion" / "ONION" 들어간 레시피 모두 매치되는지 (정규화 동작)

#### 2-D. 단위 테스트
```
./gradlew test
```
모두 통과 확인. 만약 `RecipeCustomRepositoryTest` / `BlogRecipeCustomRepositoryTest` / `YoutubeRecipeCustomRepositoryTest` 의 ExactToken 케이스가 깨지면 **테스트 DB 의 FULLTEXT 인덱스 + searchTokens 백필 적용 여부** 확인 (`SEARCH_NORI_MIGRATION.md` 참조).

---

### Step 3 — 운영 환경 적용

```
1. 운영 DB 에 Step 1 의 DDL + INSERT 적용
2. 코드 배포
3. Step 2 의 검증 절차 수행
4. 모니터링: matchRate 분포 / 추천 결과 변화 빈도
```

#### 롤백 시나리오

문제 발견 시:
- **코드 롤백**: 이번 시리즈의 첫 커밋 (`5b931f3`) 직전인 `5168081 feat: 1글자 검색어는 정확 매칭...` 으로 revert
- **DB 롤백 불필요**: `IngredientSynonym` 테이블이 남아있어도 무영향 (이전 코드는 안 읽음)

---

### Step 4 — 운영 중 동의어 추가/수정

새 동의어 그룹 추가:
```sql
-- 다음 사용 가능한 groupId 확인
SELECT MAX(groupId) FROM IngredientSynonym;

-- 새 그룹
INSERT INTO IngredientSynonym (groupId, name) VALUES
  (15, '아보카도'), (15, 'avocado');
```

기존 그룹에 단어 추가:
```sql
INSERT INTO IngredientSynonym (groupId, name) VALUES (1, '왕새우');
```

⚠️ **반영 절차**:
- 캐시는 부팅 시 1회 로드. DB 만 수정하면 적용 안 됨
- **인스턴스 재시작** 필요 (다운타임 약 1분)
- 또는 **수동 reload endpoint 추가** (선택, 운영 부담이 커지면 도입)

---

### Step 5 — 후속 검토 (선택)

#### Public 추천 API 의 동의어 확장 ✅ 완료

`findPublicRecommendedRecipesByIngredients` 도 사용자 냉장고 추천과 동일하게 `IngredientSynonymCache.expand` 후 FULLTEXT 매칭하도록 통일. matchRate 도 expanded ingredients 기반으로 계산.

- `RecipeSearchService` 에 `IngredientSynonymCache` 의존성 추가
- `findPublicRecommendedRecipesByIngredients`: `expand(input)` → `findRecipesInFridge(expanded)` → `RecommendedRecipesResponse.from(..., expanded, ...)`
- 테스트: `RecipeSearchServiceTest` 에 cache mock 주입 + Public 추천 케이스 2개 (expand 인자 전달 검증 / 빈 입력)

#### 수동 cache reload endpoint
운영자가 동의어 추가 후 재시작 없이 적용하려면:
```java
@PostMapping("/admin/ingredient-synonyms/reload")
public void reload() {
    ingredientSynonymCache.reload();
}
```
인증/권한 정책 결정 후 도입.

#### 사용자 정의 사전 (운영 데이터로 확장)
운영해보면서 자주 다른 표현으로 등록되는 재료명 패턴이 보이면 동의어 그룹 추가:

```sql
-- 운영 데이터에서 비슷한 재료명 패턴 찾기
SELECT ingredientName, COUNT(*)
FROM RecipeIngredient
GROUP BY ingredientName
ORDER BY COUNT(*) DESC
LIMIT 100;
```

상위 100개에서 변종 (예: "양파 1/2개" vs "양파" vs "어니언") 보이면 후보. 단 자유 입력 ingredientName 자체는 동의어 테이블이 매핑 못 하므로 (마스터 단위 매핑이라), 이 케이스는 클라이언트에서 정규화하거나 별도 자유텍스트 매핑 테이블 검토가 필요할 수 있음.

#### 디버그 코드 정리
세션 중 `JwtFilter.java` 에 추가된 임시 `System.out.println(jwtUtil.createAccessToken(23988L));` 라인은 운영 배포 전 제거 권장 (이번 작업 범위 밖이라 commit 안 함).

---

## 참고

- 동의어 그룹 모델은 transitive 가 항상 true. 비대칭이 필요한 케이스는 현재 모델로 표현 불가
- 캐시는 단순 in-memory `Map<String, Set<String>>`. Caffeine 등 별도 캐시 인프라 사용 안 함 (재시작 시 자동 새로 로드)
- `IngredientSynonym.name` 에 unique 제약 — 같은 단어가 여러 그룹에 못 속함. 도메인상 자연스러운 제약
