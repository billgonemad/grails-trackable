package grails.plugins.trackable

import com.demo.Country
import com.demo.User
import grails.testing.mixin.integration.Integration
import org.springframework.test.annotation.Rollback
import org.springframework.transaction.annotation.Transactional
import spock.lang.Specification

@Rollback
@Transactional
@Integration
class TrackableSpec extends Specification {

    def setup() {
        new User(
                username: 'User 1',
                password: 'letmein'
        ).save()
        new User(
                username: 'User 2',
                password: 'letmein'
        ).save()
        new User(
                username: 'User 3',
                password: 'letmein'
        ).save()
        new Country(name: 'United States').save()
        new Country(name: 'Mexico').save()
        new Country(name: 'Canada').save()
        new Country(name: 'France').save()
        new Country(name: 'United Kingdom').save()
        new Country(name: 'Japan').save()
        new Country(name: 'China').save()
    }

    def "test total tracked of none"() {
        List<User> users = User.list()
        expect:
        for (User user : users) {
            0 == Country.getTotalTrcked(user.id)
        }
    }

    def "test total tracked when tracked"() {
        setup:
        User user1 = User.findByUsername("User 1")
        Country.findByName("United States").track(user1)
        Country.findByName("Mexico").track(user1)
        Country.findByName("Canada").track(user1)

        expect:
        3 == Country.getTotalTrcked(user1.id)
    }

    def "test get percentage total tracked of none"() {
        List<User> users = User.list()
        expect:
        for (User user : users) {
            0 == Country.getPercentageTracked(user.id)
        }
    }

    def "test get top tracked wien none tracked"() {
        expect:
        null == Country.getTopTracked()
    }

    def "test get top tracker when none tracked"() {
        expect:
        null == Country.getTopTracker()
    }

    def "test get total users when none tracked"() {
        given:
        List<Country> countries = Country.list()

        expect:
        for (Country country : countries) {
            0 == country.getTotalUsers()
        }
    }

    // TODO - delete this - stupid testing setup of test
    def "test"() {
        expect:
        Country.count() == 7
    }
}
