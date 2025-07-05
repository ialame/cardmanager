import {
    advancedFormContextKey,
    useAdvancedFormContext,
    useAdvancedFormInput,
    useProvideAdvancedFormContext
} from "@components/form/advanced";
import {nextTick, ref} from "vue";
import {flushPromises, mount} from "@vue/test-utils";
import {getValidationMessage, getValidationStatus, ValidationResult} from "@/validation";
import {describe, expect, it} from 'vitest';

describe('useProvideAdvancedFormContext', () => {
    it('returns not null or undefined', () => {
        const context = useProvideAdvancedFormContext(ref({v: 0}));

        expect(context).not.toBeNull();
        expect(context).not.toBeUndefined();
    });
});
describe('useAdvancedFormContext', () => {
    it('throws without provided context', () => {
        expect(() => useAdvancedFormContext()).toThrow();
    });
    it('returns the exact context', () => {
        const context = useProvideAdvancedFormContext(ref({v: 0}));

        mount({
            setup: () => {
                const c = useAdvancedFormContext();

                expect(c).toEqual(context);
            }
        }, {
            global: { provide: { [advancedFormContextKey]: context}  }
        });
    });
});
describe('useAdvancedFormInput', () => {
    it('returns not null or undefined', () => {
        const context = useProvideAdvancedFormContext(ref({v: 0}));

        expect(context).not.toBeNull();
        expect(context).not.toBeUndefined();
    });
    it.each<[ValidationResult]>([
        ['valid'],
        ['invalid'],
        ['warning'],
        ['new']
    ])('is %s', (k) => {
        const context = useProvideAdvancedFormContext(ref({v: 0}), { rules: { v: { validators: [() => k] } }});

        mount({
            setup: async () => {
                const {validationResults, statusClass} = useAdvancedFormInput('v');

                await nextTick();
                await flushPromises();
                expect(getValidationStatus(validationResults.value[0])).toEqual(k);
                expect(getValidationMessage(validationResults.value[0])).toEqual('');
                expect(statusClass.value).toEqual(`is-${k}`);
            }
        }, { global: { provide: { [advancedFormContextKey]: context } } });

    });
    it.each<[ValidationResult]>([
        ['valid'],
        ['invalid'],
        ['warning'],
        ['new']
    ])('is %s with message', (s) => {
        const context = useProvideAdvancedFormContext(ref({v: 0}), { rules: { v: { validators: [() => [k, 'test']] } }});

        mount({
            setup: async () => {
                const {validationResults, statusClass} = useAdvancedFormInput('v');

                await nextTick();
                await flushPromises();
                expect(getValidationStatus(validationResults.value[0])).toEqual(s);
                expect(getValidationMessage(validationResults.value[0])).toEqual('test');
                expect(statusClass.value).toEqual(`is-${s}`);
            }
        }, { global: { provide: { [advancedFormContextKey]: context } } });

    });
    it('has no status', () => {
        const context = useProvideAdvancedFormContext(ref({v: 0}));

        mount({
            setup: async () => {
                const {validationResults, statusClass} = useAdvancedFormInput('v');

                await nextTick();
                await flushPromises();
                expect(getValidationStatus(validationResults.value[0])).toEqual('valid');
                expect(statusClass.value).toEqual('');
            }
        }, { global: { provide: { [advancedFormContextKey]: context } } });

    });
    it('adds itself to inputs', () => {
        const context = useProvideAdvancedFormContext(ref({v: 0}));

        mount({ setup: () => useAdvancedFormInput('v') }, { global: { provide: { [advancedFormContextKey]: context } } });
        const inputs = context.inputs.value;

        expect(inputs).not.toBeNull();
        expect(inputs).not.toBeUndefined();
        expect(inputs).toHaveLength(1);
        expect(inputs[0].path()).toEqual('v');
    });
    it('removes itself to inputs on unmounted', () => {
        const context = useProvideAdvancedFormContext(ref({v: 0}));

        const wrapper = mount({ setup: () => useAdvancedFormInput('v') }, { global: { provide: { [advancedFormContextKey]: context } } });

        wrapper.unmount();

        const inputs = context.inputs.value;

        expect(inputs).not.toBeNull();
        expect(inputs).not.toBeUndefined();
        expect(inputs).toHaveLength(0);
    });
});
