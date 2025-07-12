import {AdvancedFormCheckbox, useProvideAdvancedFormContext} from "@components/form/advanced";
import {ref} from "vue";
import {flushPromises, mount} from "@vue/test-utils";
import {describe, expect, it} from 'vitest';


const v = ref({v: false});
const TestComponent = {
    components: { AdvancedFormCheckbox },
    setup() {
        useProvideAdvancedFormContext(v);
    },
    template: '<div class="test-parent"><AdvancedFormCheckbox path="v" /></div>'
}

describe('AdvancedFormCheckbox', () => {
    it('should be visible', () => {
        const wrapper = mount(TestComponent);

        expect(wrapper.findComponent(AdvancedFormCheckbox).exists()).toBeTruthy();
        expect(wrapper.find('input[type=checkbox]').exists()).toBeTruthy();
    });
    it('should change values', async () => {
        const wrapper = mount(TestComponent);

        await wrapper.find('input[type=checkbox]').setValue(true);
        await flushPromises();
        expect(v.value.v).toBe(true);
    });
    it('should change values on click', async () => {
        const wrapper = mount(TestComponent);

        await wrapper.find('input[type=checkbox]').trigger('click');
        expect(v.value.v).toBe(true);
    });
});
