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

    def "test get top tracked when clear winner"() {
        given:
        User user1 = User.findByUsername('User 1')
        User user2 = User.findByUsername('User 2')
        Country.findByName('Japan').track(user1).track(user2)
        Country.findByName('China').track(user2)
        Country.findByName('United Kingdom').track(user1)

        expect:
        Tuple2<Integer, List<Trackable>> results = Country.topTracked
        2 == results.first
        [Country.findByName('Japan')] == results.second
    }

    def "test get top tracked with tie for winner"() {
        given:
        User user1 = User.findByUsername('User 1')
        User user2 = User.findByUsername('User 2')
        User user3 = User.findByUsername('User 3')
        Country.findByName('Japan').track(user1).track(user2).track(user3)
        Country.findByName('China').track(user2).track(user3)
        Country.findByName('United Kingdom').track(user1).track(user3).track(user2)

        expect:
        Tuple2<Integer, List<Trackable>> results = Country.topTracked
        3 == results.first
        2 == results.second.size()
        results.second.contains(Country.findByName('Japan'))
        results.second.contains(Country.findByName('United Kingdom'))
    }

    def "test get top tracked when none tracked"() {
        expect:
        Tuple2<Integer, List<Trackable>> results =  Country.getTopTracked()
        null == results.first
        null == results.second
    }

    def "test get top tracker when clear winner"() {
        given:
        User user1 = User.findByUsername('User 1')
        User user2 = User.findByUsername('User 2')
        Country.findByName('Japan').track(user1).track(user2)
        Country.findByName('China').track(user2).track(user1)
        Country.findByName('United Kingdom').track(user1)

        expect:
        Tuple2<Integer, List> results = Country.topTracker
        3 == results.first
        [user1] == results.second
    }

    def "test get top tracker with tie for winner"() {
        given:
        User user1 = User.findByUsername('User 1')
        User user2 = User.findByUsername('User 2')
        User user3 = User.findByUsername('User 3')
        Country.findByName('Japan').track(user1).track(user3)
        Country.findByName('China').track(user1).track(user2).track(user3)
        Country.findByName('United Kingdom').track(user1).track(user3).track(user2)

        expect:
        Tuple2<Integer, List<Trackable>> results = Country.topTracker
        3 == results.first
        2 == results.second.size()
        results.second.contains(user1)
        results.second.contains(user3)
    }

    def "test get top tracker when none tracked"() {
        expect:
        Tuple2<Integer, List> results = Country.getTopTracker()
        null == results.first
        null == results.second
    }

    def "test get total users when none tracked"() {
        given:
        List<Country> countries = Country.list()

        expect:
        for (Country country : countries) {
            0 == country.getTotalUsers()
        }
    }

    def "test get total users when tracked"() {
        given:
        User user1 = User.findByUsername('User 1')
        User user2 = User.findByUsername('User 2')
        User user3 = User.findByUsername('User 3')
        Country mexico = Country.findByName('Mexico').track(user1).track(user2)
        Country canada = Country.findByName("Canada").track(user3)

        expect:
        0 == Country.findByName('United States').getTotalUsers()
        2 == mexico.getTotalUsers()
        1 == canada.getTotalUsers()
    }
}
