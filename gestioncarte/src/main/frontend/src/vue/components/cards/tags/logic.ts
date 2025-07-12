import {RestComposables} from "@/vue/composables/RestComposables";
import createSharedRestComposable = RestComposables.createSharedRestComposable;

export const useCardTagTypes = createSharedRestComposable<string[]>('/api/cards/tags/types', []);
