package sn.ondmoney.notificationservice.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class NotificationLogCriteriaTest {

    @Test
    void newNotificationLogCriteriaHasAllFiltersNullTest() {
        var notificationLogCriteria = new NotificationLogCriteria();
        assertThat(notificationLogCriteria).is(criteriaFiltersAre(Objects::isNull));
    }

    @Test
    void notificationLogCriteriaFluentMethodsCreatesFiltersTest() {
        var notificationLogCriteria = new NotificationLogCriteria();

        setAllFilters(notificationLogCriteria);

        assertThat(notificationLogCriteria).is(criteriaFiltersAre(Objects::nonNull));
    }

    @Test
    void notificationLogCriteriaCopyCreatesNullFilterTest() {
        var notificationLogCriteria = new NotificationLogCriteria();
        var copy = notificationLogCriteria.copy();

        assertThat(notificationLogCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::isNull)),
            criteria -> assertThat(criteria).isEqualTo(notificationLogCriteria)
        );
    }

    @Test
    void notificationLogCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var notificationLogCriteria = new NotificationLogCriteria();
        setAllFilters(notificationLogCriteria);

        var copy = notificationLogCriteria.copy();

        assertThat(notificationLogCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(Objects::nonNull)),
            criteria -> assertThat(criteria).isEqualTo(notificationLogCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var notificationLogCriteria = new NotificationLogCriteria();

        assertThat(notificationLogCriteria).hasToString("NotificationLogCriteria{}");
    }

    private static void setAllFilters(NotificationLogCriteria notificationLogCriteria) {
        notificationLogCriteria.id();
        notificationLogCriteria.notificationId();
        notificationLogCriteria.userId();
        notificationLogCriteria.type();
        notificationLogCriteria.channel();
        notificationLogCriteria.status();
        notificationLogCriteria.message();
        notificationLogCriteria.recipient();
        notificationLogCriteria.timestamp();
        notificationLogCriteria.sentAt();
        notificationLogCriteria.retryCount();
        notificationLogCriteria.channelResults();
        notificationLogCriteria.action();
        notificationLogCriteria.details();
        notificationLogCriteria.notificationId();
        notificationLogCriteria.distinct();
    }

    private static Condition<NotificationLogCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getNotificationId()) &&
                condition.apply(criteria.getUserId()) &&
                condition.apply(criteria.getType()) &&
                condition.apply(criteria.getChannel()) &&
                condition.apply(criteria.getStatus()) &&
                condition.apply(criteria.getMessage()) &&
                condition.apply(criteria.getRecipient()) &&
                condition.apply(criteria.getTimestamp()) &&
                condition.apply(criteria.getSentAt()) &&
                condition.apply(criteria.getRetryCount()) &&
                condition.apply(criteria.getChannelResults()) &&
                condition.apply(criteria.getAction()) &&
                condition.apply(criteria.getDetails()) &&
                condition.apply(criteria.getNotificationId()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<NotificationLogCriteria> copyFiltersAre(
        NotificationLogCriteria copy,
        BiFunction<Object, Object, Boolean> condition
    ) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getNotificationId(), copy.getNotificationId()) &&
                condition.apply(criteria.getUserId(), copy.getUserId()) &&
                condition.apply(criteria.getType(), copy.getType()) &&
                condition.apply(criteria.getChannel(), copy.getChannel()) &&
                condition.apply(criteria.getStatus(), copy.getStatus()) &&
                condition.apply(criteria.getMessage(), copy.getMessage()) &&
                condition.apply(criteria.getRecipient(), copy.getRecipient()) &&
                condition.apply(criteria.getTimestamp(), copy.getTimestamp()) &&
                condition.apply(criteria.getSentAt(), copy.getSentAt()) &&
                condition.apply(criteria.getRetryCount(), copy.getRetryCount()) &&
                condition.apply(criteria.getChannelResults(), copy.getChannelResults()) &&
                condition.apply(criteria.getAction(), copy.getAction()) &&
                condition.apply(criteria.getDetails(), copy.getDetails()) &&
                condition.apply(criteria.getNotificationId(), copy.getNotificationId()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}
