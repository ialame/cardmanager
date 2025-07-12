import {mount} from "@vue/test-utils";
import FormButton from "@components/form/FormButton.vue";
import {ComponentTestHelper} from "@tu/helpers";
import {describe, expect, it} from 'vitest';

describe('FormButton', () => {
    ComponentTestHelper.exists(FormButton);
    it('emit click on click', async () => {
        const wrapper = mount(FormButton);

        expect(wrapper.find('button').exists()).toBeTruthy();
        await wrapper.find('button').trigger('click');
        expect(wrapper.emitted().click).toBeTruthy();
    });
});
