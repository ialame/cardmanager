<template>
    <div class="container mt-3">
        <FormRow>
            <FormInput label="Nom PCA" v-model="feature.pcaName" />
        </FormRow>
        <FormRow>
            <Column>
                <label>Ordre</label>
                <input type="number" class="form-control" v-model="feature.order" />
            </Column>
            <Column>
                <Checkbox label="Visible" v-model="feature.visible" />
                <Checkbox label="Ajout PCA" v-model="feature.pcaFeature" />
            </Column>
        </FormRow>
    </div>
    <hr>
    <TranslationForm v-model="feature.translations" v-model:localizations="localizationCodes" :show="true" :useOriginalName="false">
        <template v-slot:name-after="slotProps">
            <RegexInput v-if="feature.translations[slotProps.localization.code]" v-model="feature.translations[slotProps.localization.code].verificationPattern" label="Regex de verification du nom" />
        </template>
        <template v-slot="slotProps">
            <FeatureTranslationForm v-if="feature.translations[slotProps.localization.code]" :modelValue="slotProps.item" @update:modelValue="v => feature.translations[slotProps.localization.code] = v" v-model:patterns="patterns" :localization="slotProps.localization.code" :showPatterns="!feature.pcaFeature" />
        </template>
    </TranslationForm>
    <div class="container mt-3">
        <FormButton ref="saveButton" class="float-end mt-4 mb-4" @click="save">Enregistrer</FormButton>
    </div>
</template>

<script lang="ts" setup>
import {localizationCodes} from '@/localization';
import rest from '@/rest';
import {FeatureDTO, FeatureTranslationPatternDTO} from '@/types';
import TranslationForm from '@components/cards/pokemon/TranslationForm.vue';
import FormButton from '@components/form/FormButton.vue';
import Checkbox from '@components/form/Checkbox.vue';
import {computed, onMounted, ref, watch} from 'vue';
import FormInput from '@components/form/FormInput.vue';
import FormRow from '@components/form/FormRow.vue';
import Column from '@components/grid/Column.vue';
import {DOMComposables} from "@/vue/composables/DOMComposables";
import {SaveComposables} from "@/vue/composables/SaveComposables";
import FeatureTranslationForm from "@components/cards/pokemon/feature/FeatureTranslationForm.vue";
import RegexInput from "@components/form/RegexInput.vue";
import {useRoute} from "vue-router";
import {usePageTitle} from "@/vue/composables/PageComposables";
import {useRaise} from "@/alert";

const route = useRoute();
const raise = useRaise();
const setUrl = DOMComposables.useSetUrl();

const saveButton = ref<typeof FormButton>();
const feature = ref<FeatureDTO>({
    id: '',
    visible: true,
    order: 1,
    pcaName: '',
    translations: {},
    pcaFeature: false
});
const patterns = ref<FeatureTranslationPatternDTO[]>([]);

usePageTitle(computed(() => {
    const edit = feature.value.id;
    const end = edit ? ` (id: ${feature.value.id})` : '';

    return `Pokémon - ${edit? 'Modification' : 'Ajout'} de particularité${end}`;
}));


const load = async () => {
    if (route.params.featureId) {
        feature.value = await rest.get(`/api/cards/pokemon/features/${route.params.featureId}`)
    }
}
const loadPatterns = (id?: string) => {
    id = id || feature.value.id;
    if (id) {
        rest.get(`/api/cards/pokemon/features/${id}/patterns`, {
            success: data => patterns.value = data
        });
    }
}
const save = SaveComposables.useSaveAsync(async () => {
    let id = feature.value.id;

    if (!id) {
        id = await rest.post('/api/cards/pokemon/features', {
            data: feature.value,
            buttons: saveButton.value
        });
        feature.value = await rest.get(`/api/cards/pokemon/features/${id}`);
        setUrl("Pokémon - Ajout de particularité", `/cards/pokemon/features/${id}`);
    } else {
        await rest.put('/api/cards/pokemon/features', { 
            data: feature.value,
            buttons: saveButton.value
        });
    }
    if (!feature.value.pcaFeature) {
        await rest.put(`/api/cards/pokemon/features/${id}/patterns`, {
            data: patterns.value,
            buttons: saveButton.value,
            success: () => raise.success('Particularité enregistrée avec succès')
        });
    }
});

watch(feature, (newValue, oldValue) => {
    if (newValue.id && newValue.id !== oldValue.id) {
        loadPatterns(newValue.id);
    }
});
onMounted(load);
</script>
