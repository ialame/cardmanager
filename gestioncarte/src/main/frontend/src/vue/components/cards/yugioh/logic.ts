import {YuGiOhCardDTO, YuGiOhCardTranslationDTO} from "@/types";
import {getTranslationField} from "@/localization";

function getYuGiOhTranslationField<T extends keyof YuGiOhCardTranslationDTO>(f: T, d: YuGiOhCardTranslationDTO[T]) {
    return (card: YuGiOhCardDTO): YuGiOhCardTranslationDTO[T] => getTranslationField<YuGiOhCardTranslationDTO, T>(card.translations, f)?.[1] || d;
}

export const getYuGiOhCardNumber = getYuGiOhTranslationField("number", "");
export const getYuGiOhCardName = getYuGiOhTranslationField("name", "");
export const getYuGiOhCardRarity = getYuGiOhTranslationField("rarity", "");
