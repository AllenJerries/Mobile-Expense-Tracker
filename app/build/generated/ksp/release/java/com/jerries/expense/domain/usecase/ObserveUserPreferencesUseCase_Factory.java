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
public final class ObserveUserPreferencesUseCase_Factory implements Factory<ObserveUserPreferencesUseCase> {
  private final Provider<UserPreferencesRepository> userPreferencesRepositoryProvider;

  public ObserveUserPreferencesUseCase_Factory(
      Provider<UserPreferencesRepository> userPreferencesRepositoryProvider) {
    this.userPreferencesRepositoryProvider = userPreferencesRepositoryProvider;
  }

  @Override
  public ObserveUserPreferencesUseCase get() {
    return newInstance(userPreferencesRepositoryProvider.get());
  }

  public static ObserveUserPreferencesUseCase_Factory create(
      Provider<UserPreferencesRepository> userPreferencesRepositoryProvider) {
    return new ObserveUserPreferencesUseCase_Factory(userPreferencesRepositoryProvider);
  }

  public static ObserveUserPreferencesUseCase newInstance(
      UserPreferencesRepository userPreferencesRepository) {
    return new ObserveUserPreferencesUseCase(userPreferencesRepository);
  }
}
