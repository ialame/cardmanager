import {computed, nextTick, onMounted, onUnmounted, ref, Ref} from "vue";
import {ModelComposables} from "@/vue/composables/model/ModelComposables";
import {cloneDeep, get, isEqual, isString, set} from "lodash";
import {ComposablesHelper} from "@/vue/composables/ComposablesHelper";
import {computedAsync, MaybeRefOrGetter, toValue} from "@vueuse/core";
import {
    AmbiguousValidationRules,
    getRules,
    getStrongestValidationStatus,
    getValidationClass,
    isReviewable,
    Requirement,
    validate,
    ValidationResult,
    ValidationResultMap,
    ValidationRules,
    ValidationStatus
} from "@/validation";

import {addDevtoolTags, advancedFormLayer} from "@/vue/devtool";
import {arePathEqual, Path, pathToString} from "@/path";
import log from "loglevel";
import {timeout} from "@/retriever";
import {useRaise} from "@/alert";
import {useAdvancedFormSide} from "@components/form/advanced/merge";
import {useAdvancedFormTab, useAdvancedFormTabControl} from "@components/form/advanced/tab";
import {useProvideAlignmentContext} from "@components/form/advanced/merge/alignment";

export type BaseAdvancedFormContext<T> = {
    data: Ref<T>,
    mergeSources: Ref<T[]>
};
export type AdvancedFormInputElement = {
    readonly key: symbol,
    path: () => Path,
    status: () => ValidationStatus,
    element: () => HTMLElement | undefined,
    side: () => number,
    validate: () => Promise<boolean>
};
export type AdvancedFormTrigger<T> = (data: T, path: Path) => void;
type PrivateContext<T> = BaseAdvancedFormContext<T> & {
    update: (v: T, path: Path) => void,
    disabled: Ref<boolean>,
    inputs: Ref<AdvancedFormInputElement[]>,
    addInput: (i: AdvancedFormInputElement) => void,
    removeInput: (i: AdvancedFormInputElement) => void,
    validationResults: Ref<ValidationResultMap>,
    reviewedPaths: Ref<Path[]>,
    optionalPaths: Ref<Path[]>,
    hoverPath: Ref<Path>,
    rules: Ref<ValidationRules<T>>,
    validating: Ref<boolean>,
    triggers: Ref<AdvancedFormTrigger<T>[]>
};

type AdvancedFormContextOptions<T> = {
    mergeSources?: MaybeRefOrGetter<T[] | undefined>,
    disabled?: MaybeRefOrGetter<boolean | undefined>,
    rules?: MaybeRefOrGetter<ValidationRules<T>>,
    reviewedPaths?: Ref<Path[]>,
    optionalPaths?: Ref<Path[]>,
}

const [_useProvideAdvancedFormContext, _useAdvancedFormContext, _advancedFormContextKey] = ModelComposables.createInjectionState(<T>(value: Ref<T>, options?: AdvancedFormContextOptions<T>): PrivateContext<T> => {
    const trackEnd = advancedFormLayer.trackStart("ready");
    const ready = ref(false);
    const raise = useRaise();
    const update = (v: T, path: Path) => {
        if (!isEqual(value.value, v)) {
            triggers.value.forEach(t => t(v, path));
            value.value = v;
        }
    }
    const inputs = ref<AdvancedFormInputElement[]>([]);
    const addInput = (input: AdvancedFormInputElement) => {
        inputs.value.push(input);
    }
    const removeInput = (input: AdvancedFormInputElement) => {
        inputs.value = inputs.value.filter(i => i.key !== input.key);
    }
    const mergeSources = computed<T[]>(() => toValue(options?.mergeSources) || []);
    const reviewedPaths = computed({
        get: () => options?.reviewedPaths?.value || [],
        set: v => {
            if (options?.reviewedPaths) {
                options.reviewedPaths.value = v;
            }
        }
    });
    const optionalPaths = computed({
        get: () => options?.optionalPaths?.value || [],
        set: v => {
            if (options?.optionalPaths) {
                options.optionalPaths.value = v;
            }
        }
    });
    const rules = computed(() => toValue(options?.rules) || {});
    const validating = ref(false);
    const validationResults = computedAsync(async () => {
        if (ready.value && rules.value) {
            const end = advancedFormLayer.trackStart("validate", {rules: rules.value, value: value.value, mergeSources: mergeSources.value, optionalPaths: optionalPaths.value, reviewedPaths: reviewedPaths.value});
            const result = await validate<T>(rules.value, value.value, mergeSources.value, optionalPaths.value, reviewedPaths.value);

            end({result});
            return result;
        }
        return {};
    }, {}, {
        onError: e => {
            log.error(e);
            raise.error("Une erreur est survenue lors de la validation du formulaire");
        },
        evaluating: validating
    });
    const disabled = computed(() => !!toValue(options?.disabled));
    const hoverPath = ref<string>('');

    const triggers = ref<AdvancedFormTrigger<T>[]>([]);

    onMounted(() => {
        trackEnd();
        ready.value = true;
    });
    return { data: value, update, rules, validationResults, mergeSources, disabled, inputs, addInput, removeInput, reviewedPaths, optionalPaths, hoverPath, validating, triggers };
}, "advanced form context");

