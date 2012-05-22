import org.codehaus.groovy.grails.commons.GrailsClassUtils

/*
* Copyright 2012 Goran Ehrsson.
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*     http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
* under the License.
*/
class UserTagGrailsPlugin {
    // the plugin version
    def version = "0.4"
    // the version or versions of Grails the plugin is designed for
    def grailsVersion = "1.3.1 > *"
    // the other plugins this plugin depends on
    def dependsOn = [:]
    // resources that are excluded from plugin packaging
    def pluginExcludes = [
            "grails-app/domain/test/TestEntity.groovy",
            "grails-app/views/error.gsp"
    ]

    def title = "User Tag Plugin" // Headline display name of the plugin
    def author = "Goran Ehrsson"
    def authorEmail = "goran@technipelago.se"
    def description = '''\
This plugin let users put private tags on domain instances.
'''

    // URL to the plugin's documentation
    def documentation = "http://grails.org/plugin/user-tag"

    // Extra (optional) plugin metadata

    // License: one of 'APACHE', 'GPL2', 'GPL3'
    def license = "APACHE"

    // Details of company behind the plugin (if there is one)
    def organization = [name: "Technipelago AB", url: "http://www.technipelago.se/"]

    // Any additional developers beyond the author specified above.
    //    def developers = [ [ name: "Joe Bloggs", email: "joe@bloggs.net" ]]

    // Location of the plugin's issue tracker.
    def issueManagement = [system: "GITHUB", url: "https://github.com/goeh/grails-user-tag/issues"]

    // Online location of the plugin's browseable source code.
    def scm = [url: "https://github.com/goeh/grails-user-tag"]

    def observe = ["domain"]

    def doWithDynamicMethods = { ctx ->
        def userTagService = ctx.getBean("userTagService")
        for (domainClass in application.domainClasses) {
            def taggableProperty = getTaggableProperty(domainClass)
            if (taggableProperty) {
                addDomainMethods(domainClass.clazz.metaClass, userTagService)
            }
        }
    }

    def onChange = { event ->
        def ctx = event.ctx
        if (event.source && ctx && event.application) {
            def service = ctx.getBean('userTagService')
            // enhance domain classes with taggable property
            if ((event.source instanceof Class) && application.isDomainClass(event.source)) {
                def domainClass = application.getDomainClass(event.source.name)
                if (getTaggableProperty(domainClass)) {
                    addDomainMethods(domainClass.metaClass, service)
                }
            }
        }
    }

    private void addDomainMethods(MetaClass mc, def userTagService) {
        mc.addUserTag = { String tagName, String username, Long tenant = null ->
            userTagService.tag(delegate, tagName, username, tenant)
        }
        mc.removeUserTag = { String tagName, String username, Long tenant = null ->
            userTagService.untag(delegate, tagName, username, tenant)
        }
        mc.getUserTags = { String username = null, Long tenant = null ->
            userTagService.getTags(delegate, username, tenant)
        }
        mc.isUserTagged = { String tagName, String username, Long tenant = null ->
            userTagService.isTagged(delegate, tagName, username, tenant)
        }
    }

    public static final String TAGGABLE_PROPERTY_NAME = "taggable";

    private getTaggableProperty(domainClass) {
        GrailsClassUtils.getStaticPropertyValue(domainClass.clazz, TAGGABLE_PROPERTY_NAME)
    }
}
