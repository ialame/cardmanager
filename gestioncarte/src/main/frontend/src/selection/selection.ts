import {invoke, MaybeRefOrGetter, toValue} from "@vueuse/core";
import {computed, Ref, ref, watch} from "vue";
import {ModelComposables} from "@/vue/composables/model/ModelComposables";

type SelectionReturn<T> ={
    selected: Ref<T[]>,
    isSelected: (t: T) => boolean,
    select: (t?: T, value?: boolean) => void,
    revert: () => void,
}

const [_useProvideSelection, _useSelection, _useSelectionKey] = ModelComposables.createInjectionState(<T, Id>(array: MaybeRefOrGetter<T[]>, idGetter: (t: T) => Id) => {
    const selectedIds = ref<Id[]>([]) as Ref<Id[]>;
    const selected = computed<T[]>(() => toValue(array).filter(i => selectedIds.value.includes(idGetter(i))));

    const isSelected = (t: T) => selectedIds.value.includes(idGetter(t));
    const select = (t?: T, value?: boolean) => {
        if (!t) {
            return;
        }

        const id = idGetter(t);
        const s = value ?? !isSelected(t);

        if (s) {
            selectedIds.value.push(id);
        } else {
            selectedIds.value.splice(selectedIds.value.indexOf(id), 1);
        }
    }
    const revert = () => selectedIds.value = toValue(array)
        .map(idGetter)
        .filter(id => !selectedIds.value.includes(id));
    watch(() => toValue(array), a => selectedIds.value = selectedIds.value.filter(id => a.some(i => idGetter(i) === id)));
    return { selected, isSelected, select, revert };
}, "selection");

export const useSelectionKey = _useSelectionKey as symbol;
export const useProvideSelection = <T, Id>(array: MaybeRefOrGetter<T[]>, idGetter: (t: T) => Id) => _useProvideSelection(array, idGetter as any) as SelectionReturn<T>;
export const useSelection = <T>() => _useSelection() as SelectionReturn<T> || invoke(() => { throw new Error("advanced form context not provided"); });
