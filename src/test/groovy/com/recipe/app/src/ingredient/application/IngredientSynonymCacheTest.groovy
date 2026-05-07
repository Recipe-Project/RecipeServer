package com.recipe.app.src.ingredient.application

import com.recipe.app.src.ingredient.domain.IngredientSynonym
import com.recipe.app.src.ingredient.infra.IngredientSynonymRepository
import spock.lang.Specification

class IngredientSynonymCacheTest extends Specification {

    private IngredientSynonymRepository repository = Mock()
    private IngredientSynonymCache cache = new IngredientSynonymCache(repository)

    def "동의어 그룹 안의 단어를 입력하면 그룹 전체를 반환한다"() {

        given:
        repository.findAll() >> [
                IngredientSynonym.builder().synonymId(1L).groupId(1L).name("새우").build(),
                IngredientSynonym.builder().synonymId(2L).groupId(1L).name("대하").build(),
                IngredientSynonym.builder().synonymId(3L).groupId(2L).name("계란").build(),
                IngredientSynonym.builder().synonymId(4L).groupId(2L).name("달걀").build(),
        ]
        cache.reload()

        expect:
        cache.expand([input]) as Set == expected as Set

        where:
        input  || expected
        "새우"   || ["새우", "대하"]
        "대하"   || ["새우", "대하"]
        "계란"   || ["계란", "달걀"]
        "달걀"   || ["계란", "달걀"]
    }

    def "3-way 그룹은 transitive 하게 모두 반환한다 (새싹채소-어린잎채소-무순)"() {

        given:
        repository.findAll() >> [
                IngredientSynonym.builder().synonymId(1L).groupId(10L).name("새싹채소").build(),
                IngredientSynonym.builder().synonymId(2L).groupId(10L).name("어린잎채소").build(),
                IngredientSynonym.builder().synonymId(3L).groupId(10L).name("무순").build(),
        ]
        cache.reload()

        expect: "그룹 안의 어떤 단어로 들어와도 셋 다 반환"
        cache.expand([input]) as Set == ["새싹채소", "어린잎채소", "무순"] as Set

        where:
        input << ["새싹채소", "어린잎채소", "무순"]
    }

    def "그룹에 없는 단어는 그 단어만 반환한다"() {

        given:
        repository.findAll() >> [
                IngredientSynonym.builder().synonymId(1L).groupId(1L).name("새우").build(),
                IngredientSynonym.builder().synonymId(2L).groupId(1L).name("대하").build(),
        ]
        cache.reload()

        expect:
        cache.expand(["감자"]) == ["감자"] as Set
    }

    def "여러 단어 입력 시 모두 확장해서 합친다"() {

        given:
        repository.findAll() >> [
                IngredientSynonym.builder().synonymId(1L).groupId(1L).name("새우").build(),
                IngredientSynonym.builder().synonymId(2L).groupId(1L).name("대하").build(),
                IngredientSynonym.builder().synonymId(3L).groupId(2L).name("계란").build(),
                IngredientSynonym.builder().synonymId(4L).groupId(2L).name("달걀").build(),
        ]
        cache.reload()

        expect:
        cache.expand(["새우", "계란", "감자"]) as Set == ["새우", "대하", "계란", "달걀", "감자"] as Set
    }

    def "null/빈 입력은 빈 Set 을 반환한다"() {

        given:
        repository.findAll() >> []
        cache.reload()

        expect:
        cache.expand(input).isEmpty()

        where:
        input << [null, []]
    }
}
