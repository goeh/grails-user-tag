package grails.plugins.usertag

/**
 * Tag library to interact with User Tags.
 */
class UserTagTagLib {

    static namespace = "usertag"

    def userTagService

    /**
     * Render tag body if the specified domain instance is tagged with the specified tag.
     *
     * @attr bean REQUIRED The domain instance to check
     * @attr tag REQUIRED name of tag
     * @attr username check if the domain instance is tagged by a specific user
     * @attr tenant optional tenant ID in a multi-tenant environment
     */
    def isTagged = { attrs, body ->
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

    /**
     * Render tag body if the specified domain instance is NOT tagged with the specified tag.
     *
     * @attr bean REQUIRED The domain instance to check
     * @attr tag REQUIRED name of tag
     * @attr username check if the domain instance is NOT tagged by a specific user
     * @attr tenant optional tenant ID in a multi-tenant environment
     */
    def isNotTagged = { attrs, body ->
        if (!attrs.bean) {
            throwTagError("Tag [isTagged] is missing required attribute [bean]")
        }
        if (!attrs.tag) {
            throwTagError("Tag [isTagged] is missing required attribute [tag]")
        }
        def tenant = attrs.tenant ? Long.valueOf(attrs.tenant.toString()) : null
        if (!userTagService.isTagged(attrs.bean, attrs.tag, attrs.username, tenant)) {
            out << body()
        }
    }

    /**
     * Iterate over all tags on a domain instance and render tag body for each tag.
     *
     * @attr bean REQUIRED The tagged domain instance
     * @attr username iterate only tags owned by a specific user
     * @attr tenant optional tenant ID in a multi-tenant environment
     * @attr var name of iteration variable (default "it")
     * @attr status name of iteration counter variable
     */
    def eachTag = { attrs, body ->
        if (!attrs.bean) {
            throwTagError("Tag [eachTag] is missing required attribute [bean]")
        }
        def tenant = attrs.tenant ? Long.valueOf(attrs.tenant.toString()) : null
        def list = userTagService.getTags(attrs.bean, attrs.username, tenant)
        list.eachWithIndex { s, i ->
            def map = [(attrs.var ?: 'it'): s]
            if (attrs.status) {
                map[attrs.status] = i
            }
            out << body(map)
        }
    }

    /**
     * Find domain instances tagged with a specific value, render tag body for each instance.
     *
     * @attr tag REQUIRED tag name
     * @attr type limit query to a specific domain class. Specifiy domain class or "property name" for domain class
     * @attr username limit query to instances tagged by a specific user
     * @attr tenant optional tenant ID in a multi-tenant environment
     * @attr var name of iteration variable (default "it")
     * @attr status name of iteration counter variable
     */
    def eachTagged = { attrs, body ->
        if (!attrs.tag) {
            throwTagError("Tag [eachTagged] is missing required attribute [tag]")
        }
        def clazz = attrs.type ? ((attrs.type instanceof Class) ? attrs.type : userTagService.getDomainClass(attrs.type.toString())) : null
        def tenant = attrs.tenant ? Long.valueOf(attrs.tenant.toString()) : null
        def list
        if (clazz) {
            list = userTagService.findTagged(clazz, attrs.tag, attrs.username, tenant)
        } else {
            list = userTagService.findAllTagged(attrs.tag, attrs.username, tenant)
        }
        list.eachWithIndex { s, i ->
            def map = [(attrs.var ?: 'it'): s]
            if (attrs.status) {
                map[attrs.status] = i
            }
            out << body(map)
        }
    }
}
