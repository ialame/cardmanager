import {cloneDeep, isNil, isString} from "lodash";
import {Consumer} from "@/types";
import {inject, InjectionKey} from "vue";
import {provide} from "vue-demi";
import {MaybeRefOrGetter, toValue} from "@vueuse/core";
import {isInComponentSetup} from "@/vue/composables/ComposablesHelper";

export namespace ModelComposables {

	type Getter<T> = MaybeRefOrGetter<T | undefined>;
	type Emit<T, S extends string> = (event: S, t: T) => void;

	type Update<T> = Consumer<Consumer<T>>;

	export function useUpdateModel<T>(model: Getter<T>, emit: Emit<T, 'update:modelValue'>): Update<T>;
	export function useUpdateModel<T, S extends string>(model: Getter<T>, emit: Emit<T, S>, name: S): Update<T>;

	export function useUpdateModel<T, S extends string>(model: Getter<T>, emit: Emit<T, S | 'update:modelValue'>, name?: S): Update<T> {
		const n = name ? name : 'update:modelValue';

		return (setter: Consumer<T>) => {
			const copy = cloneDeep(toValue(model));

			if (copy !== undefined) {
				setter(copy);
				emit(n, copy);
			}
		}
	}

	export function createInjectionState<Arguments extends Array<any>, Return>(composable: (...args: Arguments) => Return, name?: string | symbol): readonly [useProvidingState: (...args: Arguments) => Return, useInjectedState: () => Return | undefined, key: string | InjectionKey<Return>] {
		const key: string | InjectionKey<Return> = isString(name) || isNil(name) ? Symbol(name || 'InjectionState') : name;
		const useProvidingState = (...args: Arguments) => {
			const c = composable(...args);

			if (isInComponentSetup()) {
				provide(key, c);
			}
			return c;
		}
		const useInjectedState = () => inject(key, undefined);
		return [useProvidingState, useInjectedState, key]
	}
}
