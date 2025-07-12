import Alert from '@/alert/Alert.vue';
import {ComponentTestHelper} from "@tu/vue/components/ComponentTestHelper";
import {mount} from "@vue/test-utils";
import {describe, expect, it} from 'vitest';

describe('Alert', () => {
    ComponentTestHelper.exists(Alert, { props: {
        type: 'success',
        message: 'bar'
    }});
    it('should contains a span with the message', () => {
        const wrapper = mount(Alert, { props: {
            type: 'success',
            message: 'bar'
        }});

        expect(wrapper.find('span').text()).toBe('bar');
    });
    it('should emit close when the close button is clicked', async () => {
        const wrapper = mount(Alert, { props: {
                type: 'success',
                message: 'bar'
            }});

        await wrapper.find('button').trigger('click');
        expect(wrapper.emitted('close')).toBeTruthy();
    });
});
