# Grails User Tag Plugin

This plugin lets individual users tag domain instances.
It is inspired by the **taggable** plugin and adds support for individual (user) tags and multi-tenancy.

## Example

    // Joe is an application user and Person with id 42 is an important friend to Joe.
    def person = Person.get(42)
    userTagService.tag(person, "friend", "joe")
    userTagService.tag(person, "vip", "joe")
    assert userTagService.getTags(person, "joe").size() == 2
    assert userTagService.isTagged(person, "friend")
    assert userTagService.isTagged(person, "friend", "joe")
    assert ! userTagService.isTagged(person, "monkey", "joe")

The following methods are added to all domain classes that have a static *taggable* property:

    addUserTag(String tagName, String username, Long tenant = null)

    removeUserTag(String tagName, String username, Long tenant = null)

    getUserTags(String username = null, Long tenant = null)

    isUserTagged(String tagName, String username, Long tenant = null)
