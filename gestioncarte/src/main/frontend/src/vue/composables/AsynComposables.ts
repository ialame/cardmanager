import {computed, ComputedGetter, WritableComputedRef} from "vue";
import {computedAsync} from "@vueuse/core";

export type WritableAsyncComputedOptions<T> = {
    get: ComputedGetter<Promise<T> | T>;
    set: (v: T) => Promise<void> | void;
}

export function computedAsyncGetSet<T extends object>(options: WritableAsyncComputedOptions<T>): WritableComputedRef<T> {
    const cav= computedAsync<T>(() => options.get());

    return computed({
        get: () => cav.value,
        set: (value: T) => options.set(value)
    });
}
