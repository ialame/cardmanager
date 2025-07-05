<template>
    <tr>
        <td>
            <div v-if="set" class="d-flex flex-row">
                <PokemonSetLabel :set="set" showParent />
                <FormButton v-if="setBulbapediaLink" color="link" class="pt-0" :href="setBulbapediaLink" newTab>
                    <BulbapediaIcon />
                </FormButton>
            </div>
        </td>
        <td>
            <template v-if="card">
                <div class="d-flex flex-row">
                    <PokemonCardLabel :card="card" :localization="promo.localization" />
                    <HoverDataList v-if="card.brackets.length > 0" class="ms-4 me-auto" #default="{ value: bracket }" :value="card.brackets.filter(b => b.localization === promo.localization)">
                        <BracketLabel :bracket="bracket" hideFlag />
                    </HoverDataList>
                </div>
            </template>
            <span v-else>!!! CARTE MANQUANTE !!!</span>
        </td>
        <td>{{ promo.name }}</td>
        <td>
            <PromoCardVersionLabel v-if="promo.versionId" :version="promo.versionId" />
        </td>
        <td>
            <FormButton color="link" @click="emit('remove')"><Icon name="trash" class="text-danger" /></FormButton>
        </td>
    </tr>
</template>

<script lang="ts" setup>
import {PromoCardDTO} from "@/types";
import {PromoCardVersionLabel} from "@components/cards/promo/version";
import PokemonCardLabel from "@components/cards/pokemon/PokemonCardLabel.vue";
import {computedAsync} from "@vueuse/core";
import {pokemonCardService} from "@components/cards/pokemon/service";
import PokemonSetLabel from "@components/cards/pokemon/set/PokemonSetLabel.vue";
import {PokemonComposables} from "@/vue/composables/pokemon/PokemonComposables";
import FormButton from "@components/form/FormButton.vue";
import BulbapediaIcon from "@components/cards/pokemon/BulbapediaIcon.vue";
import HoverDataList from "@components/table/HoverDataList.vue";
import {BracketLabel} from "@components/cards/pokemon/bracket";
import Icon from "@components/Icon.vue";
import pokemonSetService = PokemonComposables.pokemonSetService;

interface Props {
    promo: PromoCardDTO;
}
interface Events {
    (e: 'remove'): void;
}

const props = defineProps<Props>();
const emit = defineEmits<Events>();

const card = computedAsync(() => pokemonCardService.findWithPromo(props.promo.id));
const set = computedAsync(async () => card.value ? (await pokemonSetService.filterByLocalization(card.value.setIds, props.promo.localization))[0] : undefined);

const expansionsBulbapedia = PokemonComposables.useExpansionsBulbapedia();
const setBulbapediaLink = computedAsync(() => {
    if (!set.value) {
        return "";
    }

    const bulbapediaSets = expansionsBulbapedia.value.filter(e => e.setId === set.value?.id);

    if (bulbapediaSets.length === 0) {
        return "";
    }

    return bulbapediaSets[0].url;
});
</script>
