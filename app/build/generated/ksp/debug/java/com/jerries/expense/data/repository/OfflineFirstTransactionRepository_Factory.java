package com.jerries.expense.data.repository;

import com.jerries.expense.data.local.dao.TransactionDao;
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
public final class OfflineFirstTransactionRepository_Factory implements Factory<OfflineFirstTransactionRepository> {
  private final Provider<TransactionDao> transactionDaoProvider;

  public OfflineFirstTransactionRepository_Factory(
      Provider<TransactionDao> transactionDaoProvider) {
    this.transactionDaoProvider = transactionDaoProvider;
  }

  @Override
  public OfflineFirstTransactionRepository get() {
    return newInstance(transactionDaoProvider.get());
  }

  public static OfflineFirstTransactionRepository_Factory create(
      Provider<TransactionDao> transactionDaoProvider) {
    return new OfflineFirstTransactionRepository_Factory(transactionDaoProvider);
  }

  public static OfflineFirstTransactionRepository newInstance(TransactionDao transactionDao) {
    return new OfflineFirstTransactionRepository(transactionDao);
  }
}
