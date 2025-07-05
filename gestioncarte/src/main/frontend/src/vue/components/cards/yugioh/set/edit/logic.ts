import {YuGiOhSerieDTO, YuGiOhSetDTO, YuGiOhSetTranslationDTO} from "@/types";
import {Translations} from "@/localization";


export type EditedYuGiOhSetTranslation = YuGiOhSetTranslationDTO & {
    officialSitePids: string[];
    yugipediaSets?: string[];

};
export type EditedYuGiOhSet = Omit<YuGiOhSetDTO, 'serieId' | 'translations' | 'officialSitePids'| 'yugipediaSets'> & {
    serie: YuGiOhSerieDTO,
    translations: Translations<EditedYuGiOhSetTranslation>;
};
