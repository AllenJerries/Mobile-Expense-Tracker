package com.jerries.expense;

import android.app.Activity;
import android.app.Service;
import android.view.View;
import androidx.fragment.app.Fragment;
import androidx.hilt.work.HiltWorkerFactory;
import androidx.hilt.work.WorkerAssistedFactory;
import androidx.hilt.work.WorkerFactoryModule_ProvideFactoryFactory;
import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.ViewModel;
import androidx.work.ListenableWorker;
import com.jerries.expense.core.common.CoroutineModule_ProvideApplicationScopeFactory;
import com.jerries.expense.core.common.CoroutineModule_ProvideDefaultDispatcherFactory;
import com.jerries.expense.core.common.SystemTimeProvider;
import com.jerries.expense.core.common.UuidIdGenerator;
import com.jerries.expense.data.local.dao.AccountDao;
import com.jerries.expense.data.local.dao.CategoryDao;
import com.jerries.expense.data.local.dao.TransactionDao;
import com.jerries.expense.data.local.database.DatabaseModule_ProvideAccountDaoFactory;
import com.jerries.expense.data.local.database.DatabaseModule_ProvideCategoryDaoFactory;
import com.jerries.expense.data.local.database.DatabaseModule_ProvideDatabaseFactory;
import com.jerries.expense.data.local.database.DatabaseModule_ProvideTransactionDaoFactory;
import com.jerries.expense.data.local.database.ExpenseDatabase;
import com.jerries.expense.data.preferences.UserPreferencesDataSource;
import com.jerries.expense.data.repository.OfflineFirstAccountRepository;
import com.jerries.expense.data.repository.OfflineFirstCategoryRepository;
import com.jerries.expense.data.repository.OfflineFirstTransactionRepository;
import com.jerries.expense.data.repository.UserDataPreferencesRepository;
import com.jerries.expense.domain.usecase.AddTransactionUseCase;
import com.jerries.expense.domain.usecase.InitializeAppDataUseCase;
import com.jerries.expense.domain.usecase.ObserveAccountBalancesUseCase;
import com.jerries.expense.domain.usecase.ObserveAccountsUseCase;
import com.jerries.expense.domain.usecase.ObserveCategoriesUseCase;
import com.jerries.expense.domain.usecase.ObserveRecentTransactionsUseCase;
import com.jerries.expense.domain.usecase.ObserveTotalBalanceUseCase;
import com.jerries.expense.domain.usecase.ObserveUserPreferencesUseCase;
import com.jerries.expense.domain.usecase.SetCurrencyCodeUseCase;
import com.jerries.expense.domain.usecase.SetDynamicColorsUseCase;
import com.jerries.expense.domain.usecase.SetThemeSettingUseCase;
import com.jerries.expense.feature.accounts.AccountsViewModel;
import com.jerries.expense.feature.accounts.AccountsViewModel_HiltModules;
import com.jerries.expense.feature.accounts.AccountsViewModel_HiltModules_BindsModule_Binds_LazyMapKey;
import com.jerries.expense.feature.accounts.AccountsViewModel_HiltModules_KeyModule_Provide_LazyMapKey;
import com.jerries.expense.feature.addtransaction.AddTransactionViewModel;
import com.jerries.expense.feature.addtransaction.AddTransactionViewModel_HiltModules;
import com.jerries.expense.feature.addtransaction.AddTransactionViewModel_HiltModules_BindsModule_Binds_LazyMapKey;
import com.jerries.expense.feature.addtransaction.AddTransactionViewModel_HiltModules_KeyModule_Provide_LazyMapKey;
import com.jerries.expense.feature.analytics.AnalyticsViewModel;
import com.jerries.expense.feature.analytics.AnalyticsViewModel_HiltModules;
import com.jerries.expense.feature.analytics.AnalyticsViewModel_HiltModules_BindsModule_Binds_LazyMapKey;
import com.jerries.expense.feature.analytics.AnalyticsViewModel_HiltModules_KeyModule_Provide_LazyMapKey;
import com.jerries.expense.feature.budgets.BudgetsViewModel;
import com.jerries.expense.feature.budgets.BudgetsViewModel_HiltModules;
import com.jerries.expense.feature.budgets.BudgetsViewModel_HiltModules_BindsModule_Binds_LazyMapKey;
import com.jerries.expense.feature.budgets.BudgetsViewModel_HiltModules_KeyModule_Provide_LazyMapKey;
import com.jerries.expense.feature.categories.CategoriesViewModel;
import com.jerries.expense.feature.categories.CategoriesViewModel_HiltModules;
import com.jerries.expense.feature.categories.CategoriesViewModel_HiltModules_BindsModule_Binds_LazyMapKey;
import com.jerries.expense.feature.categories.CategoriesViewModel_HiltModules_KeyModule_Provide_LazyMapKey;
import com.jerries.expense.feature.dashboard.DashboardViewModel;
import com.jerries.expense.feature.dashboard.DashboardViewModel_HiltModules;
import com.jerries.expense.feature.dashboard.DashboardViewModel_HiltModules_BindsModule_Binds_LazyMapKey;
import com.jerries.expense.feature.dashboard.DashboardViewModel_HiltModules_KeyModule_Provide_LazyMapKey;
import com.jerries.expense.feature.settings.SettingsViewModel;
import com.jerries.expense.feature.settings.SettingsViewModel_HiltModules;
import com.jerries.expense.feature.settings.SettingsViewModel_HiltModules_BindsModule_Binds_LazyMapKey;
import com.jerries.expense.feature.settings.SettingsViewModel_HiltModules_KeyModule_Provide_LazyMapKey;
import com.jerries.expense.feature.transactions.TransactionsViewModel;
import com.jerries.expense.feature.transactions.TransactionsViewModel_HiltModules;
import com.jerries.expense.feature.transactions.TransactionsViewModel_HiltModules_BindsModule_Binds_LazyMapKey;
import com.jerries.expense.feature.transactions.TransactionsViewModel_HiltModules_KeyModule_Provide_LazyMapKey;
import dagger.hilt.android.ActivityRetainedLifecycle;
import dagger.hilt.android.ViewModelLifecycle;
import dagger.hilt.android.internal.builders.ActivityComponentBuilder;
import dagger.hilt.android.internal.builders.ActivityRetainedComponentBuilder;
import dagger.hilt.android.internal.builders.FragmentComponentBuilder;
import dagger.hilt.android.internal.builders.ServiceComponentBuilder;
import dagger.hilt.android.internal.builders.ViewComponentBuilder;
import dagger.hilt.android.internal.builders.ViewModelComponentBuilder;
import dagger.hilt.android.internal.builders.ViewWithFragmentComponentBuilder;
import dagger.hilt.android.internal.lifecycle.DefaultViewModelFactories;
import dagger.hilt.android.internal.lifecycle.DefaultViewModelFactories_InternalFactoryFactory_Factory;
import dagger.hilt.android.internal.managers.ActivityRetainedComponentManager_LifecycleModule_ProvideActivityRetainedLifecycleFactory;
import dagger.hilt.android.internal.managers.SavedStateHandleHolder;
import dagger.hilt.android.internal.modules.ApplicationContextModule;
import dagger.hilt.android.internal.modules.ApplicationContextModule_ProvideContextFactory;
import dagger.internal.DaggerGenerated;
import dagger.internal.DoubleCheck;
import dagger.internal.LazyClassKeyMap;
import dagger.internal.MapBuilder;
import dagger.internal.Preconditions;
import dagger.internal.Provider;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import javax.annotation.processing.Generated;
import kotlinx.coroutines.CoroutineScope;

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
public final class DaggerJerriesExpenseApplication_HiltComponents_SingletonC {
  private DaggerJerriesExpenseApplication_HiltComponents_SingletonC() {
  }

