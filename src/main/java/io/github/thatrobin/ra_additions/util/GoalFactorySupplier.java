package io.github.thatrobin.ra_additions.util;

import io.github.thatrobin.ra_additions.goals.factories.Goal;
import io.github.thatrobin.ra_additions.goals.factories.GoalFactory;

public interface GoalFactorySupplier<T extends Goal> {

    GoalFactory<T> createFactory();
}