#Grails User Tag Plugin

This plugin lets individual users tag domain instances.

    // Joe is a system user and Person with id 42 is an important friend to Joe.
    def person = Person.get(42)
    userTagService.tag(person, "friend", "joe")
    userTagService.tag(person, "vip", "joe")
    assert userTagService.getTags(person, "joe").size() == 2

> No methods are added to domain classes (yet). I'm not convinced that it's
> a good idea to add dozens of methods to all domain and/or controller classes.
> 
> But maybe there will be one "userTag" method added to all domain instances
> in a future version of this plugin.
