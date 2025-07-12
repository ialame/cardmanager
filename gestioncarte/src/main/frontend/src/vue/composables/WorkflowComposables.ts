import {MaybeRefOrGetter, toValue} from "@vueuse/core";
import {computed, ref} from "vue";

export namespace WorkflowComposables {

    type UseWorkflowOptions<T> = {
        isValid: (entity: T) => boolean;
    }

    export function useWorkflow<T>(entries: MaybeRefOrGetter<T[]>, options?: UseWorkflowOptions<T>) {
        const index = ref<number | undefined>(undefined);
        const isValid = (entry: T): boolean => entry !== undefined && (options?.isValid(entry) ?? false);
        const nextIndex = (e: T[], startIndex: number) => {
            for (let i = startIndex; i < e.length; i++) {
                if (!isValid(e[i])) {
                    return i;
                }
            }
            return undefined;
        };
        const next = (pi?: number) => {
            const e = toValue(entries);

            if ( pi !== undefined && pi >= 0 && pi < e.length) {
                if (pi === index.value) {
                    return pi;
                }
                index.value = pi;
                return pi;
            }

            let i = nextIndex(e, index.value !== undefined ? index.value + 1 : 0);

            if (i === undefined) {
                i = nextIndex(e, 0);
            }
            index.value = i;
            return i;
        };
        const reset = () => index.value = undefined;
        const current = computed(() => index.value !== undefined ? toValue(entries)[index.value] : undefined);
        const total = computed(() => toValue(entries)?.length ?? 0);
        const progress = computed(() => toValue(entries)?.filter(isValid).length ?? 0);
        const complete = computed(() => progress.value === total.value);

        return { index, isValid, next, reset, current, progress, total, complete };
    }

}
