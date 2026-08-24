package com.jerries.expense.domain.usecase;

import com.jerries.expense.domain.repository.AccountRepository;
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
public final class ObserveAccountBalancesUseCase_Factory implements Factory<ObserveAccountBalancesUseCase> {
  private final Provider<AccountRepository> accountRepositoryProvider;

  public ObserveAccountBalancesUseCase_Factory(
      Provider<AccountRepository> accountRepositoryProvider) {
    this.accountRepositoryProvider = accountRepositoryProvider;
  }

  @Override
  public ObserveAccountBalancesUseCase get() {
    return newInstance(accountRepositoryProvider.get());
  }

  public static ObserveAccountBalancesUseCase_Factory create(
      Provider<AccountRepository> accountRepositoryProvider) {
    return new ObserveAccountBalancesUseCase_Factory(accountRepositoryProvider);
  }

  public static ObserveAccountBalancesUseCase newInstance(AccountRepository accountRepository) {
    return new ObserveAccountBalancesUseCase(accountRepository);
  }
}
