import {ModelComposables} from "@/vue/composables/model/ModelComposables";
import {MaybeRefOrGetter, toValue} from "@vueuse/core";
import {computed, Ref, ref} from "vue";

export type ConsolidationSource<T> = {
    value?: T;
    weight?: number;
    name?: string;
    link?: string;
    blocking?: boolean;
};

type UseConsolidationSourcesReturn<T> = {
    sources: Ref<ConsolidationSource<T>[]>;
    names: Ref<string[]>;
}

const [_useProvideConsolidationSources, _useConsolidationSources, _consolidationSourcesKey] = ModelComposables.createInjectionState(<T>(sources: MaybeRefOrGetter<ConsolidationSource<T>[]>, names?: MaybeRefOrGetter<string[]>): UseConsolidationSourcesReturn<T> => ({
    sources: computed<ConsolidationSource<T>[]>(() => toValue(sources)),
    names: computed<string[]>(() => toValue(names) ?? [])
}), "consolidation sources");

export const consolidationSourcesKey = _consolidationSourcesKey as symbol;
export const useConsolidationSources = <T>(): UseConsolidationSourcesReturn<T> => _useConsolidationSources() as UseConsolidationSourcesReturn<T> ?? (() => ({
    sources: ref([]),
    names: ref([])
}))();
export const useProvideConsolidationSources = <T>(sources: MaybeRefOrGetter<ConsolidationSource<T>[]>, names?: MaybeRefOrGetter<string[]>): UseConsolidationSourcesReturn<T> => _useProvideConsolidationSources(sources, names) as UseConsolidationSourcesReturn<T>;
