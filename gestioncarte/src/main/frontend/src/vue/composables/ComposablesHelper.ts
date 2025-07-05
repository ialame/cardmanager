import {MaybeRefOrGetter, toValue} from "@vueuse/core";
import {computed, getCurrentInstance, Ref} from "vue";


export namespace ComposablesHelper {

    export type Unref<T> = T extends MaybeRefOrGetter<infer U> ? U : T;

    export type Unwrapped<T, U extends (keyof T | undefined) = undefined> = Unref<{
        [key in keyof T]: key extends U ? T[key] : Unref<T[key]>
    }>;

    export type MaybeUnwrapped<T, U extends (keyof T | undefined) = undefined> = MaybeRefOrGetter<Unwrapped<T, U> | T>;
    export const unwrap = <T, U extends (keyof T | undefined) = undefined>(value: MaybeRefOrGetter<T>, ...ignoredProperties: U[]): Ref<Unwrapped<T, U>> => computed(() => {
        const v = toValue(value);
        const result: Unwrapped<T, U> = {} as any;

        for (const key in v) {
            (result as any)[key] = ignoredProperties?.includes(key as any) ? v[key] : toValue(v[key]);
        }
        return result;
    });
}

export function isInComponentSetup() {
    return !!getCurrentInstance();
}
export function isInBrowser() {
    return !!window && typeof window !== 'undefined';
}
