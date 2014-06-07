package grails.plugins.usertag

import test.TestEntity

class DynamicMethodTests extends GroovyTestCase {

    void testAddTag() {
        def m = new TestEntity(name: "Joe Average", age: 40).save(failOnError: true, flush: true)

        m.addUserTag("test", "peter")
        m.addUserTag("test", "mary")

        assert m.isUserTagged("test", "peter")
        assert m.isUserTagged("test", "mary")
        assert !m.isUserTagged("test", "john")
    }

    void testRemoveTag() {
        def m = new TestEntity(name: "Joe Average", age: 40).save(failOnError: true, flush: true)

        m.addUserTag("test", "peter")
        m.addUserTag("test", "mary")

        assert m.isUserTagged("test", "peter")
        assert m.isUserTagged("test", "mary")
        assert !m.isUserTagged("test", "john")
        m.removeUserTag("test", "mary")
        assert !m.isUserTagged("test", "mary")
    }

    void testListTags() {
        def m = new TestEntity(name: "Joe Average", age: 40).save(failOnError: true, flush: true)

        m.addUserTag("test", "peter")
        m.addUserTag("test", "mary")

        assert m.isUserTagged("test", "peter")
        assert m.isUserTagged("test", "mary")
        assert !m.isUserTagged("test", "john")
        m.removeUserTag("test", "mary")
        assert !m.isUserTagged("test", "mary")

        assert m.addUserTag("foo", "peter")
        assert m.addUserTag("bar", "john")

        def result = m.getUserTags("peter")
        assert result.size() == 2
        assert result.find {it == "test"}
        assert result.find {it == "foo"}
        assert !result.find {it == "bar"}

        result = m.getUserTags("john")
        assert result.size() == 1
        assert result.find {it == "bar"}

        result = m.getUserTags("mary")
        assert result.size() == 0

        result = m.getUserTags()
        assert result.size() == 3
        assert result.find {it.username == "peter"}
        assert result.find {it.username == "john"}
        assert !result.find {it.username == "mary"}
    }
}
