<template>
    <PokemonSetEditForm v-model="editSetForm.set"
                        v-model:icon="icon"
                        v-model:expansionsBulbapedia="editSetForm.expansionsBulbapedia"
                        v-model:ptcgoSets="editSetForm.ptcgoSets"
                        v-model:wikiUrls="editSetForm.wikiUrls"
                        v-model:officialSitePaths="editSetForm.officialSitePath"
                        v-model:pkmncardsComSetPath="editSetForm.pkmncardsComSetPath"
                        v-model:japaneseOfficialSitePgs="editSetForm.japaneseOfficialSitePgs"
                        v-model:pokecardexCode="editSetForm.pokecardexCode"
                        v-model:localizations="localizations"
                        @save="save">
        <template #save-buttons>
            <AdvancedFormSaveButton v-if="editSetForm.set.id === ''" color="secondary" @save="saveAndNew" :shortcut="false" class="mt-4 mb-4 me-2">Enregistrer et rester</AdvancedFormSaveButton>
        </template>
    </PokemonSetEditForm>
</template>

<script lang="ts" setup>
import rest from '@/rest';
import {PokemonSetService} from '@/services/card/pokemon/set/PokemonSetService';
import {EditSetForm, ExtractedImageDTO} from '@/types';
import {computed, ref, watch} from 'vue';
import {usePageTitle} from "@/vue/composables/PageComposables";
import {useRoute, useRouter} from "vue-router";
import {getInitialExtensionBulbapedia, PokemonSetEditForm} from "@components/cards/pokemon/set";
import {LocalizationCode} from '@/localization';
import {AdvancedFormSaveButton} from "@components/form/advanced";
import {useRaise} from "@/alert";
import log from "loglevel";


const route = useRoute();
const router = useRouter();
const raise = useRaise();
const localizations = ref<LocalizationCode[]>([]);
const localization = computed<LocalizationCode>(() => localizations.value?.[0] || 'us');
const editSetForm = ref<EditSetForm>(PokemonSetService.newEditSetForm());
usePageTitle(computed(() => {
    const edit = editSetForm.value.set.id;
    const end = edit ? ` (id: ${edit})` : '';

    return `Pokémon - ${edit ? 'Modification' : 'Ajout'} d'extension${end}`;
}));
const icon = ref<ExtractedImageDTO>();

const doSave = async () => {
    let id = editSetForm.value.set.id;

    log.info('Saving set', id);
    if (!id) {
        id = await rest.post("/api/cards/pokemon/sets/form", {data: editSetForm.value});
    } else {
        await rest.put("/api/cards/pokemon/sets/form", {data: editSetForm.value});
    }
    if (editSetForm.value.set.shortName) {
        if (icon.value && icon.value.source !== 'saved') {
            await rest.put(`/api/cards/pokemon/sets/${id}/image`, { data: icon.value.base64Image })
            icon.value.source = 'saved';
        }
    }
    raise.success("Extension enregistrée avec succès.");
    return id;
};
const save = async () => {
    const id = await doSave()

    if (!editSetForm.value.set.id) {
        await router.push(`/cards/pokemon/sets/${id}`);
    }
    editSetForm.value = await rest.get(`/api/cards/pokemon/sets/${id}/form`);
};
const saveAndNew = async () => {
    await doSave();
    await reset();
}

const reset = async () => {
    editSetForm.value = await PokemonSetService.cloneEditSetForm(localizations.value, route.params.setId as string);
}

watch(route, async route => {
    const l = route.query.localization;

    localizations.value = l ? (await rest.get(`/api/localizations/groups/${l}`)) || [] : [];
    await reset();

    if (editSetForm.value.set.id !== "") {
        return;
    }

    if (editSetForm.value.expansionsBulbapedia.length === 0) {
        editSetForm.value.expansionsBulbapedia.push(getInitialExtensionBulbapedia('', localization.value));
    }
    if (localizations.value.includes('us') && editSetForm.value.officialSitePath.length === 0) {
        editSetForm.value.officialSitePath.push('');
    }
}, { immediate: true });
</script>
