<template>
    <Dropdown ref="dropdown" class="p-0 no-focus me-1 source-translation-dropdown" labelClass="no-after p-0" :disabled="disableDropdown" @toggle="openedTab = activeTabs[0]">
        <template #label>
            <Flag :lang="localization" :class="validationClass" />
            <Tooltip v-if="!!messages && messages.length > 0">
                <template v-for="(message, index) in messages" :key="index">
                    <div>{{message}}</div>
                </template>
            </Tooltip>
        </template>
        <div class="source-translation-div">
            <LocalizationLabel :localization="localization" class="ms-1 me-2" />
            <span class="ms-5">{{card?.translations?.[localization]?.number}} - {{card?.translations?.[localization]?.name}}</span>
            <hr>
            <AdvancedFormTabManager>
                <AdvancedFormTabButton class="no-scale" tab="name">Nom</AdvancedFormTabButton>
                <AdvancedFormTabButton class="no-scale" tab="labelName">Nom d'étiquette</AdvancedFormTabButton>
                <AdvancedFormTabButton class="no-scale" tab="originalName">Nom en caractère kanji (日本語)</AdvancedFormTabButton>
            </AdvancedFormTabManager>
            <AdvancedFormTab tab="name">
                <PokemonCardEditFormTranslationSourceList class="mb-0 light-bg" :card="card" :localization="localization" :sourceTranslations="sourceTranslations" :path="`translations.${localization}.name`" @update:value="v => submit('name', v)" />
            </AdvancedFormTab>
            <AdvancedFormTab tab="labelName">
                <PokemonCardEditFormTranslationSourceList class="mb-0 light-bg" :card="card" :localization="localization" :sourceTranslations="sourceTranslations" :path="`translations.${localization}.labelName`" @update:value="v => submit('labelName', v)" />
            </AdvancedFormTab>
            <AdvancedFormTab tab="originalName">
                <PokemonCardEditFormTranslationSourceList class="mb-0 light-bg" :card="card" :localization="localization" :sourceTranslations="sourceTranslations" :path="`translations.${localization}.originalName`" @update:value="v => submit('originalName', v)" />
            </AdvancedFormTab>
        </div>
    </Dropdown>
</template>

<script lang="ts" setup>
import {PokemonCardDTO, SourcedPokemonCardTranslationDTO} from '@/types';
import {Flag, LocalizationCode, LocalizationLabel, localizations} from "@/localization";
import {computed, ref} from 'vue';
import Dropdown from "@components/dropdown/Dropdown.vue";
import {PokemonCardEditFormTranslationSourceList, useValidateTranslation} from "@components/cards/pokemon/edit";
import {get} from "lodash";
import {compareStatus, getValidationClass, getValidationMessage, ValidationResult} from "@/validation";
import {computedAsync} from "@vueuse/core";
import Tooltip from "@components/tooltip/Tooltip.vue"
import {
    AdvancedFormTab,
    AdvancedFormTabButton,
    AdvancedFormTabManager,
    useProvideAdvancedFormTabControl
} from "@components/form/advanced";
import {NameKey} from "@components/cards/pokemon/extracted/list";
import {Path, pathArrayIncludes} from "@/path";


interface Props {
    card: PokemonCardDTO
    localization: LocalizationCode;
    sourceTranslations: SourcedPokemonCardTranslationDTO[];
    reviewedPaths?: Path[],
    optionalPaths?: Path[],
}
interface Emits {
    (e: 'submit', k: NameKey, f: string): void
}

const props = withDefaults(defineProps<Props>(), {
    reviewedPaths: () => [],
    optionalPaths: () => [],
});
const emit = defineEmits<Emits>();

const hasOriginalName = computed(() => localizations[props.localization].hasOriginalName);
const {activeTabs, openedTab, goToNextTab} = useProvideAdvancedFormTabControl(() => ({ 'originalName': hasOriginalName.value }));
const dropdown = ref<typeof Dropdown>();
const disableDropdown = computed(() => !props.sourceTranslations.some(t => t.localization === props.localization));

const doValidate = useValidateTranslation(() => props.card, () => props.sourceTranslations);
const validate = (p: NameKey) => {
    const path = `translations.${props.localization}.${p}`;

    return doValidate(get(props.card, path), path, pathArrayIncludes(props.optionalPaths, path), pathArrayIncludes(props.reviewedPaths, path));
}

const status = computedAsync(async () => ({
    name: await validate('name'),
    labelName: await validate('labelName'),
    originalName: hasOriginalName.value ? await validate('originalName') : 'valid',
}), {
    name: 'valid',
    labelName: 'valid',
    originalName: 'valid',
});
const validationClass = computed(() => {
    const s = Object.values(status.value);
    let v: ValidationResult = 'valid';

    for (const i of s) {
        if (compareStatus(i, v) > 0) {
            v = i;
        }
    }
    return getValidationClass(v);
});
const messages = computed(() => Object.values(status.value)
    .map(s => getValidationMessage(s))
    .filter(v => !!v));

const submit = (k: NameKey, form: string) => {
    emit('submit', k, form);
    if (openedTab.value === activeTabs.value[activeTabs.value.length - 1]) {
        dropdown.value?.hide();
    } else {
        goToNextTab();
    }
};
</script>

<style lang="scss" scoped>

@import "src/vue/components/form/advanced/AdvancedForm.scss";

.source-translation-div {
    width: 600px;
    padding: 1rem;
}

.source-translation-dropdown:deep(>.dropdown-menu) { background-color: $light-bg; }
.source-translation-dropdown:deep(a.disabled) { opacity: 1; }

@include for-each-status() using ($status, $color) {
    .flag.is-#{$status} {
        @include highlight($color);
    }
}

.source-translation-dropdown:deep(.dropdown-menu) {
    @include shadow-outline;
    background-color: $dark-bg !important;
}
</style>
