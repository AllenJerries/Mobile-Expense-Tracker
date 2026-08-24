package com.jerries.expense.core.common;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;
import kotlinx.coroutines.CoroutineDispatcher;
import kotlinx.coroutines.CoroutineScope;

@ScopeMetadata("javax.inject.Singleton")
@QualifierMetadata({
    "com.jerries.expense.core.common.ApplicationScope",
    "com.jerries.expense.core.common.DefaultDispatcher"
})
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
public final class CoroutineModule_ProvideApplicationScopeFactory implements Factory<CoroutineScope> {
  private final Provider<CoroutineDispatcher> dispatcherProvider;

  public CoroutineModule_ProvideApplicationScopeFactory(
      Provider<CoroutineDispatcher> dispatcherProvider) {
    this.dispatcherProvider = dispatcherProvider;
  }

  @Override
  public CoroutineScope get() {
    return provideApplicationScope(dispatcherProvider.get());
  }

  public static CoroutineModule_ProvideApplicationScopeFactory create(
      Provider<CoroutineDispatcher> dispatcherProvider) {
    return new CoroutineModule_ProvideApplicationScopeFactory(dispatcherProvider);
  }

  public static CoroutineScope provideApplicationScope(CoroutineDispatcher dispatcher) {
    return Preconditions.checkNotNullFromProvides(CoroutineModule.INSTANCE.provideApplicationScope(dispatcher));
  }
}
