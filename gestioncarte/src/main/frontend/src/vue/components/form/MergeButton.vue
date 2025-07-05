<template>
    <FormButton v-if="side !== 'none'" color="info" class="form-btn" :class="{ 'ms-2': side === 'left', 'me-2': side === 'right' }">
        <Icon :name="side === 'left' ? 'chevron-forward' : 'chevron-back'" />
    </FormButton>
</template>

<script lang="ts" setup>
import {Merge} from '@/types';
import FormButton from './FormButton.vue';
import Icon from "@components/Icon.vue";

interface Props {
    side?: Merge;
}

withDefaults(defineProps<Props>(), {
    side: 'none'
});
</script>
