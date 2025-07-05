import {validate, ValidationJson, ValidationRules} from "@/validation";

export async function validateJson<T>(rules: ValidationRules<T>, code: string) {
    const json = await import(`@tr/form/advanced/data/advance-form-data-${code}.json`) as ValidationJson<T>;

    return validate<T>(rules, json.data, json.mergeData || [], json.optionalPaths, json.reviewedPaths);
}
