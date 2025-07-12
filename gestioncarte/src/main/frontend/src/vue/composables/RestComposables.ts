import {
    createSharedComposable,
    get,
    MaybeRef,
    MaybeRefOrGetter,
    useAsyncState,
    UseAsyncStateOptions,
    UseAsyncStateReturn,
    useIntervalFn,
    useStorageAsync
} from "@vueuse/core";
import rest from "@/rest";
import {Ref, UnwrapRef, watch, WatchSource} from "vue";

export namespace RestComposables {

    interface UseRestComposableReturn<T, Params extends any[], Shallow extends boolean> extends UseAsyncStateReturn<T, Params, Shallow> {
        refresh: () => Promise<T>;
    }
    type Fetcher<T> = (() => Promise<T>) | string;
    type RestComposable<T, Params extends any[], FullState extends boolean, Shallow extends boolean> = FullState extends true ? UseRestComposableReturn<T, Params, Shallow> : (Shallow extends true ? Ref<T> : Ref<UnwrapRef<T>>);
    interface Options<FullState extends boolean, Shallow extends boolean> extends UseAsyncStateOptions<Shallow> {
        storageKey?: string;
        fullState?: FullState;
        interval?: MaybeRefOrGetter<number>;
        watch?: WatchSource<any>[] | WatchSource<any>;
    }

    async function getPromise<T>(fetcher: Fetcher<T>, storage: Ref<T> | undefined) { // eslint-disable-line no-inner-declarations
        const value = fetcher instanceof Function ? await fetcher() : (await rest.get(fetcher) as T);

        if (storage) {
            storage.value = value;
        }
        return value;
    }

    export function useRestComposable<T, Params extends any[] = [], FullState extends boolean = false, Shallow extends boolean = true>(fetcher: Fetcher<T>, defaultValue: MaybeRef<T>, options?: Options<FullState, Shallow>): RestComposable<T, Params, FullState, Shallow> {
        const unwrappedDefaultValue = get(defaultValue);
        let storage: Ref<T> | undefined;

        if (options?.storageKey) {
            storage = useStorageAsync<T>(options?.storageKey, unwrappedDefaultValue);
        }

        const value = useAsyncState(getPromise(fetcher, storage), storage?.value ?? unwrappedDefaultValue,  options) as UseRestComposableReturn<T, Params, Shallow>;

        value.refresh = async () => value.state.value = await getPromise(fetcher, storage);
        if (options?.interval) {
            useIntervalFn(value.refresh, options.interval);
        }
        if (options?.watch && (options.watch instanceof Array ? options.watch : [options.watch]).length > 0) {
            watch(options.watch, value.refresh, {deep: true}); // TODO watchEffect
        }
        if (options?.fullState) {
            return value as RestComposable<T, Params, FullState, Shallow>;
        } else {
            return value.state as RestComposable<T, Params, FullState, Shallow>;
        }
    }

    export function createSharedRestComposable<T, FullState extends boolean = false, Shallow extends boolean = true>(fetcher: Fetcher<T>, defaultValue: MaybeRef<T>, options?: Options<FullState, Shallow>) {
        return createSharedComposable(() => useRestComposable(fetcher, defaultValue, options));
    }
}
