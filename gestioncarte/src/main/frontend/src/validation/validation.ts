import {cloneDeep, get, isArray, isEmpty, isNil, isObject, mapKeys, range, set, toPath, uniq} from "lodash";
import {concatPaths, Path, pathArrayIncludes, pathToString, trimPathList} from "@/path";
import log from "loglevel";
import {
    compareStatus,
    getValidationStatus,
    isReviewable,
    ValidationResult,
    ValidationResultMap,
    ValidationStatus
} from "@/validation/result";

export type Requirement = "required" | "collapsable" | "optional";

export type ValidationContext<T, U> = {
    value: U | undefined,
    data: T,
    mergeValues: U[],
    mergeData: T[],
    path: Path,
    required: boolean,
    reviewed: boolean,
    rule: ValidationRules<T, U>
}
export type Validator<T, U> = (context: ValidationContext<T, U>) => Promise<ValidationResult> | ValidationResult;

type Comparator<T> = (t1: T, t2: T) => boolean;
export type ValidationRule<T, U> = Partial<{
    validators: Validator<T, U>[];
    comparator: Comparator<U>;
    required: Requirement; // TODO also use function to determine if required
    mapped: ValidationRules<U>;
}>;
type EachValidationRule<T, U, K> = Partial<{
    each: ValidationRules<T, U> & { keys?: K[] }
}>;
type Except<K, T> = K extends keyof ValidationRule<infer T1, infer T2> ?
        ValidationRule<T1, T2>[K] :
    K extends keyof EachValidationRule<infer T1, infer T2, infer T3> ?
        EachValidationRule<T1, T2, T3>[K] : T;
export type ValidationRules<T, U = T> = Partial<
    U extends ValidationRule<T, U> ?
        U :
    U extends (infer V)[] ?
        ValidationRule<T, U> & EachValidationRule<T, V, keyof U> :
    U extends object ?
        ValidationRule<T, U> & { [K in keyof U]: Except<K, ValidationRules<T, U[K]>> } & (U extends { [key in keyof U]?: infer V; } ? EachValidationRule<T, V, keyof U> : {}) :
        ValidationRule<T, U>
    >;

export type AmbiguousValidationRules<T, U = T> = ValidationRules<T> | ValidationRules<T, U> | ValidationRules<U>;

export type ValidationJson<T> = {
    data: T,
    mergeData?: T[],
    optionalPaths?: string[],
    reviewedPaths?: string[]
}

export type CreateValidationContextOptions<T, U> = {
    mergeData?: T[],
    path?: Path,
    optional?: boolean,
    reviewed?: boolean,
    rule?: ValidationRules<T, U>
}
export function createValidationContext<T, U>(data: T, options?: CreateValidationContextOptions<T, U>): ValidationContext<T, U> {
    const md: T[] = options?.mergeData ?? [];
    const p = options?.path ?? '';

    return {
        value: p ? get(data, p) : data,
        data: data,
        mergeValues: md.map(d => p ? get(d, p) : d),
        mergeData: md,
        path: p,
        required: !options?.optional && options?.rule?.required !== 'optional',
        reviewed: !!options?.reviewed,
        rule:  options?.rule ?? {}
    };
}
export async function validatePath<T, U>(rule: ValidationRules<T, U>, path: Path, data: T, mergeData?: T[], optional?: boolean, reviewed?: boolean): Promise<ValidationResult[]> {
    const results: ValidationResult[] = [];
    const context = createValidationContext<T, U>(data, {path, rule, mergeData, optional, reviewed});

    for (const validator of rule.validators ?? []) {
        try {
            const r = await validator(context);

            if (!context.reviewed || !isReviewable(r)) {
                results.push(r);
            }
        } catch (e) {
            log.error(e);
        }
    }
    return results;
}

function mergeValidationResults(r1: ValidationResultMap, r2: ValidationResultMap): ValidationResultMap {
    const keys = uniq([...Object.keys(r1), ...Object.keys(r2)]);

    return keys.reduce((acc, key) => {
        acc[key] = uniq([...(r1[key] || []), ...(r2[key] || [])]);
        return acc;
    }, {} as ValidationResultMap);
}

const ignoredKeys = ['validators', 'comparator', 'required', 'mapped', 'each', 'keys'];

