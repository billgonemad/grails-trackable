package grails.plugins.trackable

import grails.plugin.springsecurity.SpringSecurityService

class TrackableTagLib {
    static namespace = "trackable"
//    static defaultEncodeAs = [taglib:'html']

    SpringSecurityService springSecurityService

    /**
     *
     */
    Closure trackedForm = { Map attrs->
        if (!springSecurityService.isLoggedIn()) {
            return
        }

        String doneLabel = attrs.get('doneLabel') ?: 'Done'
        String notDoneLabel = attrs.get('notDoneLabel') ?: 'Not Done'
        Trackable trackable = attrs.get('bean') as Trackable
        def user = springSecurityService.currentUser
        UserTracked userTracked = UserTracked.lookup(user.id, user.class.name, trackable.id, trackable.class.name)

        out << render(template: '/trackable/widget', model: [notDoneLabel: notDoneLabel, doneLabel: doneLabel, userTracked: userTracked, trackable: trackable])
    }
}
