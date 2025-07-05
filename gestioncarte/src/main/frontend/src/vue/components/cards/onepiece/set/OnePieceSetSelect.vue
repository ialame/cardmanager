<template>
    <component v-bind="$attrs" :is="component" :values="sets" label="Extension" #default="{ value: set }">
        <OnePieceSetLabel v-if="!isNil(set) && !isEmpty(set)" :set="set" />
        <span v-else-if="hasAllOption" class="default-option">Toutes</span>
        <span v-else class="default-option" />
    </component>
</template>

<script lang="ts" setup>
import {computed} from "vue";
import {AdvancedFormSelect} from "@components/form/advanced";
import FormSelect from "@components/form/FormSelect.vue";
import {computedAsync} from "@vueuse/core";
import {isEmpty, isNil} from "lodash";
import OnePieceSetLabel from "@components/cards/onepiece/set/OnePieceSetLabel.vue";
import {onePieceSetService} from "@components/cards/onepiece/set/service";
import {OnePieceSetDTO} from "@/types";

interface Props {
    values?: OnePieceSetDTO[];
    advanced?: boolean;
    hasAllOption?: boolean;
}

const props = withDefaults(defineProps<Props>(), {
    values: () => [],
    advanced: false,
    hasAllOption: false,
});

const sets = computedAsync<OnePieceSetDTO[]>(() => !isEmpty(props.values) ? props.values : onePieceSetService.all.value, []);
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
