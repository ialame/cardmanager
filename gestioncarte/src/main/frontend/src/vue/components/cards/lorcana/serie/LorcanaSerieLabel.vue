<template>
    <div class="d-flex flex-row overflow-hidden">
        <Flag v-if="localization" class="mt-2 me-1" :lang="localization" />
        {{ name }}
        <IdTooltip :id="serie?.id" />
    </div>
</template>

<script lang="ts" setup>
import {LorcanaSerieDTO} from "@/types";
import {computed} from "vue";
import IdTooltip from "@components/tooltip/IdTooltip.vue";
import {Flag, localizationCodes} from "@/localization";

interface Props {
    serie?: LorcanaSerieDTO;
}

const props = defineProps<Props>();

const name = computed(() => {
    for (const l of localizationCodes) {
        const name = props.serie?.translations?.[l]?.name;

        if (name) {
            return name;
        }
    }
    return '!!! SERIE SANS NOM !!!';
});
const localization = computed(() => {
    for (const l of localizationCodes) {
        if (props.serie?.translations?.[l]) {
            return l;
        }
    }
    return undefined;
});
</script>
