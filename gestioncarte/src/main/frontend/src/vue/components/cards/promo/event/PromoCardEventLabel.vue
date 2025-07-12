<template>
    <Flag v-if="localization" :lang="localization" /> {{ name }} <IdTooltip :id="event?.id" />
</template>

<script lang="ts" setup>
import IdTooltip from "@components/tooltip/IdTooltip.vue";
import {Flag, getTranslationField} from "@/localization";
import {PromoCardEventDTO, PromoCardEventTranslationDTO} from "@/types";
import {computed} from "vue";

interface Props {
    event?: PromoCardEventDTO;
}

const props = defineProps<Props>();

const name = computed(() => props.event?.name || props.event?.translations?.us?.name || props.event?.translations?.jp?.name || props.event?.translations?.cn?.name || '!!! EVENT SANS NOM !!!');
const localization = computed(() => getTranslationField<PromoCardEventTranslationDTO>(props.event?.translations)?.[0]);
</script>
