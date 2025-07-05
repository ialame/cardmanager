import {ComponentTestHelper} from "@tu/vue/components/ComponentTestHelper";
import List from "@/vue/pages/cards/pokemon/List.vue";
import {routeLocationKey} from "vue-router";
import {describe} from 'vitest';

describe('List', () => {
    ComponentTestHelper.exists(List, {
        global: { provide: { [routeLocationKey as any]: { params: {} } } }
    });
});
