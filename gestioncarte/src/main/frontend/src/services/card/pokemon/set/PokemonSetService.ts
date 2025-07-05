import {EditSetForm, PokemonSetDTO} from '@/types';
import rest from "@/rest";
import {LocalizationCode, sortLocalizations} from "@/localization";

export namespace PokemonSetService {
    export const cloneEditSetForm = async (localizations: LocalizationCode[], setId?: string): Promise<EditSetForm> => {
        if (setId) {
            const value: EditSetForm = await rest.get(`/api/cards/pokemon/sets/${setId}/form`);
            const translations = value.set.translations;

            localizations?.forEach(l => {
                if (translations && !translations[l]) {
                    translations[l] = {
                        available: false,
                        localization: l,
                        name: "",
                        originalName: "",
                        releaseDate: ""
                    };
                }
            });
            return value;
        }
        return Promise.resolve(PokemonSetService.newEditSetForm());
    };


    export const newSet = (): PokemonSetDTO => ({
        id: "",
        translations: {},
        totalNumber: '',
        serieId: "",
        promo: false,
        shortName: ''
    });

    export const newEditSetForm = (): EditSetForm => ({
        set: newSet(),
        expansionsBulbapedia: [],
        ptcgoSets: [],
        wikiUrls: {},
        officialSitePath: [],
        pkmncardsComSetPath: '',
        japaneseOfficialSitePgs: [],
    });


    export const findSet = (setId: string, sets: PokemonSetDTO[]) => sets.find(s => s.id === setId);

    export const getSetLocalizations = (setIds: string[], sets: PokemonSetDTO[]): LocalizationCode[] => sortLocalizations(setIds
        .map(id => PokemonSetService.findSet(id, sets))
        .flatMap(set => (set ? Object.values(set.translations)
            .filter(t => t.available)
            .map(t => t.localization) : []))
        .filter(l => l !== 'kr' && l !== 'cn' && l !== 'zh'));
}
