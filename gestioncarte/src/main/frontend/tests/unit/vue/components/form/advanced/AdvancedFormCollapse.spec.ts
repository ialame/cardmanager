import {AdvancedFormCollapse, advancedFormContextKey, useProvideAdvancedFormContext} from "@components/form/advanced";
import {mount} from "@vue/test-utils";
import Collapse from "@components/collapse/Collapse.vue";
import {ref} from "vue";
import {describe, expect, it} from 'vitest';

describe('AdvancedFormCollapse', () => {
    it('should have a collapse and button', () => {
        const context = useProvideAdvancedFormContext(ref({v: {}}));
        const wrapper = mount(AdvancedFormCollapse, {
            global: { provide: { [advancedFormContextKey]: context } }
        });

        expect(wrapper.find('button').exists()).toBeTruthy();
        expect(wrapper.findComponent(Collapse).exists()).toBeTruthy();
    });
    it('should not have a collapse button when required', () => {
        const context = useProvideAdvancedFormContext(ref({v: {}}), { rules: { required: 'required' }});
        const wrapper = mount(AdvancedFormCollapse, {
            slots: { default: ({required}: any) => { expect(required).toBeTruthy() } },
            global: { provide: { [advancedFormContextKey]: context } }
        });

        expect(wrapper.find('button').exists()).toBeFalsy();
    });
    it('should not have a collapse button when optional', () => {
        const context = useProvideAdvancedFormContext(ref({v: {}}), { rules: { required: 'optional' }});
        const wrapper = mount(AdvancedFormCollapse, {
            slots: { default: ({required}: any) => { expect(required).toBeFalsy()  } },
            global: { provide: { [advancedFormContextKey]: context } }
        });

        expect(wrapper.find('button').exists()).toBeFalsy();
    });
    it('should be required if opened', () => {
        const context = useProvideAdvancedFormContext(ref({v: {}}));
        const wrapper = mount(AdvancedFormCollapse, {
            slots: { default: ({required}: any) => { expect(required).toBeTruthy() } },
            global: { provide: { [advancedFormContextKey]: context } }
        });
        expect((wrapper.findComponent(Collapse).vm as any).isOpen).toBeTruthy();
    });
});