export const advancedFormContextKey = _advancedFormContextKey as symbol;
export const useAdvancedFormContext = <T>() => _useAdvancedFormContext() as PrivateContext<T> || (() => { throw new Error("advanced form context not provided"); })();
export const useProvideAdvancedFormContext = <T>(value: Ref<T>, options?: AdvancedFormContextOptions<T>) => {
    const context = _useProvideAdvancedFormContext(value, options as any);

    useProvideAlignmentContext();
    return context;
}

export type AdvancedFormContext<T> = ComposablesHelper.Unwrapped<BaseAdvancedFormContext<T>>;
export type UseAdvancedFormInputReturn<T, U> = {
    value: Ref<U | undefined>,
    side: Ref<number>,
    source: Ref<U | undefined>,
    validationResults: Ref<ValidationResult[]>,
    statusClass: Ref<string>,
    readOnly: Ref<boolean>,
    context: Ref<AdvancedFormContext<T>>,
    reviewable: Ref<boolean>,
    reviewed: Ref<boolean>,
    rules: Ref<AmbiguousValidationRules<T, U>>,
    inputProps: Ref<{
        ref: Ref<HTMLElement | undefined>,
        'data-path': string,
        class: string,
        disabled: boolean,
    }>
};

function useFromPathList(list: Ref<Path[]>, path: MaybeRefOrGetter<Path>, readOnly: MaybeRefOrGetter<boolean>) {
    return computed({
        get: () => list.value.some(p => arePathEqual(p, toValue(path))),
        set: (v) => {
            if (toValue(readOnly)) {
                return;
            }
            
            let copy = cloneDeep(list.value);

            if (v) {
                copy.push(toValue(path));
            } else {
                copy = copy.filter(p => !arePathEqual(p, toValue(path)));
            }
            list.value = copy;
        }
    })
}

