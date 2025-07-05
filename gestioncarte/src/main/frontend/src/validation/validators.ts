import {isArray, isEmpty, isEqual, isNil} from "lodash";
import {ValidationContext, Validator} from "@/validation/validation";

const isValid = (v: any) => {
    if (typeof v === "boolean" || typeof v === "number") {
        return true;
    }
    return !isNil(v) && !isEmpty(v);
}
export const defaultValidator: Validator<any, any> = (context: ValidationContext<any, any>)  => {
    const valid = isValid(context.value);

    if (!valid && context.required) {
        return "invalid";
    }

    const blocking = context.required && !context.reviewed;
    const equals = context.rule.comparator || isEqual;
    if (context.mergeValues.length === 0) {
        return "valid";
    } else if (context.mergeValues.some(m => isValid(m) && !equals(context.value, m))) {
        return {
            status: blocking ? "invalid" : "warning",
            message: "Valeur différente",
            reviewable: blocking,
            legendKeys: [blocking ? "breaking-change" : "change"]
        };
    } else if (valid && context.mergeData.every((m, i) => isValid(m) && !isValid(context.mergeValues[i]))) {
        return "new";
    }
    return "valid";
};
export function arrayValidator<T, U>(reviewable?: boolean): Validator<T, U[]> {
    return context => {
        if (!isArray(context.value)) {
            return 'valid';
        }

        const equals = context.rule.each?.comparator ?? context.rule.each?.mapped?.comparator ?? isEqual;
        const array = context.value ?? [];

        if (reviewable && context.reviewed) {
            return 'valid';
        } else if (context.mergeValues.some(a => a.some(l => !array.some(v => equals(v, l))))) {
            return {
                status: reviewable ? 'invalid' : 'warning',
                message: 'Une valeur a été supprimée',
                reviewable: reviewable,
                legendKeys: [reviewable ? "breaking-change" : "change"]
            };
        }
        return "valid";
    }
}
export const regexValidator: Validator<any, any> = (context: ValidationContext<any, string>)  => {
    if (!context.value) {
        return 'valid';
    }
    try {
        new RegExp(context.value);
        return 'valid';
    } catch (e: any) {
        return ['invalid', `La Regex est invalide: ${e.message.replace('Invalid regular expression: ','')}`];
    }
}
