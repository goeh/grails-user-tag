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
    def version = "0.5"
    def grailsVersion = "1.3.1 > *"
    def dependsOn = [:]
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
    def documentation = "http://grails.org/plugin/user-tag"
    def license = "APACHE"
    def organization = [name: "Technipelago AB", url: "http://www.technipelago.se/"]
    def issueManagement = [system: "github", url: "https://github.com/goeh/grails-user-tag/issues"]
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
