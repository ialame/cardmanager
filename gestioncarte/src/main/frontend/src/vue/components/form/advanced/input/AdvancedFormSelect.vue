<template>
    <AdvancedFormCollapse :label="label" #default="{required: r}" :path="path">
        <div class="d-flex flex-row w-100" :class="statusClass">
            <FormSelect v-bind="mergeProps(inputProps, $attrs)" v-model="mappedValue" :labelClass="inputProps.class" :values="values" :required="r" class="advanced-form-select w-100">
                <template #default="{ value: v }">
                    <slot :value="v" />
                </template>
            </FormSelect>
            <FormButton v-if="reviewable" color="success" class="form-btn ms-2" @click="reviewed = true">
                <Icon name="checkmark-outline" />
            </FormButton>
            <AdvancedFormMergeButton v-if="readOnly" :path="path" />
            <AdvancedFormSourceButton v-else :path="path" #default="{ value }">
                <slot :value="value" />
            </AdvancedFormSourceButton>
        </div>
        <AdvancedFormFeedback class="mt-2" :validationResults="validationResults" />
    </AdvancedFormCollapse>
</template>

<script lang="ts" setup>
import {mergeProps} from "vue";
import Icon from "@components/Icon.vue";
import FormButton from '@components/form/FormButton.vue';
import FormSelect from "@components/form/FormSelect.vue";
import {Path} from "@/path";
import AdvancedFormMergeButton from "@components/form/advanced/merge/AdvancedFormMergeButton.vue";
import AdvancedFormFeedback from "@components/form/advanced/AdvancedFormFeedback.vue";
import {useAdvancedFormInput} from "@components/form/advanced/logic";
import AdvancedFormCollapse from "@components/form/advanced/AdvancedFormCollapse.vue";
import AdvancedFormSourceButton from "@components/form/advanced/source/AdvancedFormSourceButton.vue";
import {computedAsyncGetSet} from "@/vue/composables/AsynComposables";

interface Props {
    path: Path;
    label?: string;
    values: any[];
    mapper?: {
        get: (v: any) => Promise<any>;
        set: (v: any) => Promise<any>;
    }
}

const props = withDefaults(defineProps<Props>(), {
    label: '',
});

const { value, source, validationResults, statusClass, reviewable, reviewed, inputProps, readOnly } = useAdvancedFormInput<any, any>(() => props.path);
const mappedValue = computedAsyncGetSet({
    get: async () => {
        if (props.mapper) {
            return await props.mapper.get(source.value);
        }
        return source.value;
    },
    set: async (v) => {
        if (props.mapper) {
            value.value = await props.mapper.set(v);
        } else {
            value.value = v;
        }
    }
})
</script>

<style lang="scss" scoped>
@import "../AdvancedForm.scss";

.advanced-form-select {
    @include for-each-status(true) using($status, $color) {
        &:deep(a.is-#{$status} + .dropdown-menu-select) {
            border-color: $color;
        }
    }
}
</style>
