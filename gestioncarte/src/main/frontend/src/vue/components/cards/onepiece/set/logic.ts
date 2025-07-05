import {getTranslationName} from "@/localization";
import {OnePieceSetDTO} from "@/types";
import {RestComposables} from "@/vue/composables/RestComposables";
import createSharedRestComposable = RestComposables.createSharedRestComposable;

export const getSetName = (set?: OnePieceSetDTO): string => getTranslationName("EXTENSION", set?.translations);

export const useUnsavedSets = createSharedRestComposable<OnePieceSetDTO[], true>('/api/cards/onepiece/sets/unsaved', [], {
    fullState: true,
});
