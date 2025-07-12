<template>
        <Flag v-if="localization" class="mt-2 me-1" :lang="localization" />{{ name }}<IdTooltip :id="computedSerie?.id" />
</template>

<script lang="ts" setup>
import { YuGiOhSerieDTO} from "@/types";
import {computed} from "vue";
import IdTooltip from "@components/tooltip/IdTooltip.vue";
import {Flag, localizationCodes} from "@/localization";
import {computedAsync} from "@vueuse/core";
import {isString} from "lodash";
import {yugiohSerieService} from "@components/cards/yugioh/serie/service";

interface Props {
    serie?: YuGiOhSerieDTO | string
}

const props = defineProps<Props>();

const computedSerie = computedAsync(() => isString(props.serie) ? yugiohSerieService.get(props.serie) : props.serie);
const name = computed(() => {
    for (const l of localizationCodes) {
        const name = computedSerie.value?.translations?.[l]?.name;

        if (name) {
            return name;
        }
    }
    return '!!! SERIE SANS NOM !!!';
});

const localization = computed(() => {
    for (const l of localizationCodes) {
        if (computedSerie.value?.translations?.[l]) {
            return l;
        }
    }
    return undefined;
});
</script>
