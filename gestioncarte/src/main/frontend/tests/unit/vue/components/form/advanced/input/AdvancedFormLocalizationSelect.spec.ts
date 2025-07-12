import {AdvancedFormLocalizationSelect, useProvideAdvancedFormContext} from "@components/form/advanced";
import {ref} from "vue";
import {mount} from "@vue/test-utils";
import {describe, expect, it} from 'vitest';

const testComponent = (v: any) => ({
    components: { AdvancedFormLocalizationSelect },
    setup() {
        useProvideAdvancedFormContext(v);
    },
    template: '<div class="test-parent"><AdvancedFormLocalizationSelect path="v" v-bind="$attrs" /></div>'
});

describe('AdvancedFormLocalizationSelect', () => {
    it('should be visible', () => {
        const wrapper = mount(testComponent(ref({v: "us"})));

        expect(wrapper.findComponent(AdvancedFormLocalizationSelect).exists()).toBeTruthy();
        expect(wrapper.find('button').exists()).toBeTruthy();
        expect(wrapper.find('button[data-localization=us]').classes()).toContain('selected');
    });
    it('should change values', async () => {
        const v = ref({v: "us"});
        const wrapper = mount(testComponent(v));

        await wrapper.find('button[data-localization=jp]').trigger('click');
        expect(v.value.v).toBe('jp');
        expect(wrapper.find('button[data-localization=jp]').classes()).toContain('selected');
    });
    it('should add jp', async () => {
        const v = ref({v: ["us"]});
        const wrapper = mount(testComponent(v), {
            props: { multiple: true }
        });

        await wrapper.find('button[data-localization=jp]').trigger('click');
        expect(v.value.v).toEqual(['us', 'jp']);
        expect(wrapper.find('button[data-localization=us]').classes()).toContain('selected');
        expect(wrapper.find('button[data-localization=jp]').classes()).toContain('selected');
    });
    it('should remove us', async () => {
        const v = ref({v: ["us"]});
        const wrapper = mount(testComponent(v), {
            props: { multiple: true }
        });

        await wrapper.find('button[data-localization=us]').trigger('click');
        expect(v.value.v).toEqual([]);
        expect(wrapper.find('button[data-localization=us]').classes()).not.toContain('selected');
        expect(wrapper.find('button[data-localization=jp]').classes()).not.toContain('selected');
    });
});
