import {cloneDeep, get, isEmpty, isNil, set} from "lodash";

const iterate = (obj: any, consumer: (k: string | number,  v: any) => void, rootKey?: string) => {
    for (const [key, value] of Object.entries(obj)) {
        const deepKey = rootKey ? `${rootKey}.${key}` : key;

        consumer(deepKey, value);
        if (typeof value === 'object' && !isNil(value)) {
            iterate(value, consumer, deepKey);
        }
    }
}

export const merge = <T>(values: T[], source?: T): T => {
    const data = cloneDeep(source ?? {}) as any;

    for (const value of values) {
        iterate(value, (k, v) => {
            if (isNil(v)) {
                return;
            }

            if (!values.filter((v2, k2) => k2 !== k)
                .map(v2 => get(v2, k))
                .filter(v2 => !isNil(v2) || !isEmpty(v2))
                .some(v2 => v2 !== v)) {
                set(data, k, v);
            }
        });
    }
    return data as T;
};
