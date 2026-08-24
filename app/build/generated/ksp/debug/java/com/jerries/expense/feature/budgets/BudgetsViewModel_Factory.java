package com.jerries.expense.feature.budgets;

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
public final class BudgetsViewModel_Factory implements Factory<BudgetsViewModel> {
  private final Provider<ObserveUserPreferencesUseCase> observeUserPreferencesProvider;

  public BudgetsViewModel_Factory(
      Provider<ObserveUserPreferencesUseCase> observeUserPreferencesProvider) {
    this.observeUserPreferencesProvider = observeUserPreferencesProvider;
  }

  @Override
  public BudgetsViewModel get() {
    return newInstance(observeUserPreferencesProvider.get());
  }

  public static BudgetsViewModel_Factory create(
      Provider<ObserveUserPreferencesUseCase> observeUserPreferencesProvider) {
    return new BudgetsViewModel_Factory(observeUserPreferencesProvider);
  }

  public static BudgetsViewModel newInstance(ObserveUserPreferencesUseCase observeUserPreferences) {
    return new BudgetsViewModel(observeUserPreferences);
  }
}
