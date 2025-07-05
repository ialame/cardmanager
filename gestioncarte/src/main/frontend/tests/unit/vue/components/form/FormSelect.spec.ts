import {shallowMount} from "@vue/test-utils";
import FormSelect from "@components/form/FormSelect.vue";
import Dropdown from "@components/dropdown/Dropdown.vue";
import {ComponentTestHelper} from "@tu/helpers";
import {describe, expect, it} from 'vitest';

describe('FormSelect', () => {
    ComponentTestHelper.exists(FormSelect);
    it('has a Dropdown', async () => {
        const wrapper = shallowMount(FormSelect);

        expect(wrapper.findComponent(Dropdown).exists()).toBeTruthy();
    });
});
