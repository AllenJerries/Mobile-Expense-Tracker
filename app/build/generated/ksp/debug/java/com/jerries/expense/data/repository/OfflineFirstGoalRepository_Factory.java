package com.jerries.expense.data.repository;

import com.jerries.expense.data.local.dao.GoalDao;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata("javax.inject.Singleton")
@QualifierMetadata
@DaggerGenerated
@Generated(
    value = "dagger.internal.codegen.ComponentProcessor",
    comments = "https://dagger.dev"
)
@SuppressWarnings({
    "unchecked",
    "rawtypes",
    "KotlinInternal",
    "KotlinInternalInJava",
    "cast",
    "deprecation",
    "nullness:initialization.field.uninitialized"
})
public final class OfflineFirstGoalRepository_Factory implements Factory<OfflineFirstGoalRepository> {
  private final Provider<GoalDao> goalDaoProvider;

  public OfflineFirstGoalRepository_Factory(Provider<GoalDao> goalDaoProvider) {
    this.goalDaoProvider = goalDaoProvider;
  }

  @Override
  public OfflineFirstGoalRepository get() {
    return newInstance(goalDaoProvider.get());
  }

  public static OfflineFirstGoalRepository_Factory create(Provider<GoalDao> goalDaoProvider) {
    return new OfflineFirstGoalRepository_Factory(goalDaoProvider);
  }

  public static OfflineFirstGoalRepository newInstance(GoalDao goalDao) {
    return new OfflineFirstGoalRepository(goalDao);
  }
}
