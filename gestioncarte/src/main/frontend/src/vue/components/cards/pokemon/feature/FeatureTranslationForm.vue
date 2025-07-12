<template>
    <FormRow>
        <FormInput :modelValue="modelValue.zebraName" @update:modelValue="v => update(m => m.zebraName = v)">
            <template v-slot:label>
                <span class="fw-bold">Nom d'étiquette</span>
            </template>
        </FormInput>
        <RegexInput :modelValue="modelValue.labelVerificationPattern" @update:modelValue="v => update(m => m.labelVerificationPattern = v)">
            <template v-slot:label>
                <a href="https://regex101.com/" target="_blank" rel="noopener">Regex</a> de verification du nom d'étiquette
            </template>
        </RegexInput>
    </FormRow>
    <template v-if="showPatterns && sources?.length > 0">
        <hr>
        <Row v-if="remainingSources.length > 0">
            <Column>
                <label class="form-label ps-0">Source Manquantes: </label>
                <FormButton v-for="source in remainingSources" :key="source" color="primary" class="badge rounded-pill ms-2 missing-source" @click="addPattern(source)">{{source}}</FormButton>
            </Column>
        </Row>
        <FormRow>
            <Column>
                <ListInput label="Patterns" itemClass="card border-primary p-2 mb-3" :modelValue="filteredPatterns" @update:modelValue="setPatterns" :initialValue="initPattern()" v-slot="{ item: item, items: items }">
                    <FeaturePatternEditForm v-model="item.value" :localization="localization" :otherPatterns="items.filter(i => i.key !== item.key).map(i => i.value)" />
                    <div class="ms-2 d-flex flex-column">
                        <FormButton color="info" class="mt-auto" @click="copyPattern(item.value)"><Icon name="copy-outline" /></FormButton>
                    </div>
                </ListInput>
            </Column>
        </FormRow>
    </template>
</template>

<script lang="ts" setup>
import {FeatureTranslationDTO, FeatureTranslationPatternDTO} from '@/types';
import FeaturePatternEditForm from '@components/cards/pokemon/feature/FeaturePatternEditForm.vue';
import FormButton from '@components/form/FormButton.vue';
import {cloneDeep} from 'lodash';
import FormInput from '@components/form/FormInput.vue';
import FormRow from '@components/form/FormRow.vue';
import ListInput from '@components/form/list/ListInput.vue';
import Column from '@components/grid/Column.vue';
import {computed, toRef} from 'vue';
import Row from "@components/grid/Row.vue";
import RegexInput from "@components/form/RegexInput.vue";
import {useSources} from "@components/cards/pokemon/feature/logic";
import {ModelComposables} from "@/vue/composables/model/ModelComposables";
import Icon from "@components/Icon.vue";
import {LocalizationCode} from "@/localization";

interface Props {
    localization: LocalizationCode;
    modelValue: FeatureTranslationDTO;
    patterns: FeatureTranslationPatternDTO[];
    showPatterns: boolean;
}
interface Emits {
    (e: 'update:modelValue', value: FeatureTranslationDTO): void;
    (e: 'update:patterns', value: FeatureTranslationPatternDTO[]): void;
}

const props = defineProps<Props>();
const emit = defineEmits<Emits>();

const update = ModelComposables.useUpdateModel<FeatureTranslationDTO>(() => props.modelValue, emit);

const sources = useSources(toRef(props, 'localization'));

const filteredPatterns = computed(() => props.patterns.filter(p => p.localization === props.localization));
const remainingSources = computed(() => sources.value.filter(s => filteredPatterns.value.find(p => p.source === s) === undefined));

const initPattern = (): FeatureTranslationPatternDTO => {
    return {
        id: 0,
        source: '',
        imgHref: '',
        title: '',
        localization: props.localization,
        regex: '(^|[\\s-]+).....([\\s-]+|$)'
    }
};

const setPatterns = (newPatterns: FeatureTranslationPatternDTO[]) => emit('update:patterns', props.patterns
    .filter(p => p.localization !== props.localization)
    .concat(newPatterns));
const copyPattern = (pattern: FeatureTranslationPatternDTO) => {
    let index = filteredPatterns.value.findIndex(p => p.id === pattern.id);
    let copy = cloneDeep(pattern);

    copy.id = 0;
    setPatterns([...filteredPatterns.value.slice(0, index), copy, ...filteredPatterns.value.slice(index)]);
}
const addPattern = (source: string) => {
    let newPattern = initPattern();

    newPattern.source = source;
    setPatterns([...filteredPatterns.value, newPattern]);
}
</script>

<style lang="scss" scoped>
.missing-source {
    font-size: 1em;
}
</style>
