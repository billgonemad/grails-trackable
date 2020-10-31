package grails.plugins.trackable

import grails.compiler.GrailsCompileStatic
import grails.gorm.transactions.Transactional

@Transactional
@GrailsCompileStatic
class TrackableService {
    void save(UserTracked userTracked) {
        userTracked.save()
    }

    void delete(UserTracked userTracked) {
        userTracked?.delete()
    }
}
