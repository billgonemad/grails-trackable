package com.demo

import grails.compiler.GrailsCompileStatic
import grails.plugins.trackable.Trackable

@GrailsCompileStatic
class Country implements Trackable {
    String name

    static constraints = {
    }
}
