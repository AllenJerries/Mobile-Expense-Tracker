package com.jerries.expense.feature.accounts;

import com.jerries.expense.domain.usecase.ObserveAccountBalancesUseCase;
import com.jerries.expense.domain.usecase.ObserveAccountsUseCase;
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
public final class AccountsViewModel_Factory implements Factory<AccountsViewModel> {
  private final Provider<ObserveAccountsUseCase> observeAccountsProvider;

  private final Provider<ObserveAccountBalancesUseCase> observeBalancesUseCaseProvider;

  private final Provider<ObserveUserPreferencesUseCase> observeUserPreferencesProvider;

  public AccountsViewModel_Factory(Provider<ObserveAccountsUseCase> observeAccountsProvider,
      Provider<ObserveAccountBalancesUseCase> observeBalancesUseCaseProvider,
      Provider<ObserveUserPreferencesUseCase> observeUserPreferencesProvider) {
    this.observeAccountsProvider = observeAccountsProvider;
    this.observeBalancesUseCaseProvider = observeBalancesUseCaseProvider;
    this.observeUserPreferencesProvider = observeUserPreferencesProvider;
  }

  @Override
  public AccountsViewModel get() {
    return newInstance(observeAccountsProvider.get(), observeBalancesUseCaseProvider.get(), observeUserPreferencesProvider.get());
  }

  public static AccountsViewModel_Factory create(
      Provider<ObserveAccountsUseCase> observeAccountsProvider,
      Provider<ObserveAccountBalancesUseCase> observeBalancesUseCaseProvider,
      Provider<ObserveUserPreferencesUseCase> observeUserPreferencesProvider) {
    return new AccountsViewModel_Factory(observeAccountsProvider, observeBalancesUseCaseProvider, observeUserPreferencesProvider);
  }

  public static AccountsViewModel newInstance(ObserveAccountsUseCase observeAccounts,
      ObserveAccountBalancesUseCase observeBalancesUseCase,
      ObserveUserPreferencesUseCase observeUserPreferences) {
    return new AccountsViewModel(observeAccounts, observeBalancesUseCase, observeUserPreferences);
  }
}