export function sortArrayToValue<T>(array: T[], value?: T[], comparator?: Comparator<T>) {
    if (!value) {
        return array;
    }

    const usedIndexes: number[] = [];
    const result: T[] = [];
    const remaining: T[] = [];

    for (const v of array) {
        const indexes = value.reduce<number[]>((acc, a, i) => {
            if (comparator?.(a, v) || a === v) {
                acc.push(i);
            }
            return acc;
        }, []);

        let added = false;

        for (const i of indexes) {
            if (!usedIndexes.includes(i)) {
                result[i] = v;
                usedIndexes.push(i);
                added = true;
                break;
            }
        }
        if (!added) {
            remaining.push(v);
        }
    }

    for (const v of remaining) {
        for (let i = 0; i < value.length; i++) {
            if (!usedIndexes.includes(i)) {
                result[i] = v;
                usedIndexes.push(i);
                break;
            }
        }
    }

    return result;
}

async function doValidateRules<T, U>(rules: ValidationRules<T, U>, data: T, path: Path, mergeData?: T[], optionalPaths?: Path[], reviewedPaths?: Path[]): Promise<ValidationResultMap> {
    let results: ValidationResultMap = {};
    const promises: Promise<any>[] = [];

    if (rules.validators) {
        promises.push(validatePath<T, U>(rules, path, data, mergeData, pathArrayIncludes(optionalPaths, path), pathArrayIncludes(reviewedPaths, path)).then(r => results[pathToString(path)] = r));
    }
    for (const [key, rule] of Object.entries(rules)) {
        if (ignoredKeys.includes(key)) {
            continue;
        }

        const p = path ? concatPaths([path, key]) : key;

        promises.push(doValidateRules(rule, data, p, mergeData, optionalPaths, reviewedPaths).then(r => results = mergeValidationResults(results, r)));
    }
    if (rules.mapped) {
        promises.push(doValidateRules(rules.mapped, get(data, path), '',  mergeData?.map(d => get(d, path)) ?? [], trimPathList(optionalPaths, path), trimPathList(reviewedPaths, path))
            .then(r => results = mergeValidationResults(results, mapKeys(r, (_v, k) => pathToString(concatPaths([path, k]))))));
    }

    const each = (rules as any).each;

    if (each) {
        const collection = get(data, path);

        let keys = isObject(collection) ? Object.keys(collection || {}) : range(0, (collection || []).length);

        if (each.keys) {
            keys = uniq([...keys, ...each.keys]);
        }

        let md = mergeData ?? [];

        if (isArray(collection)) {
            md = md.map(d => {
                const c = cloneDeep(d) as any;

                set(c, path, sortArrayToValue(get(d, path), collection, each.comparator ?? each.mapped?.comparator));
                return c;
            });
        }

        for (const key of keys) {
            promises.push(doValidateRules((rules as any).each, data, concatPaths([path, key]), md, optionalPaths, reviewedPaths).then(r => results = mergeValidationResults(results, r)));
        }
    }
    await Promise.all(promises);
    return results;
}
export async function validate<T>(rules: ValidationRules<T>, data: T, mergeData?: T[], optionalPaths?: Path[], reviewedPaths?: Path[]): Promise<ValidationResultMap> {
    log.debug('validate', {rules, data, mergeData, optionalPaths, reviewedPaths});
    return doValidateRules<T, T>(rules, data, '', mergeData, optionalPaths, reviewedPaths);
}

export function getRules<T, U = T>(rules: ValidationRules<T>, path: Path):  AmbiguousValidationRules<T, U> {
    if (isNil(rules) || isEmpty(rules)) {
        return {};
    } else if (!path) {
        return rules;
    }
    const parts = toPath(path);

    let rule: ValidationRules<any> = rules;

    for (const part of parts) {
        const r = rule[part];

        rule = r || rule.mapped?.[part] || rule.each || {};
    }
    if (rule.mapped && Object.keys(rule).length === 1) {
        return rule.mapped;
    }
    return rule;
}

export function getStrongestValidationStatus(value: ValidationResult[]) {
    if (!value?.length) {
        return 'valid';
    }
    
    let status: ValidationStatus = 'valid';

    for (const v of value) {
        const s = getValidationStatus(v);

        if (compareStatus(status, s) < 0) {
            status = s;
        }
    }
    return status;
}
