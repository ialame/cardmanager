import {buildCardGroups, ExtractedCardGroup, ExtractedPokemonCardEntry} from "@components/cards/pokemon/extracted/list";
import {CardGroupTestHelper} from "@tu/vue/components/cards/pokemon/extracted/list/CardGroupTestHelper";
import {beforeAll, describe, expect, it} from 'vitest';

const cards: ExtractedPokemonCardEntry[] = [
    CardGroupTestHelper.mockCardGroup(1, 1),
    CardGroupTestHelper.mockCardGroup(1, 2),
    CardGroupTestHelper.mockCardGroup(2, 3)
];
describe('buildCardGroups', () => {
    let groups: ExtractedCardGroup[];

    beforeAll(() => {
        groups = buildCardGroups(cards, CardGroupTestHelper.testSets);
    });
    it('returns cards groups array', () => expect(groups).toBeInstanceOf(Array));
    it ('returns empty array if no cards', () => {
       let emptyGroups = buildCardGroups([], CardGroupTestHelper.testSets);

         expect(emptyGroups.length).toBe(0);
    });
    it('returns 1 groups', () => expect(groups.length).toBe(1));
    it('all elements have at least 2 entries and not beeing reviewed', () => {
        groups.forEach(group => {
            expect(group.entries.length).toBeGreaterThan(1);
            expect(group.reviewed).toBeFalsy();
        });
    });
});
