<template>
    <form class="advanced-form" :class="{ 'highlight': highlight, 'small-advanced-form': small }">
        <AdvancedFormTabManager>
            <slot name="tabs" />
        </AdvancedFormTabManager>
        <div class="container-fluid">
            <div class="float-end round-btn-sm mt-5" v-if="envName === 'development'">
                <FormButton color="primary" class="round-btn-sm" title="Telecharger les donnés de debug" @click="downloadDebugJson"><Icon src="/svg/download.svg" /></FormButton>
            </div>
            <Row class="mt-1">
                <Column v-for="i in computedMergeSources.length" :key="i" class="vl merge-column">
                    <AdvancedFormMergeSide :side="computedMergeSources.length - i">
                        <slot />
                    </AdvancedFormMergeSide>
                </Column>
                <Column class="merge-column">
                    <AdvancedFormMergeSide>
                        <slot />
                    </AdvancedFormMergeSide>
                </Column>
            </Row>
            <Row>
                <slot name="out-of-side" />
            </Row>
        </div>
    </form>
</template>

<script lang="ts" setup>
import {computed} from "vue";
import {useVModel} from "@vueuse/core";
import Row from "@components/grid/Row.vue";
import Column from "@components/grid/Column.vue";
import {ValidationJson, ValidationRules} from "@/validation";
import {Path, pathToString} from "@/path";
import {envName} from '@/utils';
import FormButton from "@components/form/FormButton.vue";
import Icon from "@components/Icon.vue";
import {downloadData, getDateStr} from "@/retriever";
import AdvancedFormMergeSide from "@components/form/advanced/merge/AdvancedFormMergeSide.vue";
import AdvancedFormTabManager from "@components/form/advanced/tab/AdvancedFormTabManager.vue";
import {useProvideAdvancedFormContext} from "@components/form/advanced/logic";
import {useProvideAdvancedFormTabControl} from "@components/form/advanced/tab";

interface Props {
    modelValue: any;
    mergeSources?: any[];
    tabConfig?: Record<string, boolean>;
    rules?: ValidationRules<any>,
    disabled?: boolean;
    reviewedPaths?: Path[],
    optionalPaths?: Path[],
    highlight?: boolean;
    small?: boolean;
}
interface Emits {
    (e: 'update:modelValue', v: any): void
    (e: 'update:reviewedPaths', v: Path[]): void
    (e: 'update:optionalPaths', v: Path[]): void
}

const props = withDefaults(defineProps<Props>(), {
    mergeSources: () => [],
    disabled: false,
    tabConfig: () => ({}),
    rules: () => ({}),
    reviewedPaths: () => [],
    optionalPaths: () => [],

    highlight: true,
    small: false,
});
const emit = defineEmits<Emits>();

useProvideAdvancedFormContext(useVModel(props, 'modelValue', emit), {
    mergeSources: () => props.mergeSources,
    disabled: () => props.disabled,
    rules: () => props.rules,
    reviewedPaths: useVModel(props, 'reviewedPaths', emit, {passive: true}),
    optionalPaths: useVModel(props, 'optionalPaths', emit, {passive: true}),
});
const tabControl = useProvideAdvancedFormTabControl(() => props.tabConfig);

const computedMergeSources = computed(() => props.mergeSources?.filter(s => s).reverse() || []);

const downloadDebugJson = () => downloadData("advance-form-data-" + getDateStr() + ".json", {
    data: props.modelValue,
    mergeData: props.mergeSources,
    reviewedPaths: props.reviewedPaths.map(pathToString),
    optionalPaths: props.optionalPaths.map(pathToString),
} satisfies ValidationJson<any>);

defineExpose({ tabControl });
</script>

<style lang="scss" scoped>
@import "./AdvancedForm.scss";

@mixin label-margin {
    margin-bottom: 0.75rem;
    margin-top: 1rem;

    & ~ * {
        margin-top: 1rem;
    }
}

.advanced-form {
    :deep(.card) {
        padding: 0.5rem;

        hr.card-separator {
            border-top: 3px solid;
            opacity: 0.5;
            margin-top: 2rem;
        }
    }
    :deep(.error-blink) {
        animation: error-blink 1s;
        animation-iteration-count: 1;
    }
    :deep(.merge-column) {
        padding: 0;
        &.vl {
            padding-right: 0.75rem !important;

            + .merge-column {
                padding-left: 0.75rem !important;
            }
        }
    }

    &.highlight {
        @include for-each-status() using ($status, $color) {
            :deep(.form-control.is-#{$status}) {
                @include highlight($color);
            }
        }
    }

    @include for-each-status() using ($status, $color) {
        :deep(.is-#{$status}-highlight) {
            @include highlight($color);
        }
    }

    :deep {
        @include form-validation-state('warning', $yellow, $form-feedback-icon-warning);
        @include form-validation-state('new', $green, $form-feedback-icon-new);
        @include form-validation-state('merge', $info, $form-feedback-icon-merge);
    }
    &.small-advanced-form:deep {
        .form-control {
            padding-top: 1px;
            padding-bottom: 1px;
            &.form-select {
                min-height: 28px;
            }
        }
        .form-label {
            @include label-margin;
            &:empty {
                display: none;
            }
        }
        .collapse-button {
            @include label-margin;
        }
        .form-row {
            margin-bottom: 0.25rem;
        }
        .btn.form-btn {
            padding: 1px 5px;
            width: 28px;
            height: 28px;
            border-radius: 0.375rem !important;
        }

        .advanced-form-localization-select>.btn {
            margin-top: 0;
        }
    }
}
@keyframes error-blink {
    0% { background-color: inherit; }
    50% { background-color: $danger; }
    100% { background-color: inherit; }
}
</style>
