<template>
    <div v-if="messages.length">
        <template v-for="(m, i) in messages" :key="i">
            <div v-if="m.message" class="feedback" :class="m.class">{{m.message}}</div>
        </template>
    </div>
</template>

<script lang="ts" setup>
import {getValidationClass, getValidationMessage, ValidationResult} from "@/validation";
import {computed} from "vue";

interface Props {
    validationResults: ValidationResult[];
}

const props = defineProps<Props>();

const messages = computed(() => props.validationResults.map(r => ({
    class: getValidationClass(r),
    message: getValidationMessage(r)
})).filter(m => m.message));
</script>

<style lang="scss" scoped>
@import "./AdvancedForm.scss";

.feedback {
    width: 100%;
    @include font-size($form-feedback-font-size);
    font-style: $form-feedback-font-style;
    @include for-each-status(true) using ($status, $color) {
        &.is-#{$status} {
            color: $color;
        }
    }
}
:not(.feedback) + .feedback {
    margin-top: $form-feedback-margin-top;
}
</style>
