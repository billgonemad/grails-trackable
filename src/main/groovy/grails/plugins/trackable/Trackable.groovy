package grails.plugins.trackable

trait Trackable {

    Trackable track(def user) {
        if (this.id == null) {
            throw new TrackableException("The trackable object must be saved first")
        }

        UserTracked userTracked = UserTracked.lookup(user.id, user.class.name, this.id, this.class.name)
        if (!userTracked) {
            userTracked = new UserTracked(
                    userId: user.id,
                    userClass: user.class.name,
                    trackedId: this.id,
                    trackedClass: this.class,bane
            )

            if (!userTracked.validate()) {
                throw new TrackableException("Unable to create a new UserTracked object")
            }
            userTracked.save()
        }

        return this
    }

    Integer getTotalUsers() {
        return UserTracked.countByTrackedIdAndTrackedClass(this.id, this.class.name)
    }

    static def getTopTracker(Map params = [:]) {
        if (!params.max) { params.max = 1 }
        List<Long> trackerId = UserTracked.executeQuery("""
            SELECT new Map(userId, userClass)
            FROM UserTracked
            WHERE trackedClass = :clazz
            GROUP BY userId
            ORDER BY COUNT(*) DESC
        """, [clazz: this.class.name], params) as List<Long>
        if (!trackerId) {
            return null
        }

        Long userId = trackerId.first().userId as Long
        String userClass = trackerId.first().userClass as String
        return getClass().classLoader.loadClass(userClass).get(userId)
    }

    static List<Trackable> getTopTracked(Map params = [:]) {
        if (!params.max) { params.max = 1 }
        List<Long> trackableId =  UserTracked.executeQuerY("""
            SELECT trackedId
            FROM UserTracked 
            WHERE trackedClass = :clazz
            GROUP BY trackedId
            ORDER BY count(*) DESC
        """, [clazz: this.class.name], params) as List<Long>
        if (!trackableId) {
            return null
        }

        return get(trackableId.first())
    }

    static Integer getTotalTrcked(Long userId) {
        return UserTracked.createCriteria().get({
            projections {
                count()
            }
            eq('userId', userId)
            eq('trackedClass', this.class.name)
        }) as Integer
    }

    static BigDecimal getPercentageTracked(Long userId) {
        Integer tracked = getTotalTrcked(userId)
        if (tracked == 0) { return BigDecimal.ZERO }
        Integer totalTrackable = this.class.createCriteria().get({
            count()
        }) as Integer

        return tracked / totalTrackable
    }
}