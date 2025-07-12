import {
    arrayValidator,
    createValidationContext,
    CreateValidationContextOptions,
    defaultValidator,
    getValidationStatus,
    regexValidator
} from "@/validation";
import {describe, expect, it} from 'vitest';

describe('defaultValidator', () => {
    it.each([
        ['test', undefined],
        [true, undefined],
        [false, undefined],
        [0, undefined],
        [1, undefined],
        ['', { rule: { required: 'optional' } } as CreateValidationContextOptions<any, any>],
        ['', { optional: true } as CreateValidationContextOptions<any, any>],
        ['test1', { mergeData: ['test1'] } as CreateValidationContextOptions<any, any>],
        ['test2', { mergeData: ['test2', 'test2'] } as CreateValidationContextOptions<any, any>],
        [{test: 'test3'}, { path: 'test', mergeData: [{test: 'test3'}, {test: 'test3'}] } as CreateValidationContextOptions<any, any>],
        [{test: ''}, { path: 'test', rule: { required: 'optional' }, mergeData: [{test: ''}] } as CreateValidationContextOptions<any, any>],
    ])(`%s %s is valid`, async (value: any, options?: CreateValidationContextOptions<any, any>) => {
        expect(getValidationStatus(await defaultValidator(createValidationContext<any, any>(value, options)))).toEqual('valid');
    });
    it.each([
        ['test1', { mergeData: ['test'], optional: true } as CreateValidationContextOptions<any, any>],
        ['test2', { mergeData: ['test2', 'test'], optional: true } as CreateValidationContextOptions<any, any>],
        [{test: 'test3'}, { path: 'test', mergeData: [{test: 'test2'}, {test: 'test3'}], optional: true } as CreateValidationContextOptions<any, any>],
        [{test: 'test4'}, { path: 'test', mergeData: [{test: 'test4'}, {test: 'test3'}], optional: true } as CreateValidationContextOptions<any, any>],
    ])(`%s %s is warning`, async (value: any, options?: CreateValidationContextOptions<any, any>) => {
        expect(getValidationStatus(await defaultValidator(createValidationContext<any, any>(value, options)))).toEqual('warning');
    });
    it.each([
        [{test: 'test3'}, { path: 'test', mergeData: [{test: ''}] } as CreateValidationContextOptions<any, any>],
    ])(`%s %s is new`, async (value: any, options?: CreateValidationContextOptions<any, any>) => {
        expect(getValidationStatus(await defaultValidator(createValidationContext<any, any>(value, options)))).toEqual('new');
    });
    it.each([
        ['', undefined],
        [undefined, undefined],
        [[], undefined],
        ['test1', { mergeData: ['test']} as CreateValidationContextOptions<any, any>],
        ['test2', { mergeData: ['test2', 'test'] } as CreateValidationContextOptions<any, any>],
        [{test: 'test3'}, { path: 'test', mergeData: [{test: 'test2'}, {test: 'test3'}] } as CreateValidationContextOptions<any, any>],
        [{test: 'test4'}, { path: 'test', mergeData: [{test: 'test4'}, {test: 'test3'}] } as CreateValidationContextOptions<any, any>],
    ])(`%s %s is invalid`, async (value: any, options?: CreateValidationContextOptions<any, any>) => {
        expect(getValidationStatus(await defaultValidator(createValidationContext<any, any>(value, options)))).toEqual('invalid');
    });
});
describe('arrayValidator', () => {
    it.each([
        [[], undefined],
        [undefined, undefined],
        [['test1'], { mergeData: [['test1']] } as CreateValidationContextOptions<any, any[]>],
    ])(`%s %s is valid when not reviewable`, async (value?: any[], options?: CreateValidationContextOptions<any, any[]>) => {
        expect(getValidationStatus(await arrayValidator()(createValidationContext<any, any[]>(value, options)))).toEqual('valid');
    });
    it.each([
        [['test1'], { mergeData: [['test']] } as CreateValidationContextOptions<any, any[]>],
        [['test2'], { mergeData: [['test2'], ['test']] } as CreateValidationContextOptions<any, any>],
    ])(`%s %s is warning when not reviewable`, async (value: any[], options?: CreateValidationContextOptions<any, any[]>) => {
        expect(getValidationStatus(await arrayValidator()(createValidationContext<any, any[]>(value, options)))).toEqual('warning');
    });
    it('is valid when reviewable and reviewed', async () => {
        expect(getValidationStatus(await arrayValidator(true)(createValidationContext<any, any[]>(['test1'], {reviewed: true, mergeData: [['test2'], ['test']]})))).toEqual('valid');
    });
    it.each([
        [['test1'], { mergeData: [['test']] } as CreateValidationContextOptions<any, any[]>],
        [['test2'], { mergeData: [['test2'], ['test']] } as CreateValidationContextOptions<any, any>],
    ])(`%s %s is invalid when reviewable`, async (value: any[], options?: CreateValidationContextOptions<any, any[]>) => {
        expect(getValidationStatus(await arrayValidator(true)(createValidationContext<any, any[]>(value, options)))).toEqual('invalid');
    });
});
describe('regexValidator', () => {
    it.each([
        [''],
        ['test'],
        ['[a-z]'],
        ['[a-z]{1,}'],
        ['[a-z]{1,2}'],
    ])(`%s %s is valid`, async (regex: string) => {
        expect(getValidationStatus(await regexValidator(createValidationContext<any, any>(regex)))).toEqual('valid');
    });
    it.each([
        ['[a-z'],
    ])(`%s %s is invalid`, async (regex: string) => {
        expect(getValidationStatus(await regexValidator(createValidationContext<any, any>(regex)))).toEqual('invalid');
    });
});
