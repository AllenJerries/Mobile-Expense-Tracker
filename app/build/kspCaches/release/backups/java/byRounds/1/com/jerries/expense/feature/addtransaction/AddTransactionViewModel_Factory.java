package com.jerries.expense.feature.addtransaction;

import com.jerries.expense.core.common.IdGenerator;
import com.jerries.expense.core.common.TimeProvider;
import com.jerries.expense.domain.usecase.AddTransactionUseCase;
import com.jerries.expense.domain.usecase.ObserveAccountsUseCase;
import com.jerries.expense.domain.usecase.ObserveCategoriesUseCase;
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
public final class AddTransactionViewModel_Factory implements Factory<AddTransactionViewModel> {
  private final Provider<ObserveAccountsUseCase> observeAccountsProvider;

  private final Provider<ObserveCategoriesUseCase> observeCategoriesProvider;

  private final Provider<AddTransactionUseCase> addTransactionProvider;

  private final Provider<TimeProvider> timeProvider;

  private final Provider<IdGenerator> idGeneratorProvider;

  public AddTransactionViewModel_Factory(Provider<ObserveAccountsUseCase> observeAccountsProvider,
      Provider<ObserveCategoriesUseCase> observeCategoriesProvider,
      Provider<AddTransactionUseCase> addTransactionProvider, Provider<TimeProvider> timeProvider,
      Provider<IdGenerator> idGeneratorProvider) {
    this.observeAccountsProvider = observeAccountsProvider;
    this.observeCategoriesProvider = observeCategoriesProvider;
    this.addTransactionProvider = addTransactionProvider;
    this.timeProvider = timeProvider;
    this.idGeneratorProvider = idGeneratorProvider;
  }

  @Override
  public AddTransactionViewModel get() {
    return newInstance(observeAccountsProvider.get(), observeCategoriesProvider.get(), addTransactionProvider.get(), timeProvider.get(), idGeneratorProvider.get());
  }

  public static AddTransactionViewModel_Factory create(
      Provider<ObserveAccountsUseCase> observeAccountsProvider,
      Provider<ObserveCategoriesUseCase> observeCategoriesProvider,
      Provider<AddTransactionUseCase> addTransactionProvider, Provider<TimeProvider> timeProvider,
      Provider<IdGenerator> idGeneratorProvider) {
    return new AddTransactionViewModel_Factory(observeAccountsProvider, observeCategoriesProvider, addTransactionProvider, timeProvider, idGeneratorProvider);
  }

  public static AddTransactionViewModel newInstance(ObserveAccountsUseCase observeAccounts,
      ObserveCategoriesUseCase observeCategories, AddTransactionUseCase addTransaction,
      TimeProvider timeProvider, IdGenerator idGenerator) {
    return new AddTransactionViewModel(observeAccounts, observeCategories, addTransaction, timeProvider, idGenerator);
  }
}
