import {YuGiOhSetDTO} from "@/types";
import {RestComposables} from "@/vue/composables/RestComposables";
import createSharedRestComposable = RestComposables.createSharedRestComposable;

/**
 * @deprecated
 */
export const useUnsavedSets = createSharedRestComposable<YuGiOhSetDTO[], true>('/api/cards/yugioh/official-site/sets', [], {
    fullState: true,
});
