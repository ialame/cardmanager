import {ModelComposables} from "@/vue/composables/model/ModelComposables";
import {defineComponent, h, ref} from "vue";
import {mount} from "@vue/test-utils";
import {describe, expect, it, vi} from 'vitest';

describe('ModelComposables.useUpdateModel', () => {
    it('Call emit with \'update:modelValue\'', () => {
        let emit = vi.fn();
        let update = ModelComposables.useUpdateModel(() => ({v: 0}), emit);

        update(d => d.v = 1);
        expect(emit).toHaveBeenCalledWith('update:modelValue', {v: 1});
    });
    it('Call emit with \'update:test\'', () => {
        let emit = vi.fn();
        let update = ModelComposables.useUpdateModel(() => ({v: 0}), emit, 'update:test');

        update(d => d.v = 1);
        expect(emit).toHaveBeenCalledWith('update:test', {v: 1});
    });
    it('Call emit with \'update:modelValue\' from ref', () => {
        const r = ref({v: 0});
        let emit = vi.fn();
        let update = ModelComposables.useUpdateModel(r, emit);

        update(d => d.v = 1);
        expect(emit).toHaveBeenCalledWith('update:modelValue', {v: 1});
    });
    it('Call emit with \'update:test\' from ref', () => {
        const r = ref({v: 0});
        let emit = vi.fn();
        let update = ModelComposables.useUpdateModel(r, emit, 'update:test');

        update(d => d.v = 1);
        expect(emit).toHaveBeenCalledWith('update:test', {v: 1});
    });
    it('Don\'t all emit with undefined value', () => {
        const r = ref(undefined);
        let emit = vi.fn();
        let update = ModelComposables.useUpdateModel(r, emit);

        update(d =>{});
        expect(emit).not.toBeCalled();
    });
});

const [useProvideCountState, useCountState] = ModelComposables.createInjectionState((initialValue: number) => ref(initialValue));

const ChildComponent = defineComponent({
    setup() {
        const count = useCountState()
        expect(count?.value).toBe(0)

        return () => h('div')
    },
})

const RootComponent = defineComponent({
    setup() {
        useProvideCountState(0)

        return () => h(ChildComponent)
    },
})

describe('ModelComposables.createInjectionState', () => {
    it('should work', () => {
        mount(RootComponent)
    })
})
