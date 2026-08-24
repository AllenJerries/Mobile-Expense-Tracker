package com.jerries.expense.feature.settings;

import com.jerries.expense.domain.usecase.ObserveUserPreferencesUseCase;
import com.jerries.expense.domain.usecase.SetCurrencyCodeUseCase;
import com.jerries.expense.domain.usecase.SetDynamicColorsUseCase;
import com.jerries.expense.domain.usecase.SetThemeSettingUseCase;
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
public final class SettingsViewModel_Factory implements Factory<SettingsViewModel> {
  private final Provider<ObserveUserPreferencesUseCase> observeUserPreferencesProvider;

  private final Provider<SetThemeSettingUseCase> setThemeSettingProvider;

  private final Provider<SetDynamicColorsUseCase> setDynamicColorsProvider;

  private final Provider<SetCurrencyCodeUseCase> setCurrencyCodeProvider;

  public SettingsViewModel_Factory(
      Provider<ObserveUserPreferencesUseCase> observeUserPreferencesProvider,
      Provider<SetThemeSettingUseCase> setThemeSettingProvider,
      Provider<SetDynamicColorsUseCase> setDynamicColorsProvider,
      Provider<SetCurrencyCodeUseCase> setCurrencyCodeProvider) {
    this.observeUserPreferencesProvider = observeUserPreferencesProvider;
    this.setThemeSettingProvider = setThemeSettingProvider;
    this.setDynamicColorsProvider = setDynamicColorsProvider;
    this.setCurrencyCodeProvider = setCurrencyCodeProvider;
  }

  @Override
  public SettingsViewModel get() {
    return newInstance(observeUserPreferencesProvider.get(), setThemeSettingProvider.get(), setDynamicColorsProvider.get(), setCurrencyCodeProvider.get());
  }

  public static SettingsViewModel_Factory create(
      Provider<ObserveUserPreferencesUseCase> observeUserPreferencesProvider,
      Provider<SetThemeSettingUseCase> setThemeSettingProvider,
      Provider<SetDynamicColorsUseCase> setDynamicColorsProvider,
      Provider<SetCurrencyCodeUseCase> setCurrencyCodeProvider) {
    return new SettingsViewModel_Factory(observeUserPreferencesProvider, setThemeSettingProvider, setDynamicColorsProvider, setCurrencyCodeProvider);
  }

  public static SettingsViewModel newInstance(ObserveUserPreferencesUseCase observeUserPreferences,
      SetThemeSettingUseCase setThemeSetting, SetDynamicColorsUseCase setDynamicColors,
      SetCurrencyCodeUseCase setCurrencyCode) {
    return new SettingsViewModel(observeUserPreferences, setThemeSetting, setDynamicColors, setCurrencyCode);
  }
}
