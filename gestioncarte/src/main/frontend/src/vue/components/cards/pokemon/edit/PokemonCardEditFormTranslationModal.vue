<template>
	<Modal ref="modal" label="Traductions" size="lg">
        <template #label>
            <Flag :lang="localization" class="icon-24" /> Traductions
            <span class="ms-5">{{context.data?.translations?.[localization]?.number}} - {{context.data?.translations?.[localization]?.name}}</span>
        </template>
        <PokemonCardEditFormTranslationSourceList v-bind="$attrs" :path="path" :localization="localization" :card="context.data" @update:value="setValue" />
	</Modal>
</template>

<script lang="ts" setup>
import Modal from '@components/modal/Modal.vue';
import {EditedPokemonCard, PokemonCardEditFormTranslationSourceList} from "@components/cards/pokemon/edit";
import {ref} from "vue";
import {PokemonCardDTO} from "@/types";
import {Flag, LocalizationCode} from "@/localization";
import {useAdvancedFormInput} from "@components/form/advanced";
import {Path} from "@/path";

interface Props {
    path: Path;
    localization: LocalizationCode;
}

const props = defineProps<Props>();

const { value, context, reviewed } = useAdvancedFormInput<PokemonCardDTO | EditedPokemonCard, any>(() => props.path);

const modal = ref<typeof Modal>();
const open = () => modal.value?.show();
const setValue = (v: any) => {
    value.value = v;
    reviewed.value = true;
    modal.value?.hide();
}

defineExpose({ open });
</script>
