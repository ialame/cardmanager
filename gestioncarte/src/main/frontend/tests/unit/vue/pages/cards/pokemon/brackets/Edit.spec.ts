import {ComponentTestHelper} from "@tu/vue/components/ComponentTestHelper";
import Edit from "@/vue/pages/cards/pokemon/brackets/Edit.vue";
import {routeLocationKey} from "vue-router";
import createMocker from "vitest-fetch-mock";
import {flushPromises, mount} from "@vue/test-utils";
import {AdvancedFormInput} from "@components/form/advanced";
import {beforeEach, describe, expect, it, vi} from 'vitest';

const fetchMock = createMocker(vi);
window.location.href = "http://localhost/";

describe('Edit', () => {
    beforeEach(() => {
        fetchMock.enableMocks();
    });
    ComponentTestHelper.exists(Edit, {
        global: { provide: { [routeLocationKey as any]: { params: {} } } }
    });
    it('has filed filled according to fetched data', async () => {
        fetchMock.mockResponse(r => {
            if (r.url === 'http://localhost/api/cards/pokemon/brackets/testId') {
                return Promise.resolve(JSON.stringify({ id: "testId", name: "testName", localization: 'us', translations: {} }));
            } else if  ((r.url === 'http://localhost/api/localizations/groups/western')) {
                return Promise.resolve(JSON.stringify(['us', 'de', 'fr']));
            }
            return Promise.resolve("");
        });
        const wrapper = mount(Edit, {
            global: { provide: { [routeLocationKey as any]: { params: { bracketId: 'testId' } } } }
        });

        await flushPromises();
        expect(wrapper.findComponent(AdvancedFormInput).exists()).toBeTruthy();
        expect((wrapper.find('input[data-path="name"]').exists())).toBeTruthy();
        expect((wrapper.find('input[data-path="name"]').element as HTMLInputElement).value).toBe('testName');
    });

});
