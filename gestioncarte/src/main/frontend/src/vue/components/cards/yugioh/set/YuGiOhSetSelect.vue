<template>
    <component v-bind="$attrs" :is="component" :values="sets" label="Extension" #default="{ value: set }">
        <YuGiOhSetLabel v-if="!isNil(set) && !isEmpty(set)" :set="set" />
        <span v-else-if="hasAllOption" class="default-option">Toutes</span>
        <span v-else class="default-option" />
    </component>
</template>

<script lang="ts" setup>
import {YuGiOhSetDTO} from "@/types";
import {computed} from "vue";
import {AdvancedFormSelect} from "@components/form/advanced";
import FormSelect from "@components/form/FormSelect.vue";
import {computedAsync} from "@vueuse/core";
import {isEmpty, isNil} from "lodash";
import YuGiOhSetLabel from "@components/cards/yugioh/set/YuGiOhSetLabel.vue";
import {yugiohSetService} from "@components/cards/yugioh/set/service";

interface Props {
    values?: YuGiOhSetDTO[];
    advanced?: boolean;
    hasAllOption?: boolean;
}

const props = withDefaults(defineProps<Props>(), {
    values: () => [],
    advanced: false,
    hasAllOption: false,
});

const sets = computedAsync<YuGiOhSetDTO[]>(() => !isEmpty(props.values) ? props.values : yugiohSetService.all.value, []);
const component = computed(() => {
    if (props.advanced) {
        return AdvancedFormSelect;
    }
    return FormSelect;
});
</script>

<style lang="scss" scoped>
:deep(div):has(>span.default-option) {
    height: 34px;
}
</style>
