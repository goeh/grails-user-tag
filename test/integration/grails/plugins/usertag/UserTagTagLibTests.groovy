package grails.plugins.usertag

import grails.test.GroovyPagesTestCase
import test.TestEntity

/**
 * Integration test for UserTagTagLib
 */
class UserTagTagLibTests extends GroovyPagesTestCase {

    def userTagService

    void testIsTagged() {
        def m = new TestEntity(name: "Joe Average", age: 40).save(failOnError: true, flush: true)

        userTagService.tag(m, "test", "peter")
        userTagService.tag(m, "test", "mary")

        def template = '<usertag:isTagged bean="\${m}" tag="test">YES</usertag:isTagged>'
        assert applyTemplate(template, [m: m]) == 'YES'

        template = '<usertag:isTagged bean="\${m}" tag="test" username="mary">YES</usertag:isTagged>'
        assert applyTemplate(template, [m: m]) == 'YES'

        template = '<usertag:isTagged bean="\${m}" tag="test" username="john">YES</usertag:isTagged>'
        assert applyTemplate(template, [m: m]) == ''
    }

    void testIsNotTagged() {
        def m = new TestEntity(name: "Joe Average", age: 40).save(failOnError: true, flush: true)

        userTagService.tag(m, "foo", "peter")
        userTagService.tag(m, "bar", "mary")

        def template = '<usertag:isNotTagged bean="\${m}" tag="foo" username="peter">add foo</usertag:isNotTagged>'
        assert applyTemplate(template, [m: m]) == ''

        template = '<usertag:isNotTagged bean="\${m}" tag="bar" username="peter">add bar</usertag:isNotTagged>'
        assert applyTemplate(template, [m: m]) == 'add bar'

        template = '<usertag:isNotTagged bean="\${m}" tag="bar">add bar</usertag:isNotTagged>'
        assert applyTemplate(template, [m: m]) == ''
    }

    void testEachTag() {
        def m = new TestEntity(name: "Joe Average", age: 40).save(failOnError: true, flush: true)

        userTagService.tag(m, "test", "peter")
        userTagService.tag(m, "vip", "mary")
        userTagService.tag(m, "friend", "mary")

        def template = '<usertag:eachTag bean="\${m}" username="mary">\${it}+</usertag:eachTag>'
        assert applyTemplate(template, [m: m]) == 'friend+vip+'
    }

    void testListTagged() {
        userTagService.tag(new TestEntity(name: "Joe Average", age: 40).save(failOnError: true, flush: true), "parent", "liza")
        userTagService.tag(new TestEntity(name: "Liza Average", age: 37).save(failOnError: true, flush: true), "parent", "joe")
        userTagService.tag(new TestEntity(name: "Josh Average", age: 19).save(failOnError: true, flush: true), "child", "joe")
        userTagService.tag(new TestEntity(name: "Mary Average", age: 16).save(failOnError: true, flush: true), "child", "liza")


        def template = '<usertag:eachTagged tag="parent">\${it}+</usertag:eachTagged>'
        def result = applyTemplate(template)
        assert result == 'Joe Average+Liza Average+' || result == '+Liza Average+Joe Average'

        template = '<usertag:eachTagged tag="child" username="joe">\${it}+</usertag:eachTagged>'
        assert applyTemplate(template) == 'Josh Average+'

        template = '<usertag:eachTagged tag="child" username="liza">\${it}+</usertag:eachTagged>'
        assert applyTemplate(template) == 'Mary Average+'

        template = '<usertag:eachTagged type="' + TestEntity.name + '" tag="child">\${it}+</usertag:eachTagged>'
        result = applyTemplate(template)
        assert result == 'Josh Average+Mary Average+' || result == 'Mary Average+Josh Average+'
    }

}
