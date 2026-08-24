package com.jerries.expense.core.common;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;

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
public final class UuidIdGenerator_Factory implements Factory<UuidIdGenerator> {
  @Override
  public UuidIdGenerator get() {
    return newInstance();
  }

  public static UuidIdGenerator_Factory create() {
    return InstanceHolder.INSTANCE;
  }

  public static UuidIdGenerator newInstance() {
    return new UuidIdGenerator();
  }

  private static final class InstanceHolder {
    private static final UuidIdGenerator_Factory INSTANCE = new UuidIdGenerator_Factory();
  }
}
