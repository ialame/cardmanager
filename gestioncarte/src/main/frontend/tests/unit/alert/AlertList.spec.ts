import AlertList from '@/alert/AlertList.vue';
import {ComponentTestHelper} from "@tu/vue/components/ComponentTestHelper";
import {mount} from "@vue/test-utils";
import {useRaise} from "@/alert";
import Alert from '@/alert/Alert.vue';
import {nextTick} from "vue";
import {describe, expect, it, vi} from 'vitest';

vi.useFakeTimers();

describe('AlertList', () => {
    ComponentTestHelper.exists(AlertList);

    it('should remove alert on button click', async () => {
        const wrapper = mount(AlertList);
        const raise = useRaise();

        raise.genericError();
        await nextTick();
        expect(wrapper.findAllComponents(Alert).length).toBe(1);
        await wrapper.find('button').trigger('click');
        expect(wrapper.findAllComponents(Alert).length).toBe(0);
        vi.runAllTimers();
    });
});
