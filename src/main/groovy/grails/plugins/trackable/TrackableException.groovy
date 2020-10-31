package grails.plugins.trackable

import groovy.transform.CompileStatic

@CompileStatic
class TrackableException extends RuntimeException {
    TrackableException(String message) {
        super(message)
    }
}
