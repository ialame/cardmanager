import {shallowRef} from "vue";
import {createExtractedCardEntry, ExtractedCardEntry, useSelectedCards} from "@components/extraction";
import {describe, expect, it, vi} from 'vitest';

const getEntries = () => shallowRef<ExtractedCardEntry<any>[]>([
    createExtractedCardEntry(1),
    createExtractedCardEntry(2),
    createExtractedCardEntry(3),
]);

describe('useSelectedCards', () => {
    it('should select entry', () => {
        const entries = getEntries();
        const { select, isSelected } = useSelectedCards(entries, e => e, vi.fn());

        select(entries.value[1], true);
        expect(isSelected(entries.value[0])).toBe(false);
        expect(isSelected(entries.value[1])).toBe(true);
        expect(isSelected(entries.value[2])).toBe(false);
    });
    it('should not select entry when out of bound', () => {
        const entries = getEntries();
        const { select, isSelected } = useSelectedCards(entries, e => e, vi.fn());

        select(entries.value[3], true);
        for (const entry of entries.value) {
            expect(isSelected(entry)).toBe(false);
        }
    });
    it('should invert selection', () => {
        const entries = getEntries();
        const { revert, select, isSelected } = useSelectedCards(entries, e => e, vi.fn());

        select(entries.value[1], true);
        revert();
        expect(isSelected(entries.value[0])).toBe(true);
        expect(isSelected(entries.value[1])).toBe(false);
        expect(isSelected(entries.value[2])).toBe(true);
    });
    it('should remove selected', async () => {
        const entries = getEntries();
        const ignoreCard = vi.fn(async (entry: ExtractedCardEntry<any>) => {
            entries.value = entries.value.filter(e => e !== entry)
        });
        const { removeSelected, select, isSelected } = useSelectedCards(entries, e => e, ignoreCard);

        select(entries.value[1], true);
        await removeSelected();
        expect(ignoreCard).toBeCalledTimes(1);
        expect(entries.value.length).toBe(2);
        for (const entry of entries.value) {
            expect(isSelected(entry)).toBe(false);
        }
    });
    it('should select all', () => {
        const entries = getEntries();
        const { selectAll, isSelected } = useSelectedCards(entries, e => e, vi.fn());

        selectAll(entries.value, true);
        for (const entry of entries.value) {
            expect(isSelected(entry)).toBe(true);
        }
    });
});
