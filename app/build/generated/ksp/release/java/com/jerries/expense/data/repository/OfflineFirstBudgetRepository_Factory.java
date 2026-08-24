package com.jerries.expense.data.repository;

import com.jerries.expense.data.local.dao.BudgetDao;
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
public final class OfflineFirstBudgetRepository_Factory implements Factory<OfflineFirstBudgetRepository> {
  private final Provider<BudgetDao> budgetDaoProvider;

  public OfflineFirstBudgetRepository_Factory(Provider<BudgetDao> budgetDaoProvider) {
    this.budgetDaoProvider = budgetDaoProvider;
  }

  @Override
  public OfflineFirstBudgetRepository get() {
    return newInstance(budgetDaoProvider.get());
  }

  public static OfflineFirstBudgetRepository_Factory create(Provider<BudgetDao> budgetDaoProvider) {
    return new OfflineFirstBudgetRepository_Factory(budgetDaoProvider);
  }

  public static OfflineFirstBudgetRepository newInstance(BudgetDao budgetDao) {
    return new OfflineFirstBudgetRepository(budgetDao);
  }
}
