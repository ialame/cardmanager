import {flushPromises, shallowMount} from "@vue/test-utils";
import ReassociationForm from "@components/cards/pokemon/extracted/list/reassociation/ReassociationForm.vue";
import ReassociationFormEntry from "@components/cards/pokemon/extracted/list/reassociation/ReassociationFormEntry.vue";
import {CardGroupTestHelper} from "../CardGroupTestHelper";
import {ComponentTestHelper} from "@tu/helpers";
import {buildCardGroups, ExtractedPokemonCardEntry} from "@components/cards/pokemon/extracted/list";
import {beforeAll, describe, expect, it, vi} from 'vitest';
import createMocker from "vitest-fetch-mock";

const fetchMock = createMocker(vi);
window.location.href = "http://localhost/";

const cards: ExtractedPokemonCardEntry[] = [
    CardGroupTestHelper.mockCardGroup(1, 1),
    CardGroupTestHelper.mockCardGroup(1, 2),
    CardGroupTestHelper.mockCardGroup(2, 3)
];

const getWrapper = (group: any) => shallowMount(ReassociationForm, { props: { 'modelValue': group } });

describe('ReassociationForm', () => {
    beforeAll(() => {
        fetchMock.enableMocks();
    });

    const groups = buildCardGroups(cards, CardGroupTestHelper.testSets);

    ComponentTestHelper.exists(ReassociationForm);
    it('should have 2 ReassociationFormEntry', async () => {
        const wrapper = getWrapper(groups[0]);

        await flushPromises();
        expect(wrapper.findAllComponents(ReassociationFormEntry).length).toBe(2);
    });
    it('should reassociate on trigger', async () => {
        const wrapper = getWrapper(groups[0]);
        const entryWrapper = wrapper.findAllComponents(ReassociationFormEntry).at(0);

        await flushPromises();
        entryWrapper!.vm.$emit('reassociate', '1', '2');
        await flushPromises();
        expect(wrapper.emitted('update:modelValue')).toBeTruthy();
    });
});
