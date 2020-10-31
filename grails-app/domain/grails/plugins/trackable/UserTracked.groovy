package grails.plugins.trackable

import grails.compiler.GrailsCompileStatic
import groovy.transform.CompileDynamic

@GrailsCompileStatic
class UserTracked {
    Long userId
    String userClass
    Long trackedId
    String trackedClass

    static constraints = {
        userId(min: 0L)
        userClass(blank: false)
        trackedId(min: 0L)
        trackedClass(blank: false)
    }

    @CompileDynamic
    def getUser() {
        return getClass().classLoader.loadClass(userClass).get(userId)
    }

    @CompileDynamic
    def getTracked() {
        return getClass().classLoader.loadClass(trackedClass).get(trackedId)
    }

    static UserTracked lookup(Long uId, String uClass, Long tId, String tClass) {
        where {
            userId == uId && userClass == uClass && trackedId == tId && trackedClass == tClass
        }.get()
    }
}
