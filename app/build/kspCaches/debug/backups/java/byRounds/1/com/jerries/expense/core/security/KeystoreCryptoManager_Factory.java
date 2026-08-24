package com.jerries.expense.core.security;

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
public final class KeystoreCryptoManager_Factory implements Factory<KeystoreCryptoManager> {
  @Override
  public KeystoreCryptoManager get() {
    return newInstance();
  }

  public static KeystoreCryptoManager_Factory create() {
    return InstanceHolder.INSTANCE;
  }

  public static KeystoreCryptoManager newInstance() {
    return new KeystoreCryptoManager();
  }

  private static final class InstanceHolder {
    private static final KeystoreCryptoManager_Factory INSTANCE = new KeystoreCryptoManager_Factory();
  }
}
