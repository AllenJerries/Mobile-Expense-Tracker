package com.jerries.expense.domain.usecase;

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
public final class SetCurrencyCodeUseCase_Factory implements Factory<SetCurrencyCodeUseCase> {
  private final Provider<UserPreferencesRepository> userPreferencesRepositoryProvider;

  public SetCurrencyCodeUseCase_Factory(
      Provider<UserPreferencesRepository> userPreferencesRepositoryProvider) {
    this.userPreferencesRepositoryProvider = userPreferencesRepositoryProvider;
  }

  @Override
  public SetCurrencyCodeUseCase get() {
    return newInstance(userPreferencesRepositoryProvider.get());
  }

  public static SetCurrencyCodeUseCase_Factory create(
      Provider<UserPreferencesRepository> userPreferencesRepositoryProvider) {
    return new SetCurrencyCodeUseCase_Factory(userPreferencesRepositoryProvider);
  }

  public static SetCurrencyCodeUseCase newInstance(
      UserPreferencesRepository userPreferencesRepository) {
    return new SetCurrencyCodeUseCase(userPreferencesRepository);
  }
}
