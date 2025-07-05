<template>
    <div class="d-flex flex-row overflow-hidden">
        <div class="set-icon">
            <OptionalImage v-if="computedSet?.shortName" :src="getSetIconLink(computedSet?.shortName)" />
        </div>
        <Flag v-if="langue" class="mt-2 me-1" :lang="langue" />
        <template v-if="computedShowParent">
            {{ parentName }}
            <IdTooltip :id="computedSet?.parentId" />
            &nbsp;|&nbsp;
        </template>
        {{ name }}
        <IdTooltip :id="computedSet?.id" />
    </div>
</template>

<script lang="ts" setup>
import {PokemonSetDTO, PokemonSetTranslationDTO} from "@/types";
import {computed} from "vue";
import IdTooltip from "@components/tooltip/IdTooltip.vue";
import OptionalImage from "@components/OptionalImage.vue";
import {Flag, getTranslationField, LocalizationCode} from "@/localization";
import {computedAsync} from "@vueuse/core";
import {PokemonComposables} from "@/vue/composables/pokemon/PokemonComposables";
import {isString} from "lodash";
import {getSetName} from "@components/cards/pokemon/set/logic";
import {getSetIconLink} from "@components/cards/pokemon/set/icon";
import pokemonSetService = PokemonComposables.pokemonSetService;

interface Props {
    set?: PokemonSetDTO;// | string;
    showParent?: boolean;
    langue: LocalizationCode;
}

const props = withDefaults(defineProps<Props>(), {
    showParent: () => false,
});

const computedSet = computedAsync<PokemonSetDTO | undefined>(() => props.set);

const name = computed(() => props.set?.translations?.[props.langue]?.name);
//const localization = computed(() => props.set?.translations?.[props.langue]);
const computedShowParent = computed(() => props.showParent && computedSet.value?.parentId);
const parentName = computedAsync(async () => computedShowParent.value ? getSetName(await pokemonSetService.get(computedSet.value?.parentId as string)) : '', '');

</script>

<style lang="scss" scoped>
.set-icon {
    width: 30px;
    margin-right: 0.5rem;
    text-align: center;

    :deep(object) {
        height: 16px;
    }
}
</style>
