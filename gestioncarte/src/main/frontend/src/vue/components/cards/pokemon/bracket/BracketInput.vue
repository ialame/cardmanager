<template>
    <AdvancedFormSelect :path="path" :values="filteredBrackets" #default="{ value: bracket }">
        <BracketLabel :bracket="bracket" />
    </AdvancedFormSelect>
    <div v-if="!value?.id && !readOnly" class="ms-2 d-flex flex-column">
        <FormButton color="secondary" class="form-btn" @click="edit()"><Icon name="pencil"/></FormButton>
    </div>
    <Modal ref="editModal" label="Crochet de carte" dynamic>
        <BracketEditForm v-model="edited" @save="save()" />
    </Modal>
</template>

<script lang="ts" setup>
import {AdvancedFormSelect, useAdvancedFormInput} from "@components/form/advanced";
import FormButton from "@components/form/FormButton.vue";
import Icon from "@components/Icon.vue";
import Modal from "@components/modal/Modal.vue";
import {computed, ref} from "vue";
import {BracketDTO} from "@/types";
import {BracketEditForm, BracketLabel} from "@components/cards/pokemon/bracket";
import {PokemonComposables} from "@/vue/composables/pokemon/PokemonComposables";
import {LocalizationCode} from "@/localization";
import {Path} from "@/path";

interface Props {
    path: Path;
    localization?: LocalizationCode;
}

const props = defineProps<Props>();

const brackets = PokemonComposables.bracketService.all
const filteredBrackets = computed(() => props.localization ? brackets.value.filter(b => b.localization === props.localization) : brackets.value);
const { value, readOnly } = useAdvancedFormInput<any, BracketDTO>(() => props.path);

const editModal = ref<typeof Modal>();
const edited = ref<BracketDTO>();
const edit = () => {
    edited.value = value.value;
    editModal.value?.show();
}
const save = () => {
    value.value = edited.value;
    editModal.value?.hide();
}
</script>
