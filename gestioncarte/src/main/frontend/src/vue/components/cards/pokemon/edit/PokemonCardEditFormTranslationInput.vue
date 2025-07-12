<template>
    <AdvancedFormInput :path="path" v-bind="$attrs">
        <template v-if="!disabled && side === -1" #after>
            <FormButton color="info" class="form-btn ms-2" @click="open" :disabled="filteredTranslators.length === 0"><Icon name="list" /></FormButton>
            <PokemonCardEditFormTranslationModal ref="modal" :path="path" :localization="localization" :sourceTranslations="filteredTranslators" />
        </template>
    </AdvancedFormInput>
</template>
<script lang="ts" setup>
import {AdvancedFormInput, useAdvancedFormContext, useAdvancedFormSide} from "@components/form/advanced";
import {SourcedPokemonCardTranslationDTO} from "@/types";
import PokemonCardEditFormTranslationModal
    from "@components/cards/pokemon/edit/PokemonCardEditFormTranslationModal.vue";
import FormButton from "@components/form/FormButton.vue";
import {computed, ref} from "vue";
import Icon from "@components/Icon.vue";
import {Path} from "@/path";
import {LocalizationCode} from "@/localization";

interface Props {
    path: Path;
    localization: LocalizationCode;
    sourceTranslations: SourcedPokemonCardTranslationDTO[];
}

const props = withDefaults(defineProps<Props>(), {
    sourceTranslations: () => []
});

const side = useAdvancedFormSide();
const { disabled }  = useAdvancedFormContext();
const modal = ref<typeof PokemonCardEditFormTranslationModal>();
const open = () => modal.value?.open();
const filteredTranslators = computed(() => props.sourceTranslations.filter(s => s.localization === props.localization));
</script>
