import {ComponentTestHelper} from "@tu/vue/components/ComponentTestHelper";
import HistoryView from "@components/HistoryView.vue";
import {describe} from 'vitest';

describe('HistoryView', () => {
    ComponentTestHelper.exists(HistoryView);
});
