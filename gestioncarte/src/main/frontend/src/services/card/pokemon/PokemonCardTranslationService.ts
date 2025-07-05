import {getCode, Localization, LocalizationCode, Translations} from "@/localization";
import {PokemonCardTranslationDTO,} from "@/types";

export namespace PokemonCardTranslationService {

    export const get = (translations: Translations<PokemonCardTranslationDTO>, localization: Localization | LocalizationCode): PokemonCardTranslationDTO | undefined => {
        return translations[getCode(localization)];
    }

    export const create = (localization: LocalizationCode): PokemonCardTranslationDTO => ({
        name: "",
        number: "",
        labelName: "",
        available: true,
        localization: localization
    });

}
