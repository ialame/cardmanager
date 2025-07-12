import {
    advancedFormContextKey,
    AdvancedFormSaveButton,
    advancedFormSideKey,
    advancedFormTabKey,
    advancedFormTabKeyControl
} from "@components/form/advanced";
import {ref} from "vue";
import {mount} from "@vue/test-utils";
import {describe, expect, it, vi} from 'vitest';

describe('AdvancedFormSaveButton', () => {
    it('should be visible', () => {
        const wrapper = mount(AdvancedFormSaveButton, {
            global: { provide: { [advancedFormContextKey]: { data: ref({v: {}}), inputs: ref([])} } }
        });

        expect(wrapper.findComponent(AdvancedFormSaveButton).exists()).toBeTruthy();
    });
    it('should emit save on click', async () => {
        const wrapper = mount(AdvancedFormSaveButton, {
            global: { provide: { [advancedFormContextKey]: { data: ref({v: {}}), inputs: ref([])} } }
        });

        await wrapper.find('button').trigger('click');
        expect(wrapper.emitted('save')).toBeTruthy();
    });
    it('should not emit save on click is invalid form', async () => {
        const callback = vi.fn(() => false)
        const wrapper = mount(AdvancedFormSaveButton, {
            global: { provide: { [advancedFormContextKey]: { data: ref({v: {}}), inputs: ref([{validate: callback}])} } }
        });

        await wrapper.find('button').trigger('click');
        expect(callback).toHaveBeenCalled();
        expect(wrapper.emitted('save')).toBeFalsy();
    });
    it('should not emit save on click if not on active tab', async () => {
        const callback = vi.fn(() => true)
        const wrapper = mount(AdvancedFormSaveButton, {
            global: { provide: {
                [advancedFormContextKey]: { data: ref({v: {}}), inputs: ref([{validate: callback}]) },
                [advancedFormTabKeyControl]: { openedTab: ref('tab2') },
                [advancedFormTabKey]: ref('tab1')
            } }
        });

        await wrapper.find('button').trigger('click');
        expect(callback).not.toHaveBeenCalled();
        expect(wrapper.emitted('save')).toBeFalsy();
    });
    it('should not emit save on click if not on merge side', async () => {
        const callback = vi.fn(() => true)
        const wrapper = mount(AdvancedFormSaveButton, {
            global: { provide: {
                    [advancedFormContextKey]: { data: ref({v: {}}), inputs: ref([{validate: callback}]) },
                    [advancedFormSideKey]: ref(0),
                } }
        });

        await wrapper.find('button').trigger('click');
        expect(callback).not.toHaveBeenCalled();
        expect(wrapper.emitted('save')).toBeFalsy();
    });
});
