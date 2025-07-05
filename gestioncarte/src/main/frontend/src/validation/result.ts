import {isArray, isNil, isObject, uniq} from "lodash";

export type ValidationStatus = "valid" | "invalid" | "new" | "warning";
type ValidationResultObject = {
    status: ValidationStatus,
    message?: string,
    reviewable?: boolean,
    additionalInfo?: any
    legendKeys?: string[]
};
export type ValidationResult = ValidationStatus | [ValidationStatus] | [ValidationStatus , string] | ValidationResultObject;

export type ValidationResultMap = Record<string, ValidationResult[]>;

export function getValidationStatus(result?: ValidationResult): ValidationStatus {
    if (isNil(result)) {
        return "valid";
    } else if (isArray(result)) {
        return result[0];
    } else if (isObject(result)) {
        return result.status;
    }
    return result;
}
export function getValidationClass(status?: ValidationResult): string {
    const s = getValidationStatus(status);

    switch (s) {
        case "invalid":
            return "is-invalid";
        case "warning":
            return "is-warning";
        case "new":
            return "is-new";
        case "valid":
            return "is-valid";
        default:
            return "";
    }
}
export function getValidationMessage(result?: ValidationResult): string {
    if (isArray(result)) {
        return result[1] ?? '';
    } else if (isObject(result)) {
        return result.message ?? '';
    }
    return '';
}
export const isReviewable = (result?: ValidationResult) => getValidationStatus(result) === 'invalid' && !!(result as any)?.reviewable;
export const getAdditionalInfo = (result?: ValidationResult) => (result as any)?.additionalInfo;

export const getLegendKeys = (result?: ValidationResult | ValidationResultMap): string[] => {
    if (isObject(result)) {
        if ('legendKeys' in result && isArray(result.legendKeys)) {
            return uniq(result.legendKeys as string[]) || [];
        }
        return uniq(Object.values(result)
            .map(r => getLegendKeys(r))
            .flat());
    }
    return [];
}

const statusValue = (result?: ValidationResult) => {
    const s = getValidationStatus(result);

    switch (s) {
        case 'valid':
            return 1;
        case 'invalid':
            return 4;
        case 'warning':
            return 3;
        case 'new':
            return 2;
        default:
            return 0;
    }
};
export const compareStatus = (s1?: ValidationResult, s2?: ValidationResult) => statusValue(s1) - statusValue(s2);
