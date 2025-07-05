import {ComponentTestHelper} from "@tu/vue/components/ComponentTestHelper";
import {SelectionCheckbox, useProvideSelection, useSelectionKey} from "@/selection";
import {ref} from "vue";
import {mount} from "@vue/test-utils";
import {describe, expect, it} from 'vitest';

describe("SelectionCheckbox", () => {
    const array = ref([1, 2, 3]);

    const selection = useProvideSelection(array, (i: number) => i);
    ComponentTestHelper.exists(SelectionCheckbox, {
        props: { entry: 1 },
        global: { provide: { [useSelectionKey]: selection } }
    });
    it("should not be selected", () => {
        const array = ref([1, 2, 3]);

        const selection = useProvideSelection(array, (i: number) => i);
        const wrapper = mount(SelectionCheckbox, {
            props: { entry: 1 },
            global: { provide: { [useSelectionKey]: selection } }
        });

        expect(wrapper.find('input').element.checked).toBeFalsy();
    });
    it("Should select on check", async () => {
        const array = ref([1, 2, 3]);

        const selection = useProvideSelection(array, (i: number) => i);
        const wrapper = mount(SelectionCheckbox, {
            props: { entry: 1 },
            global: { provide: { [useSelectionKey]: selection } }
        });

        await wrapper.find('input').setValue(true);

        expect(selection.selected.value).toEqual([1]);
    });
});
