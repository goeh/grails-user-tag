package grails.plugins.usertag

import test.TestEntity

class UserTagServiceTests extends GroovyTestCase {

    def userTagService

    void testAddTag() {
        def m = new TestEntity(name: "Joe Average", age: 40).save(failOnError: true, flush: true)

        userTagService.tag(m, "test", "peter")
        userTagService.tag(m, "test", "mary")

        assert userTagService.isTagged(m, "test", "peter")
        assert userTagService.isTagged(m, "test", "mary")
        assert !userTagService.isTagged(m, "test", "john")
    }

    void testRemoveTag() {
        def m = new TestEntity(name: "Joe Average", age: 40).save(failOnError: true, flush: true)

        userTagService.tag(m, "test", "peter")
        userTagService.tag(m, "test", "mary")

        assert userTagService.isTagged(m, "test", "peter")
        assert userTagService.isTagged(m, "test", "mary")
        assert !userTagService.isTagged(m, "test", "john")
        userTagService.untag(m, "test", "mary")
        assert !userTagService.isTagged(m, "test", "mary")
    }

    void testListTags() {
        def m = new TestEntity(name: "Joe Average", age: 40).save(failOnError: true, flush: true)

        userTagService.tag(m, "test", "peter")
        userTagService.tag(m, "test", "mary")

        assert userTagService.isTagged(m, "test", "peter")
        assert userTagService.isTagged(m, "test", "mary")
        assert !userTagService.isTagged(m, "test", "john")
        userTagService.untag(m, "test", "mary")
        assert !userTagService.isTagged(m, "test", "mary")

        assert userTagService.tag(m, "foo", "peter")
        assert userTagService.tag(m, "bar", "john")

        def result = userTagService.getTags(m, "peter")
        assert result.size() == 2
        assert result.find {it == "test"}
        assert result.find {it == "foo"}
        assert !result.find {it == "bar"}

        result = userTagService.getTags(m, "john")
        assert result.size() == 1
        assert result.find {it == "bar"}

        result = userTagService.getTags(m, "mary")
        assert result.size() == 0

        result = userTagService.getTags(m)
        assert result.size() == 3
        assert result.find {it.username == "peter"}
        assert result.find {it.username == "john"}
        assert !result.find {it.username == "mary"}
    }

    void testFindByTag() {
        def m = new TestEntity(name: "Joe Average", age: 40).save(failOnError: true, flush: true)
        userTagService.tag(m, "friend", "peter")
        userTagService.tag(m, "friend", "mary")

        m = new TestEntity(name: "Liza Average", age: 38).save(failOnError: true, flush: true)
        userTagService.tag(m, "friend", "mary")

        m = new TestEntity(name: "Alan Gray", age: 52).save(failOnError: true, flush: true)
        userTagService.tag(m, "friend", "john")
        userTagService.tag(m, "vip", "john")

        m = new TestEntity(name: "Linda Svenson", age: 47).save(failOnError: true, flush: true)

        def result = userTagService.findTagged(TestEntity, "friend", "peter")
        assert result.size() == 1
        assert result[0].name == "Joe Average"

        assert userTagService.findTagged(TestEntity, "vip", "peter").size() == 0

        result = userTagService.findTagged(TestEntity, "friend", "mary")
        assert result.size() == 2
        assert result.find {it.name == "Joe Average"}
        assert result.find {it.name == "Liza Average"}

        assert userTagService.findTagged(TestEntity, "vip", "mary").size() == 0

        assert userTagService.findTagged(TestEntity, "vip").size() == 1
        assert userTagService.findTagged(TestEntity, "friend").size() == 3

        assert userTagService.findTagged(TestEntity, "none").size() == 0
    }

    void testFindAllByTag() {
        def m = new TestEntity(name: "Joe Average", age: 40).save(failOnError: true, flush: true)
        userTagService.tag(m, "friend", "peter")
        userTagService.tag(m, "friend", "mary")

        m = new TestEntity(name: "Liza Average", age: 38).save(failOnError: true, flush: true)
        userTagService.tag(m, "friend", "mary")

        m = new TestEntity(name: "Alan Gray", age: 52).save(failOnError: true, flush: true)
        userTagService.tag(m, "friend", "john")
        userTagService.tag(m, "vip", "john")

        new TestEntity(name: "Linda Svenson", age: 47).save(failOnError: true, flush: true)

        def result = userTagService.findAllTagged("friend", "peter")
        assert result.size() == 1
        assert result[0].name == "Joe Average"

        assert userTagService.findAllTagged("vip", "peter").size() == 0

        result = userTagService.findAllTagged("friend", "mary")
        assert result.size() == 2
        assert result.find {it.name == "Joe Average"}
        assert result.find {it.name == "Liza Average"}

        assert userTagService.findAllTagged("vip", "mary").size() == 0

        assert userTagService.findAllTagged("vip").size() == 1
        assert userTagService.findAllTagged("friend").size() == 3

        assert userTagService.findAllTagged("none").size() == 0
    }
}