export const useAdvancedFormInput = <T, U>(path: MaybeRefOrGetter<Path>): UseAdvancedFormInputReturn<T, U> => {
    const context = useAdvancedFormContext<T>();
    const side = useAdvancedFormSide();
    const tab = useAdvancedFormTab();
    const tabControl = useAdvancedFormTabControl();
    const raise = useRaise();

    const element = ref<HTMLElement>();
    const stringPath = computed(() => pathToString(toValue(path)));
    const unwrappedContext = ComposablesHelper.unwrap({ data: context.data, mergeSources: context.mergeSources });
    const rawMergeSources = computed<U[]>(() => context.mergeSources.value.map(s => get(s, toValue(path))));
    const isMainSide = computed(() => side.value === -1);
    const readOnly = computed(() => context.disabled.value || !isMainSide.value);
    const reviewed = useFromPathList(context.reviewedPaths, path, readOnly);
    const value = computed<U | undefined>({
        get: () => get(context.data.value, toValue(path)),
        set: v => {
            const p = toValue(path);
            const trimmed = isString(v) ? v.trim() : v;
            const old = get(context.data.value, p);

            if (old !== trimmed) {
                const copy = cloneDeep(context.data.value) as any;

                log.info(`Setting value '${pathToString(p)}'`, trimmed);
                set(copy, p, trimmed);
                context.update(copy, p);
            }
        }
    });
    const source = computed(() => {
        if (isMainSide.value) {
            return value.value;
        }
        return rawMergeSources.value[side.value];
    });
    const validationResults = computed<ValidationResult[]>(() => !isMainSide.value || context.validating.value ? [] : context.validationResults.value[stringPath.value] || []);
    const mainStatus = computed(() => getStrongestValidationStatus(validationResults.value));
    const input = {
        key: Symbol(stringPath.value),
        path: () => toValue(path),
        status: () => mainStatus.value,
        element: () => toValue(element),
        side: () => side.value,
        validate: async () => {
            if (readOnly.value) {
                return true;
            } else if (mainStatus.value === "invalid") {
                let e = toValue(element);

                if (e instanceof HTMLInputElement && e.type === "hidden" && e.parentElement) {
                    e = e.parentElement;
                }
                if (e && window.getComputedStyle(e).display !== "none") {
                    tabControl?.setTab(tab.value);
                    await nextTick();
                    const rect = e.getBoundingClientRect();

                    if (rect.top < 0 || rect.left < 0 || rect.bottom > (window.innerHeight || document.documentElement.clientHeight) || rect.right > (window.innerWidth || document.documentElement.clientWidth)) {
                        e.scrollIntoView({behavior: "smooth", block: "end", inline: "nearest"});
                        await timeout(1000);
                    }
                    e.classList.add("error-blink");
                    e.focus();
                    await timeout(1000);
                    e.classList.remove("error-blink");
                    raise.warn("Un ou plusieurs champs sont invalides");
                } else {
                    log.warn(`the field '${stringPath.value}' is invalid but has no element to scroll to`);
                    raise.error("Un champs est invalide, mais celui-ci n'a pas d'élément à afficher");
                }
                return false;
            }
            return true;
        }
    };
    const statusClass = computed(() => {
        if (!isMainSide.value) {
            return '';
        } else if (context.hoverPath.value === toValue(path)) {
            return 'is-merge';
        }
        return getValidationClass(mainStatus.value)
    });
    const rules = computed(() => getRules<T, U>(context.rules.value, toValue(path)));

    const inputProps = computed(() => ({
        ref: element,
        'data-path': stringPath.value,
        class: statusClass.value,
        disabled: readOnly.value,
    }));
    const reviewable = computed(() => !readOnly.value && !reviewed.value && validationResults.value.some(isReviewable));

    setDevtoolPath(path, side);
    onMounted(() => context.addInput(input));
    onUnmounted(() => context.removeInput(input));
    return { value, side, source, validationResults, statusClass, readOnly, context: unwrappedContext, reviewable, reviewed, rules, inputProps };
}

export const watchAdvancedForm = <T>(f: AdvancedFormTrigger<T>) => {
    const context = useAdvancedFormContext<T>();
    const side = useAdvancedFormSide();

    onMounted(() => {
        if (side.value === -1) {
            context.triggers.value = [...context.triggers.value, f]
        }
    });
}

export const useAdvancedFormCollapse = (path: MaybeRefOrGetter<Path>) => {
    const context = useAdvancedFormContext<any>();
    const side = useAdvancedFormSide();
    const readOnly = computed(() => context.disabled.value || side.value !== -1);
    const optional = useFromPathList(context.optionalPaths, path, readOnly);
    const requirement = computed<Requirement>(() => getRules<any>(context.rules.value, toValue(path))?.required || 'collapsable');
    const open = computed<boolean>({
        get: () => requirement.value === 'required' || (requirement.value === 'collapsable' && !optional.value),
        set: v => {
            if (optional.value === v && (requirement.value === 'collapsable' || requirement.value === 'optional')) {
                optional.value = !v;
            }
        }
    });

    return { requirement, open };
}

const setDevtoolPath = (path: MaybeRefOrGetter<Path>, side: Ref<number>) => {
    const p = pathToString(toValue(path));

    addDevtoolTags({
        label: p,
        textColor: 0xffffff,
        backgroundColor: side.value == -1 ? 0xc4161c : 0x336699,
        tooltip: `path: ${p}`
    });
}
