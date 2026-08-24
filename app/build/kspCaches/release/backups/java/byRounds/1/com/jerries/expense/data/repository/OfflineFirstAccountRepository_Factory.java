package com.jerries.expense.data.repository;

import com.jerries.expense.data.local.dao.AccountDao;
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
public final class OfflineFirstAccountRepository_Factory implements Factory<OfflineFirstAccountRepository> {
  private final Provider<AccountDao> accountDaoProvider;

  private final Provider<TransactionDao> transactionDaoProvider;

  public OfflineFirstAccountRepository_Factory(Provider<AccountDao> accountDaoProvider,
      Provider<TransactionDao> transactionDaoProvider) {
    this.accountDaoProvider = accountDaoProvider;
    this.transactionDaoProvider = transactionDaoProvider;
  }

  @Override
  public OfflineFirstAccountRepository get() {
    return newInstance(accountDaoProvider.get(), transactionDaoProvider.get());
  }

  public static OfflineFirstAccountRepository_Factory create(
      Provider<AccountDao> accountDaoProvider, Provider<TransactionDao> transactionDaoProvider) {
    return new OfflineFirstAccountRepository_Factory(accountDaoProvider, transactionDaoProvider);
  }

  public static OfflineFirstAccountRepository newInstance(AccountDao accountDao,
      TransactionDao transactionDao) {
    return new OfflineFirstAccountRepository(accountDao, transactionDao);
  }
}
