<template>
    <Flag v-if="computedLocalization" class="mt-2 me-1" :lang="computedLocalization" /> {{ name }}{{number ? ` (${number})` : ''}} <IdTooltip :id="computedCard?.id"/>
</template>

<script lang="ts" setup>
import IdTooltip from "@components/tooltip/IdTooltip.vue";
import {OnePieceCardDTO, OnePieceCardTranslationDTO} from "@/types";
import {computed} from "vue";
import {Flag, getTranslationField, LocalizationCode} from "@/localization";
import {computedAsync} from "@vueuse/core";
import {isString} from "lodash";
import {onePieceCardService} from "@components/cards/onepiece/service";

interface Props {
    card?: OnePieceCardDTO | string;
    localization?: LocalizationCode;
}

const props = defineProps<Props>();

const computedCard = computedAsync<OnePieceCardDTO>(() => isString(props.card) ? onePieceCardService.get(props.card) : props.card);
const name = computed(() => {
    if (props.localization) {
        const name = computedCard.value?.translations[props.localization]?.name;

        if (name) {
            return name;
        }
    }
    return getTranslationField<OnePieceCardTranslationDTO, "name">(computedCard.value?.translations, "name")?.[1] || '!!! CARTE SANS NOM !!!'
});
const number = computed(() => props.card?.number ?? '');

const computedLocalization = computed(() => props.localization || getTranslationField<OnePieceCardTranslationDTO>(computedCard.value?.translations)?.[0]);
</script>
