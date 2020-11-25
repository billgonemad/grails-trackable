package grails.plugins.trackable

import grails.plugin.springsecurity.SpringSecurityService

class TrackableTagLib {
    static namespace = "trackable"

    SpringSecurityService springSecurityService

    /**
     *
     */
    Closure trackedForm = { Map attrs->
        if (!springSecurityService.isLoggedIn()) {
            return
        }

        String widgetLabel = attrs.get('widgetLabel') ?: 'Tracker Status'
        String doneLabel = attrs.get('doneLabel') ?: 'Done'
        String notDoneLabel = attrs.get('notDoneLabel') ?: 'Not Done'
        Trackable trackable = attrs.get('bean') as Trackable
        def user = springSecurityService.getCurrentUser()
        UserTracked userTracked = UserTracked.lookup(user.id, user.class.name, trackable.id, trackable.class.name)

        out << render(template: '/trackable/widget', model: [label: widgetLabel, notDoneLabel: notDoneLabel, doneLabel: doneLabel, userTracked: userTracked, trackable: trackable])
    }
}
