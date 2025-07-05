import {AsyncPredicate} from "@/types";
import {LocalizationCode} from "@/localization";
import {ModelComposables} from "@/vue/composables/model/ModelComposables";
import {MaybeRefOrGetter, toRef, toValue} from "@vueuse/core";
import {computed, Ref} from "vue";

export type AdvancedFormTranslationContext = {
    fixedLocalizations: Ref<boolean>;
    removeConfirmation: Ref<AsyncPredicate<LocalizationCode> | undefined>;
}

const [_useProvideAdvancedFormTranslationContext, _useAdvancedFormTranslationContext, _advancedFormTranslationContextKey] = ModelComposables.createInjectionState((fixedLocalizations?: MaybeRefOrGetter<boolean>, removeConfirmation?: MaybeRefOrGetter<AsyncPredicate<LocalizationCode>>): AdvancedFormTranslationContext => ({
    fixedLocalizations: computed(() => !!toValue(fixedLocalizations)),
    removeConfirmation: toRef(removeConfirmation)
}));

export const useProvideAdvancedFormTranslationContext = _useProvideAdvancedFormTranslationContext;
export const useAdvancedFormTranslationContext = _useAdvancedFormTranslationContext;
export const advancedFormTranslationContextKey = _advancedFormTranslationContextKey as symbol;
