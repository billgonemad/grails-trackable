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

    static Tuple2<Integer, List> getTopTracker() {
        Integer topTrackerCount = getTopTrackerCount()
        if (topTrackerCount == null) {
            return new Tuple2<Integer, List>(null, null)
        }
        String trackedClass = GrailsNameUtils.getFullClassName(this)
        List<Long> trackerId = UserTracked.executeQuery("""
            SELECT new Map(userId as userId, userClass as userClass)
            FROM UserTracked
            WHERE trackedClass = :clazz
            GROUP BY userId
            HAVING COUNT(*) = :topCount
        """, [clazz: trackedClass, topCount: topTrackerCount as Long]) as List<Long>
        if (!trackerId) {
            return new Tuple2<Integer, List>(topTrackerCount, [])
        }

        return new Tuple2<Integer, List>(topTrackerCount, trackerId.collect {
            Long userId = it.userId as Long
            String userClass = it.userClass as String
            return getClass().classLoader.loadClass(userClass).get(userId)
        })
    }

    static Tuple2<Integer, List<Trackable>> getTopTracked() {
        Integer topTrackedCount = getTopTrackedCount()
        if (topTrackedCount == null) {
            return new Tuple2<Integer, List<Trackable>>(null, null)
        }

        String trackedClass = GrailsNameUtils.getFullClassName(this)
        List<Long> trackableId =  UserTracked.executeQuery("""
            SELECT trackedId
            FROM UserTracked 
            WHERE trackedClass = :clazz
            GROUP BY trackedId
            HAVING COUNT(*) = :topCount
            ORDER BY NULL
        """, [clazz: trackedClass, topCount: topTrackedCount as Long]) as List<Long>
        if (!trackableId) {
            return new Tuple2<Integer, List<Trackable>>(topTrackedCount, [])
        }

        return new Tuple2<Integer, List<Trackable>>(topTrackedCount,trackableId.collect { get(it) })
    }

    static Integer getTotalTrcked(Long userId) {
        log.info("getting total tracked for {}", userId)
        String trackedClass = GrailsNameUtils.getFullClassName(this)
        return UserTracked.countByUserIdAndTrackedClass(userId, trackedClass)
    }

    static Integer getTopTrackerCount() {
        String trackedClass = GrailsNameUtils.getFullClassName(this)
        List<Integer> topCounts =  UserTracked.executeQuery("""
            SELECT COUNT(*)
            FROM UserTracked 
            WHERE trackedClass = :clazz
            GROUP BY userId
            ORDER BY COUNT(*) DESC
        """, [clazz: trackedClass], [max: 1]) as List<Integer>
        if (!topCounts) {
            return null
        }

        return topCounts.first()
    }

    static Integer getTopTrackedCount() {
        String trackedClass = GrailsNameUtils.getFullClassName(this)
        List<Integer> topCounts =  UserTracked.executeQuery("""
            SELECT COUNT(*)
            FROM UserTracked 
            WHERE trackedClass = :clazz
            GROUP BY trackedId
            ORDER BY COUNT(*) DESC
        """, [clazz: trackedClass], [max: 1]) as List<Integer>
        if (!topCounts) {
            return null
        }

        return topCounts.first()
    }
}