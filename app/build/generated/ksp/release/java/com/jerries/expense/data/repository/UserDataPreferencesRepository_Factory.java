package com.jerries.expense.data.repository;

import com.jerries.expense.data.preferences.UserPreferencesDataSource;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata("javax.inject.Singleton")
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
public final class UserDataPreferencesRepository_Factory implements Factory<UserDataPreferencesRepository> {
  private final Provider<UserPreferencesDataSource> dataSourceProvider;

  public UserDataPreferencesRepository_Factory(
      Provider<UserPreferencesDataSource> dataSourceProvider) {
    this.dataSourceProvider = dataSourceProvider;
  }

  @Override
  public UserDataPreferencesRepository get() {
    return newInstance(dataSourceProvider.get());
  }

  public static UserDataPreferencesRepository_Factory create(
      Provider<UserPreferencesDataSource> dataSourceProvider) {
    return new UserDataPreferencesRepository_Factory(dataSourceProvider);
  }

  public static UserDataPreferencesRepository newInstance(UserPreferencesDataSource dataSource) {
    return new UserDataPreferencesRepository(dataSource);
  }
}
