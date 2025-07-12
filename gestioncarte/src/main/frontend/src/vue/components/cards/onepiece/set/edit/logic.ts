import {OnePieceSerieDTO, OnePieceSetDTO, OnePieceSetTranslationDTO} from "@/types";
import {Translations} from "@/localization";

export type EditedOnePieceSetTranslation = OnePieceSetTranslationDTO & {
    officialSiteIds?: number[];
};
export type EditedOnePieceSet = Omit<OnePieceSetDTO, 'serieId' | 'officialSiteIds' | 'translations'> & {
    serie: OnePieceSerieDTO,
    translations: Translations<EditedOnePieceSetTranslation>;
};
