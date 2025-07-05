import {ComponentTestHelper} from "@tu/vue/components/ComponentTestHelper";
import DropdownItem from "@components/dropdown/DropdownItem.vue";
import {shallowMount} from "@vue/test-utils";
import {RouterLink} from "vue-router";
import {describe, expect, it} from 'vitest';

describe('DropdownItem', () => {
    ComponentTestHelper.exists(DropdownItem);
    it('has a an anchor', async () => {
        const wrapper = shallowMount(DropdownItem,{
            props: { href: '/test/url' }
        });

        expect(wrapper.find('a').exists()).toBeTruthy();
        expect(wrapper.find('a').attributes('href')).toBe('/test/url');
    });
    it('has a RouterLink', async () => {
        const wrapper = shallowMount(DropdownItem,{
            props: { to: '/test/url' }
        });

        expect(wrapper.findComponent(RouterLink).exists()).toBeTruthy();
        expect(wrapper.findComponent(RouterLink).props().to).toBe('/test/url');
    });
    it('has a div', async () => {
        const wrapper = shallowMount(DropdownItem);

        expect(wrapper.find('div').exists()).toBeTruthy();
    });
});
