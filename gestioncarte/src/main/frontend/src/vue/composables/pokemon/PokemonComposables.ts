import {RestComposables} from "@/vue/composables/RestComposables";
import {BracketDTO, ExpansionBulbapediaDTO, FeatureDTO, PokemonCardDTO, PokemonSetDTO, Service} from "@/types";
import {computedAsync, createSharedComposable, MaybeRefOrGetter, toValue} from "@vueuse/core";
import {PokemonSetService} from "@/services/card/pokemon/set/PokemonSetService";
import {isEmpty, isNil} from "lodash";
import rest from "@/rest";
import {useRaise} from "@/alert";
import {LocalizationCode} from "@/localization";

/**
 * @deprecated
 */
export namespace PokemonComposables { // TODO remove namespace

    /**
     * @deprecated
     */
    export const usePokemonCardTypes = RestComposables.createSharedRestComposable<string[], false, true>('/api/cards/pokemon/types', [], { storageKey: 'pokemonCardTypes' });
    /**
     * @deprecated
     */
    export const useExpansionsBulbapedia = RestComposables.createSharedRestComposable<ExpansionBulbapediaDTO[], false, true>('/api/cards/pokemon/bulbapedia/expansions', [], { storageKey: 'expansionsBulbapedia' });

    export const pokemonSetService= new class extends Service<PokemonSetDTO, string> {
        constructor() {
            super('/api/cards/pokemon/sets');
        }

        async filterByLocalization(setIds: string[], localization: LocalizationCode): Promise<PokemonSetDTO[]> {
            return (await Promise.all(setIds.map(i => this.get(i))))
                .filter(s => s?.translations?.[localization]?.available) || [];
        }
    };

    export const pokemonFeatureService = new Service<FeatureDTO, string>('/api/cards/pokemon/features');
    export const bracketService = new class extends Service<BracketDTO, string> {
        constructor() {
            super('/api/cards/pokemon/brackets');
        }

        async findAllByName(name: string): Promise<BracketDTO[]> {
            return this.find({name});
        }

        async findAllCards(bracketId: string): Promise<PokemonCardDTO[]> {
            return rest.get(`/api/cards/pokemon/brackets/${bracketId}/cards`);
        }
    }
    export const setsService = new class extends Service<PokemonSetDTO, string> {
        constructor() {
            super('/api/cards/pokemon/sets');
        }

        public fetch() {
            super.fetch();
        }
    }

    /**
     * @deprecated
     */
    export const useSetsWithBulbapedia = createSharedComposable(() => {
        const expansionsBulbapedia = useExpansionsBulbapedia();

        return computedAsync(async () => await pokemonSetService.find(s => !isNil(expansionsBulbapedia.value.find(e => e.setId === s.id))));
    });

    const isNotSet = (t: any) => isNil(t) || t === 0 || isEmpty(t);

    /**
     * @deprecated
     */
    export const useIndependentSets = createSharedComposable(() => computedAsync(async () => await pokemonSetService.find(async s => isNotSet(s.parentId) && await pokemonSetService.find(s2 => s2.parentId === s.id) === undefined), []));

    export const useOpenEditSet = (sets?: MaybeRefOrGetter<PokemonSetDTO[]>) => {
        const raise = useRaise();
        const s = sets != undefined ? sets : pokemonSetService.all;

        return (id: string) => {
            const set = PokemonSetService.findSet(id, toValue(s));

            if (set && set.id) {
                window.open("/cards/pokemon/sets/" + set.id, '_blank');
            } else {
                raise.warn("Aucune extension selectionée.");
            }
        };
    }
}
