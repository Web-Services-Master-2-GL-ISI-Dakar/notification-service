package sn.ondmoney.notificationservice.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class NotificationPreferenceCriteriaTest {

    @Test
    void newNotificationPreferenceCriteriaHasAllFiltersNullTest() {
        var notificationPreferenceCriteria = new NotificationPreferenceCriteria();
        assertThat(notificationPreferenceCriteria).is(criteriaFiltersAre(Objects::isNull));
    }

    @Test
    void notificationPreferenceCriteriaFluentMethodsCreatesFiltersTest() {
        var notificationPreferenceCriteria = new NotificationPreferenceCriteria();

        setAllFilters(notificationPreferenceCriteria);

        assertThat(notificationPreferenceCriteria).is(criteriaFiltersAre(Objects::nonNull));
    }

    @Test
    void notificationPreferenceCriteriaCopyCreatesNullFilterTest() {
        var notificationPreferenceCriteria = new NotificationPreferenceCriteria();
        var copy = notificationPreferenceCriteria.copy();

        assertThat(notificationPreferenceCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::isNull)),
            criteria -> assertThat(criteria).isEqualTo(notificationPreferenceCriteria)
        );
    }

    @Test
    void notificationPreferenceCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var notificationPreferenceCriteria = new NotificationPreferenceCriteria();
        setAllFilters(notificationPreferenceCriteria);

        var copy = notificationPreferenceCriteria.copy();

        assertThat(notificationPreferenceCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::nonNull)),
            criteria -> assertThat(criteria).isEqualTo(notificationPreferenceCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var notificationPreferenceCriteria = new NotificationPreferenceCriteria();

        assertThat(notificationPreferenceCriteria).hasToString("NotificationPreferenceCriteria{}");
    }

    private static void setAllFilters(NotificationPreferenceCriteria notificationPreferenceCriteria) {
        notificationPreferenceCriteria.id();
        notificationPreferenceCriteria.userId();
        notificationPreferenceCriteria.smsEnabled();
        notificationPreferenceCriteria.emailEnabled();
        notificationPreferenceCriteria.pushEnabled();
        notificationPreferenceCriteria.mutedTypes();
        notificationPreferenceCriteria.language();
        notificationPreferenceCriteria.updatedAt();
        notificationPreferenceCriteria.distinct();
    }

    private static Condition<NotificationPreferenceCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getUserId()) &&
                condition.apply(criteria.getSmsEnabled()) &&
                condition.apply(criteria.getEmailEnabled()) &&
                condition.apply(criteria.getPushEnabled()) &&
                condition.apply(criteria.getMutedTypes()) &&
                condition.apply(criteria.getLanguage()) &&
                condition.apply(criteria.getUpdatedAt()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<NotificationPreferenceCriteria> copyFiltersAre(
        NotificationPreferenceCriteria copy,
        BiFunction<Object, Object, Boolean> condition
    ) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getUserId(), copy.getUserId()) &&
                condition.apply(criteria.getSmsEnabled(), copy.getSmsEnabled()) &&
                condition.apply(criteria.getEmailEnabled(), copy.getEmailEnabled()) &&
                condition.apply(criteria.getPushEnabled(), copy.getPushEnabled()) &&
                condition.apply(criteria.getMutedTypes(), copy.getMutedTypes()) &&
                condition.apply(criteria.getLanguage(), copy.getLanguage()) &&
                condition.apply(criteria.getUpdatedAt(), copy.getUpdatedAt()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
