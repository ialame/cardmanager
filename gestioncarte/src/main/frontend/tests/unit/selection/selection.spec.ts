import {ref} from "vue";
import {useProvideSelection} from "@/selection";
import {describe, expect, it} from 'vitest';

describe("useProvideSelection", () => {
    it("should provide a selection list", () => {
        const array = ref([1, 2, 3]);

        const {selected} = useProvideSelection(array, (i: number) => i);

        expect(selected.value).toEqual([]);
    });
    it("should provide a selection list with selected items", () => {
        const array = ref([1, 2, 3]);

        const {selected, isSelected, select} = useProvideSelection(array, (i: number) => i);

        select(2);

        expect(selected.value).toEqual([2]);
        expect(isSelected(2)).toBe(true);
    });
    it("should deselect selected element", () => {
        const array = ref([1, 2, 3]);

        const {selected, isSelected, select} = useProvideSelection(array, (i: number) => i);

        select(2);

        expect(selected.value).toEqual([2]);
        expect(isSelected(2)).toBe(true);

        select(2);

        expect(selected.value).toEqual([]);
        expect(isSelected(2)).toBe(false);
    });
    it("should not select with undefined", () => {
        const array = ref([1, 2, 3]);

        const {select, selected} = useProvideSelection(array, (i: number) => i);

        select();

        expect(selected.value).toEqual([]);
    });
    it("should revert all", () => {
        const array = ref([1, 2, 3]);

        const {selected, revert} = useProvideSelection(array, (i: number) => i);

        revert();

        expect(selected.value).toEqual([1, 2, 3]);
    });
    it("should revert with only one selection", () => {
        const array = ref([1, 2, 3]);

        const {selected, select, revert} = useProvideSelection(array, (i: number) => i);

        select(2);
        revert();

        expect(selected.value).toEqual([1, 3]);
    });
});
