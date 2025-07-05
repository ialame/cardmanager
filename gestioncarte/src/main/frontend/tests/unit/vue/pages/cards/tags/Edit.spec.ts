import {ComponentTestHelper} from "@tu/vue/components/ComponentTestHelper";
import Edit from "@/vue/pages/cards/tags/Edit.vue";
import {routeLocationKey} from "vue-router";
import {flushPromises, mount} from "@vue/test-utils";
import {AdvancedFormInput} from "@components/form/advanced";
import {mockServiceCalls} from "@tu/types/ServiceTestHelper";
import {cardTagService} from "@components/cards/tags";
import {beforeEach, describe, expect, it} from 'vitest';

describe('Edit', () => {
    beforeEach(() => {
        mockServiceCalls(cardTagService, [
            { id: "testId", type: "testType", translations: {} }
        ])
    });
    ComponentTestHelper.exists(Edit, {
        global: { provide: { [routeLocationKey as any]: { params: {} } } }
    });
    it('has filed filled according to fetched data', async () => {
        const wrapper = mount(Edit, {
            global: { provide: { [routeLocationKey as any]: { params: { tagId: 'testId' } } } }
        });

        await flushPromises();
        expect(wrapper.findComponent(AdvancedFormInput).exists()).toBeTruthy();
        expect((wrapper.find('div[data-path="type"] input[type="hidden"]').exists())).toBeTruthy();
        expect((wrapper.find('div[data-path="type"] input[type="hidden"]').element as HTMLInputElement).value).toBe('testType');
    });

});
