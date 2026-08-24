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
public final class SetDynamicColorsUseCase_Factory implements Factory<SetDynamicColorsUseCase> {
  private final Provider<UserPreferencesRepository> userPreferencesRepositoryProvider;

  public SetDynamicColorsUseCase_Factory(
      Provider<UserPreferencesRepository> userPreferencesRepositoryProvider) {
    this.userPreferencesRepositoryProvider = userPreferencesRepositoryProvider;
  }

  @Override
  public SetDynamicColorsUseCase get() {
    return newInstance(userPreferencesRepositoryProvider.get());
  }

  public static SetDynamicColorsUseCase_Factory create(
      Provider<UserPreferencesRepository> userPreferencesRepositoryProvider) {
    return new SetDynamicColorsUseCase_Factory(userPreferencesRepositoryProvider);
  }

  public static SetDynamicColorsUseCase newInstance(
      UserPreferencesRepository userPreferencesRepository) {
    return new SetDynamicColorsUseCase(userPreferencesRepository);
  }
}
