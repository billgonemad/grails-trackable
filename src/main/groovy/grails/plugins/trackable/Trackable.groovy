package grails.plugins.trackable


import grails.util.GrailsNameUtils
import groovy.util.logging.Slf4j

@Slf4j
trait Trackable {

    Trackable track(def user) {
        if (this.id == null) {
            throw new TrackableException("The trackable object must be saved first")
        }

        UserTracked userTracked = UserTracked.lookup(user.id, user.class.name, this.id, this.class.name)
        if (!userTracked) {
            log.info("{} was not tracked by {} yet", this, user)
            userTracked = new UserTracked(
                    userId: user.id,
                    userClass: user.class.name,
                    trackedId: this.id,
                    trackedClass: this.class.name
            )

            if (!userTracked.validate()) {
                throw new TrackableException("Unable to create a new UserTracked object")
            }
            userTracked.save(flush: true)
            log.info("saved with id of {}", userTracked.id)
        }

        return this
    }

    Integer getTotalUsers() {
        return UserTracked.countByTrackedIdAndTrackedClass(this.id, this.class.name)
    }

    static def getTopTracker(Map params = [:]) {
        if (!params.max) { params.max = 1 }
        String trackedClass = GrailsNameUtils.getFullClassName(this)
        List<Long> trackerId = UserTracked.executeQuery("""
            SELECT new Map(userId, userClass)
            FROM UserTracked
            WHERE trackedClass = :clazz
            GROUP BY userId
            ORDER BY COUNT(*) DESC
        """, [clazz: trackedClass], params) as List<Long>
        if (!trackerId) {
            return null
        }

        Long userId = trackerId.first().userId as Long
        String userClass = trackerId.first().userClass as String
        return getClass().classLoader.loadClass(userClass).get(userId)
    }

    static List<Trackable> getTopTracked(Map params = [:]) {
        if (!params.max) { params.max = 1 }
        String trackedClass = GrailsNameUtils.getFullClassName(this)
        List<Long> trackableId =  UserTracked.executeQuery("""
            SELECT trackedId
            FROM UserTracked 
            WHERE trackedClass = :clazz
            GROUP BY trackedId
            ORDER BY count(*) DESC
        """, [clazz: trackedClass], params) as List<Long>
        if (!trackableId) {
            return null
        }

        return get(trackableId.first())
    }

    static Integer getTotalTrcked(Long userId) {
        log.info("getting total tracked for {}", userId)
        String trackedClass = GrailsNameUtils.getFullClassName(this)
        return UserTracked.createCriteria().get({
            projections {
                count()
            }
            eq('userId', userId)
            eq('trackedClass', trackedClass)
        }) as Integer
    }
}