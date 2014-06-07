# Grails User Tag Plugin

This plugin lets individual users tag domain instances.
It is inspired by the **taggable** plugin and adds support for individual (user) tags and multi-tenancy.

## UserTagService

    // Joe is an application user and Person with id 42 is an important friend to Joe.
    def person = Person.get(42)
    userTagService.tag(person, "friend", "joe")
    userTagService.tag(person, "vip", "joe")
    assert userTagService.getTags(person, "joe").size() == 2
    assert userTagService.isTagged(person, "friend")
    assert userTagService.isTagged(person, "friend", "joe")
    assert ! userTagService.isTagged(person, "monkey", "joe")

## Domain methods

The following methods are added to all domain classes that have a static *taggable* property:

    class MyDomain {
        ...
        static taggable = true
    }

### Att tags to a domain instance

    Object addUserTag(String tagName, String username, Long tenant = null)

### Remove tags from a domain instance

    boolean removeUserTag(String tagName, String username, Long tenant = null)

### List tags on a domain instance

    List getUserTags(String username = null, Long tenant = null)

### Check if a domain instance is tagged

    List isUserTagged(String tagName, String username, Long tenant = null)

Using the same code examples as for **UserTagService** above but with domain methods looks like this:

    def person = Person.get(42)
    person.addUserTag "friend", "joe"
    person.addUserTag "vip", "joe"
    assert person.getUserTags("joe").size() == 2
    assert person.isTagged("friend")
    assert person.isTagged("friend", "joe")
    assert ! person.isTagged("monkey", "joe")