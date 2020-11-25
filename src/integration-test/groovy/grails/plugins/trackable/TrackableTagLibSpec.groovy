package grails.plugins.trackable

import com.demo.Country
import com.demo.User
import grails.plugin.springsecurity.SpringSecurityService
import grails.testing.mixin.integration.Integration
import grails.testing.web.taglib.TagLibUnitTest
import org.springframework.test.annotation.Rollback
import org.springframework.transaction.annotation.Transactional
import spock.lang.Specification

@Integration
@Rollback
@Transactional
class TrackableTagLibSpec extends Specification implements TagLibUnitTest<TrackableTagLib> {

    void "test trackedForm when not logged in"() {
        given:
        tagLib.springSecurityService = Mock(SpringSecurityService)
        tagLib.springSecurityService.isLoggedIn() >> false

        expect:
        tagLib.trackedForm() == ''
    }

    void "test trackedForm when logged in and not tracked yet"() {
        given:
        User user = new User(username: 'user 1', password: 'letmein').save()
        Country country = new Country(name: 'Japan').save()
        tagLib.springSecurityService = Mock(SpringSecurityService)
        tagLib.springSecurityService.isLoggedIn() >> true
        tagLib.springSecurityService.getCurrentUser() >> user

        expect:
        String output = tagLib.trackedForm([bean: country])
        println(output)
        output.contains('<input type="hidden" name="tracked" value="false"/>')
        output.contains('<input type="hidden" name="trackedId" value="' + country.id + '"/>')
        output.contains('<input type="hidden" name="trackedClass" value="' + country.class.name + '"/>')
        output.contains('data-onlabel="Done"')
        output.contains('data-offlabel="Not Done"')
        output.contains('<span class="trackable-label">Tracker Status</span>')
        !output.contains('checked')
    }

    void "test trackedForm when logged in and not tracked yet with custom labels"() {
        given:
        User user = new User(username: 'user 1', password: 'letmein').save()
        Country country = new Country(name: 'Japan').save()
        tagLib.springSecurityService = Mock(SpringSecurityService)
        tagLib.springSecurityService.isLoggedIn() >> true
        tagLib.springSecurityService.getCurrentUser() >> user

        expect:
        String output = tagLib.trackedForm([bean: country, widgetLabel: 'Passport Stamp?', doneLabel: "Obtained", notDoneLabel: 'Need To Visit'])
        println(output)
        output.contains('<input type="hidden" name="tracked" value="false"/>')
        output.contains('<input type="hidden" name="trackedId" value="' + country.id + '"/>')
        output.contains('<input type="hidden" name="trackedClass" value="' + country.class.name + '"/>')
        output.contains('data-onlabel="Obtained"')
        output.contains('data-offlabel="Need To Visit"')
        output.contains('<span class="trackable-label">Passport Stamp?</span>')
        !output.contains('checked')
    }

    void "test trackedForm when logged in and tracked"() {
        given:
        User user = new User(username: 'user 1', password: 'letmein').save()
        Country country = new Country(name: 'Japan').save().track(user)
        tagLib.springSecurityService = Mock(SpringSecurityService)
        tagLib.springSecurityService.isLoggedIn() >> true
        tagLib.springSecurityService.getCurrentUser() >> user

        expect:
        String output = tagLib.trackedForm([bean: country])
        println(output)
        output.contains('<input type="hidden" name="tracked" value="true"/>')
        output.contains('<input type="hidden" name="trackedId" value="' + country.id + '"/>')
        output.contains('<input type="hidden" name="trackedClass" value="' + country.class.name + '"/>')
        output.contains('data-onlabel="Done"')
        output.contains('data-offlabel="Not Done"')
        output.contains('<span class="trackable-label">Tracker Status</span>')
        output.contains('checked')
    }

    void "test trackedForm when logged in and tracked with custom labels"() {
        given:
        User user = new User(username: 'user 1', password: 'letmein').save()
        Country country = new Country(name: 'Japan').save().track(user)
        tagLib.springSecurityService = Mock(SpringSecurityService)
        tagLib.springSecurityService.isLoggedIn() >> true
        tagLib.springSecurityService.getCurrentUser() >> user

        expect:
        String output = tagLib.trackedForm([bean: country, widgetLabel: 'Passport Stamp?', doneLabel: "Obtained", notDoneLabel: 'Need To Visit'])
        println(output)
        output.contains('<input type="hidden" name="tracked" value="true"/>')
        output.contains('<input type="hidden" name="trackedId" value="' + country.id + '"/>')
        output.contains('<input type="hidden" name="trackedClass" value="' + country.class.name + '"/>')
        output.contains('data-onlabel="Obtained"')
        output.contains('data-offlabel="Need To Visit"')
        output.contains('<span class="trackable-label">Passport Stamp?</span>')
        output.contains('checked')
    }
}
