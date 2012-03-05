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

    void testEachTag() {
        def m = new TestEntity(name: "Joe Average", age: 40).save(failOnError: true, flush: true)

        userTagService.tag(m, "test", "peter")
        userTagService.tag(m, "vip", "mary")
        userTagService.tag(m, "friend", "mary")

        def template = '<usertag:eachTag bean="\${m}" username="mary">\${it}+</usertag:eachTag>'
        assert applyTemplate(template, [m: m]) == 'friend+vip+'
    }

}
