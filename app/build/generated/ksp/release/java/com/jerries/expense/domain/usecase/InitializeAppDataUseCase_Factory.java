package com.jerries.expense.domain.usecase;

import com.jerries.expense.domain.repository.AccountRepository;
import com.jerries.expense.domain.repository.CategoryRepository;
import com.jerries.expense.domain.repository.UserPreferencesRepository;
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
public final class InitializeAppDataUseCase_Factory implements Factory<InitializeAppDataUseCase> {
  private final Provider<CategoryRepository> categoryRepositoryProvider;

  private final Provider<AccountRepository> accountRepositoryProvider;

  private final Provider<UserPreferencesRepository> userPreferencesRepositoryProvider;

  public InitializeAppDataUseCase_Factory(Provider<CategoryRepository> categoryRepositoryProvider,
      Provider<AccountRepository> accountRepositoryProvider,
      Provider<UserPreferencesRepository> userPreferencesRepositoryProvider) {
    this.categoryRepositoryProvider = categoryRepositoryProvider;
    this.accountRepositoryProvider = accountRepositoryProvider;
    this.userPreferencesRepositoryProvider = userPreferencesRepositoryProvider;
  }

  @Override
  public InitializeAppDataUseCase get() {
    return newInstance(categoryRepositoryProvider.get(), accountRepositoryProvider.get(), userPreferencesRepositoryProvider.get());
  }

  public static InitializeAppDataUseCase_Factory create(
      Provider<CategoryRepository> categoryRepositoryProvider,
      Provider<AccountRepository> accountRepositoryProvider,
      Provider<UserPreferencesRepository> userPreferencesRepositoryProvider) {
    return new InitializeAppDataUseCase_Factory(categoryRepositoryProvider, accountRepositoryProvider, userPreferencesRepositoryProvider);
  }

  public static InitializeAppDataUseCase newInstance(CategoryRepository categoryRepository,
      AccountRepository accountRepository, UserPreferencesRepository userPreferencesRepository) {
    return new InitializeAppDataUseCase(categoryRepository, accountRepository, userPreferencesRepository);
  }
}
