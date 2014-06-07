# Grails Individual User Tags Plugin

This plugin lets individual users tag domain instances.
It is inspired by the **taggable** plugin and adds support for individual (user) tags and multi-tenancy.
This means that multiple users can tag the same domain instance with same or different tags.

## UserTagService

    // Joe is an application user and Person with id 42 is an important friend to Joe.
    def person = Person.get(42)
    userTagService.tag(person, "friend", "joe")
    userTagService.tag(person, "tennis", "joe")
    userTagService.tag(person, "friend", "mary")
    userTagService.tag(person, "golf", "mary")
    assert userTagService.getTags(person, "joe").size() == 2
    assert userTagService.isTagged(person, "friend")
    assert userTagService.isTagged(person, "friend", "joe")
    assert userTagService.isTagged(person, "friend", "mary")
    assert ! userTagService.isTagged(person, "monkey", "joe")
    assert userTagService.distinctTags(Person, "joe").size() == 2
    assert userTagService.distinctTags(Person, "joe") == ["friend", "tennis"]
    assert userTagService.distinctTags(Person, "mary") == ["friend", "golf"]
    assert userTagService.distinctTags(Person) == ["friend", "golf", "tennis]

## Domain methods

The following methods are added to all domain classes that have a static *taggable* property:

    class MyDomain {
        ...
        static taggable = true
    }

### Add tags to a domain instance

    Object addUserTag(String tagName, String username, Long tenant = null)

### Remove tags from a domain instance

    boolean removeUserTag(String tagName, String username, Long tenant = null)

### List tags on a domain instance

    List getUserTags(String username = null, Long tenant = null)

### Check if a domain instance is tagged

    List isUserTagged(String tagName, String username, Long tenant = null)

Using the same code examples as for *UserTagService* above but with domain methods looks like this:

    def person = Person.get(42)
    person.addUserTag("friend", "joe").addUserTag("vip", "joe")
    assert person.getUserTags("joe").size() == 2
    assert person.isTagged("friend")
    assert person.isTagged("friend", "joe")
    assert ! person.isTagged("monkey", "joe")

## GSP Tags

The following GSP tags are provided by the plugin:

### Check if domain instance is tagged

    <usertag:isTagged bean="${person}" tag="friend" username="${currentUser.username}">
       <p>This person is your friend</p>
    </usertag:isTagged>

### Check if domain instance is NOT tagged

    <usertag:isNotTagged bean="${person}" tag="friend" username="${currentUser.username}">
       <g:link action="addtag" params="${[id: person.id, tag: 'friend']}">Add as friend</g:link>
    </usertag:isNotTagged>

### List all tags on a domain instance

    <ul>
      <usertag:eachTag bean="${person}" var="tag">
         <li>${tag.encodeAsHTML()}</li>
      </usertag:eachTag>
    </ul>

### List all domain instances tagged with a specific tag

    <ul>
      <usertag:eachTagged tag="friend" var="friend">
         <li>${friend.encodeAsHTML()}</li>
      </usertag:eachTagged>
    </ul>