<template>
  <FormSelect v-model="value" :values="sourceLocalizations" #default="{ value: v}">
    <span v-if="v"><Flag :lang="v.code" /> {{ v.name }}</span>
  </FormSelect>
</template>

<script lang="ts" setup>
import {
    Flag,
    Localization,
    LocalizationCode,
    localizationCodes,
    localizations as allLocalizations
} from '@/localization';
import {computed} from 'vue';
import FormSelect from "@components/form/FormSelect.vue";

type LocalizationOrAll = Localization | 'all';
//type LocalizationCode = LocalizationCode ;//| 'all';

interface Props {
    modelValue: LocalizationCode;//OrAll;
    localizations?: LocalizationCode[];
}
interface Emits {
    (e: 'update:modelValue', value: LocalizationCode): void; //OrAll
}

const props = withDefaults(defineProps<Props>(), {
    localizations: () => localizationCodes
});
const emit = defineEmits<Emits>();

const value = computed({
    get: () => allLocalizations[props.modelValue],
    set: (v: Localization) => emit('update:modelValue',  v.code)
})

const sourceLocalizations = computed(() => Object.values(allLocalizations).filter(l => props.localizations.includes(l.code)));
</script>