  public static Builder builder() {
    return new Builder();
  }

  public static final class Builder {
    private ApplicationContextModule applicationContextModule;

    private Builder() {
    }

    public Builder applicationContextModule(ApplicationContextModule applicationContextModule) {
      this.applicationContextModule = Preconditions.checkNotNull(applicationContextModule);
      return this;
    }

    public JerriesExpenseApplication_HiltComponents.SingletonC build() {
      Preconditions.checkBuilderRequirement(applicationContextModule, ApplicationContextModule.class);
      return new SingletonCImpl(applicationContextModule);
    }
  }

  private static final class ActivityRetainedCBuilder implements JerriesExpenseApplication_HiltComponents.ActivityRetainedC.Builder {
    private final SingletonCImpl singletonCImpl;

    private SavedStateHandleHolder savedStateHandleHolder;

    private ActivityRetainedCBuilder(SingletonCImpl singletonCImpl) {
      this.singletonCImpl = singletonCImpl;
    }

    @Override
    public ActivityRetainedCBuilder savedStateHandleHolder(
        SavedStateHandleHolder savedStateHandleHolder) {
      this.savedStateHandleHolder = Preconditions.checkNotNull(savedStateHandleHolder);
      return this;
    }

    @Override
    public JerriesExpenseApplication_HiltComponents.ActivityRetainedC build() {
      Preconditions.checkBuilderRequirement(savedStateHandleHolder, SavedStateHandleHolder.class);
      return new ActivityRetainedCImpl(singletonCImpl, savedStateHandleHolder);
    }
  }

