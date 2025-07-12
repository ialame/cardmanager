import {Ref, ref} from "vue";

export namespace MergeComposables {

    export type MergeBindings = {[k: string]: boolean}; 

    export function useMergeBindings(): { mergeBindings: Ref<MergeBindings>; setMergeBinding: (key: string, value: boolean) => void } {
        const mergeBindings = ref<MergeBindings>({});
        const setMergeBinding = (key: string, value: boolean) => mergeBindings.value[key] = value;

        return {mergeBindings, setMergeBinding};
    }
}
