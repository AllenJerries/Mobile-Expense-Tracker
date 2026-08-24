package com.jerries.expense.feature.categories;

import com.jerries.expense.domain.usecase.ObserveCategoriesUseCase;
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
public final class CategoriesViewModel_Factory implements Factory<CategoriesViewModel> {
  private final Provider<ObserveCategoriesUseCase> observeCategoriesProvider;

  public CategoriesViewModel_Factory(Provider<ObserveCategoriesUseCase> observeCategoriesProvider) {
    this.observeCategoriesProvider = observeCategoriesProvider;
  }

  @Override
  public CategoriesViewModel get() {
    return newInstance(observeCategoriesProvider.get());
  }

  public static CategoriesViewModel_Factory create(
      Provider<ObserveCategoriesUseCase> observeCategoriesProvider) {
    return new CategoriesViewModel_Factory(observeCategoriesProvider);
  }

  public static CategoriesViewModel newInstance(ObserveCategoriesUseCase observeCategories) {
    return new CategoriesViewModel(observeCategories);
  }
}
