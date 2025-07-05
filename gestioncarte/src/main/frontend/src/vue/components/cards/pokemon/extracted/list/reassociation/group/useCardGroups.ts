import {PokemonSetDTO} from "@/types";
import {get, MaybeRef, MaybeRefOrGetter, toValue} from "@vueuse/core";
import {cloneDeep} from "lodash";
import {onMounted, onUnmounted, ref, Ref, watchEffect} from "vue";
import {PokemonComposables} from "@/vue/composables/pokemon/PokemonComposables";
import {ExtractedPokemonCardEntry} from "@components/cards/pokemon/extracted/list";
import log from "loglevel";
import {ExtractedCardGroup} from "@components/cards/pokemon/extracted/list/reassociation/group/ExtractedCardGroup";
import CardGroupsWorker from "./cardGroupsWorker?worker";

export function useCardGroups(entries: MaybeRefOrGetter<ExtractedPokemonCardEntry[]>, sets?: MaybeRef<PokemonSetDTO[]>) {
    const setsRef = sets ?? PokemonComposables.pokemonSetService.all;
    const groups = ref<ExtractedCardGroup[]>([]) as Ref<ExtractedCardGroup[]>;
    const worker = new CardGroupsWorker();

    watchEffect(() => {
        const s = get(setsRef);
        const e = toValue(entries);

        if (s.length === 0 || e.length === 0) {
            return;
        }

        log.debug("Updating card groups");
        worker.postMessage({ type: "build", sets: cloneDeep(s), entries: cloneDeep(e) });
    });

    const setReviewed = (i: number, reviewed?: boolean) => worker.postMessage({type: "review", index: i, reviewed})

    onMounted(() => worker.onmessage = (e) => {
        groups.value = e.data;
        log.debug("Updated card groups", groups.value);
    });
    onUnmounted(() => worker.terminate());
    return {groups, setReviewed};
}
