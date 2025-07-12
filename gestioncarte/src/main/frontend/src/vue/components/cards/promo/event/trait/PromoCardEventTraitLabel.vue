<template>
    <Flag v-if="localization" :lang="localization" /> {{ name }} <IdTooltip :id="trait?.id" />
</template>

<script lang="ts" setup>
import IdTooltip from "@components/tooltip/IdTooltip.vue";
import {Flag} from "@/localization";
import {PromoCardEventTraitDTO} from "@/types";
import {computed} from "vue";

interface Props {
    trait?: PromoCardEventTraitDTO;
}

const props = defineProps<Props>();

const name = computed(() => props.trait?.name || props.trait?.translations?.us?.name || props.trait?.translations?.jp?.name || props.trait?.translations?.cn?.name || '!!! CARACTERISTIQUE SANS NOM !!!');
const localization = computed(() => {
    if (props.trait?.translations?.us) {
        return 'us';
    } else if (props.trait?.translations?.jp) {
        return 'jp';
    } else if (props.trait?.translations?.cn) {
        return 'cn';
    }
    return undefined;
});
</script>
