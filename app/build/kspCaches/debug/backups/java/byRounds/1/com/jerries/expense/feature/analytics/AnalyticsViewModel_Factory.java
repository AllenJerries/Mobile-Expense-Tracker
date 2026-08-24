package com.jerries.expense.feature.analytics;

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
public final class AnalyticsViewModel_Factory implements Factory<AnalyticsViewModel> {
  private final Provider<ObserveUserPreferencesUseCase> observeUserPreferencesProvider;

  public AnalyticsViewModel_Factory(
      Provider<ObserveUserPreferencesUseCase> observeUserPreferencesProvider) {
    this.observeUserPreferencesProvider = observeUserPreferencesProvider;
  }

  @Override
  public AnalyticsViewModel get() {
    return newInstance(observeUserPreferencesProvider.get());
  }

  public static AnalyticsViewModel_Factory create(
      Provider<ObserveUserPreferencesUseCase> observeUserPreferencesProvider) {
    return new AnalyticsViewModel_Factory(observeUserPreferencesProvider);
  }

  public static AnalyticsViewModel newInstance(
      ObserveUserPreferencesUseCase observeUserPreferences) {
    return new AnalyticsViewModel(observeUserPreferences);
  }
}
