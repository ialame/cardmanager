import {mount} from "@vue/test-utils";
import Tooltip from "@components/tooltip/Tooltip.vue"
import {ComponentTestHelper} from "@tu/helpers";
import {describe, expect, it} from 'vitest';

const TestComponent = {
    components: { Tooltip },
    template: '<div class="test-parent"><Tooltip>test</Tooltip></div>'
}

describe('Tooltip', () => {
    ComponentTestHelper.exists(Tooltip);
    it('to be hidden by default', () => {
        const wrapper = mount(TestComponent);

        expect(wrapper.findComponent(Tooltip).exists()).toBeTruthy();
        expect(wrapper.find('div.r-tooltip-inner').exists()).toBeFalsy();
    });
});
