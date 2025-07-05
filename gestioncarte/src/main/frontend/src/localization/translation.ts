import {LocalizationCode, localizationCodes} from "@/localization/localization";
import {isEmpty, isNil} from "lodash";

export type Translations<T> = Partial<Record<LocalizationCode, T>>;

export function getTranslationField<U, T extends Translations<U> = Translations<U>>(translations?: T): [LocalizationCode, U] | undefined;
export function getTranslationField<U, K extends keyof U, T extends Translations<U> = Translations<U>>(translations: T | undefined, key: K): [LocalizationCode, U[K]] | undefined;
export function getTranslationField<U, K extends keyof U, T extends Translations<U> = Translations<U>>(translations?: T, key?: K): [LocalizationCode, U[K] | U] | undefined {
    if (translations === undefined) {
        return undefined;
    } else if (key === undefined) {
        for (const l of localizationCodes) {
            if (translations[l] !== undefined) {
                return [l, translations[l]!];
            }
        }
    } else {
        for (const l of localizationCodes) {
            if (translations[l]?.[key] !== undefined) {
                return [l, translations[l]![key]!];
            }
        }
    }
    return undefined;
}

export function getTranslationName<U, K extends keyof U, T extends Translations<U> = Translations<U>>(objectName?: string, translations?: T, key?: K): string {
    const value = getTranslationField<U, K, T>(translations, (key ?? 'name') as K)?.[1];

    return isNil(value) || isEmpty(value) ? `!!! ${objectName?.toUpperCase() ?? 'OBJET'} SANS NOM !!!` : value?.toString() as string;
}