  private static final class ActivityCBuilder implements JerriesExpenseApplication_HiltComponents.ActivityC.Builder {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private Activity activity;

    private ActivityCBuilder(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
    }

    @Override
    public ActivityCBuilder activity(Activity activity) {
      this.activity = Preconditions.checkNotNull(activity);
      return this;
    }

    @Override
    public JerriesExpenseApplication_HiltComponents.ActivityC build() {
      Preconditions.checkBuilderRequirement(activity, Activity.class);
      return new ActivityCImpl(singletonCImpl, activityRetainedCImpl, activity);
    }
  }

  private static final class FragmentCBuilder implements JerriesExpenseApplication_HiltComponents.FragmentC.Builder {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private Fragment fragment;

    private FragmentCBuilder(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, ActivityCImpl activityCImpl) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;
    }

    @Override
    public FragmentCBuilder fragment(Fragment fragment) {
      this.fragment = Preconditions.checkNotNull(fragment);
      return this;
    }

    @Override
    public JerriesExpenseApplication_HiltComponents.FragmentC build() {
      Preconditions.checkBuilderRequirement(fragment, Fragment.class);
      return new FragmentCImpl(singletonCImpl, activityRetainedCImpl, activityCImpl, fragment);
    }
  }

  private static final class ViewWithFragmentCBuilder implements JerriesExpenseApplication_HiltComponents.ViewWithFragmentC.Builder {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private final FragmentCImpl fragmentCImpl;

    private View view;

    private ViewWithFragmentCBuilder(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, ActivityCImpl activityCImpl,
        FragmentCImpl fragmentCImpl) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;
      this.fragmentCImpl = fragmentCImpl;
    }

    @Override
    public ViewWithFragmentCBuilder view(View view) {
      this.view = Preconditions.checkNotNull(view);
      return this;
    }

    @Override
    public JerriesExpenseApplication_HiltComponents.ViewWithFragmentC build() {
      Preconditions.checkBuilderRequirement(view, View.class);
      return new ViewWithFragmentCImpl(singletonCImpl, activityRetainedCImpl, activityCImpl, fragmentCImpl, view);
    }
  }

  private static final class ViewCBuilder implements JerriesExpenseApplication_HiltComponents.ViewC.Builder {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private View view;

    private ViewCBuilder(SingletonCImpl singletonCImpl, ActivityRetainedCImpl activityRetainedCImpl,
        ActivityCImpl activityCImpl) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;
    }

    @Override
    public ViewCBuilder view(View view) {
      this.view = Preconditions.checkNotNull(view);
      return this;
    }

    @Override
    public JerriesExpenseApplication_HiltComponents.ViewC build() {
      Preconditions.checkBuilderRequirement(view, View.class);
      return new ViewCImpl(singletonCImpl, activityRetainedCImpl, activityCImpl, view);
    }
  }

  private static final class ViewModelCBuilder implements JerriesExpenseApplication_HiltComponents.ViewModelC.Builder {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private SavedStateHandle savedStateHandle;

    private ViewModelLifecycle viewModelLifecycle;

    private ViewModelCBuilder(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
    }

    @Override
    public ViewModelCBuilder savedStateHandle(SavedStateHandle handle) {
      this.savedStateHandle = Preconditions.checkNotNull(handle);
      return this;
    }

    @Override
    public ViewModelCBuilder viewModelLifecycle(ViewModelLifecycle viewModelLifecycle) {
      this.viewModelLifecycle = Preconditions.checkNotNull(viewModelLifecycle);
      return this;
    }

    @Override
    public JerriesExpenseApplication_HiltComponents.ViewModelC build() {
      Preconditions.checkBuilderRequirement(savedStateHandle, SavedStateHandle.class);
      Preconditions.checkBuilderRequirement(viewModelLifecycle, ViewModelLifecycle.class);
      return new ViewModelCImpl(singletonCImpl, activityRetainedCImpl, savedStateHandle, viewModelLifecycle);
    }
  }

  private static final class ServiceCBuilder implements JerriesExpenseApplication_HiltComponents.ServiceC.Builder {
    private final SingletonCImpl singletonCImpl;

    private Service service;

    private ServiceCBuilder(SingletonCImpl singletonCImpl) {
      this.singletonCImpl = singletonCImpl;
    }

    @Override
    public ServiceCBuilder service(Service service) {
      this.service = Preconditions.checkNotNull(service);
      return this;
    }

    @Override
    public JerriesExpenseApplication_HiltComponents.ServiceC build() {
      Preconditions.checkBuilderRequirement(service, Service.class);
      return new ServiceCImpl(singletonCImpl, service);
    }
  }

  private static final class ViewWithFragmentCImpl extends JerriesExpenseApplication_HiltComponents.ViewWithFragmentC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private final FragmentCImpl fragmentCImpl;

    private final ViewWithFragmentCImpl viewWithFragmentCImpl = this;

    private ViewWithFragmentCImpl(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, ActivityCImpl activityCImpl,
        FragmentCImpl fragmentCImpl, View viewParam) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;
      this.fragmentCImpl = fragmentCImpl;


    }
  }

  private static final class FragmentCImpl extends JerriesExpenseApplication_HiltComponents.FragmentC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private final FragmentCImpl fragmentCImpl = this;

    private FragmentCImpl(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, ActivityCImpl activityCImpl,
        Fragment fragmentParam) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;


    }

    @Override
    public DefaultViewModelFactories.InternalFactoryFactory getHiltInternalFactoryFactory() {
      return activityCImpl.getHiltInternalFactoryFactory();
    }

    @Override
    public ViewWithFragmentComponentBuilder viewWithFragmentComponentBuilder() {
      return new ViewWithFragmentCBuilder(singletonCImpl, activityRetainedCImpl, activityCImpl, fragmentCImpl);
    }
  }

  private static final class ViewCImpl extends JerriesExpenseApplication_HiltComponents.ViewC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private final ViewCImpl viewCImpl = this;

    private ViewCImpl(SingletonCImpl singletonCImpl, ActivityRetainedCImpl activityRetainedCImpl,
        ActivityCImpl activityCImpl, View viewParam) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;


    }
  }

  private static final class ActivityCImpl extends JerriesExpenseApplication_HiltComponents.ActivityC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl = this;

    private ActivityCImpl(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, Activity activityParam) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;


    }

    @Override
    public void injectMainActivity(MainActivity mainActivity) {
    }

    @Override
    public DefaultViewModelFactories.InternalFactoryFactory getHiltInternalFactoryFactory() {
      return DefaultViewModelFactories_InternalFactoryFactory_Factory.newInstance(getViewModelKeys(), new ViewModelCBuilder(singletonCImpl, activityRetainedCImpl));
    }

    @Override
    public Map<Class<?>, Boolean> getViewModelKeys() {
      return LazyClassKeyMap.<Boolean>of(MapBuilder.<String, Boolean>newMapBuilder(9).put(AccountsViewModel_HiltModules_KeyModule_Provide_LazyMapKey.lazyClassKeyName, AccountsViewModel_HiltModules.KeyModule.provide()).put(AddTransactionViewModel_HiltModules_KeyModule_Provide_LazyMapKey.lazyClassKeyName, AddTransactionViewModel_HiltModules.KeyModule.provide()).put(AnalyticsViewModel_HiltModules_KeyModule_Provide_LazyMapKey.lazyClassKeyName, AnalyticsViewModel_HiltModules.KeyModule.provide()).put(BudgetsViewModel_HiltModules_KeyModule_Provide_LazyMapKey.lazyClassKeyName, BudgetsViewModel_HiltModules.KeyModule.provide()).put(CategoriesViewModel_HiltModules_KeyModule_Provide_LazyMapKey.lazyClassKeyName, CategoriesViewModel_HiltModules.KeyModule.provide()).put(DashboardViewModel_HiltModules_KeyModule_Provide_LazyMapKey.lazyClassKeyName, DashboardViewModel_HiltModules.KeyModule.provide()).put(MainActivityViewModel_HiltModules_KeyModule_Provide_LazyMapKey.lazyClassKeyName, MainActivityViewModel_HiltModules.KeyModule.provide()).put(SettingsViewModel_HiltModules_KeyModule_Provide_LazyMapKey.lazyClassKeyName, SettingsViewModel_HiltModules.KeyModule.provide()).put(TransactionsViewModel_HiltModules_KeyModule_Provide_LazyMapKey.lazyClassKeyName, TransactionsViewModel_HiltModules.KeyModule.provide()).build());
    }

    @Override
    public ViewModelComponentBuilder getViewModelComponentBuilder() {
      return new ViewModelCBuilder(singletonCImpl, activityRetainedCImpl);
    }

    @Override
    public FragmentComponentBuilder fragmentComponentBuilder() {
      return new FragmentCBuilder(singletonCImpl, activityRetainedCImpl, activityCImpl);
    }

    @Override
    public ViewComponentBuilder viewComponentBuilder() {
      return new ViewCBuilder(singletonCImpl, activityRetainedCImpl, activityCImpl);
    }
  }

  private static final class ViewModelCImpl extends JerriesExpenseApplication_HiltComponents.ViewModelC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ViewModelCImpl viewModelCImpl = this;

    private Provider<AccountsViewModel> accountsViewModelProvider;

    private Provider<AddTransactionViewModel> addTransactionViewModelProvider;

    private Provider<AnalyticsViewModel> analyticsViewModelProvider;

    private Provider<BudgetsViewModel> budgetsViewModelProvider;

    private Provider<CategoriesViewModel> categoriesViewModelProvider;

    private Provider<DashboardViewModel> dashboardViewModelProvider;

    private Provider<MainActivityViewModel> mainActivityViewModelProvider;

    private Provider<SettingsViewModel> settingsViewModelProvider;

    private Provider<TransactionsViewModel> transactionsViewModelProvider;

    private ViewModelCImpl(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, SavedStateHandle savedStateHandleParam,
        ViewModelLifecycle viewModelLifecycleParam) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;

      initialize(savedStateHandleParam, viewModelLifecycleParam);

    }

    private ObserveAccountsUseCase observeAccountsUseCase() {
      return new ObserveAccountsUseCase(singletonCImpl.offlineFirstAccountRepositoryProvider.get());
    }

    private ObserveAccountBalancesUseCase observeAccountBalancesUseCase() {
      return new ObserveAccountBalancesUseCase(singletonCImpl.offlineFirstAccountRepositoryProvider.get());
    }

    private ObserveUserPreferencesUseCase observeUserPreferencesUseCase() {
      return new ObserveUserPreferencesUseCase(singletonCImpl.userDataPreferencesRepositoryProvider.get());
    }

    private ObserveCategoriesUseCase observeCategoriesUseCase() {
      return new ObserveCategoriesUseCase(singletonCImpl.offlineFirstCategoryRepositoryProvider.get());
    }

    private AddTransactionUseCase addTransactionUseCase() {
      return new AddTransactionUseCase(singletonCImpl.offlineFirstTransactionRepositoryProvider.get(), singletonCImpl.offlineFirstAccountRepositoryProvider.get(), singletonCImpl.offlineFirstCategoryRepositoryProvider.get());
    }

    private ObserveTotalBalanceUseCase observeTotalBalanceUseCase() {
      return new ObserveTotalBalanceUseCase(singletonCImpl.offlineFirstAccountRepositoryProvider.get());
    }

    private ObserveRecentTransactionsUseCase observeRecentTransactionsUseCase() {
      return new ObserveRecentTransactionsUseCase(singletonCImpl.offlineFirstTransactionRepositoryProvider.get());
    }

    private SetThemeSettingUseCase setThemeSettingUseCase() {
      return new SetThemeSettingUseCase(singletonCImpl.userDataPreferencesRepositoryProvider.get());
    }

    private SetDynamicColorsUseCase setDynamicColorsUseCase() {
      return new SetDynamicColorsUseCase(singletonCImpl.userDataPreferencesRepositoryProvider.get());
    }

    private SetCurrencyCodeUseCase setCurrencyCodeUseCase() {
      return new SetCurrencyCodeUseCase(singletonCImpl.userDataPreferencesRepositoryProvider.get());
    }

    @SuppressWarnings("unchecked")
    private void initialize(final SavedStateHandle savedStateHandleParam,
        final ViewModelLifecycle viewModelLifecycleParam) {
      this.accountsViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 0);
      this.addTransactionViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 1);
      this.analyticsViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 2);
      this.budgetsViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 3);
      this.categoriesViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 4);
      this.dashboardViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 5);
      this.mainActivityViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 6);
      this.settingsViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 7);
      this.transactionsViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 8);
    }

    @Override
    public Map<Class<?>, javax.inject.Provider<ViewModel>> getHiltViewModelMap() {
      return LazyClassKeyMap.<javax.inject.Provider<ViewModel>>of(MapBuilder.<String, javax.inject.Provider<ViewModel>>newMapBuilder(9).put(AccountsViewModel_HiltModules_BindsModule_Binds_LazyMapKey.lazyClassKeyName, ((Provider) accountsViewModelProvider)).put(AddTransactionViewModel_HiltModules_BindsModule_Binds_LazyMapKey.lazyClassKeyName, ((Provider) addTransactionViewModelProvider)).put(AnalyticsViewModel_HiltModules_BindsModule_Binds_LazyMapKey.lazyClassKeyName, ((Provider) analyticsViewModelProvider)).put(BudgetsViewModel_HiltModules_BindsModule_Binds_LazyMapKey.lazyClassKeyName, ((Provider) budgetsViewModelProvider)).put(CategoriesViewModel_HiltModules_BindsModule_Binds_LazyMapKey.lazyClassKeyName, ((Provider) categoriesViewModelProvider)).put(DashboardViewModel_HiltModules_BindsModule_Binds_LazyMapKey.lazyClassKeyName, ((Provider) dashboardViewModelProvider)).put(MainActivityViewModel_HiltModules_BindsModule_Binds_LazyMapKey.lazyClassKeyName, ((Provider) mainActivityViewModelProvider)).put(SettingsViewModel_HiltModules_BindsModule_Binds_LazyMapKey.lazyClassKeyName, ((Provider) settingsViewModelProvider)).put(TransactionsViewModel_HiltModules_BindsModule_Binds_LazyMapKey.lazyClassKeyName, ((Provider) transactionsViewModelProvider)).build());
    }

    @Override
    public Map<Class<?>, Object> getHiltViewModelAssistedMap() {
      return Collections.<Class<?>, Object>emptyMap();
    }

    private static final class SwitchingProvider<T> implements Provider<T> {
      private final SingletonCImpl singletonCImpl;

      private final ActivityRetainedCImpl activityRetainedCImpl;

      private final ViewModelCImpl viewModelCImpl;

      private final int id;

      SwitchingProvider(SingletonCImpl singletonCImpl, ActivityRetainedCImpl activityRetainedCImpl,
          ViewModelCImpl viewModelCImpl, int id) {
        this.singletonCImpl = singletonCImpl;
        this.activityRetainedCImpl = activityRetainedCImpl;
        this.viewModelCImpl = viewModelCImpl;
        this.id = id;
      }

      @SuppressWarnings("unchecked")
      @Override
      public T get() {
        switch (id) {
          case 0: // com.jerries.expense.feature.accounts.AccountsViewModel 
          return (T) new AccountsViewModel(viewModelCImpl.observeAccountsUseCase(), viewModelCImpl.observeAccountBalancesUseCase(), viewModelCImpl.observeUserPreferencesUseCase());

          case 1: // com.jerries.expense.feature.addtransaction.AddTransactionViewModel 
          return (T) new AddTransactionViewModel(viewModelCImpl.observeAccountsUseCase(), viewModelCImpl.observeCategoriesUseCase(), viewModelCImpl.addTransactionUseCase(), singletonCImpl.systemTimeProvider.get(), singletonCImpl.uuidIdGeneratorProvider.get());

          case 2: // com.jerries.expense.feature.analytics.AnalyticsViewModel 
          return (T) new AnalyticsViewModel(viewModelCImpl.observeUserPreferencesUseCase());

          case 3: // com.jerries.expense.feature.budgets.BudgetsViewModel 
          return (T) new BudgetsViewModel(viewModelCImpl.observeUserPreferencesUseCase());

          case 4: // com.jerries.expense.feature.categories.CategoriesViewModel 
          return (T) new CategoriesViewModel(viewModelCImpl.observeCategoriesUseCase());

          case 5: // com.jerries.expense.feature.dashboard.DashboardViewModel 
          return (T) new DashboardViewModel(viewModelCImpl.observeTotalBalanceUseCase(), viewModelCImpl.observeRecentTransactionsUseCase(), viewModelCImpl.observeCategoriesUseCase(), viewModelCImpl.observeUserPreferencesUseCase(), singletonCImpl.systemTimeProvider.get());

          case 6: // com.jerries.expense.MainActivityViewModel 
          return (T) new MainActivityViewModel(viewModelCImpl.observeUserPreferencesUseCase());

          case 7: // com.jerries.expense.feature.settings.SettingsViewModel 
          return (T) new SettingsViewModel(viewModelCImpl.observeUserPreferencesUseCase(), viewModelCImpl.setThemeSettingUseCase(), viewModelCImpl.setDynamicColorsUseCase(), viewModelCImpl.setCurrencyCodeUseCase());

          case 8: // com.jerries.expense.feature.transactions.TransactionsViewModel 
          return (T) new TransactionsViewModel(singletonCImpl.offlineFirstTransactionRepositoryProvider.get(), viewModelCImpl.observeCategoriesUseCase(), viewModelCImpl.observeUserPreferencesUseCase(), singletonCImpl.systemTimeProvider.get());

          default: throw new AssertionError(id);
        }
      }
    }
  }

  private static final class ActivityRetainedCImpl extends JerriesExpenseApplication_HiltComponents.ActivityRetainedC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl = this;

    private Provider<ActivityRetainedLifecycle> provideActivityRetainedLifecycleProvider;

    private ActivityRetainedCImpl(SingletonCImpl singletonCImpl,
        SavedStateHandleHolder savedStateHandleHolderParam) {
      this.singletonCImpl = singletonCImpl;

      initialize(savedStateHandleHolderParam);

    }

    @SuppressWarnings("unchecked")
    private void initialize(final SavedStateHandleHolder savedStateHandleHolderParam) {
      this.provideActivityRetainedLifecycleProvider = DoubleCheck.provider(new SwitchingProvider<ActivityRetainedLifecycle>(singletonCImpl, activityRetainedCImpl, 0));
    }

    @Override
    public ActivityComponentBuilder activityComponentBuilder() {
      return new ActivityCBuilder(singletonCImpl, activityRetainedCImpl);
    }

    @Override
    public ActivityRetainedLifecycle getActivityRetainedLifecycle() {
      return provideActivityRetainedLifecycleProvider.get();
    }

    private static final class SwitchingProvider<T> implements Provider<T> {
      private final SingletonCImpl singletonCImpl;

      private final ActivityRetainedCImpl activityRetainedCImpl;

      private final int id;

      SwitchingProvider(SingletonCImpl singletonCImpl, ActivityRetainedCImpl activityRetainedCImpl,
          int id) {
        this.singletonCImpl = singletonCImpl;
        this.activityRetainedCImpl = activityRetainedCImpl;
        this.id = id;
      }

      @SuppressWarnings("unchecked")
      @Override
      public T get() {
        switch (id) {
          case 0: // dagger.hilt.android.ActivityRetainedLifecycle 
          return (T) ActivityRetainedComponentManager_LifecycleModule_ProvideActivityRetainedLifecycleFactory.provideActivityRetainedLifecycle();

          default: throw new AssertionError(id);
        }
      }
    }
  }

  private static final class ServiceCImpl extends JerriesExpenseApplication_HiltComponents.ServiceC {
    private final SingletonCImpl singletonCImpl;

    private final ServiceCImpl serviceCImpl = this;

    private ServiceCImpl(SingletonCImpl singletonCImpl, Service serviceParam) {
      this.singletonCImpl = singletonCImpl;


    }
  }

  private static final class SingletonCImpl extends JerriesExpenseApplication_HiltComponents.SingletonC {
    private final ApplicationContextModule applicationContextModule;

    private final SingletonCImpl singletonCImpl = this;

    private Provider<ExpenseDatabase> provideDatabaseProvider;

    private Provider<OfflineFirstCategoryRepository> offlineFirstCategoryRepositoryProvider;

    private Provider<OfflineFirstAccountRepository> offlineFirstAccountRepositoryProvider;

    private Provider<UserPreferencesDataSource> userPreferencesDataSourceProvider;

    private Provider<UserDataPreferencesRepository> userDataPreferencesRepositoryProvider;

    private Provider<CoroutineScope> provideApplicationScopeProvider;

    private Provider<OfflineFirstTransactionRepository> offlineFirstTransactionRepositoryProvider;

    private Provider<SystemTimeProvider> systemTimeProvider;

    private Provider<UuidIdGenerator> uuidIdGeneratorProvider;

    private SingletonCImpl(ApplicationContextModule applicationContextModuleParam) {
      this.applicationContextModule = applicationContextModuleParam;
      initialize(applicationContextModuleParam);

    }

    private HiltWorkerFactory hiltWorkerFactory() {
      return WorkerFactoryModule_ProvideFactoryFactory.provideFactory(Collections.<String, javax.inject.Provider<WorkerAssistedFactory<? extends ListenableWorker>>>emptyMap());
    }

    private CategoryDao categoryDao() {
      return DatabaseModule_ProvideCategoryDaoFactory.provideCategoryDao(provideDatabaseProvider.get());
    }

    private AccountDao accountDao() {
      return DatabaseModule_ProvideAccountDaoFactory.provideAccountDao(provideDatabaseProvider.get());
    }

    private TransactionDao transactionDao() {
      return DatabaseModule_ProvideTransactionDaoFactory.provideTransactionDao(provideDatabaseProvider.get());
    }

    private InitializeAppDataUseCase initializeAppDataUseCase() {
      return new InitializeAppDataUseCase(offlineFirstCategoryRepositoryProvider.get(), offlineFirstAccountRepositoryProvider.get(), userDataPreferencesRepositoryProvider.get());
    }

    @SuppressWarnings("unchecked")
    private void initialize(final ApplicationContextModule applicationContextModuleParam) {
      this.provideDatabaseProvider = DoubleCheck.provider(new SwitchingProvider<ExpenseDatabase>(singletonCImpl, 1));
      this.offlineFirstCategoryRepositoryProvider = DoubleCheck.provider(new SwitchingProvider<OfflineFirstCategoryRepository>(singletonCImpl, 0));
      this.offlineFirstAccountRepositoryProvider = DoubleCheck.provider(new SwitchingProvider<OfflineFirstAccountRepository>(singletonCImpl, 2));
      this.userPreferencesDataSourceProvider = DoubleCheck.provider(new SwitchingProvider<UserPreferencesDataSource>(singletonCImpl, 4));
      this.userDataPreferencesRepositoryProvider = DoubleCheck.provider(new SwitchingProvider<UserDataPreferencesRepository>(singletonCImpl, 3));
      this.provideApplicationScopeProvider = DoubleCheck.provider(new SwitchingProvider<CoroutineScope>(singletonCImpl, 5));
      this.offlineFirstTransactionRepositoryProvider = DoubleCheck.provider(new SwitchingProvider<OfflineFirstTransactionRepository>(singletonCImpl, 6));
      this.systemTimeProvider = DoubleCheck.provider(new SwitchingProvider<SystemTimeProvider>(singletonCImpl, 7));
      this.uuidIdGeneratorProvider = DoubleCheck.provider(new SwitchingProvider<UuidIdGenerator>(singletonCImpl, 8));
    }

    @Override
    public void injectJerriesExpenseApplication(
        JerriesExpenseApplication jerriesExpenseApplication) {
      injectJerriesExpenseApplication2(jerriesExpenseApplication);
    }

    @Override
    public Set<Boolean> getDisableFragmentGetContextFix() {
      return Collections.<Boolean>emptySet();
    }

    @Override
    public ActivityRetainedComponentBuilder retainedComponentBuilder() {
      return new ActivityRetainedCBuilder(singletonCImpl);
    }

    @Override
    public ServiceComponentBuilder serviceComponentBuilder() {
      return new ServiceCBuilder(singletonCImpl);
    }

    private JerriesExpenseApplication injectJerriesExpenseApplication2(
        JerriesExpenseApplication instance) {
      JerriesExpenseApplication_MembersInjector.injectWorkerFactory(instance, hiltWorkerFactory());
      JerriesExpenseApplication_MembersInjector.injectInitializeAppData(instance, initializeAppDataUseCase());
      JerriesExpenseApplication_MembersInjector.injectApplicationScope(instance, provideApplicationScopeProvider.get());
      return instance;
    }

    private static final class SwitchingProvider<T> implements Provider<T> {
      private final SingletonCImpl singletonCImpl;

      private final int id;

      SwitchingProvider(SingletonCImpl singletonCImpl, int id) {
        this.singletonCImpl = singletonCImpl;
        this.id = id;
      }

      @SuppressWarnings("unchecked")
      @Override
      public T get() {
        switch (id) {
          case 0: // com.jerries.expense.data.repository.OfflineFirstCategoryRepository 
          return (T) new OfflineFirstCategoryRepository(singletonCImpl.categoryDao());

          case 1: // com.jerries.expense.data.local.database.ExpenseDatabase 
          return (T) DatabaseModule_ProvideDatabaseFactory.provideDatabase(ApplicationContextModule_ProvideContextFactory.provideContext(singletonCImpl.applicationContextModule));

          case 2: // com.jerries.expense.data.repository.OfflineFirstAccountRepository 
          return (T) new OfflineFirstAccountRepository(singletonCImpl.accountDao(), singletonCImpl.transactionDao());

          case 3: // com.jerries.expense.data.repository.UserDataPreferencesRepository 
          return (T) new UserDataPreferencesRepository(singletonCImpl.userPreferencesDataSourceProvider.get());

          case 4: // com.jerries.expense.data.preferences.UserPreferencesDataSource 
          return (T) new UserPreferencesDataSource(ApplicationContextModule_ProvideContextFactory.provideContext(singletonCImpl.applicationContextModule));

          case 5: // @com.jerries.expense.core.common.ApplicationScope kotlinx.coroutines.CoroutineScope 
          return (T) CoroutineModule_ProvideApplicationScopeFactory.provideApplicationScope(CoroutineModule_ProvideDefaultDispatcherFactory.provideDefaultDispatcher());

          case 6: // com.jerries.expense.data.repository.OfflineFirstTransactionRepository 
          return (T) new OfflineFirstTransactionRepository(singletonCImpl.transactionDao());

          case 7: // com.jerries.expense.core.common.SystemTimeProvider 
          return (T) new SystemTimeProvider();

          case 8: // com.jerries.expense.core.common.UuidIdGenerator 
          return (T) new UuidIdGenerator();

          default: throw new AssertionError(id);
        }
      }
    }
  }
}
