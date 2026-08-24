package com.jerries.expense.domain.usecase;

import com.jerries.expense.domain.repository.AccountRepository;
import com.jerries.expense.domain.repository.CategoryRepository;
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
public final class AddTransactionUseCase_Factory implements Factory<AddTransactionUseCase> {
  private final Provider<TransactionRepository> transactionRepositoryProvider;

  private final Provider<AccountRepository> accountRepositoryProvider;

  private final Provider<CategoryRepository> categoryRepositoryProvider;

  public AddTransactionUseCase_Factory(
      Provider<TransactionRepository> transactionRepositoryProvider,
      Provider<AccountRepository> accountRepositoryProvider,
      Provider<CategoryRepository> categoryRepositoryProvider) {
    this.transactionRepositoryProvider = transactionRepositoryProvider;
    this.accountRepositoryProvider = accountRepositoryProvider;
    this.categoryRepositoryProvider = categoryRepositoryProvider;
  }

  @Override
  public AddTransactionUseCase get() {
    return newInstance(transactionRepositoryProvider.get(), accountRepositoryProvider.get(), categoryRepositoryProvider.get());
  }

  public static AddTransactionUseCase_Factory create(
      Provider<TransactionRepository> transactionRepositoryProvider,
      Provider<AccountRepository> accountRepositoryProvider,
      Provider<CategoryRepository> categoryRepositoryProvider) {
    return new AddTransactionUseCase_Factory(transactionRepositoryProvider, accountRepositoryProvider, categoryRepositoryProvider);
  }

  public static AddTransactionUseCase newInstance(TransactionRepository transactionRepository,
      AccountRepository accountRepository, CategoryRepository categoryRepository) {
    return new AddTransactionUseCase(transactionRepository, accountRepository, categoryRepository);
  }
}
