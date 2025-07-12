<template>
    <FormButton color="link">
        <Icon :name="name" :src="src" />
    </FormButton>
</template>

<script lang="ts" setup>
import {defineProps} from "vue";
import Icon from "@components/Icon.vue";
import FormButton from "@components/form/FormButton.vue";

interface Props {
    name?: string;
    src?: string;
}

defineProps<Props>();
</script>
