import {AdvancedFormInput, useProvideAdvancedFormContext} from "@components/form/advanced";
import {ref} from "vue";
import {mount} from "@vue/test-utils";
import {describe, expect, it} from 'vitest';

const v = ref({v: "0"});
const TestComponent = {
    components: { AdvancedFormInput },
    setup() {
        useProvideAdvancedFormContext(v);
    },
    template: '<div class="test-parent"><AdvancedFormInput path="v" /></div>'
}

describe('AdvancedFormInput', () => {
    it('should be visible', () => {
        const wrapper = mount(TestComponent);

        expect(wrapper.findComponent(AdvancedFormInput).exists()).toBeTruthy();
        expect(wrapper.find('input').exists()).toBeTruthy();
    });
    it('should change values', async () => {
        const wrapper = mount(TestComponent);

        await wrapper.find('input').setValue('1');
        expect(v.value.v).toBe('1');
    });
});
