import {ComponentTestHelper} from "@tu/vue/components/ComponentTestHelper";
import SideBarLink from "@components/sidebar/SideBarLink.vue";
import {routeLocationKey} from "vue-router";
import {shallowMount} from "@vue/test-utils";
import {describe, expect, it} from 'vitest';

describe('SideBarLink', () => {
    ComponentTestHelper.exists(SideBarLink, {
        global: { provide: { [routeLocationKey as any]: { fullPath: '' } } }
    });
    it('is selected if rout equals its path', () => {
        const wrapper = shallowMount(SideBarLink, {
            props: { to: '/test' },
            global: { provide: { [routeLocationKey as any]: { fullPath: '/test' } } }
        });

        expect(wrapper.classes()).toContain('selected');
    });
});
