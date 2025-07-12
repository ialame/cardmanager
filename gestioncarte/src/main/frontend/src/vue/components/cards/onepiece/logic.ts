import {computed} from "vue";
import {MaybeRefOrGetter, toValue} from "@vueuse/core";
import {usePageTitle} from "@/vue/composables/PageComposables";
import {OnePieceCardDTO, OnePieceCardTranslationDTO} from "@/types";
import {getTranslationField} from "@/localization";

export const useOnePieceTitle = (title: MaybeRefOrGetter<string>) => usePageTitle(computed(() => `One Piece - ${toValue(title)}`));

function getOnePieceTranslationField<T extends keyof OnePieceCardTranslationDTO>(f: T, d: OnePieceCardTranslationDTO[T]) {
    return (card: OnePieceCardDTO): OnePieceCardTranslationDTO[T] => getTranslationField<OnePieceCardTranslationDTO, T>(card.translations, f)?.[1] || d;
}

export const getOnePieceCardName = getOnePieceTranslationField("name", "");
