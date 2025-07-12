import {
    ExpansionBulbapediaDTO,
    ExtractedImageDTO,
    PokemonSerieDTO,
    PokemonSetDTO,
    PtcgoSetDTO,
    WikiUrlDTO
} from "@/types";
import {LocalizationCode} from "@/localization";
import {isEmpty, isNil} from "lodash";

export type WikiUrls = Partial<Record<LocalizationCode, WikiUrlDTO[]>>;

export type EditedPokemonSet = Omit<PokemonSetDTO, 'serieId' | 'parentId'> & {
    serie: PokemonSerieDTO,
    parent: PokemonSetDTO,
    icon?: ExtractedImageDTO,
    expansionsBulbapedia: {
        name: string,
        list: ExpansionBulbapediaDTO[]
    }
    ptcgoSets: PtcgoSetDTO[],
    wikiUrls: WikiUrls,
    officialSitePaths: string[],
    pkmncardsComSetPath: string,
    japaneseOfficialSitePgs: string[],
    pokecardexCode: string,
};

export const getInitialExtensionBulbapedia = (name?: string, localization?: LocalizationCode): ExpansionBulbapediaDTO => ({
    id: 0,
    setId: "",
    serieId: "",
    name: name ?? '',
    page2Name: '',
    h3Name: '',
    localization: isNil(localization) || isEmpty(localization) ? 'us' : localization,
    firstNumber: '',
    tableName: '',
    url: '',
    promo: false
});

export const getUsJp = (set: PokemonSetDTO | EditedPokemonSet): LocalizationCode => {
    if (set.translations.jp?.available) {
        return 'jp';
    }
    return 'us';
}
