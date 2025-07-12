import {ComponentTestHelper} from "@tu/helpers";
import Collapse from "@components/collapse/Collapse.vue";
import {mount} from "@vue/test-utils";
import {describe, expect, it} from 'vitest';

describe('Collapse', () => {
    ComponentTestHelper.exists(Collapse);
    it('should have a button', () => {
        const wrapper = mount(Collapse);

        expect(wrapper.find('button').exists()).toBeTruthy();
    });
    it('should have content in slot', () => {
        const wrapper = mount(Collapse, {
            slots: { default: '<div class="test">test</div>' }
        });

        expect(wrapper.find('div.test').text()).toBe('test');
        expect(wrapper.find('div.test').isVisible()).toBeTruthy();
    });
    it('close on click', async () => {
        const wrapper = mount(Collapse, {
            slots: { default: '<div class="test">test</div>' }
        });

        await wrapper.find('button').trigger('click');
        expect(wrapper.find('div.test').exists()).toBeFalsy();
    });
});
