package com.jerries.expense.data.preferences;

import android.content.Context;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata("javax.inject.Singleton")
@QualifierMetadata("dagger.hilt.android.qualifiers.ApplicationContext")
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
public final class UserPreferencesDataSource_Factory implements Factory<UserPreferencesDataSource> {
  private final Provider<Context> contextProvider;

  public UserPreferencesDataSource_Factory(Provider<Context> contextProvider) {
    this.contextProvider = contextProvider;
  }

  @Override
  public UserPreferencesDataSource get() {
    return newInstance(contextProvider.get());
  }

  public static UserPreferencesDataSource_Factory create(Provider<Context> contextProvider) {
    return new UserPreferencesDataSource_Factory(contextProvider);
  }

  public static UserPreferencesDataSource newInstance(Context context) {
    return new UserPreferencesDataSource(context);
  }
}
