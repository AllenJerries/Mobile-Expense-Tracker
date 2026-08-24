package com.jerries.expense.feature.dashboard;

import com.jerries.expense.core.common.TimeProvider;
import com.jerries.expense.domain.usecase.ObserveCategoriesUseCase;
import com.jerries.expense.domain.usecase.ObserveRecentTransactionsUseCase;
import com.jerries.expense.domain.usecase.ObserveTotalBalanceUseCase;
import com.jerries.expense.domain.usecase.ObserveUserPreferencesUseCase;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata
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
public final class DashboardViewModel_Factory implements Factory<DashboardViewModel> {
  private final Provider<ObserveTotalBalanceUseCase> observeTotalBalanceProvider;

  private final Provider<ObserveRecentTransactionsUseCase> observeRecentTransactionsProvider;

  private final Provider<ObserveCategoriesUseCase> observeCategoriesProvider;

  private final Provider<ObserveUserPreferencesUseCase> observeUserPreferencesProvider;

  private final Provider<TimeProvider> timeProvider;

  public DashboardViewModel_Factory(
      Provider<ObserveTotalBalanceUseCase> observeTotalBalanceProvider,
      Provider<ObserveRecentTransactionsUseCase> observeRecentTransactionsProvider,
      Provider<ObserveCategoriesUseCase> observeCategoriesProvider,
      Provider<ObserveUserPreferencesUseCase> observeUserPreferencesProvider,
      Provider<TimeProvider> timeProvider) {
    this.observeTotalBalanceProvider = observeTotalBalanceProvider;
    this.observeRecentTransactionsProvider = observeRecentTransactionsProvider;
    this.observeCategoriesProvider = observeCategoriesProvider;
    this.observeUserPreferencesProvider = observeUserPreferencesProvider;
    this.timeProvider = timeProvider;
  }

  @Override
  public DashboardViewModel get() {
    return newInstance(observeTotalBalanceProvider.get(), observeRecentTransactionsProvider.get(), observeCategoriesProvider.get(), observeUserPreferencesProvider.get(), timeProvider.get());
  }

  public static DashboardViewModel_Factory create(
      Provider<ObserveTotalBalanceUseCase> observeTotalBalanceProvider,
      Provider<ObserveRecentTransactionsUseCase> observeRecentTransactionsProvider,
      Provider<ObserveCategoriesUseCase> observeCategoriesProvider,
      Provider<ObserveUserPreferencesUseCase> observeUserPreferencesProvider,
      Provider<TimeProvider> timeProvider) {
    return new DashboardViewModel_Factory(observeTotalBalanceProvider, observeRecentTransactionsProvider, observeCategoriesProvider, observeUserPreferencesProvider, timeProvider);
  }

  public static DashboardViewModel newInstance(ObserveTotalBalanceUseCase observeTotalBalance,
      ObserveRecentTransactionsUseCase observeRecentTransactions,
      ObserveCategoriesUseCase observeCategories,
      ObserveUserPreferencesUseCase observeUserPreferences, TimeProvider timeProvider) {
    return new DashboardViewModel(observeTotalBalance, observeRecentTransactions, observeCategories, observeUserPreferences, timeProvider);
  }
}
