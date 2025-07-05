<template>
    <SingleLineCard :card="card">
        <template v-slot:buttons>
            <FormButton color="secondary" title="Telecharger le resultat en json" @click="downloadJson" class="me-2"><Icon src="/svg/download.svg" /></FormButton>
            <FormButton color="info" title="Voir la carte" @Click="$emit('openInfo')" class="me-2"><Icon name="information-circle-outline" /></FormButton>
        </template>
    </SingleLineCard>
</template>

<script lang="ts" setup>
import {PokemonCardDTO} from "@/types";
import SingleLineCard from "@components/cards/pokemon/SingleLineCard.vue";
import {downloadData, getDateStr} from "@/retriever";
import FormButton from '@components/form/FormButton.vue';
import Icon from "@components/Icon.vue";

interface Props {
    card: PokemonCardDTO;
    showCompare?: boolean;
}
interface Emits {
    (e: 'openInfo'): void;
}
const props = withDefaults(defineProps<Props>(), {
    showCompare: true,
});
defineEmits<Emits>();

const downloadJson = () => downloadData("pokemon-card-" + getDateStr() + ".json", props.card);
</script>
