import {
    advancedFormContextKey,
    AdvancedFormImageSelect,
    useProvideAdvancedFormContext
} from "@components/form/advanced";
import {ref} from "vue";
import {flushPromises, mount} from "@vue/test-utils";
import {describe, expect, it} from 'vitest';

describe('AdvancedFormImageSelect', () => {
    it('should not be visible', () => {
        const context = useProvideAdvancedFormContext(ref({v: {}}));
        const wrapper = mount(AdvancedFormImageSelect, {
            props: { path: 'v' },
            global: { provide: { [advancedFormContextKey]: context } }
        });

        expect(wrapper.findComponent(AdvancedFormImageSelect).exists()).toBeTruthy();
        expect(wrapper.find('div.advanced-form-image-select').exists()).toBeFalsy();
    });
    it('should be visible', () => {
        const context = useProvideAdvancedFormContext(ref({v: {}}));
        const wrapper = mount(AdvancedFormImageSelect, {
            props: { path: 'v', images: [{base64Image: 'dGVzdA==', source: 'upload'}] },
            global: { provide: { [advancedFormContextKey]: context } }
        });

        expect(wrapper.findComponent(AdvancedFormImageSelect).exists()).toBeTruthy();
        expect(wrapper.find('div.advanced-form-image-select').exists()).toBeTruthy();
        expect(wrapper.find('div.advanced-form-image-select .selected').exists()).toBeFalsy();
    });
    it('should have selected image', () => {
        const context = useProvideAdvancedFormContext(ref({v: {base64Image: 'dGVzdA==', source: 'upload'}}));
        const wrapper = mount(AdvancedFormImageSelect, {
            props: { path: 'v', images: [{base64Image: 'dGVzdA==', source: 'upload'}] },
            global: { provide: { [advancedFormContextKey]: context } }
        });

        expect(wrapper.findComponent(AdvancedFormImageSelect).exists()).toBeTruthy();
        expect(wrapper.find('div.advanced-form-image-select').exists()).toBeTruthy();
        expect(wrapper.find('div.advanced-form-image-select .selected').exists()).toBeTruthy();
    });
    it('should upload file', async () => {
        const context = useProvideAdvancedFormContext(ref({v: {}}));
        const wrapper = mount(AdvancedFormImageSelect, {
            props: { path: 'v' },
            global: { provide: { [advancedFormContextKey]: context } }
        });

        (wrapper.vm as any).addImage(Buffer.from('dGVzdA==', 'base64'));
        await flushPromises();

        expect((wrapper.vm as any).usedImages).toEqual([{base64Image: 'dGVzdA==', source: 'upload'}]);
        expect(wrapper.find('div.advanced-form-image-select .selected').exists()).toBeTruthy();
    });
});
