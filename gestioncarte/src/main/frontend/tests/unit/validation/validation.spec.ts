import {createValidationContext, getRules, sortArrayToValue} from "@/validation";
import {PromoCardDTO} from "@/types";
import {describe, expect, it} from 'vitest';

describe('createValidationContext', () => {
    it('has data and value', () => {
        const context = createValidationContext('test');

        expect(context.data).toEqual('test');
        expect(context.value).toEqual('test');
    });
    it('is mapped with path', () => {
        const context = createValidationContext({ test: 'test' }, {path: 'test' });

        expect(context.value).toEqual('test');
    });
});

describe('getRules', () => {
    it('get rules for simple path', () => {
        const rules = getRules<any>({ test: { required: 'required' } }, 'test');

        expect(rules).toEqual({ required: 'required' });
    });
    it('get rules for nested path', () => {
        const rules = getRules<any>({ test: { nested: { required: 'required' } } }, 'test.nested');

        expect(rules).toEqual({ required: 'required' });
    });
    it('get rules for record path', () => {
        const rules = getRules<any>({ test: { each: { required: 'required' } } }, 'test.nested');

        expect(rules).toEqual({ required: 'required' });
    });
    it('get rules for array path', () => {
        const rules = getRules<any>({ test: { each: { required: 'required' } } }, 'test[0]');

        expect(rules).toEqual({ required: 'required' });
    });
    it('get rules for mapped path', () => {
        const rules = getRules<any>({ test: { mapped: { required: 'required' } } }, 'test');

        expect(rules).toEqual({ required: 'required' });
    });
    it('get rules for mapped subpath', () => {
        const rules = getRules<any>({ test: { mapped: { nested: { required: 'required' } } } }, 'test.nested');

        expect(rules).toEqual({ required: 'required' });
    });
    it('returns empty if empty runes', () => {
        const rules = getRules<any>({}, 'v');

        expect(rules).toEqual({});
    });
});

describe('sortArrayToValue', () => {
    it('should sort promos', () => {
         const sorted = sortArrayToValue([
             {
                 "id": "01GMT59V0HNJ9XV04DPTY2VSSP",
                 "name": "VMAX Battle Triple Starter Set",
                 "localization": "jp"
             },
             {
                 "id": "01GMT59V0HNJ9XV04DPTY2VSSQ",
                 "name": "Vivid Voltage Build & Battle Box",
                 "localization": "us"
             }
         ], [
             {
                 "id": "",
                 "name": "Vivid Voltage Build & Battle Box ttt",
                 "localization": "us"
             },
             {
                 "id": "",
                 "name": "VMAX Battle Triple Starter Set",
                 "localization": "jp"
             }
         ], (a: PromoCardDTO, b: PromoCardDTO) => a?.name === b?.name);

            expect(sorted).toEqual([
                {
                    "id": "01GMT59V0HNJ9XV04DPTY2VSSQ",
                    "name": "Vivid Voltage Build & Battle Box",
                    "localization": "us"
                },
                {
                    "id": "01GMT59V0HNJ9XV04DPTY2VSSP",
                    "name": "VMAX Battle Triple Starter Set",
                    "localization": "jp"
                }
            ]);
    });
});
