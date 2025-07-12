import SetIconSelect from '@/vue/components/cards/pokemon/set/edit/SetIconSelect.vue';
import {advancedFormContextKey, useProvideAdvancedFormContext} from "@components/form/advanced";
import {ref} from "vue";
import {flushPromises, mount} from "@vue/test-utils";
import createFetchMock from "vitest-fetch-mock";
import {beforeEach, describe, expect, it, vi} from 'vitest';

const fetchMock = createFetchMock(vi);

describe('SetIconSelect', () => {
    beforeEach(() => {
        fetchMock.enableMocks();
        fetchMock.mockResponse(async r => {
            if (r.url.includes('/images/symbole/pokemon/noir/')) {
                return 'dGVzdA==';
            } else if (r.url.includes('/api/cards/pokemon/bulbapedia/images/search')) {
                return JSON.stringify(["dGVzdA=="]);
            }
            return {status: 404};
        });
    });

    it('should not be visible', () => {
        const context = useProvideAdvancedFormContext(ref({shortName: 'test'}));
        const wrapper = mount(SetIconSelect, {
            global: { provide: { [advancedFormContextKey]: context } }
        });

        expect(wrapper.findComponent(SetIconSelect).exists()).toBeTruthy();
        expect(wrapper.find('div.advanced-form-image-select').exists()).toBeFalsy();
    });
    it('should be visible', async () => {
        const context = useProvideAdvancedFormContext(ref({shortName: 'test', icon: {base64: 'dGVzdA==', source: 'saved'}}));
        const wrapper = mount(SetIconSelect, {
            global: { provide: { [advancedFormContextKey]: context } }
        });

        await flushPromises();
        expect(wrapper.findComponent(SetIconSelect).exists()).toBeTruthy();
        expect(wrapper.find('div.advanced-form-image-select').exists()).toBeTruthy();
        expect(wrapper.find('div.advanced-form-image-select .selected').exists()).toBeFalsy();
    });
});
