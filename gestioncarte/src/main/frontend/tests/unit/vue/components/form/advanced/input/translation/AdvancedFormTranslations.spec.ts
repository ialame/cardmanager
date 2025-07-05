import {
    advancedFormContextKey,
    AdvancedFormTranslations,
    useProvideAdvancedFormContext
} from "@components/form/advanced";
import {ref} from "vue";
import {mount} from "@vue/test-utils";
import {describe, expect, it} from 'vitest';

describe('AdvancedFormTranslations', () => {
    it('should be visible', () => {
        const context = useProvideAdvancedFormContext(ref({v: {}}));
        const wrapper = mount(AdvancedFormTranslations, {
            props: { path: 'v' },
            global: { provide: { [advancedFormContextKey]: context } }
        });

        expect(wrapper.findComponent(AdvancedFormTranslations).exists()).toBeTruthy();
    });
    it('should only have us and jp', () => {
        const context = useProvideAdvancedFormContext(ref({v: {}}));
        const wrapper = mount(AdvancedFormTranslations, {
            props: { path: 'v', localizations: ['us', 'jp'] },
            global: { provide: { [advancedFormContextKey]: context } }
        });

        expect(wrapper.findAll('div.advanced-form-translation')).toHaveLength(2);
        expect(wrapper.findAll('div.advanced-form-translation').map(w => w.attributes('data-path'))).toEqual(['v.us', 'v.jp']);
    });
    it('adds fr once pressed', async () => {
        const value = ref({v: {}});
        const context = useProvideAdvancedFormContext(value);
        const wrapper = mount(AdvancedFormTranslations, {
            props: { path: 'v', localizations: ['us', 'jp'] },
            global: { provide: { [advancedFormContextKey]: context } }
        });

        await Promise.all(wrapper.findAll('.missing-localizations button').map(async w => {
            if (w.find('img[src="/svg/flags/squared/fr.svg"]').exists()) {
                await w.trigger('click');
            }
        }));
        expect(wrapper.findAll('div.advanced-form-translation')).toHaveLength(3);
        expect(wrapper.findAll('div.advanced-form-translation').map(w => w.attributes('data-path'))).toEqual(['v.us', 'v.jp', 'v.fr']);
        expect((value.value.v as any).fr.available).toBeTruthy();
        expect(wrapper.emitted('update:localizations')).toHaveLength(1);
    });
    it('removes us when props are updated', async () => {
        const value = ref({v: {}});
        const context = useProvideAdvancedFormContext(value);
        const wrapper = mount(AdvancedFormTranslations, {
            props: { path: 'v', localizations: ['us', 'jp'] },
            global: { provide: { [advancedFormContextKey]: context } }
        });

        await wrapper.setProps({localizations: ['jp']});
        expect(wrapper.findAll('div.advanced-form-translation')).toHaveLength(1);
        expect(wrapper.findAll('div.advanced-form-translation').map(w => w.attributes('data-path'))).toEqual(['v.jp']);
        expect((value.value.v as any).us).toBeUndefined();
    });
    it('removes us when bin is clicked', async () => {
        const value = ref({v: { us: 'a', jp: 'b'}});
        const context = useProvideAdvancedFormContext(value);
        const wrapper = mount(AdvancedFormTranslations, {
            props: { path: 'v', localizations: ['us', 'jp'] },
            global: { provide: { [advancedFormContextKey]: context } }
        });

        await wrapper.find('div.advanced-form-translation[data-path="v.us"] button.btn-danger').trigger('click');
        expect(wrapper.emitted('update:localizations')).toHaveLength(1);
        expect((value.value.v as any).us).toBeUndefined();
        expect((value.value.v as any).jp).toBeDefined();

    });
});
