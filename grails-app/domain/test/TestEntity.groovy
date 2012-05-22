package test

class TestEntity {

    String name
    Integer age

    static constraints = {
        name(maxSize: 100)
        age(nullable:true)
    }

    static taggable = true

    String toString() {
        name
    }
}
