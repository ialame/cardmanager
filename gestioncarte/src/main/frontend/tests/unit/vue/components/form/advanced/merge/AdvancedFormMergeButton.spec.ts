import {advancedFormContextKey, AdvancedFormMergeButton, advancedFormSideKey} from "@components/form/advanced";
import {ref} from "vue";
import {mount} from "@vue/test-utils";
import {describe, expect, it, vi} from 'vitest';

const context = {
    disabled: ref(false), rules: {}
}

describe('AdvancedFormMergeButton', () => {
    it('should be visible', () => {
        const wrapper = mount(AdvancedFormMergeButton, {
            global: { provide: {
                [advancedFormContextKey]: { data: ref({v: {}}), mergeSources: ref([{v: {}}]), ...context },
                [advancedFormSideKey]: ref(0)
            } }
        });

        expect(wrapper.findComponent(AdvancedFormMergeButton).exists()).toBeTruthy();
        expect(wrapper.find('button').exists()).toBeTruthy();
    });
    it('should be copy value on click', async () => {
        const update = vi.fn();
        const wrapper = mount(AdvancedFormMergeButton, {
            props: { path: 'v' },
            global: { provide: {
                    [advancedFormContextKey]: { data: ref({}), mergeSources: ref([{v: {}}]), update, ...context },
                    [advancedFormSideKey]: ref(0)
                } }
        });

        await wrapper.find('button').trigger('click');

        expect(update).toHaveBeenCalled();
        expect(update.mock.calls[0][0]).toEqual({v: {}});
    });
});
