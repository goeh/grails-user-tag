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
package grails.plugins.usertag

import org.codehaus.groovy.grails.orm.hibernate.cfg.GrailsHibernateUtil

/**
 * This is the main service for managing user tags.
 */
class UserTagService {

    static transactional = true

    def grailsApplication

    /**
     * Add a tag to a domain instance.
     * @param domainInstance the instance to tag
     * @param tag tag name
     * @param username (optional) username
     * @param tenant only used in multi-tenant environments
     * @return UserTag instance
     * @todo really return the UserTag instance!? Why?
     */
    def tag(def domainInstance, String tag, String username, Long tenant = null) {
        domainInstance = GrailsHibernateUtil.unwrapIfProxy(domainInstance)
        new UserTag(tenantId: tenant, taggedEntity: domainInstance.class.name, taggedId: domainInstance.ident(), username: username, taggedValue: tag).save(failOnError: true)
    }

    /**
     * Remove a tag form a domain instance.
     * @param domainInstance instance to remove tag from
     * @param tag the tag name to remove
     * @param username (optional) username that added the tag
     * @param tenant only used in multi-tenant environaments
     * @return true if tag was found and deleted, false if tag was not found
     */
    boolean untag(def domainInstance, String tag, String username, Long tenant = null) {
        domainInstance = GrailsHibernateUtil.unwrapIfProxy(domainInstance)
        def userTag = UserTag.createCriteria().get() {
            if (tenant != null) {
                eq('tenantId', tenant)
            }
            if (username) {
                eq('username', username)
            }
            eq('taggedEntity', domainInstance.class.name)
            eq('taggedId', domainInstance.ident())
            eq('taggedValue', tag)
        }
        if (userTag) {
            userTag.delete()
            return true
        }
        return false
    }

    /**
     * List all tags on a domain instance added by a specific user.
     * @param domainInstance the domain instance to check
     * @param username owner of tags or null for all tags
     * @param tenant only used in multi-tenant environments
     * @return list of tag names if username was specified or a Map [username: "username", value: "tagname"] if no username was specified
     */
    List getTags(def domainInstance, String username = null, Long tenant = null) {
        domainInstance = GrailsHibernateUtil.unwrapIfProxy(domainInstance)
        UserTag.createCriteria().list() {
            if (tenant != null) {
                eq('tenantId', tenant)
            }
            if (username) {
                eq('username', username)
            }
            eq('taggedEntity', domainInstance.class.name)
            eq('taggedId', domainInstance.ident())

            order('username', 'asc')
            order('taggedValue', 'asc')
            cache true
        }.collect {username ? it.taggedValue : [username: it.username, value: it.taggedValue]}
    }

    /**
     * Check if a domain instance is tagged with a specific value.
     * @param domainInstance the domain instance to check
     * @param tag tag name
     * @param username (optional) user name
     * @param tenant only used in multi-tenant environments
     * @return true if the domain is tagged
     */
    boolean isTagged(def domainInstance, String tag, String username, Long tenant = null) {
        domainInstance = GrailsHibernateUtil.unwrapIfProxy(domainInstance)
        UserTag.createCriteria().count() {
            if (tenant != null) {
                eq('tenantId', tenant)
            }
            if (username) {
                eq('username', username)
            }
            eq('taggedEntity', domainInstance.class.name)
            eq('taggedId', domainInstance.ident())
            eq('taggedValue', tag)
            cache true
        } > 0
    }

    /**
     * Find domain instances tagged with a specific tag (optionally by a specific user).
     * @param domainClass the domain class to search and return
     * @param tag tag name
     * @param username username or null for all users
     * @param tenant only used in multi-tenant environment
     * @return list of matched domain instances (in unpredictable order)
     */
    List findTagged(Class domainClass, String tag, String username = null, Long tenant = null) {
        UserTag.createCriteria().list() {
            projections {
                distinct('taggedId')
            }
            if (tenant != null) {
                eq('tenantId', tenant)
            }
            if (username) {
                eq('username', username)
            }
            eq('taggedEntity', domainClass.name)
            if (tag) {
                eq('taggedValue', tag)
            }
        }.collect {domainClass.get(it)}
    }

    /**
     * Find all domain instances tagged with a specific tag (optionally by a specific user).
     * This method can potentially return all type of domain classes.
     * @see #findTagged
     * @param tag tag name
     * @param username username or null for all users
     * @param tenant only used in multi-tenant environment
     * @return list of matched domain instances (sorted by domain class name but instances in unpredictable order)
     */
    List findAllTagged(String tag, String username = null, Long tenant = null) {
        def domainCache = [:]
        UserTag.createCriteria().list() {
            projections {
                distinct(['taggedEntity', 'taggedId'])
            }
            if (tenant != null) {
                eq('tenantId', tenant)
            }
            if (username) {
                eq('username', username)
            }
            if (tag) {
                eq('taggedValue', tag)
            }
            order('taggedEntity', 'asc')
            order('taggedId', 'asc')
        }.collect {
            def className = it[0]
            def domainClass = domainCache[className]
            if(! domainClass) {
                domainClass = domainCache[className] = getDomainClass(className)
            }
            domainClass.get(it[1])
        }
    }

    /**
     * Find a domain class in application context.
     *
     * @param name domain property name i.e. "homeAddress" or class name "com.mycompany.HomeAddress"
     */
    protected Class getDomainClass(String name) {
        def applicationContext = grailsApplication.mainContext
        if(applicationContext.containsBean(name)) {
            return applicationContext.getBean(name).getClass()
        }
        grailsApplication.classLoader.loadClass(name)
    }
}
