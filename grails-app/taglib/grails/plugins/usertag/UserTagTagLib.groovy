package grails.plugins.usertag

/**
 * Created by IntelliJ IDEA.
 * User: goran
 * Date: 2012-03-05
 * Time: 12:57
 * To change this template use File | Settings | File Templates.
 */
class UserTagTagLib {

    static namespace = "usertag"

    def userTagService

    def isTagged = {attrs, body ->
        if (!attrs.bean) {
            throwTagError("Tag [isTagged] is missing required attribute [bean]")
        }
        if (!attrs.tag) {
            throwTagError("Tag [isTagged] is missing required attribute [tag]")
        }
        def tenant = attrs.tenant ? Long.valueOf(attrs.tenant.toString()) : null
        if (userTagService.isTagged(attrs.bean, attrs.tag, attrs.username, tenant)) {
            out << body()
        }
    }

    def eachTag = {attrs, body ->
        if (!attrs.bean) {
            throwTagError("Tag [eachTag] is missing required attribute [bean]")
        }
        def tenant = attrs.tenant ? Long.valueOf(attrs.tenant.toString()) : null
        def list = userTagService.getTags(attrs.bean, attrs.username, tenant)
        list.eachWithIndex {s, i ->
            def map = [(attrs.var ?: 'it'): s]
            if (attrs.status) {
                map[attrs.status] = i
            }
            out << body(map)
        }
    }
}
