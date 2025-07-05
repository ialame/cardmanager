import {ModelComposables} from "@/vue/composables/model/ModelComposables";
import {MaybeRefOrGetter, toValue} from "@vueuse/core";
import {computed, ref} from "vue";

const [_useProvideAdvancedFormSide, _useAdvancedFormSide, _advancedFormSideKey] = ModelComposables.createInjectionState((side: MaybeRefOrGetter<number>) => computed(() => toValue(side)), "advanced form side");

export const advancedFormSideKey = _advancedFormSideKey as symbol;
export const useAdvancedFormSide = () => _useAdvancedFormSide() ?? (() => ref(-1))();
export const useProvideAdvancedFormSide = _useProvideAdvancedFormSide;
