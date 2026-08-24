package com.jerries.expense.feature.transactions;

import com.jerries.expense.core.common.TimeProvider;
import com.jerries.expense.domain.repository.TransactionRepository;
import com.jerries.expense.domain.usecase.ObserveCategoriesUseCase;
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
public final class TransactionsViewModel_Factory implements Factory<TransactionsViewModel> {
  private final Provider<TransactionRepository> transactionRepositoryProvider;

  private final Provider<ObserveCategoriesUseCase> observeCategoriesProvider;

  private final Provider<ObserveUserPreferencesUseCase> observeUserPreferencesProvider;

  private final Provider<TimeProvider> timeProvider;

  public TransactionsViewModel_Factory(
      Provider<TransactionRepository> transactionRepositoryProvider,
      Provider<ObserveCategoriesUseCase> observeCategoriesProvider,
      Provider<ObserveUserPreferencesUseCase> observeUserPreferencesProvider,
      Provider<TimeProvider> timeProvider) {
    this.transactionRepositoryProvider = transactionRepositoryProvider;
    this.observeCategoriesProvider = observeCategoriesProvider;
    this.observeUserPreferencesProvider = observeUserPreferencesProvider;
    this.timeProvider = timeProvider;
  }

  @Override
  public TransactionsViewModel get() {
    return newInstance(transactionRepositoryProvider.get(), observeCategoriesProvider.get(), observeUserPreferencesProvider.get(), timeProvider.get());
  }

  public static TransactionsViewModel_Factory create(
      Provider<TransactionRepository> transactionRepositoryProvider,
      Provider<ObserveCategoriesUseCase> observeCategoriesProvider,
      Provider<ObserveUserPreferencesUseCase> observeUserPreferencesProvider,
      Provider<TimeProvider> timeProvider) {
    return new TransactionsViewModel_Factory(transactionRepositoryProvider, observeCategoriesProvider, observeUserPreferencesProvider, timeProvider);
  }

  public static TransactionsViewModel newInstance(TransactionRepository transactionRepository,
      ObserveCategoriesUseCase observeCategories,
      ObserveUserPreferencesUseCase observeUserPreferences, TimeProvider timeProvider) {
    return new TransactionsViewModel(transactionRepository, observeCategories, observeUserPreferences, timeProvider);
  }
}
