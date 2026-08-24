package com.jerries.expense;

import androidx.hilt.work.HiltWorkerFactory;
import com.jerries.expense.core.common.ApplicationScope;
import com.jerries.expense.domain.usecase.InitializeAppDataUseCase;
import dagger.MembersInjector;
import dagger.internal.DaggerGenerated;
import dagger.internal.InjectedFieldSignature;
import dagger.internal.QualifierMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;
import kotlinx.coroutines.CoroutineScope;

@QualifierMetadata("com.jerries.expense.core.common.ApplicationScope")
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
public final class JerriesExpenseApplication_MembersInjector implements MembersInjector<JerriesExpenseApplication> {
  private final Provider<HiltWorkerFactory> workerFactoryProvider;

  private final Provider<InitializeAppDataUseCase> initializeAppDataProvider;

  private final Provider<CoroutineScope> applicationScopeProvider;

  public JerriesExpenseApplication_MembersInjector(
      Provider<HiltWorkerFactory> workerFactoryProvider,
      Provider<InitializeAppDataUseCase> initializeAppDataProvider,
      Provider<CoroutineScope> applicationScopeProvider) {
    this.workerFactoryProvider = workerFactoryProvider;
    this.initializeAppDataProvider = initializeAppDataProvider;
    this.applicationScopeProvider = applicationScopeProvider;
  }

  public static MembersInjector<JerriesExpenseApplication> create(
      Provider<HiltWorkerFactory> workerFactoryProvider,
      Provider<InitializeAppDataUseCase> initializeAppDataProvider,
      Provider<CoroutineScope> applicationScopeProvider) {
    return new JerriesExpenseApplication_MembersInjector(workerFactoryProvider, initializeAppDataProvider, applicationScopeProvider);
  }

  @Override
  public void injectMembers(JerriesExpenseApplication instance) {
    injectWorkerFactory(instance, workerFactoryProvider.get());
    injectInitializeAppData(instance, initializeAppDataProvider.get());
    injectApplicationScope(instance, applicationScopeProvider.get());
  }

  @InjectedFieldSignature("com.jerries.expense.JerriesExpenseApplication.workerFactory")
  public static void injectWorkerFactory(JerriesExpenseApplication instance,
      HiltWorkerFactory workerFactory) {
    instance.workerFactory = workerFactory;
  }

  @InjectedFieldSignature("com.jerries.expense.JerriesExpenseApplication.initializeAppData")
  public static void injectInitializeAppData(JerriesExpenseApplication instance,
      InitializeAppDataUseCase initializeAppData) {
    instance.initializeAppData = initializeAppData;
  }

  @InjectedFieldSignature("com.jerries.expense.JerriesExpenseApplication.applicationScope")
  @ApplicationScope
  public static void injectApplicationScope(JerriesExpenseApplication instance,
      CoroutineScope applicationScope) {
    instance.applicationScope = applicationScope;
  }
}
