package com.jerries.expense.domain.usecase;

import com.jerries.expense.domain.repository.CategoryRepository;
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
public final class ObserveCategoriesUseCase_Factory implements Factory<ObserveCategoriesUseCase> {
  private final Provider<CategoryRepository> categoryRepositoryProvider;

  public ObserveCategoriesUseCase_Factory(Provider<CategoryRepository> categoryRepositoryProvider) {
    this.categoryRepositoryProvider = categoryRepositoryProvider;
  }

  @Override
  public ObserveCategoriesUseCase get() {
    return newInstance(categoryRepositoryProvider.get());
  }

  public static ObserveCategoriesUseCase_Factory create(
      Provider<CategoryRepository> categoryRepositoryProvider) {
    return new ObserveCategoriesUseCase_Factory(categoryRepositoryProvider);
  }

  public static ObserveCategoriesUseCase newInstance(CategoryRepository categoryRepository) {
    return new ObserveCategoriesUseCase(categoryRepository);
  }
}
