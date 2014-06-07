/*
 * Copyright (c) 2014 Goran Ehrsson.
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
 */
package grails.plugins.usertag

class UserTag {

    Long tenantId
    String username
    String taggedEntity
    Long taggedId
    String taggedValue

    static constraints = {
        tenantId(nullable:true)
        username(maxSize:40, blank:false)
        taggedEntity(maxSize:255, blank:false)
        taggedId()
        taggedValue(maxSize:80, blank:false, unique:['tenantId','username','taggedEntity','taggedId'])
    }

    static mapping = {
        sort 'taggedValue'
        cache 'nonstrict-read-write'
    }

    @Override
    String toString() {
        taggedValue
    }
}
