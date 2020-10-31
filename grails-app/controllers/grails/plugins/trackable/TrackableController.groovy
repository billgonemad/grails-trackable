package grails.plugins.trackable

import groovy.util.logging.Slf4j

import javax.servlet.http.HttpServletResponse

@Slf4j
class TrackableController {

    TrackableService trackableService

    def track() {
        if (!isLoggedIn()) {
            throw new TrackableException("Must be logged in to track")
        }

        def user = authenticatedUser
        Long userId = user.id as Long
        String userClass = user.class.name
        Boolean tracked = params.boolean('tracked')
        Long trackedId = params.long('trackedId')
        String trackedClass = params.get('trackedClass')

        UserTracked userTracked = UserTracked.lookup(userId, userClass, trackedId, trackedClass)
        if (!tracked && !userTracked) {
            return render(status: HttpServletResponse.SC_OK)
        }

        if (!tracked) {
            trackableService.delete(userTracked)
            return render(status: HttpServletResponse.SC_OK)
        }

        if (!userTracked) {
            userTracked = new UserTracked(
                    userId: userId,
                    userClass: userClass,
                    trackedId: trackedId,
                    trackedClass: trackedClass
            )
        }

        trackableService.save(userTracked)

        render(status: HttpServletResponse.SC_OK)
    }
}
