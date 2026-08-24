package com.jerries.expense.domain.usecase;

import com.jerries.expense.domain.repository.TransactionRepository;
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
public final class ObserveRecentTransactionsUseCase_Factory implements Factory<ObserveRecentTransactionsUseCase> {
  private final Provider<TransactionRepository> transactionRepositoryProvider;

  public ObserveRecentTransactionsUseCase_Factory(
      Provider<TransactionRepository> transactionRepositoryProvider) {
    this.transactionRepositoryProvider = transactionRepositoryProvider;
  }

  @Override
  public ObserveRecentTransactionsUseCase get() {
    return newInstance(transactionRepositoryProvider.get());
  }

  public static ObserveRecentTransactionsUseCase_Factory create(
      Provider<TransactionRepository> transactionRepositoryProvider) {
    return new ObserveRecentTransactionsUseCase_Factory(transactionRepositoryProvider);
  }

  public static ObserveRecentTransactionsUseCase newInstance(
      TransactionRepository transactionRepository) {
    return new ObserveRecentTransactionsUseCase(transactionRepository);
  }
}
