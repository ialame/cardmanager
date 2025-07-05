<template>
    <div class="col-6" :class="{ 'ms-auto': localization === 'jp' }">
        <div ref="drag" :class="{ 'position-relative': isDragging }" :style="style">
            <SingleLineCardWithButtons :card="entry.extractedCard.card" class="bg-white" :showCompare="false" :flagFilter="[localization]" :localization="localization" @openInfo="setInfoCard(entry.extractedCard.card)" />
        </div>
        <Modal ref="infoModal" label="Info carte" size="lg">
            <PokemonCardEditForm v-if="infoCard" :modelValue="infoCard" :disabled="true"></PokemonCardEditForm>
        </Modal>
    </div>
</template>

<script lang="ts" setup>
import {computed, ref} from 'vue';
import {PokemonCardDTO} from "@/types";
import {ExtractedPokemonCardEntry} from "@components/cards/pokemon/extracted/list";
import {DragAndDropComposables} from "@/vue/composables/DragAndDropComposables";
import Modal from "@components/modal/Modal.vue";
import {PokemonCardEditForm, SingleLineCardWithButtons} from "@components/cards/pokemon/edit";
import {LocalizationCode} from "@/localization";

interface Props {
    entry: ExtractedPokemonCardEntry;
    localization: LocalizationCode;
}

const props = defineProps<Props>();

const infoModal = ref<typeof Modal>();
const infoCard = ref<PokemonCardDTO>();
const setInfoCard = (card: PokemonCardDTO) => {
    infoCard.value = card;
    infoModal.value?.show();
};

const dragData = computed(() => ({
    entry: props.entry,
    localization: props.localization,
}));
const drag = ref<HTMLDivElement>();
const {isDragging, style} = DragAndDropComposables.useDrag("reassociation", drag, dragData);
</script>
