package it.dturek.cloudhosting.util

import spock.lang.Specification

class FileUtilSpockTest extends Specification {


    def "GetExtension"() {
        when:
        def actual = FileUtil.getExtension(filename)
        then:
        expected == actual
        where:
        filename  | expected
        "foo.bar" | "bar"
        "empty"   | ""
        "a.b.c.d" | "d"
    }

    def "HasValidExtension"() {
        when:
        def actual = FileUtil.hasValidExtension(filename, whitelist as String[])
        then:
        expected == actual
        where:
        filename    | whitelist       | expected
        "image.jpg" | ["jpg", "png"]  | true
        "image.jpg" | ["jpeg", "png"] | false
    }

    def "HasValidContentType"() {
        when:
        def actual = FileUtil.hasValidContentType(contentType, whitelist as String[])
        then:
        expected == actual
        where:
        contentType | whitelist                   | expected
        "image/jpg" | ["image/jpg", "image/png"]  | true
        "image/jpg" | ["image/jpeg", "image/png"] | false
    }

    def "getFriendlySize"() {
        when:
        def actual = FileUtil.getFriendlySize(size as long)
        then:
        expected == actual
        where:
        size        | expected
        317819      | "317,8 kB"
        3178190     | "3,2 MB"
        31781900    | "31,8 MB"
        317819000   | "317,8 MB"
        10485760    | "10,5 MB"
        2372229748  | "2,4 GB"
        10737418240 | "10,7 GB"
        10739418244 | "10,7 GB"
    }

    def "createPathToFile"() {
        when:
        def actual = FileUtil.createPathToFile(id)
        then:
        expected == actual
        where:
        id    | expected
        1     | "/1"
        12    | "/1"
        19    | "/1"
        123   | "/1/2"
        125   | "/1/2"
        177   | "/1/7"
        1234  | "/1/2/3"
        12345 | "/1/2/3/4"
    }
}
