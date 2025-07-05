import {mount} from "@vue/test-utils";
import AdvancedSettings from "@components/form/AdvancedSettings.vue";
import Row from "@components/grid/Row.vue";
import {ComponentTestHelper} from "@tu/helpers";
import {describe, expect, it} from 'vitest';

const getWrapper = () => mount(AdvancedSettings, { slots: { default: '<div class="test">Hello</div>' } });

describe('AdvancedSettings', () => {
    ComponentTestHelper.exists(AdvancedSettings);
    it('to be hidden by default', () => {
        const wrapper = getWrapper();

        expect(wrapper.findComponent(Row).exists()).toBeTruthy();
        expect(wrapper.find('div.test').exists()).toBeFalsy();
    });
    it('to be visible after click', async () => {
        const wrapper = getWrapper();

        await wrapper.find('a').trigger('click');
        expect(wrapper.findComponent(Row).exists()).toBeFalsy();
        expect(wrapper.find('div.test').exists()).toBeTruthy();
    });
});
