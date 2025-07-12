import {
    compareStatus,
    getLegendKeys,
    getValidationClass,
    getValidationMessage,
    getValidationStatus,
    isReviewable,
    ValidationResult,
    ValidationStatus
} from "@/validation";
import {describe, expect, it} from 'vitest';

describe('getValidationStatus', () => {
    it.each([
        [undefined, 'valid' as ValidationStatus],
        ['valid' as ValidationResult, 'valid' as ValidationStatus],
        ['invalid' as ValidationResult, 'invalid' as ValidationStatus],
        ['warning' as ValidationResult, 'warning' as ValidationStatus],
        ['new' as ValidationResult, 'new' as ValidationStatus],
        [{ status: 'valid' } as ValidationResult, 'valid' as ValidationStatus],
        [[ 'valid' ] as ValidationResult, 'valid' as ValidationStatus],
    ])('%s returns %s', (result: ValidationResult | undefined, expected: ValidationStatus) => {
        expect(getValidationStatus(result)).toEqual(expected);
    });
});
describe('getValidationClass', () => {
    it.each([
        [undefined, 'is-valid'],
        ['valid' as ValidationResult, 'is-valid'],
        ['invalid' as ValidationResult, 'is-invalid'],
        ['warning' as ValidationResult, 'is-warning'],
        ['new' as ValidationResult, 'is-new'],
        [{ status: 'valid' } as ValidationResult, 'is-valid'],
        [[ 'valid' ] as ValidationResult, 'is-valid'],
    ])('%s returns %s', (result: ValidationResult | undefined, expected: string) => {
        expect(getValidationClass(result)).toEqual(expected);
    });
});
describe('getValidationMessage', () => {
    it.each([
        [undefined],
        ['valid' as ValidationResult],
        ['invalid' as ValidationResult],
        ['warning' as ValidationResult],
        ['new' as ValidationResult],
        [{ status: 'valid' } as ValidationResult],
        [[ 'valid' ] as ValidationResult],
        [{status: 'valid', message: ''} as ValidationResult],
    ])('%s has no message', (result: ValidationResult | undefined) => {
        expect(getValidationMessage(result)).toEqual('');
    });
    it.each([
        [['invalid', 'msg'] as ValidationResult, 'msg'],
        [{status: 'valid', message: 'msg'} as ValidationResult, 'msg'],
    ])('%s returns %s', (result: ValidationResult | undefined, expected: string) => {
        expect(getValidationMessage(result)).toEqual(expected);
    });
});
describe('isReviewable', () => {
    it.each([
        [undefined],
        ['valid' as ValidationResult],
        ['invalid' as ValidationResult],
        ['warning' as ValidationResult],
        ['new' as ValidationResult],
        [{ status: 'valid' } as ValidationResult],
        [[ 'valid' ] as ValidationResult],
        [{ status: 'valid', reviewable: true } as ValidationResult],
        [{ status: 'warning', reviewable: true } as ValidationResult],
        [{ status: 'new', reviewable: true } as ValidationResult],
    ])('%s is not reviewable', (result: ValidationResult | undefined) => {
        expect(isReviewable(result)).toBeFalsy();
    });
    it.each([
        [{ status: 'invalid', reviewable: true } as ValidationResult],
        [{ status: 'invalid', message: 'test', reviewable: true } as ValidationResult],
    ])('%s is not reviewable', (result: ValidationResult | undefined) => {
        expect(isReviewable(result)).toBeTruthy();
    });
});
describe('compareStatus', () => {
    it.each([
        ['new' as ValidationResult, 'valid' as ValidationResult],
        ['warning' as ValidationResult, 'valid' as ValidationResult],
        ['warning' as ValidationResult, 'new' as ValidationResult],
        ['invalid' as ValidationResult, 'valid' as ValidationResult],
        ['invalid' as ValidationResult, 'new' as ValidationResult],
        ['invalid' as ValidationResult, 'warning' as ValidationResult],
        [['invalid', 'msg'] as ValidationResult, 'warning' as ValidationResult],
    ])('%s is greater than %s', (s1: ValidationResult, s2: ValidationResult) => {
        expect(compareStatus(s1, s2)).toBeGreaterThan(0);
    });
});
describe('getLegendKeys', () => {
    it('returns empty for undefined', () => {
        expect(getLegendKeys(undefined)).toEqual([]);
    });
    it('returns empty for empty object', () => {
        expect(getLegendKeys({})).toEqual([]);
    });
});
