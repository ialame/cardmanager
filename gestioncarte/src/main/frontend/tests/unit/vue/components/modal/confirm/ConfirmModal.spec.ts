import {ComponentTestHelper} from "@tu/helpers";
import ConfirmModal from "@components/modal/confirm/ConfirmModal.vue";
import {describe} from 'vitest';

describe('ConfirmModal', () => {
    ComponentTestHelper.exists(ConfirmModal);
});
