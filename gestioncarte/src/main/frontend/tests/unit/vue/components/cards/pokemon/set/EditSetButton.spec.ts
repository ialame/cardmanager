import {shallowMount} from "@vue/test-utils";
import EditSetButton from "@components/cards/pokemon/set/EditSetButton.vue";
import FormButton from "@components/form/FormButton.vue";
import {ComponentTestHelper} from "@tu/helpers";
import {describe, expect, it} from 'vitest';

describe('EditSetButton', () => {
    ComponentTestHelper.exists(EditSetButton);
    it('has FormButton and is disabled', async () => {
        const wrapper = shallowMount(EditSetButton);

        expect(wrapper.findComponent(FormButton).exists()).toBeTruthy();
        expect(wrapper.findComponent(FormButton).props().disabled).toBeTruthy();
    });
});
