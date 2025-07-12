import {AsyncPredicate} from "@/types";

export const envName = process.env.NODE_ENV;
const _version = process.env.VITE_APP_VERSION;
export const version = _version?.replace('"', '');
export async function asyncFilter<T>(items: T[], predicate: AsyncPredicate<T>): Promise<T[]> {
    const result: T[] = [];

    for (const item of items) {
        if (await predicate(item)) {
            result.push(item);
        }
    }
    return result;
}
